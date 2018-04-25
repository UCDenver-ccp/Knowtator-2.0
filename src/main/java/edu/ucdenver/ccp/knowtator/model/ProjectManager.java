package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.prefs.BackingStoreException;

public class ProjectManager {
  private static final Logger log = Logger.getLogger(ProjectManager.class);
  private KnowtatorController controller;
  private File projectLocation;
  private File articlesLocation;
  private File ontologiesLocation;
  private File annotationsLocation;
  private File profilesLocation;
  private boolean projectLoaded;
  private List<ProjectListener> listeners;

  public ProjectManager(KnowtatorController controller) {
    this.controller = controller;
    projectLoaded = false;
    listeners = new ArrayList<>();
  }

  public void addListener(ProjectListener listener) {
    listeners.add(listener);
  }

  public File getProjectLocation() {
    return projectLocation;
  }

  public File getArticlesLocation() {
    return articlesLocation;
  }

  public void newProject(File projectDirectory) {
    makeFileStructure(projectDirectory);
    loadProject();
  }

  public void loadProject(File projectFile) {
    if (projectFile != null) {
      makeFileStructure(projectFile.getParentFile());
      loadProject();
      controller.getPrefs().put("Last Project", projectFile.getAbsolutePath());
      try {
        controller.getPrefs().flush();
      } catch (BackingStoreException e) {
        e.printStackTrace();
      }
    }
  }

  public void loadProject(
          File profilesLocation,
          File ontologiesLocation,
          File articlesLocation,
          File annotationsLocation) {
    makeFileStructure(profilesLocation, ontologiesLocation, articlesLocation, annotationsLocation);
    loadProject();
  }

  private void loadProject() {
    projectLoaded = false;
    listeners.forEach(ProjectListener::projectClosed);

    if (ontologiesLocation != null) {
      log.warn("Loading ontologies");
      try {
        controller.getOWLAPIDataExtractor().read(ontologiesLocation);
      } catch (IOException | OWLWorkSpaceNotSetException e) {
        log.warn("Could not load ontologies");
      }
    }

    if (profilesLocation != null) {
      log.warn("Loading profiles");
      loadFromFormat(KnowtatorXMLUtil.class, controller.getProfileManager(), profilesLocation);
    }

    if (annotationsLocation != null) {
      log.warn("Loading annotations");
      loadFromFormat(
              KnowtatorXMLUtil.class, controller.getTextSourceManager(), annotationsLocation);
    }

    if (controller.getTextSourceManager().getTextSourceCollection().getCollection().isEmpty()) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(controller.getProjectManager().getArticlesLocation());

      if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        controller.getProjectManager().addDocument(fileChooser.getSelectedFile());
      }
    }

    projectLoaded = true;

    for (ProjectListener listener : listeners) {
      listener.projectLoaded();
    }
  }

  /**
   * Allows for manual setting of the project structure
   */
  private void makeFileStructure(
          File profilesLocation,
          File ontologiesLocation,
          File articlesLocation,
          File annotationsLocation) {
    this.projectLocation = null;
    this.profilesLocation = profilesLocation;
    this.ontologiesLocation = ontologiesLocation;
    this.articlesLocation = articlesLocation;
    this.annotationsLocation = annotationsLocation;
  }

  private void makeFileStructure(File projectDirectory) {
    try {
      projectLocation = projectDirectory;
      articlesLocation = new File(projectDirectory, "Articles");
      ontologiesLocation = new File(projectDirectory, "Ontologies");
      annotationsLocation = new File(projectDirectory, "Annotations");
      profilesLocation = new File(projectDirectory, "Profiles");

      Files.createDirectories(projectDirectory.toPath());
      Files.createDirectories(articlesLocation.toPath());
      Files.createDirectories(ontologiesLocation.toPath());
      Files.createDirectories(annotationsLocation.toPath());
      Files.createDirectories(profilesLocation.toPath());
      if (FileUtils.listFiles(projectDirectory, new String[]{"knowtator"}, false).size() == 0)
        Files.createFile(
                new File(projectDirectory, projectDirectory.getName() + ".knowtator").toPath());
    } catch (IOException e) {
      System.err.println("Cannot create directories - " + e);
    }
  }

  public void saveProject() {

    if (getProjectLocation() != null) {
      //noinspection ResultOfMethodCallIgnored
      Arrays.stream(Objects.requireNonNull(profilesLocation.listFiles())).forEach(File::delete);
      //noinspection ResultOfMethodCallIgnored
      Arrays.stream(Objects.requireNonNull(annotationsLocation.listFiles())).forEach(File::delete);

      this.saveToFormat(KnowtatorXMLUtil.class, controller.getProfileManager(), profilesLocation);
      this.saveToFormat(KnowtatorXMLUtil.class, annotationsLocation);
    }
  }

  public void addDocument(File file) {
    if (!file.getParentFile().equals(articlesLocation)) {
      try {
        FileUtils.copyFile(file, new File(articlesLocation, file.getName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    controller.getTextSourceManager().addTextSource(null, file.getName(), file.getName());
  }

  public File getAnnotationsLocation() {
    return annotationsLocation;
  }

  public void saveToFormat(Class<? extends BasicIOUtil> ioClass, File file) {
    saveToFormat(ioClass, controller.getTextSourceManager(), file);
  }

  public void saveToFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file) {
    try {
      try {
        controller.getOWLAPIDataExtractor().setRenderRDFSLabel();
      } catch (OWLWorkSpaceNotSetException ignored) {
      }
      BasicIOUtil util = ioClass.getDeclaredConstructor().newInstance();
      util.write(savable != null ? savable : controller.getTextSourceManager(), file);
    } catch (InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException
            | IOException e) {
      e.printStackTrace();
    }
  }

  public void loadFromFormat(Class<? extends BasicIOUtil> ioClass, File file) {
    loadFromFormat(ioClass, controller.getTextSourceManager(), file);
  }

  public void loadFromFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file) {
    try {
      BasicIOUtil util = ioClass.getDeclaredConstructor().newInstance();
      util.read(savable, file);
    } catch (InstantiationException
            | IllegalAccessException
            | InvocationTargetException
            | NoSuchMethodException
            | IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isProjectLoaded() {
    return projectLoaded;
  }
}
