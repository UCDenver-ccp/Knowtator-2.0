package edu.ucdenver.ccp.knowtator.model;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.listeners.OWLSetupListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLObjectPropertyNotFoundException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Triple extends mxCell implements Savable, KnowtatorTextBoundObject, ProjectListener, OWLSetupListener, OWLOntologyChangeListener, OWLModelManagerListener {
  private String quantifier;
  private String quantifierValue;
  private Profile annotator;
  private String bratID;
  private KnowtatorController controller;
  private TextSource textSource;
  private OWLObjectProperty property;
  private GraphSpace graphSpace;
  @SuppressWarnings("unused")
  private Logger log = Logger.getLogger(Triple.class);
  private boolean dontRedraw;

  Triple(
          String id,
          mxCell source,
          mxCell target,
          OWLObjectProperty property,
          Profile annotator,
          String quantifier,
          String quantifierValue,
          KnowtatorController controller, TextSource textSource, GraphSpace graphSpace) throws OWLEntityNullException, OWLWorkSpaceNotSetException {
    super(String.format("%s\n%s %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(property), quantifier, quantifierValue), new mxGeometry(), null);

    this.property = property;
    this.textSource = textSource;
    this.controller = controller;
    this.annotator = annotator;
    this.quantifier = quantifier;
    this.quantifierValue = quantifierValue;
    this.graphSpace = graphSpace;

    dontRedraw = false;
    controller.getOWLAPIDataExtractor().addOWLSetupListener(this);
    controller.getProjectManager().addListener(this);

    controller.verifyId(id, this, false);

    getGeometry().setRelative(true);
    setEdge(true);
    setSource(source);
    setTarget(target);
  }

  public Triple(String id,
                mxCell source,
                mxCell target,
                String propertyID,
                Profile annotator,
                String quantifier,
                String quantifierValue,
                KnowtatorController controller, TextSource textSource, GraphSpace graphSpace) {
    super(propertyID, new mxGeometry(), null);

    this.textSource = textSource;
    this.controller = controller;
    this.annotator = annotator;
    this.quantifier = quantifier;
    this.quantifierValue = quantifierValue;
    this.graphSpace = graphSpace;

    controller.getOWLAPIDataExtractor().addOWLSetupListener(this);

    controller.verifyId(id, this, false);

    getGeometry().setRelative(true);
    setEdge(true);
    setSource(source);
    setTarget(target);

    owlSetup();
  }

  public Profile getAnnotator() {
    return annotator;
  }

  public void writeToKnowtatorXML(Document dom, Element graphElem) {
    Element tripleElem = dom.createElement(KnowtatorXMLTags.TRIPLE);
    tripleElem.setAttribute(KnowtatorXMLAttributes.ID, id);
    tripleElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, annotator.getId());
    tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_SUBJECT, getSource().getId());
    tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_OBJECT, getTarget().getId());

    String propertyID;
    try{
      propertyID = controller.getOWLAPIDataExtractor().getOWLEntityRendering(property);
    } catch (OWLEntityNullException | OWLWorkSpaceNotSetException e) {
      propertyID = getValue().toString();
    }
    tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_PROPERTY, propertyID);

    tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_QUANTIFIER, quantifier);
    tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_VALUE, quantifierValue);
    graphElem.appendChild(tripleElem);
  }

  @Override
  public void readFromKnowtatorXML(File file, Element parent) {}

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {}

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {}

  @SuppressWarnings("RedundantThrows")
  @Override
  public void writeToBratStandoff(Writer writer) throws IOException {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  String getBratID() {
    return bratID;
  }

  void setBratID(String bratID) {
    this.bratID = bratID;
  }

  public OWLObjectProperty getProperty() {
    return property;
  }

  public String getQuantifier() {
    return quantifier;
  }

  public String getQuantifierValue() {
    return quantifierValue;
  }

  @Override
  public TextSource getTextSource() {
    return textSource;
  }

  @Override
  public void setValue(Object value) {
    super.setValue(value);
    if (graphSpace != null && !dontRedraw) {
      graphSpace.reDrawGraph();
    }
  }

  @Override
  public void owlSetup() {
    try {
      controller.getOWLAPIDataExtractor().getWorkSpace().getOWLModelManager().addListener(this);
      controller.getOWLAPIDataExtractor().getWorkSpace().getOWLModelManager().addOntologyChangeListener(this);

      property = controller.getOWLAPIDataExtractor().getOWLObjectPropertyByID(getValue().toString());
      dontRedraw = true;
      setValue(String.format("%s\n%s %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(property), quantifier, quantifierValue));
      dontRedraw = false;
    } catch (OWLWorkSpaceNotSetException | OWLObjectPropertyNotFoundException | OWLEntityNullException ignored) {

    }
  }

  @Override
  public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {
    Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
    Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
    OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
    OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

    for (OWLOntologyChange chg : changes) {
      if (chg.isAxiomChange()) {
        OWLAxiomChange axChg = (OWLAxiomChange) chg;
        if (axChg.getAxiom().getAxiomType() == AxiomType.DECLARATION) {
          if (axChg instanceof AddAxiom) {
            axChg.getAxiom().accept(addedCollector);
          } else {
            axChg.getAxiom().accept(removedCollector);
          }
        }
      }
    }

    /*
    For now, I will assume that entity removed is the one that existed and the one
    that is added is the new name for it.
     */
    if (!possiblyAddedEntities.isEmpty() && !possiblyRemovedEntities.isEmpty()) {
      OWLEntity oldProperty = possiblyRemovedEntities.iterator().next();
      OWLEntity newProperty = possiblyAddedEntities.iterator().next();
      if (property == oldProperty) {
        try {
          setValue(String.format("%s\n%s, %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(newProperty), quantifier, quantifierValue));
        } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void handleChange(OWLModelManagerChangeEvent event) {
    if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
      try {
        setValue(String.format("%s\n%s %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(property), quantifier, quantifierValue));
      } catch (OWLWorkSpaceNotSetException | OWLEntityNullException ignored) {
      }
    }
  }

  @Override
  public void projectClosed() {
  }

  @Override
  public void projectLoaded() {
    owlSetup();
  }
}
