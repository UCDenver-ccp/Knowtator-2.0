package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
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

public class TextSource implements Savable, KnowtatorObject {
  @SuppressWarnings("unused")
  private static Logger log = LogManager.getLogger(TextSource.class);

  private final KnowtatorController controller;
  private final File saveFile;
  private AnnotationManager annotationManager;
  private String id;
  private File textFile;
  private String content;

  public TextSource(KnowtatorController controller, File saveFile, String textFileName) {
    this.controller = controller;
    this.saveFile = saveFile == null ? new File(controller.getProjectManager().getAnnotationsLocation().getAbsolutePath(), textFileName.replace(".txt", "") + ".xml") : saveFile;
    this.annotationManager = new AnnotationManager(controller, this);

    controller.verifyId(FilenameUtils.getBaseName(textFileName), this, true);

    textFile =
        new File(
            controller.getProjectManager().getArticlesLocation(),
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
                                  .getProjectManager()
                                  .getArticlesLocation()
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

  public File getSaveFile() {
    return saveFile;
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
  public String toString() {
    return id;
  }

  @Override
  public void readFromKnowtatorXML(File file, Element parent) {
    annotationManager.readFromKnowtatorXML(null, parent);
  }

  @Override
  public void writeToKnowtatorXML(Document dom, Element parent) {
    Element textSourceElement = dom.createElement(KnowtatorXMLTags.DOCUMENT);
    parent.appendChild(textSourceElement);
    textSourceElement.setAttribute(KnowtatorXMLAttributes.ID, id);
    textSourceElement.setAttribute(KnowtatorXMLAttributes.FILE, textFile.getName());
    annotationManager.writeToKnowtatorXML(dom, textSourceElement);
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {
    annotationManager.readFromOldKnowtatorXML(null, parent);
  }

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {
    annotationManager.readFromBratStandoff(null, annotationMap, getContent());
  }

  @Override
  public void writeToBratStandoff(Writer writer) throws IOException {
    annotationManager.writeToBratStandoff(writer);
  }

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  public String getContent() {
    if (content == null) {
      while (true) {
        try {
          content = FileUtils.readFileToString(textFile, "UTF-8");
          return content;
        } catch (IOException e) {
          textFile = new File(controller.getProjectManager().getArticlesLocation(), id + ".txt");
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

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}
}
