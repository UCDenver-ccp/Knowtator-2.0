package edu.ucdenver.ccp.knowtator.model.text.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.collection.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphSpaceManager extends GraphSpaceCollection implements KnowtatorXMLIO, BratStandoffIO, KnowtatorManager {
  private KnowtatorController controller;
  private TextSource textSource;

  public GraphSpaceManager(KnowtatorController controller, TextSource textSource) {

    this.controller = controller;
    this.textSource = textSource;
    graphSpaceCollection = new GraphSpaceCollection(controller);
  }

  /*
  GETTERS
   */

  public void getNextGraphSpace() {
    setSelection(graphSpaceCollection.getNext(getSelection()));
  }

  public void getPreviousGraphSpace() {
    setSelection(graphSpaceCollection.getPrevious(getSelection()));
  }

  public GraphSpaceCollection getGraphSpaceCollection() {
    return graphSpaceCollection;
  }

  /*
  SETTERS
   */



  /*
  ADDERS
   */

  public void addGraphSpace(String graphName) {
    GraphSpace newGraphSpace = new GraphSpace(controller, textSource, graphName);
    addGraphSpace(newGraphSpace);
  }

  public void addGraphSpace(GraphSpace newGraphSpace) {
    graphSpaceCollection.add(newGraphSpace);
    setSelection(newGraphSpace);

    textSource.save();
  }

  /*
  REMOVERS
   */

  public void removeAnnotation(Annotation annotationToRemove) {
    for (GraphSpace graphSpace : graphSpaceCollection) {
      for (Object vertex : graphSpace.getVerticesForAnnotation(annotationToRemove)) {
        graphSpace.setSelectionCell(vertex);
        graphSpace.removeSelectedCell();
      }
    }
  }

  public void removeGraphSpace(GraphSpace graphSpace) {
    graphSpaceCollection.remove(graphSpace);
    setSelection(null);
    textSource.save();
  }

  /*
  WRITERS
   */
  @Override
  public void writeToKnowtatorXML(Document dom, Element parent) {
    graphSpaceCollection.forEach(graphSpace -> graphSpace.writeToKnowtatorXML(dom, parent));
  }


  /*
  READERS
   */
  @Override
  public void readFromKnowtatorXML(File file, Element parent) {
    for (Node graphSpaceNode :
        KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.GRAPH_SPACE))) {
      Element graphSpaceElem = (Element) graphSpaceNode;

      String id = graphSpaceElem.getAttribute(KnowtatorXMLAttributes.ID);

      GraphSpace graphSpace = new GraphSpace(controller, textSource, id);
      addGraphSpace(graphSpace);

      graphSpace.readFromKnowtatorXML(null, graphSpaceElem);
    }
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {}




  String verifyID(String id, String idPrefix) {
    if (id == null) {
      id = String.format("%s_0", idPrefix);
    }
    List<String> ids = new ArrayList<>();
    for (GraphSpace graphSpace : graphSpaceCollection) {
      for (Object cell : graphSpace.getChildVertices(graphSpace.getDefaultParent())) {
        ids.add(((KnowtatorObject) cell).getId());
      }
    }

    while (ids.contains(id)) {
      int vertexIDIndex = Integer.parseInt(id.split(String.format("%s_", idPrefix))[1]);
      id = String.format("%s_%d", idPrefix, ++vertexIDIndex);
    }

    return id;
  }

  @Override
  public void dispose() {
    graphSpaceCollection.forEach(GraphSpace::dispose);
    graphSpaceCollection.getCollection().clear();
  }

  @Override
  public void makeDirectory() {

  }

  @Override
  public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

  }

  @Override
  public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) {

  }
}
