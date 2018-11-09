/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.io.BasicIO;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.BaseKnowtatorManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;


/**
 * This class defines the methods for loading and saving the project
 */
public abstract class ProjectManager extends DebugManager implements Savable {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ProjectManager.class);

    private File projectLocation;

    public boolean isNotLoading() {
        return !isLoading;
    }

    private boolean isLoading;

    /**
     * Constructor for project manager
     */
    ProjectManager() {
        super();
        isLoading = false;
    }

    /**
     * Loads the project from the location defined by project location
     */
    public void loadProject() {
        if (projectLocation.exists()) {
            makeProjectStructure();

            isLoading = true;
            getManagers().forEach(BaseKnowtatorManager::load);
            isLoading = false;
            getManagers().forEach(BaseKnowtatorManager::finishLoad);
        }
    }

    /**
     * @return Project location
     */
    @Override
    public File getSaveLocation() {
        return projectLocation;
    }

    /**
     * Sets the project location. If it does not exist, it is created. If the save location is a file and not a
     * directory, the parent of the file is set as the save location
     * @param saveLocation Set the project location
     * @throws IOException Thrown if project location could not be made
     */
    @Override
    public void setSaveLocation(File saveLocation) throws IOException {
        if (saveLocation.isFile() && saveLocation.getName().endsWith(".knowtator")) {
            saveLocation = new File(saveLocation.getParent());
        }
        if (saveLocation.exists() && saveLocation.isDirectory() && Files.list(saveLocation.toPath()).anyMatch(path -> path.toString().endsWith(".knowtator"))) {
            this.projectLocation = saveLocation;
            Files.createDirectories(projectLocation.toPath());
        }
    }

    /**
     * Creates and loads a new project
     * @param projectDirectory Directory of the project
     */
    public void newProject(File projectDirectory) {
        try {
            setSaveLocation(projectDirectory);
            makeProjectStructure();
            loadProject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Used to load the managers of a project with directories that are not all directly under the project directory
     * @param directory The directory containing files to be loaded
     * @param manager The manager to be loaded to
     * @param extension The extension of the files to be loaded. For example, the profile manager should load profiles from files ending with .xml
     * @throws IOException Thrown if the directory does not exist
     */
    void importToManager(File directory, BaseKnowtatorManager manager, String extension) throws IOException {
        if (directory != null && directory.exists()) {
            Files.newDirectoryStream(
                    Paths.get(directory.toURI()), path -> path.toString().endsWith(extension))
                    .forEach(
                            fileName -> {
                                try {
                                    Files.copy(fileName,
                                            new File(manager.getSaveLocation(), fileName.getFileName().toFile().getName()).toPath());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            });
        }
    }

    /**
     * Makes the default project structure based on the managers in the implementation.
     */
    void makeProjectStructure() {
        try {
            for (BaseKnowtatorManager knowtatorManager : getManagers()) {
                knowtatorManager.setSaveLocation(projectLocation);
            }


            if (FileUtils.listFiles(projectLocation, new String[]{"knowtator"}, false).size() == 0)
                Files.createFile(
                        new File(projectLocation, projectLocation.getName() + ".knowtator").toPath());
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
        }
    }

    /**
     * @return A list of the implementations managers
     */
    abstract List<BaseKnowtatorManager> getManagers();

    /**
     * Takes a class capable of IO and a file, and loads it with the appropriate IOUtil for that extension
     * @param basicIO A class capable of IO
     * @param file The file to load
     */
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

    /**
     * Uses an IOUtil to save an IO class to the specified file. The IOUtil specifies the output format.
     * @param ioUtilClass The IOUtil to use to save the IO class. This specifies the output format
     * @param basicIO The IO class to save
     * @param file The file to save to
     * @param <I> The IO class
     */
    <I extends BasicIO> void saveToFormat(Class<? extends BasicIOUtil<I>> ioUtilClass, I basicIO, File file) {
        try {

            BasicIOUtil<I> util = ioUtilClass.getDeclaredConstructor().newInstance();
            util.write(basicIO, file);
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads data into the IO class using the IOUtil. The IOUtil specifies the input format.
     * @param ioClass The IOUtil to use to load the IO class. This specifies the input format
     * @param basicIO The IO class to load
     * @param file The file to load from
     * @param <I> the IO class
     */
    private <I extends BasicIO> void loadFromFormat(Class<? extends BasicIOUtil<I>> ioClass, I basicIO, File file) {
        try {
            BasicIOUtil<I> util = ioClass.getDeclaredConstructor().newInstance();
            util.read(basicIO, file);
        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return The project location
     */
    public File getProjectLocation() {
        return projectLocation;
    }
}
