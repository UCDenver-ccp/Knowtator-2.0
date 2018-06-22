package edu.ucdenver.ccp.knowtator.model.text.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceSelectionListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.collection.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphSpaceManager implements Savable {
  private GraphSpace activeGraphSpace;
  private KnowtatorController controller;
  private TextSource textSource;
  private List<GraphSpaceSelectionListener> graphSpaceListeners;
  private GraphSpaceCollection graphSpaceCollection;

  public GraphSpaceManager(KnowtatorController controller, TextSource textSource) {

    this.controller = controller;
    this.textSource = textSource;
    graphSpaceListeners = new ArrayList<>();
    graphSpaceCollection = new GraphSpaceCollection(controller);
  }

  public GraphSpace getActiveGraphSpace() {
    return activeGraphSpace;
  }

  public void setSelectedGraphSpace(GraphSpace newGraphSpace) {

    if (this.activeGraphSpace != newGraphSpace) {
      this.activeGraphSpace = newGraphSpace;
      controller.refreshView();
    }
  }

  public void getNextGraphSpace() {
    setSelectedGraphSpace(graphSpaceCollection.getNext(activeGraphSpace));
  }

  public void getPreviousGraphSpace() {

    setSelectedGraphSpace(graphSpaceCollection.getPrevious(activeGraphSpace));
  }

  public void addGraphSpaceListener(GraphSpaceSelectionListener listener) {
    graphSpaceListeners.add(listener);
  }

  public void dispose() {
    this.graphSpaceListeners.clear();
    graphSpaceCollection.forEach(GraphSpace::dispose);
    graphSpaceCollection.getCollection().clear();
  }

  public GraphSpace addGraphSpace(String title) {
    GraphSpace newGraphSpace = new GraphSpace(controller, textSource, title);
    graphSpaceCollection.add(newGraphSpace);
    setSelectedGraphSpace(newGraphSpace);
    return newGraphSpace;
  }

  public void removeAnnotation(Annotation annotationToRemove) {
    for (GraphSpace graphSpace : graphSpaceCollection) {
      for (Object vertex : graphSpace.getVerticesForAnnotation(annotationToRemove)) {
        graphSpace.setSelectionCell(vertex);
        graphSpace.removeSelectedCell();
      }
    }
  }

  @Override
  public void writeToKnowtatorXML(Document dom, Element parent) {
    graphSpaceCollection.forEach(graphSpace -> graphSpace.writeToKnowtatorXML(dom, parent));
  }

  @Override
  public void readFromKnowtatorXML(File file, Element parent) {
    for (Node graphSpaceNode :
        KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.GRAPH_SPACE))) {
      Element graphSpaceElem = (Element) graphSpaceNode;

      String id = graphSpaceElem.getAttribute(KnowtatorXMLAttributes.ID);
      GraphSpace graphSpace = addGraphSpace(id);

      graphSpace.readFromKnowtatorXML(null, graphSpaceElem);
    }
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {}

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {}

  public void readFromBratStandoff() {}

  @Override
  public void writeToBratStandoff(
      Writer writer,
      Map<String, Map<String, String>> annotationConfig,
      Map<String, Map<String, String>> visualConfig)
      throws IOException {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  public GraphSpaceCollection getGraphSpaceCollection() {
    return graphSpaceCollection;
  }

  public void removeGraphSpace(GraphSpace graphSpace) {
    graphSpaceCollection.remove(graphSpace);
  }

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
}
