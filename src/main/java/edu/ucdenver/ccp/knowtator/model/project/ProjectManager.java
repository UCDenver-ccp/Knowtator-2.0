/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.project;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

public class ProjectManager {
    private static final Logger log = Logger.getLogger(ProjectManager.class);
    private KnowtatorManager manager;
    private File projectLocation;
    private File articlesLocation;
    private File ontologiesLocation;
    private File annotationsLocation;
    private File profilesLocation;

    public ProjectManager(KnowtatorManager manager) {
        this.manager = manager;
    }

    public File getProjectLocation() {
        return projectLocation;
    }

    public File getArticlesLocation() {
        return articlesLocation;
    }

    public void newProject(File projectDirectory) {
        makeFileStructure(projectDirectory);
    }

    public void closeProject(KnowtatorView view, File file) {
        if (view != null){
            log.warn("2.a: " + file);
            view.close(file);
        }
        else {
            log.warn("2.b: " + file);
            manager.close(file);
        }
    }

    public void loadProject(File projectFile) {
        if (projectFile != null) {
            log.warn("4: " + projectFile);
            File projectDirectory = projectFile.getParentFile();

            makeFileStructure(projectDirectory);

            try {
                log.warn("Loading ontologies");
                Files.newDirectoryStream(Paths.get(ontologiesLocation.toURI()),
                        path -> path.toString().endsWith(".owl"))
                        .forEach(file -> manager.getOWLAPIDataExtractor().loadOntologyFromLocation(file.toFile().toURI().toString()));

                log.warn("Loading profiles");
                Files.newDirectoryStream(Paths.get(profilesLocation.toURI()),
                        path -> path.toString().endsWith(".xml"))
                        .forEach(file -> XmlUtil.readXML(manager.getProfileManager(), file.toFile()));

//                Load annotations in parallel
//                log.warn("Loading annotations");
//                Stream<Path> annotationFilesToRead = Files.walk(Paths.get(annotationsLocation.toURI()))
//                        .filter(path -> path.toString().endsWith(".xml"));
//                annotationFilesToRead.parallel().forEach(path -> XmlUtil.readXML(manager.getTextSourceManager(), path.toFile()));

                log.warn("Loading annotations");
                Files.newDirectoryStream(Paths.get(annotationsLocation.toURI()),
                        path -> path.toString().endsWith(".xml"))
                        .forEach(file -> XmlUtil.readXML(manager.getTextSourceManager(), file.toFile()));

                manager.projectLoadedEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
                Files.createFile(new File(projectDirectory, projectDirectory.getName() + ".knowtator").toPath());
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
        }
    }

    public void saveProject() {
        if (getProjectLocation() != null) {
            manager.getProfileManager().getProfiles().values().forEach(profile -> {
                File profileFile = new File(profilesLocation, profile.getId() + ".xml");
                XmlUtil.createXML(profile, profileFile);
            });

            for (File file : Objects.requireNonNull(annotationsLocation.listFiles())) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }

            manager.getTextSourceManager().getTextSources().values().forEach(textSource -> {
                File textSourceFile = new File(annotationsLocation, textSource.getDocID() + ".xml");

                XmlUtil.createXML(textSource, textSourceFile);

            });
        }
    }

    public void importAnnotations(File file) {
        XmlUtil.readXML(manager.getTextSourceManager(), file);
    }

    public void addDocument(File file) {
        if (!file.getParentFile().equals(articlesLocation)) {
            try {
                FileUtils.copyFile(file, new File(articlesLocation, file.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        TextSource newTextSource = manager.getTextSourceManager().addTextSource(file.getName());
        manager.textSourceAddedEvent(newTextSource);
    }

    public File getAnnotationsLocation() {
        return annotationsLocation;
    }
}
