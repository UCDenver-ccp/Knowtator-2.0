package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.Savable;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.AbstractKnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollection;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class TextSource extends AbstractKnowtatorObject<TextSource> implements BratStandoffIO, Savable, KnowtatorXMLIO, KnowtatorCollectionListener {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);

    private final KnowtatorController controller;
    private final File saveFile;
    private final ConceptAnnotationCollection conceptAnnotationCollection;
    private File textFile;
    private String content;
    private final GraphSpaceCollection graphSpaceCollection;

    public TextSource(KnowtatorController controller, File saveFile, String textFileName) {
        super(null);
        this.controller = controller;
        this.saveFile = saveFile == null ? new File(controller.getTextSourceCollection().getAnnotationsLocation().getAbsolutePath(), textFileName.replace(".txt", "") + ".xml") : saveFile;
        this.conceptAnnotationCollection = new ConceptAnnotationCollection(controller, this);
        this.graphSpaceCollection = new GraphSpaceCollection(controller, this);

        //noinspection unchecked
        conceptAnnotationCollection.addCollectionListener(this);
        //noinspection unchecked
        graphSpaceCollection.addCollectionListener(this);

        controller.verifyId(FilenameUtils.getBaseName(textFileName), this, true);

        textFile =
                new File(
                        controller.getTextSourceCollection().getArticlesLocation(),
                        textFileName.endsWith(".txt") ? textFileName : textFileName + ".txt");

        if (!textFile.exists()) {
            if (JOptionPane.showConfirmDialog(
                    null,
                    String.format("Could not find file for %s. Choose file location?", id),
                    "File not found",
                    JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Select text file for " + id);

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    if (JOptionPane.showConfirmDialog(
                            null,
                            String.format("Copy %s to project?", textFile.getName()),
                            "Copy selected file?",
                            JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                        try {
                            textFile =
                                    Files.copy(
                                            Paths.get(file.toURI()),
                                            Paths.get(
                                                    controller
                                                            .getTextSourceCollection().getArticlesLocation()
                                                            .toURI()
                                                            .resolve(file.getName())))
                                            .toFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        textFile = file;
                    }
                }
            }
        }
    }

    @Override
    public int compareTo(TextSource textSource2) {
        if (this == textSource2) {
            return 0;
        }
        if (textSource2 == null) {
            return 1;
        }
        return id.toLowerCase().compareTo(textSource2.getId().toLowerCase());
    }

    public File getTextFile() {
        return textFile;
    }

    public ConceptAnnotationCollection getConceptAnnotationCollection() {
        return conceptAnnotationCollection;
    }

    @Override
    public void dispose() {
        conceptAnnotationCollection.dispose();
        graphSpaceCollection.dispose();
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        conceptAnnotationCollection.readFromKnowtatorXML(null, parent);
        graphSpaceCollection.readFromKnowtatorXML(null, parent);
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        Element textSourceElement = dom.createElement(KnowtatorXMLTags.DOCUMENT);
        parent.appendChild(textSourceElement);
        textSourceElement.setAttribute(KnowtatorXMLAttributes.ID, id);
        textSourceElement.setAttribute(KnowtatorXMLAttributes.FILE, textFile.getName());
        conceptAnnotationCollection.writeToKnowtatorXML(dom, textSourceElement);
        graphSpaceCollection.writeToKnowtatorXML(dom, textSourceElement);
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {
        conceptAnnotationCollection.readFromOldKnowtatorXML(null, parent);
        graphSpaceCollection.readFromOldKnowtatorXML(null, parent);
    }

    @Override
    public void readFromBratStandoff(
            File file, Map<Character, List<String[]>> annotationMap, String content) {
        conceptAnnotationCollection.readFromBratStandoff(null, annotationMap, getContent());
        graphSpaceCollection.readFromBratStandoff(null, annotationMap, getContent());
    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) throws IOException {
        conceptAnnotationCollection.writeToBratStandoff(writer, annotationsConfig, visualConfig);
        graphSpaceCollection.writeToBratStandoff(writer, annotationsConfig, visualConfig);
    }

    public String getContent() {
        if (content == null) {
            while (true) {
                try {
                    content = FileUtils.readFileToString(textFile, "UTF-8");
                    return content;
                } catch (IOException e) {
                    textFile = new File(controller.getTextSourceCollection().getArticlesLocation(), id + ".txt");
                    while (!textFile.exists()) {
                        JFileChooser fileChooser = new JFileChooser();
                        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                            textFile = fileChooser.getSelectedFile();
                        }
                    }
                }
            }
        } else {
            return content;
        }
    }

    public GraphSpaceCollection getGraphSpaceCollection() {
        return graphSpaceCollection;
    }

    @Override
    public void save() {
        if (controller.isNotLoading()) {
            controller.saveToFormat(KnowtatorXMLUtil.class, this, saveFile);
        }
    }

    @Override
    public void load() {

    }

    @Override
    public File getSaveLocation() {
        return new File(controller.getTextSourceCollection().getAnnotationsLocation().getAbsolutePath(), saveFile.getName());
    }

    @Override
    public void setSaveLocation(File saveLocation) {

    }

    @Override
    public void finishLoad() {
        if (conceptAnnotationCollection.size() > 0) {
            conceptAnnotationCollection.setSelection(conceptAnnotationCollection.first());
        }
        if (graphSpaceCollection.size() > 0) {
            graphSpaceCollection.setSelection(graphSpaceCollection.first());
        }
    }

    @Override
    public void added(AddEvent addedObject) {
        save();
    }

    @Override
    public void removed(RemoveEvent removedObject) {
        save();
    }

    @Override
    public void changed() {
        save();
    }

    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

    }

    @Override
    public void updated(KnowtatorObjectInterface updatedItem) {
        save();
    }

    @Override
    public void selected(SelectionChangeEvent event) {

    }
}
