package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.SavableKnowtatorManager;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TextSourceManager extends TextSourceCollection implements ProjectListener, BratStandoffIO, KnowtatorXMLIO, SavableKnowtatorManager {
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(TextSourceManager.class);

    private KnowtatorController controller;
    private File articlesLocation;
    private File annotationsLocation;


    public TextSourceManager(KnowtatorController controller) {
        super(controller);
        this.controller = controller;
        controller.addProjectListener(this);

    }

    private TextSource addTextSource(File file, String id, String textFileName) {
        if (textFileName == null || textFileName.equals("")) {
            textFileName = id;
        }
        TextSource newTextSource = get(textFileName);
        if (newTextSource == null) {
            newTextSource = new TextSource(controller, file, textFileName);
            add(newTextSource);
        }

        setSelection(newTextSource);
        return newTextSource;
    }

    public void getNextTextSource() {
        setSelection(getNext(getSelection()));
    }

    public void getPreviousTextSource() {
        setSelection(getPrevious(getSelection()));
    }


    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        getCollection()
                .forEach(textSource -> textSource.writeToKnowtatorXML(dom, parent));
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        for (Node documentNode :
                KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.DOCUMENT))) {
            Element documentElement = (Element) documentNode;
            String documentID = documentElement.getAttribute(KnowtatorXMLAttributes.ID);
            String documentFile = documentElement.getAttribute(KnowtatorXMLAttributes.FILE);
            TextSource newTextSource = addTextSource(file, documentID, documentFile);
            newTextSource.readFromKnowtatorXML(null, documentElement);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {

        String docID = parent.getAttribute(OldKnowtatorXMLAttributes.TEXT_SOURCE).replace(".txt", "");
        TextSource newTextSource = addTextSource(file, docID, null);
        newTextSource.readFromOldKnowtatorXML(null, parent);
    }

    @Override
    public void readFromBratStandoff(
            File file, Map<Character, List<String[]>> annotationMap, String content) {
        String docID = annotationMap.get(StandoffTags.DOCID).get(0)[0];

        TextSource newTextSource = addTextSource(file, docID, null);
        newTextSource.readFromBratStandoff(null, annotationMap, null);
    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) {
    }


    @Override
    public void makeDirectory() throws IOException {
        articlesLocation = new File(controller.getSaveLocation(), "Articles");
        Files.createDirectories(articlesLocation.toPath());

        setSaveLocation(new File(controller.getSaveLocation(), "Annotations"));
    }

    public void addDocument(File file) {
        if (!file.getParentFile().equals(getArticlesLocation())) {
            try {
                FileUtils.copyFile(file, new File(getAnnotationsLocation(), file.getName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        addTextSource(null, file.getName(), file.getName());
    }

    @Override
    public void load() {
        try {
            log.warn("Loading annotations");
            KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
            Files.newDirectoryStream(Paths.get(annotationsLocation.toURI()), path -> path.toString().endsWith(".xml"))
                    .forEach(inputFile -> xmlUtil.read(this, inputFile.toFile()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (getCollection().isEmpty()) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(getArticlesLocation());

            JOptionPane.showMessageDialog(null, "Please select a document to annotate");
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                addDocument(fileChooser.getSelectedFile());
            }
        }
    }

    @Override
    public void save() {
        try {
            controller.getOWLManager().setRenderRDFSLabel();
        } catch (OWLWorkSpaceNotSetException ignored) {
        }
        getCollection()
                .forEach(
                        TextSource::save);
    }

    @Override
    public void dispose() {
        forEach(TextSource::dispose);
        getCollection().clear();
    }

    public File getAnnotationsLocation() {
        return annotationsLocation;
    }

    public File getArticlesLocation() {
        return articlesLocation;
    }

    //TODO: Check where articles location would be appropriate and find out how to handle that
    @Override
    public void setSaveLocation(File newSaveLocation) throws IOException {
        this.annotationsLocation = newSaveLocation;
        Files.createDirectories(newSaveLocation.toPath());
    }

    @Override
    public File getSaveLocation() {
        return annotationsLocation;

    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void projectLoaded() {
        setSelection(getCollection().first());
    }
}
