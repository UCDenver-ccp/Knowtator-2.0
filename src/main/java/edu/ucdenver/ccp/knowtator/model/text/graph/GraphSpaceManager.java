package edu.ucdenver.ccp.knowtator.model.text.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.Savable;
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

public class GraphSpaceManager implements Savable, KnowtatorManager {
  private GraphSpace activeGraphSpace;
  private KnowtatorController controller;
  private TextSource textSource;
  private GraphSpaceCollection graphSpaceCollection;

  public GraphSpaceManager(KnowtatorController controller, TextSource textSource) {

    this.controller = controller;
    this.textSource = textSource;
    graphSpaceCollection = new GraphSpaceCollection(controller);
  }

  /*
  GETTERS
   */

  public GraphSpace getActiveGraphSpace() throws ActiveGraphSpaceNotSetException {
    if (activeGraphSpace == null) {
      throw new ActiveGraphSpaceNotSetException();
    }
    return activeGraphSpace;
  }

  public void getNextGraphSpace() {
    setSelectedGraphSpace(graphSpaceCollection.getNext(activeGraphSpace));
  }

  public void getPreviousGraphSpace() {

    setSelectedGraphSpace(graphSpaceCollection.getPrevious(activeGraphSpace));
  }

  public GraphSpaceCollection getGraphSpaceCollection() {
    return graphSpaceCollection;
  }

  /*
  SETTERS
   */

  public void setSelectedGraphSpace(GraphSpace newGraphSpace) {

    if (this.activeGraphSpace != newGraphSpace) {
      this.activeGraphSpace = newGraphSpace;
      controller.refreshView();
    }
  }

  /*
  ADDERS
   */

  public void addGraphSpace(String graphName) {
    GraphSpace newGraphSpace = new GraphSpace(controller, textSource, graphName);
    addGraphSpace(newGraphSpace);
  }

  public void addGraphSpace(GraphSpace newGraphSpace) {
    graphSpaceCollection.add(newGraphSpace);
    setSelectedGraphSpace(newGraphSpace);

    save();
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
    save();
  }


  /*
  WRITERS
   */
  @Override
  public void writeToKnowtatorXML(Document dom, Element parent) {
    graphSpaceCollection.forEach(graphSpace -> graphSpace.writeToKnowtatorXML(dom, parent));
  }

  @Override
  public void writeToBratStandoff(
          Writer writer,
          Map<String, Map<String, String>> annotationConfig,
          Map<String, Map<String, String>> visualConfig) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}


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

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {}

  public void readFromBratStandoff() {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}


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
  public File getSaveLocation(String extension) {
    return null;
  }

  @Override
  public void setSaveLocation(File newSaveLocation, String extension) {

  }

  @Override
  public void save() {
    if (controller.isProjectLoaded()) {
      textSource.save();
    }
  }
}
