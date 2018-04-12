package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.OldKnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TextSourceManager implements Savable, OWLOntologyChangeListener {
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
    TextSource newTextSource = new TextSource(controller, file, textFileName);
    textSourceCollection.add(newTextSource);

    controller.getSelectionManager().setSelected(newTextSource);
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
      log.warn("\tXML: " + newTextSource);
      newTextSource.readFromKnowtatorXML(null, documentElement);
    }
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {

    String docID = parent.getAttribute(OldKnowtatorXMLAttributes.TEXT_SOURCE).replace(".txt", "");
    TextSource newTextSource = addTextSource(file, docID, null);
    log.warn("\tOLD XML: " + newTextSource);
    newTextSource.readFromOldKnowtatorXML(null, parent);
  }

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {
    String docID = annotationMap.get(StandoffTags.DOCID).get(0)[0];

    TextSource newTextSource = addTextSource(file, docID, null);
    log.warn("\tBRAT: " + newTextSource);
    newTextSource.readFromBratStandoff(null, annotationMap, null);
  }

  @SuppressWarnings("RedundantThrows")
  @Override
  public void writeToBratStandoff(Writer writer) throws IOException {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  /*
  React to changes in the ontology. For now, only handling entity renaming
   */
  @Override
  public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {

    log.warn("Ontology Change Event");
    textSourceCollection
        .getCollection()
        .forEach(
            textSource ->
                textSource
                    .getAnnotationManager()
                    .getGraphSpaceCollection()
                    .getCollection()
                    .forEach(
                        graphSpace -> {
                          Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
                          Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
                          OWLEntityCollector addedCollector =
                              new OWLEntityCollector(possiblyAddedEntities);
                          OWLEntityCollector removedCollector =
                              new OWLEntityCollector(possiblyRemovedEntities);

                          for (OWLOntologyChange chg : changes) {
                            if (chg.isAxiomChange()) {
                              OWLAxiomChange axChg = (OWLAxiomChange) chg;
                              //                    log.warn(String.format("Axiom Change: %s",
                              // axChg));
                              if (axChg.getAxiom().getAxiomType() == AxiomType.DECLARATION) {
                                if (axChg instanceof AddAxiom) {
                                  axChg.getAxiom().accept(addedCollector);
                                } else {
                                  axChg.getAxiom().accept(removedCollector);
                                }
                              }
                            }
                          }
                          possiblyAddedEntities.forEach(
                              owlEntity -> log.warn(String.format("Added: %s", owlEntity)));

                          possiblyRemovedEntities.forEach(
                              owlEntity -> log.warn(String.format("Removed: %s", owlEntity)));

                          /*
                          For now, I will assume that entity removed is the one that existed and the one
                          that is added is the new name for it.
                           */
                          graphSpace.reassignProperty(
                              possiblyRemovedEntities.iterator().next(),
                              possiblyAddedEntities.iterator().next());
                          graphSpace.reDrawGraph();
                        }));
  }
}