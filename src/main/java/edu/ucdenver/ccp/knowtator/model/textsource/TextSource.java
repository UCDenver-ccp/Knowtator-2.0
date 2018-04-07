package edu.ucdenver.ccp.knowtator.model.textsource;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.annotation.AnnotationManager;
import org.apache.commons.io.FileUtils;
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

public class TextSource implements Savable {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(TextSource.class);
    private final KnowtatorController controller;

    public File getSaveFile() {
        return saveFile;
    }

    private final File saveFile;


    private AnnotationManager annotationManager;
    private String docID;
    private File textFile;
    //    private String content;

    public TextSource(KnowtatorController controller, File saveFile, String docID) {
        this.controller = controller;
        this.saveFile = saveFile;
        this.annotationManager = new AnnotationManager(controller, this);

        if (docID != null) {
            this.docID = docID;
            textFile = new File(controller.getProjectManager().getArticlesLocation(), docID + ".txt");

            if (!textFile.exists()) {
                if (JOptionPane.showConfirmDialog(null, String.format("Could not find file for %s. Choose file location?", docID), "File not found", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setDialogTitle("Select text file for " + docID);

                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        File file = fileChooser.getSelectedFile();
                        if (JOptionPane.showConfirmDialog(null, String.format("Copy %s to project?", textFile.getName()), "Copy selected file?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                            try {
                                textFile = Files.copy(Paths.get(file.toURI()), Paths.get(controller.getProjectManager().getArticlesLocation().toURI().resolve(file.getName()))).toFile();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            textFile = file;
                        }
                    }

                }
            }

//            try {
//                content = FileUtils.readFileToString(textFile, "UTF-8");
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
        }
//        else {
//            this.docID = "Instructions";
//            content = "***Instructions:***" +
//                    "\n" +
//                    "Create a new project: Project -> New Project" +
//                    "\n" +
//                    "Load an existing project: Project -> Load Project";
//        }

    }

    public String getDocID() {
        return docID;
    }

    public File getTextFile() {
        return textFile;
    }

    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }
    @Override
    public String toString() {
        return String.format("TextSource: docID: %s", docID);
    }
    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        Element textSourceElement = dom.createElement(KnowtatorXMLTags.DOCUMENT);
        parent.appendChild(textSourceElement);
        textSourceElement.setAttribute(KnowtatorXMLAttributes.ID, docID);
        annotationManager.writeToKnowtatorXML(dom, textSourceElement);
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent, String content) {
        annotationManager.readFromKnowtatorXML(null, parent, getContent());
    }

    public String getContent() {
        while (true) {
            try {
                return FileUtils.readFileToString(textFile, "UTF-8");
            } catch (IOException e) {
                textFile = new File(controller.getProjectManager().getArticlesLocation(), docID + ".txt");
                while (!textFile.exists()) {
                    JFileChooser fileChooser = new JFileChooser();
                    if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                        textFile = fileChooser.getSelectedFile();
                    }
                }
            }
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent, String content) {
        annotationManager.readFromOldKnowtatorXML(null, parent, getContent());
    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {
        annotationManager.readFromBratStandoff(null, annotationMap, getContent());

    }

    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {
        annotationManager.writeToBratStandoff(writer);
    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }
}
