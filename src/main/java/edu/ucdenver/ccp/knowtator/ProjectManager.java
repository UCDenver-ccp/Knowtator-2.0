package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class ProjectManager implements KnowtatorManager {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(ProjectManager.class);

  private File projectLocation;
  boolean projectLoaded;
  private List<ProjectListener> projectListeners;

  ProjectManager() {
    projectLoaded = false;
    projectListeners = new ArrayList<>();
  }

  @Override
  public File getSaveLocation(String extension) {
    return projectLocation;
  }

  @Override
  public void setSaveLocation(File newSaveLocation, String extension) throws IOException {
    this.projectLocation = newSaveLocation;
    Files.createDirectories(projectLocation.toPath());
  }



  public void addProjectListener(ProjectListener listener) {
    projectListeners.add(listener);
  }

  public void newProject(File projectDirectory) {
    makeProjectStructure(projectDirectory);
    loadProject();
  }

  public void loadProject(File projectFile) {
    if (projectFile != null) {
      makeProjectStructure(projectFile.getParentFile());
      loadProject();
    }
  }

  void importToManager(File directory, KnowtatorManager manager, String extension) throws IOException {
    if (directory != null && directory.exists()) {
      Files.newDirectoryStream(
              Paths.get(directory.toURI()), path -> path.toString().endsWith(extension))
              .forEach(
                      fileName -> {
                        try {
                          Files.copy(
                                  fileName,
                                  new File(
                                          manager.getSaveLocation(extension), fileName.getFileName().toFile().getName())
                                          .toPath());
                        } catch (IOException e) {
                          e.printStackTrace();
                        }
                      });
    }
  }

  abstract void importProject(
          File profilesLocation,
          File ontologiesLocation,
          File articlesLocation,
          File annotationsLocation,
          File projectLocation);

  abstract void loadProject();

  List<ProjectListener> getProjectListeners() {
    return projectListeners;
  }

  abstract void makeProjectStructure(File projectDirectory);

  public void saveProject() {

    getManagers().forEach(Savable::save);
  }

  abstract List<KnowtatorManager> getManagers();

  abstract void addDocument(File file);

  abstract void saveToFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file);

  public void loadWithAppropriateFormat(Savable savable, File file) {
    String[] splitOnDots = file.getName().split("\\.");
    String extension = splitOnDots[splitOnDots.length - 1];

    switch (extension) {
      case "xml":
        loadFromFormat(KnowtatorXMLUtil.class, savable, file);
        break;
      case "ann":
        loadFromFormat(BratStandoffUtil.class, savable, file);
        break;
      case "a1":
        loadFromFormat(BratStandoffUtil.class, savable, file);
        break;
    }
  }

  static void loadFromFormat(Class<? extends BasicIOUtil> ioClass, Savable savable, File file) {
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
}
