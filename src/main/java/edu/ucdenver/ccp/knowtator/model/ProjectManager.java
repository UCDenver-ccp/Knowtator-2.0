package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
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
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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

  public ProjectManager() {
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
    }
  }

  public void importProject(
          File profilesLocation,
          File ontologiesLocation,
          File articlesLocation,
          File annotationsLocation,
          File projectLocation) {
    makeFileStructure(projectLocation);
    try {
      if (profilesLocation != null && profilesLocation.exists()) {
        Files.newDirectoryStream(
                Paths.get(profilesLocation.toURI()), path -> path.toString().endsWith(".xml"))
            .forEach(
                profileFile -> {
                  try {
                    Files.copy(
                        profileFile,
                        new File(
                                this.profilesLocation, profileFile.getFileName().toFile().getName())
                            .toPath());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                });
      }
      if (ontologiesLocation != null && ontologiesLocation.exists()) {
        Files.newDirectoryStream(
                Paths.get(ontologiesLocation.toURI()), path -> path.toString().endsWith(".obo"))
            .forEach(
                ontologyFile -> {
                  try {
                    Files.copy(
                        ontologyFile,
                        new File(
                                this.ontologiesLocation,
                                ontologyFile.getFileName().toFile().getName())
                            .toPath());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                });
      }
      if (articlesLocation != null && articlesLocation.exists()) {
        Files.newDirectoryStream(
                Paths.get(articlesLocation.toURI()), path -> path.toString().endsWith(".txt"))
            .forEach(
                articleFile -> {
                  try {
                    Files.copy(
                        articleFile,
                        new File(
                                this.articlesLocation, articleFile.getFileName().toFile().getName())
                            .toPath());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                });
      }
      if (annotationsLocation != null && annotationsLocation.exists()) {
        Files.newDirectoryStream(
                Paths.get(annotationsLocation.toURI()), path -> path.toString().endsWith(".xml"))
            .forEach(
                profileFile -> {
                  try {
                    Files.copy(
                        profileFile,
                        new File(
                                this.annotationsLocation,
                                profileFile.getFileName().toFile().getName())
                            .toPath());
                  } catch (IOException e) {
                    e.printStackTrace();
                  }
                });
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
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

      JOptionPane.showMessageDialog(null, "Please select a document to annotate");
      if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
        controller.getProjectManager().addDocument(fileChooser.getSelectedFile());
      }
    }

    projectLoaded = true;
    controller.projectLoaded();

    for (ProjectListener listener : listeners) {
      listener.projectLoaded();
    }
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

  public void loadWithAppropriateFormat(File file) {
    String[] splitOnDots = file.getName().split("\\.");
    String extension = splitOnDots[splitOnDots.length - 1];

    switch (extension) {
      case "xml":
        loadFromFormat(KnowtatorXMLUtil.class, file);
        break;
      case "ann":
        loadFromFormat(BratStandoffUtil.class, file);
        break;
      case "a1":
        loadFromFormat(BratStandoffUtil.class, file);
        break;
    }
  }

  public void loadFromFormat(Class<? extends BasicIOUtil> ioClass, File file) {
    loadFromFormat(ioClass, controller.getTextSourceManager(), file);
  }

  private void loadFromFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file) {
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

  public void dispose() {
    listeners.clear();
  }

  public void setController(KnowtatorController controller) {
    this.controller = controller;
  }

  File getProfilesLocation() {
    return profilesLocation;
  }
}
