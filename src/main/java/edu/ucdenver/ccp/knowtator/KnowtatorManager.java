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

package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.listeners.*;
import edu.ucdenver.ccp.knowtator.model.ConfigProperties;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.model.annotation.TextSourceManager;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.io.Savable;
import edu.ucdenver.ccp.knowtator.model.io.XmlUtil;
import edu.ucdenver.ccp.knowtator.model.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLWorkspace;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;


/**
 * @author Harrison Pielke-Lombardo
 * @version 2.0.6
 */

public class KnowtatorManager implements Savable {
    private static final Logger log = Logger.getLogger(KnowtatorManager.class);

    private XmlUtil xmlUtil;
    private ConfigProperties configProperties;
    private TextSourceManager textSourceManager;
    private ProfileManager profileManager;
    private OWLAPIDataExtractor owlDataExtractor;

    private Set<TextSourceListener> textSourceListeners;
    private Set<ProfileListener> profileListeners;
    private Set<AnnotationListener> annotationListeners;
    private Set<SpanListener> spanListeners;
    private Set<GraphListener> graphListeners;


    /**
     * Mediates between models and views
     */
    public KnowtatorManager() {
        super();
        initListeners();
        initManagers();
        loadConfig();

        log.warn("Knowtator manager initialized");
    }

    private void initManagers() {
        textSourceManager = new TextSourceManager(this);
        profileManager = new ProfileManager(this);  //manipulates profiles and colors
        xmlUtil = new XmlUtil(this);  //reads and writes to XML
        owlDataExtractor = new OWLAPIDataExtractor();
    }

    private void initListeners() {
        textSourceListeners = new HashSet<>();
        profileListeners = new HashSet<>();
        annotationListeners = new HashSet<>();
        spanListeners = new HashSet<>();
        graphListeners = new HashSet<>();
    }

    private void loadConfig() {
        configProperties = new ConfigProperties();
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public XmlUtil getXmlUtil() {
        return xmlUtil;
    }

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public TextSourceManager getTextSourceManager() {
        return textSourceManager;
    }


    //TODO: Autoload the AO or at least provide the functionality to convert to it if desired.

    public static void main(String[] args) { }


    @Override
    public void writeToXml(Document dom, Element parent) {
        textSourceManager.writeToXml(dom, parent);
        profileManager.writeToXml(dom, parent);
    }

    @Override
    public void readFromXml(Element parent) {
        profileManager.readFromXml(parent);
        textSourceManager.readFromXml(parent);
    }

    public void close() {
        initManagers();
        loadConfig();

        log.warn("Knowtator manager initialized");
    }

    public void newProject() {
        String projectName = JOptionPane.showInputDialog("Enter project name");
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
            File projectFile = new File(projectDirectory, projectName + ".xml");
            File articleDirectory = new File(projectDirectory, "Articles");
            File ontologiesDirectory = new File(projectDirectory, "Ontologies");

            makeFileStructure(projectDirectory, projectFile, articleDirectory, ontologiesDirectory);
        }
    }

    public void loadProject(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter fileFilter = new FileNameExtensionFilter("XML", "xml");
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            saveProject();
            closeProject(view, fileChooser.getSelectedFile());
            loadProject(fileChooser.getSelectedFile());
        }
    }

    public void closeProject(KnowtatorView view, File file) {
        if (view != null) view.close(file);
        else close();
    }

    public void loadProject(File projectFile) {
        File projectDirectory = projectFile.getParentFile();
        File articleDirectory = new File(projectDirectory, "Articles");
        File ontologiesDirectory = new File(projectDirectory, "Ontologies");

        makeFileStructure(projectDirectory, projectFile, articleDirectory, ontologiesDirectory);
        loadOntologies();
        xmlUtil.read(projectFile);
    }

    private void loadOntologies() {
        try {
            Files.newDirectoryStream(Paths.get(configProperties.getOntologiesLocation().toURI()),
                    path -> path.toString().endsWith(".owl"))
                    .forEach(file -> owlDataExtractor.loadOntologyFromLocation(file.toFile().toURI().toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void makeFileStructure(File projectDirectory, File projectFile, File articleDirectory, File ontologiesDirectory) {
        try {
            Files.createDirectories(projectDirectory.toPath());
            Files.createDirectories(articleDirectory.toPath());
            Files.createDirectories(ontologiesDirectory.toPath());
            configProperties.setProjectLocation(projectFile);
            configProperties.setArticlesLocation(articleDirectory);
            configProperties.setOntologiesLocation(ontologiesDirectory);
        } catch (IOException e) {
            System.err.println("Cannot create directories - " + e);
        }
    }

    public void saveProject() {
        if (configProperties.getProjectLocation() != null) {
            saveProject(configProperties.getProjectLocation());
        }
    }

    public void saveProject(File file) {
        xmlUtil.write(file);
    }

    public void addDocument() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(configProperties.getArticlesLocation());
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            if (!file.getParentFile().equals(configProperties.getArticlesLocation())) {
                try {
                    FileUtils.copyDirectory(file, new File(configProperties.getArticlesLocation(), file.getName()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            addDocument(file);
        }
    }

    public void addDocument(File file) {
        textSourceManager.addTextSource(file.getName());
    }

    public void textSourceAddedEvent(TextSource textSource) {
        textSourceListeners.forEach(textSourceListener -> textSourceListener.textSourceAdded(textSource));
    }

    public void profileAddedEvent(Profile profile) {
        profileListeners.forEach(profileListener -> profileListener.profileAdded(profile));
    }

    public void spanSelectionChangedEvent(Span span) {
        spanListeners.forEach(spanListener -> spanListener.spanSelectionChanged(span));
    }

    public void profileSelectionChangedEvent(Profile profile) {
        profileListeners.forEach(profileListener -> profileListener.profileSelectionChanged(profile));
    }

    public void annotationAddedEvent(Annotation newAnnotation) {
        annotationListeners.forEach(listener -> listener.annotationAdded(newAnnotation));
    }

    public void annotationSelectionChangedEvent(Annotation selectedAnnotation) {
        if (selectedAnnotation != null)
            annotationListeners.forEach(listener -> listener.annotationSelectionChanged(selectedAnnotation));
    }

    public void annotationRemovedEvent(Annotation removedAnnotation) {
        annotationListeners.forEach(listener -> listener.annotationRemoved(removedAnnotation));
    }

    public void profileRemovedEvent() {
        profileListeners.forEach(ProfileListener::profileRemoved);
    }


    public void profileFilterEvent(boolean filterByProfile) {
        profileListeners.forEach(profileListener -> profileListener.profileFilterSelectionChanged(filterByProfile));
    }

    public void spanAddedEvent(Span newSpan) {
        spanListeners.forEach(spanListener -> spanListener.spanAdded(newSpan));
    }


    public void spanRemovedEvent() {
        spanListeners.forEach(SpanListener::spanRemoved);
    }

    public void addConceptAnnotationListener(AnnotationListener listener) {
        annotationListeners.add(listener);
    }

    public void colorChangedEvent() {
        profileListeners.forEach(ProfileListener::colorChanged);
    }

    public void addSpanListener(SpanListener listener) {
        spanListeners.add(listener);
    }

    public void addProfileListener(ProfileListener listener) {
        profileListeners.add(listener);
    }

    public void addGraphListener(GraphListener listener) {
        graphListeners.add(listener);
    }

    public void newGraphEvent(GraphSpace graphSpace) {
        graphListeners.forEach(listener -> listener.newGraph(graphSpace));
    }

    public void removeGraphEvent(GraphSpace graphSpace) {
        graphListeners.forEach(listener -> listener.removeGraph(graphSpace));
    }

    public void addAnnotationListener(AnnotationListener listener) {
        annotationListeners.add(listener);
    }

    public void addTextSourceListener(TextSourceListener listener) {
        textSourceListeners.add(listener);
    }

    public OWLAPIDataExtractor getOWLAPIDataExtractor() {
        return owlDataExtractor;
    }


    public void setUpOWL(OWLWorkspace owlWorkspace, OWLModelManager owlModelManager) {
        owlDataExtractor.setUpOWL(owlWorkspace, owlModelManager);
    }
}
