package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.Savable;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TextSourceCollection extends KnowtatorCollection<TextSource> implements CaretListener, BratStandoffIO, KnowtatorXMLIO, Savable {
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(TextSourceCollection.class);

    private KnowtatorController controller;
    private File articlesLocation;
    private File annotationsLocation;

    private boolean filterByProfile;

    private int start;
    private int end;
    private boolean filterByOWLClass;


    public TextSourceCollection(KnowtatorController controller) {
        super(controller);
        this.controller = controller;

        filterByProfile = false;
        filterByOWLClass = false;


        start = 0;
        end = 0;

    }

    public int getStart() {
        return start;
    }

    public int getEnd() {
        return end;
    }

    public void setStart(int start) {
        this.start = start;
    }
    public void setEnd(int end) {
        this.end = end;
    }

    public boolean isFilterByOWLClass() {
        return filterByOWLClass;
    }
    public boolean isFilterByProfile() {
        return filterByProfile;
    }
    public void setFilterByProfile(boolean filterByProfile) {
        this.filterByProfile = filterByProfile;
        collectionListeners.forEach(l -> l.updated(getSelection()));
    }
    public void setFilterByOWLClass(boolean filterByOWLClass) {
        this.filterByOWLClass = filterByOWLClass;
        collectionListeners.forEach(l -> l.updated(getSelection()));
    }

    @Override
    public void caretUpdate(CaretEvent e) {
        setStart(Math.min(e.getDot(), e.getMark()));
        setEnd(Math.max(e.getDot(), e.getMark()));
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

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        forEach(textSource -> textSource.writeToKnowtatorXML(dom, parent));
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

        if (size() == 0) {
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
        forEach(TextSource::save);
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
        articlesLocation = new File(newSaveLocation, "Articles");
        Files.createDirectories(articlesLocation.toPath());
        annotationsLocation = new File(newSaveLocation, "Annotations");
        Files.createDirectories(annotationsLocation.toPath());
    }

    @Override
    public File getSaveLocation() {
        return annotationsLocation;

    }

    public void removeActiveTextSource() {
        TextSource oldTextSource = getSelection();
        selectPrevious();
        oldTextSource.dispose();
        remove(oldTextSource);
    }
}
