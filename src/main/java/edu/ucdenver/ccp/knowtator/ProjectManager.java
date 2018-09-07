package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.io.BasicIO;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class ProjectManager extends KnowtatorManager {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ProjectManager.class);

    private File projectLocation;
    boolean projectLoaded;
    private List<ProjectListener> projectListeners;

    ProjectManager() {
        projectLoaded = false;
        projectListeners = new ArrayList<>();
    }

    public void loadProject() {
        projectLoaded = false;

        projectListeners.forEach(ProjectListener::projectClosed);

        makeProjectStructure(projectLocation);

        getManagers().forEach(KnowtatorManager::load);

        projectLoaded = true;

        for (ProjectListener listener : projectListeners) {
            listener.projectLoaded();
        }
    }

    @Override
    public File getSaveLocation() {
        return projectLocation;
    }

    @Override
    public void setSaveLocation(File saveLocation) throws IOException {
        if (!saveLocation.isDirectory()) {
            saveLocation = new File(saveLocation.getParent());
        }
        this.projectLocation = saveLocation;
        Files.createDirectories(projectLocation.toPath());
    }


    public void addProjectListener(ProjectListener listener) {
        projectListeners.add(listener);
    }

    public void newProject(File projectDirectory) {
        makeProjectStructure(projectDirectory);
        loadProject();
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
                                                    manager.getSaveLocation(), fileName.getFileName().toFile().getName())
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

    void makeProjectStructure(File projectDirectory) {
        try {
            setSaveLocation(projectDirectory);

            for (KnowtatorManager knowtatorManager : getManagers()) {
                knowtatorManager.makeDirectory();
            }


            if (FileUtils.listFiles(projectDirectory, new String[]{"knowtator"}, false).size() == 0)
                Files.createFile(
                        new File(projectDirectory, projectDirectory.getName() + ".knowtator").toPath());
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
        }
    }

    public void saveProject() {

        getManagers().forEach(KnowtatorManager::save);
    }

    abstract List<KnowtatorManager> getManagers();

    public void loadWithAppropriateFormat(BasicIO basicIO, File file) {
        String[] splitOnDots = file.getName().split("\\.");
        String extension = splitOnDots[splitOnDots.length - 1];

        switch (extension) {
            case "xml":
                loadFromFormat(KnowtatorXMLUtil.class, (KnowtatorXMLIO) basicIO, file);
                break;
            case "ann":
                loadFromFormat(BratStandoffUtil.class, (BratStandoffIO) basicIO, file);
                break;
            case "a1":
                loadFromFormat(BratStandoffUtil.class, (BratStandoffIO) basicIO, file);
                break;
        }
    }

    public <I extends BasicIO> void saveToFormat(Class<? extends BasicIOUtil<I>> ioClass, I basicIO, File file) {
        try {

            BasicIOUtil<I> util = ioClass.getDeclaredConstructor().newInstance();
            util.write(basicIO, file);
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException
                | IOException e) {
            e.printStackTrace();
        }
    }

    private <I extends BasicIO> void loadFromFormat(Class<? extends BasicIOUtil<I>> ioClass, I basicIO, File file) {
        try {
            BasicIOUtil<I> util = ioClass.getDeclaredConstructor().newInstance();
            util.read(basicIO, file);
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException
                | IOException e) {
            e.printStackTrace();
        }
    }
}
