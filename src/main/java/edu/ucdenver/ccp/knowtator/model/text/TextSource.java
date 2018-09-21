package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.Savable;
import edu.ucdenver.ccp.knowtator.model.text.annotation.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceManager;
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

public class TextSource implements KnowtatorObject, BratStandoffIO, Savable, KnowtatorXMLIO {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);

    private final KnowtatorController controller;
    private final File saveFile;
    private AnnotationManager annotationManager;
    private String id;
    private File textFile;
    private String content;
    private GraphSpaceManager graphSpaceManager;

    public TextSource(KnowtatorController controller, File saveFile, String textFileName) {
        this.controller = controller;
        this.saveFile = saveFile == null ? new File(controller.getTextSourceManager().getAnnotationsLocation().getAbsolutePath(), textFileName.replace(".txt", "") + ".xml") : saveFile;
        this.annotationManager = new AnnotationManager(controller, this);
        this.graphSpaceManager = new GraphSpaceManager(controller, this);

        controller.verifyId(FilenameUtils.getBaseName(textFileName), this, true);

        textFile =
                new File(
                        controller.getTextSourceManager().getArticlesLocation(),
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
                                                            .getTextSourceManager().getArticlesLocation()
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

    public static int compare(TextSource textSource1, TextSource textSource2) {
        if (textSource1 == textSource2) {
            return 0;
        }
        if (textSource2 == null) {
            return 1;
        }
        if (textSource1 == null) {
            return -1;
        }
        return textSource1.getId().toLowerCase().compareTo(textSource2.getId().toLowerCase());
    }

    public File getTextFile() {
        return textFile;
    }

    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void dispose() {
        annotationManager.dispose();
        graphSpaceManager.dispose();
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        annotationManager.readFromKnowtatorXML(null, parent);
        graphSpaceManager.readFromKnowtatorXML(null, parent);
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        Element textSourceElement = dom.createElement(KnowtatorXMLTags.DOCUMENT);
        parent.appendChild(textSourceElement);
        textSourceElement.setAttribute(KnowtatorXMLAttributes.ID, id);
        textSourceElement.setAttribute(KnowtatorXMLAttributes.FILE, textFile.getName());
        annotationManager.writeToKnowtatorXML(dom, textSourceElement);
        graphSpaceManager.writeToKnowtatorXML(dom, textSourceElement);
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {
        annotationManager.readFromOldKnowtatorXML(null, parent);
        graphSpaceManager.readFromOldKnowtatorXML(null, parent);
    }

    @Override
    public void readFromBratStandoff(
            File file, Map<Character, List<String[]>> annotationMap, String content) {
        annotationManager.readFromBratStandoff(null, annotationMap, getContent());
        graphSpaceManager.readFromBratStandoff(null, annotationMap, getContent());
    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) throws IOException {
        annotationManager.writeToBratStandoff(writer, annotationsConfig, visualConfig);
        graphSpaceManager.writeToBratStandoff(writer, annotationsConfig, visualConfig);
    }

    public String getContent() {
        if (content == null) {
            while (true) {
                try {
                    content = FileUtils.readFileToString(textFile, "UTF-8");
                    return content;
                } catch (IOException e) {
                    textFile = new File(controller.getTextSourceManager().getArticlesLocation(), id + ".txt");
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

    public GraphSpaceManager getGraphSpaceManager() {
        return graphSpaceManager;
    }

    @Override
    public void save() {
        controller.saveToFormat(KnowtatorXMLUtil.class, this, saveFile);
    }

    @Override
    public void load() {

    }

    @Override
    public File getSaveLocation() {
        return new File(controller.getTextSourceManager().getAnnotationsLocation().getAbsolutePath(), saveFile.getName());
    }

    @Override
    public void setSaveLocation(File saveLocation) {

    }


}
