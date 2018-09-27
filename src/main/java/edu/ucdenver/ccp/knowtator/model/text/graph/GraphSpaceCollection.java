package edu.ucdenver.ccp.knowtator.model.text.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphSpaceCollection extends KnowtatorCollection<GraphSpace> implements KnowtatorXMLIO, BratStandoffIO {
  private KnowtatorController controller;
  private TextSource textSource;

  public GraphSpaceCollection(KnowtatorController controller, TextSource textSource) {
    super(controller);
    this.controller = controller;
    this.textSource = textSource;
  }

  /*
  GETTERS
   */



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
    add(newGraphSpace);
    setSelection(newGraphSpace);
  }

  /*
  REMOVERS
   */

  public void removeAnnotation(ConceptAnnotation conceptAnnotationToRemove) {
    for (GraphSpace graphSpace : this) {
      for (Object vertex : graphSpace.getVerticesForAnnotation(conceptAnnotationToRemove)) {
        graphSpace.setSelectionCell(vertex);
        graphSpace.removeSelectedCell();
      }
    }
  }

  public void removeGraphSpace(GraphSpace graphSpace) {
    remove(graphSpace);
    setSelection(null);
  }

  /*
  WRITERS
   */
  @Override
  public void writeToKnowtatorXML(Document dom, Element parent) {
    forEach(graphSpace -> graphSpace.writeToKnowtatorXML(dom, parent));
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
    for (GraphSpace graphSpace : this) {
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
  public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

  }

  @Override
  public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) {

  }

    void refresh() {
    collectionListeners.forEach(l -> l.updated(getSelection()));
  }
}
