package edu.ucdenver.ccp.knowtator.model;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.owl.OWLObjectPropertyNotFoundException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class Triple extends mxCell implements Savable, KnowtatorObject {
  private String quantifier;
  private String quantifierValue;
  private Profile annotator;
  private String bratID;
  private KnowtatorController controller;
  private TextSource textSource;

  Triple(
          String id,
          mxCell source,
          mxCell target,
          Object property,
          Profile annotator,
          String quantifier,
          String quantifierValue,
          KnowtatorController controller, TextSource textSource) {
    super(property, new mxGeometry(), null);

    this.textSource = textSource;
    this.controller = controller;
    this.annotator = annotator;
    this.quantifier = quantifier;
    this.quantifierValue = quantifierValue;

    controller.verifyId(id, this, false);

    getGeometry().setRelative(true);
    setEdge(true);
    setSource(source);
    setTarget(target);
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
    tripleElem.setAttribute(
        KnowtatorXMLAttributes.TRIPLE_PROPERTY,
        getValue() instanceof OWLObjectProperty
            ? ((OWLObjectProperty) getValue()).getIRI().getShortForm()
            : getValue().toString());
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

  @Override
  public Object getValue() {
    try {
      setValue(controller.getOWLAPIDataExtractor().getOWLObjectPropertyByID((String) value));
    } catch (OWLObjectPropertyNotFoundException | OWLWorkSpaceNotSetException ignored) {
    }
    return value;
  }

  String getBratID() {
    return bratID;
  }

  void setBratID(String bratID) {
    this.bratID = bratID;
  }

  public Object getProperty() {
    return getValue();
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
}
