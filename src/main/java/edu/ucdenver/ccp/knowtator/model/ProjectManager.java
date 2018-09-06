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

public class ProjectManager implements KnowtatorManager {
  private static final Logger log = Logger.getLogger(ProjectManager.class);
  private KnowtatorController controller;
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
                                controller.getProfileManager().getProfilesLocation(), profileFile.getFileName().toFile().getName())
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
                                controller.getOWLManager().getOntologiesLocation(),
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
                                controller.getTextSourceManager().getArticlesLocation(), articleFile.getFileName().toFile().getName())
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
                                controller.getTextSourceManager().getAnnotationsLocation(),
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

    if (controller.getOWLManager().getOntologiesLocation() != null) {
      log.warn("Loading ontologies");
      try {
        controller.getOWLManager().read(controller.getOWLManager().getOntologiesLocation());
      } catch (IOException | OWLWorkSpaceNotSetException e) {
        log.warn("Could not load ontologies");
      }
    }

    if (controller.getProfileManager().getProfilesLocation() != null) {
      log.warn("Loading profiles");
      loadFromFormat(KnowtatorXMLUtil.class, controller.getProfileManager(), controller.getProfileManager().getProfilesLocation());
    }

    if (controller.getTextSourceManager().getAnnotationsLocation() != null) {
      log.warn("Loading annotations");
      loadFromFormat(
              KnowtatorXMLUtil.class, controller.getTextSourceManager(), controller.getTextSourceManager().getAnnotationsLocation());
    }

    if (controller.getTextSourceManager().getTextSourceCollection().getCollection().isEmpty()) {
      JFileChooser fileChooser = new JFileChooser();
      fileChooser.setCurrentDirectory(controller.getTextSourceManager().getArticlesLocation());

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
      controller.setProjectLocation(projectDirectory);
      controller.getTextSourceManager().setArticlesLocation(new File(projectDirectory, "Articles"));
      controller.getOWLManager().setOntologiesLocation(new File(projectDirectory, "Ontologies"));
      controller.getTextSourceManager().setAnnotationsLocation(new File(projectDirectory, "Annotations"));
      controller.getProfileManager().setProfilesLocation(new File(projectDirectory, "Profiles"));


      if (FileUtils.listFiles(projectDirectory, new String[]{"knowtator"}, false).size() == 0)
        Files.createFile(
                new File(projectDirectory, projectDirectory.getName() + ".knowtator").toPath());
    } catch (IOException e) {
      System.err.println("Cannot create directories - " + e);
    }
  }

  public void saveProject() {

    if (controller.getProjectLocation() != null) {
      controller.getProfileManager().save();
      controller.getTextSourceManager().save();
    }
  }

  public void addDocument(File file) {
    if (!file.getParentFile().equals(controller.getTextSourceManager().getArticlesLocation())) {
      try {
        FileUtils.copyFile(file, new File(controller.getTextSourceManager().getAnnotationsLocation(), file.getName()));
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    controller.getTextSourceManager().addTextSource(null, file.getName(), file.getName());
  }

  public void saveToFormat(Class<? extends BasicIOUtil> ioClass, File file) {
    saveToFormat(ioClass, controller.getTextSourceManager(), file);
  }

  public void saveToFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file) {
    try {
      try {
        controller.getOWLManager().setRenderRDFSLabel();
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
        loadFromFormat(KnowtatorXMLUtil.class, controller.getTextSourceManager(), file);
        break;
      case "ann":
        loadFromFormat(BratStandoffUtil.class, controller.getTextSourceManager(), file);
        break;
      case "a1":
        loadFromFormat(BratStandoffUtil.class, controller.getTextSourceManager(), file);
        break;
    }
  }

  private static void loadFromFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file) {
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

  @Override
  public void dispose() {
    listeners.clear();
  }

  public void setController(KnowtatorController controller) {
    this.controller = controller;
  }
}
