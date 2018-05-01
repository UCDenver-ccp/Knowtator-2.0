package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.OldKnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class TextSourceManager implements Savable {
  @SuppressWarnings("unused")
  private Logger log = Logger.getLogger(TextSourceManager.class);

  private TextSourceCollection textSourceCollection;
  private KnowtatorController controller;

  public TextSourceManager(KnowtatorController controller) {
    this.controller = controller;
    textSourceCollection = new TextSourceCollection(controller);
  }

  TextSource addTextSource(File file, String id, String textFileName) {
    if (textFileName == null || textFileName.equals("")) {
      textFileName = id;
    }
    TextSource newTextSource = textSourceCollection.get(textFileName);
    if (newTextSource == null){
      newTextSource = new TextSource(controller, file, textFileName);
      textSourceCollection.add(newTextSource);
    }

    controller.getSelectionManager().setSelectedTextSource(newTextSource);
    return newTextSource;
  }

  public TextSourceCollection getTextSourceCollection() {
    return textSourceCollection;
  }

  @Override
  public void writeToKnowtatorXML(Document dom, Element parent) {
    textSourceCollection
        .getCollection()
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

  @SuppressWarnings("RedundantThrows")
  @Override
  public void writeToBratStandoff(Writer writer) throws IOException {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}
}
