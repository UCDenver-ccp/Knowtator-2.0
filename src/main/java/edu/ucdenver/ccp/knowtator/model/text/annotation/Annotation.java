package edu.ucdenver.ccp.knowtator.model.text.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.KnowtatorTextBoundObject;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.collection.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class Annotation implements Savable, KnowtatorTextBoundObject {

  private final Date date;

  @SuppressWarnings("unused")
  private Logger log = Logger.getLogger(Annotation.class);

  private OWLClass owlClass;
  private String annotation_type;
  private SpanCollection spanCollection;
  private Set<Annotation> overlappingAnnotations;
  private String id;
  private TextSource textSource;
  private Profile annotator;
  private String bratID;
  private String owlClassID;
  private String owlClassLabel;
  private KnowtatorController controller;

  public Annotation(
      KnowtatorController controller,
      String annotationID,
      OWLClass owlClass,
      String owlClassID,
      String owlClassLabel,
      Profile annotator,
      String annotation_type,
      TextSource textSource) {
    this.textSource = textSource;
    this.annotator = annotator;
    this.controller = controller;
    this.date = new Date();
    this.owlClass = owlClass;
    this.owlClassID = owlClassID;
    this.owlClassLabel = owlClassLabel;
    this.annotation_type = annotation_type;

    controller.verifyId(annotationID, this, false);


    spanCollection = new SpanCollection(controller);
    overlappingAnnotations = new HashSet<>();
  }

  /*
  GETTERS
   */

  @Override
  public TextSource getTextSource() {
    return textSource;
  }

  public String getOwlClassID() {
    return owlClassID;
  }

  public String getOwlClassLabel() { return owlClassLabel;}

  public Profile getAnnotator() {
    return annotator;
  }

  public Date getDate() {
    return date;
  }

  @Override
  public String getId() {
    return id;
  }

  public OWLClass getOwlClass() {
    return owlClass;
  }

  public SpanCollection getSpanCollection() {
    return spanCollection;
  }

  /**
   * @return the size of the Span associated with the annotation. If the annotation has more than
   *     one Span, then the sum of the size of the spanCollection is returned.
   */
  public int getSize() {
    int size = 0;
    for (Span span : this.spanCollection) {
      size += span.getSize();
    }
    return size;
  }

  public String getSpannedText() {
    StringBuilder sb = new StringBuilder();
    spanCollection
            .forEach(
                    span -> {
                      sb.append(String.format("%s", span.getSpannedText()));
                      sb.append("\n");
                    });
    return sb.toString();
  }

  @SuppressWarnings("unused")
  public Set<Annotation> getOverlappingAnnotations() {
    return overlappingAnnotations;
  }

  public String getType() {
    return annotation_type;
  }

  private String getBratID() {
    return bratID;
  }


  /*
  SETTERS
   */

  @Override
  public void setId(String id) {
    this.id = id;
  }

  void setOwlClass(OWLClass owlClass) {
    this.owlClass = owlClass;
    try {
      this.owlClassID = controller.getOWLManager().getOWLEntityRendering(owlClass);
    } catch (OWLWorkSpaceNotSetException | OWLEntityNullException ignored) {
    }
  }

  void setBratID(String bratID) {
    this.bratID = bratID;
  }

  void setOWLClassID(String owlClassID) {
    this.owlClassID = owlClassID;
  }


  /*
  WRITERS
   */

  @Override
  public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) throws IOException {
    String renderedOwlClassID;
    try {
      renderedOwlClassID = controller
              .getOWLManager()
              .getOWLEntityRendering(owlClass);
    } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
      renderedOwlClassID = this.owlClassID;

    }
    renderedOwlClassID = renderedOwlClassID.replace(":", "_").replace(" ", "_");
    annotationsConfig.get(StandoffTags.annotationsEntities).put(renderedOwlClassID, "");

    writer.append(String.format("%s\t%s ", getBratID(), renderedOwlClassID));


    if (getOwlClassLabel() != null) {
      visualConfig.get("labels").put(renderedOwlClassID, getOwlClassLabel());
      visualConfig.get("drawing").put(renderedOwlClassID, String.format("bgColor:%s", Profile.convertToHex(controller.getProfileManager().getProfile("Default").getColor(this))));

    }


    Iterator<Span> spanIterator = spanCollection.iterator();
    StringBuilder spannedText = new StringBuilder();
    for (int i = 0; i < spanCollection.size(); i++) {
      Span span = spanIterator.next();
      span.writeToBratStandoff(writer, annotationsConfig, visualConfig);
      String[] spanLines = span.getSpannedText().split("\n");
      for (int j = 0; j < spanLines.length; j++) {
        spannedText.append(spanLines[j]);
        if (j != spanLines.length -1) {
          spannedText.append(" ");
        }
      }
      if (i != spanCollection.size() - 1) {
        writer.append(";");
        spannedText.append(" ");
      }
    }
    writer.append(String.format("\t%s\n", spannedText.toString()));
  }

  @Override
  public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
    Element annotationElem = dom.createElement(KnowtatorXMLTags.ANNOTATION);
    annotationElem.setAttribute(KnowtatorXMLAttributes.ID, id);
    annotationElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, annotator.getId());
    annotationElem.setAttribute(KnowtatorXMLAttributes.TYPE, annotation_type);

    Element classElement = dom.createElement(KnowtatorXMLTags.CLASS);

    try {
      classElement.setAttribute(KnowtatorXMLAttributes.ID, controller.getOWLManager().getOWLEntityRendering(owlClass));
    } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
      classElement.setAttribute(KnowtatorXMLAttributes.ID, getOwlClassID());
    }

    if (owlClassLabel != null) {
      classElement.setAttribute(KnowtatorXMLAttributes.LABEL, getOwlClassLabel());
    }

    annotationElem.appendChild(classElement);

    spanCollection.forEach(span -> span.writeToKnowtatorXML(dom, annotationElem));

    textSourceElement.appendChild(annotationElem);
  }

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  @Override
  public void save() {

  }


  /*
  READERS
   */
  @Override
  public void readFromKnowtatorXML(File file, Element parent) {
    Element spanElement;
    String spanId;
    int spanStart;
    int spanEnd;
    for (Node spanNode :
            KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.SPAN))) {
      if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
        spanElement = (Element) spanNode;
        spanStart = Integer.parseInt(spanElement.getAttribute(KnowtatorXMLAttributes.SPAN_START));
        spanEnd = Integer.parseInt(spanElement.getAttribute(KnowtatorXMLAttributes.SPAN_END));
        spanId = spanElement.getAttribute(KnowtatorXMLAttributes.ID);

        Span newSpan = new Span(spanId, spanStart, spanEnd, textSource, controller);
        addSpan(newSpan);
      }
    }
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {
    for (Node spanNode :
            KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.SPAN))) {
      if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
        Element spanElement = (Element) spanNode;
        int spanStart =
                Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_START));
        int spanEnd =
                Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_END));

        addSpan(new Span(null, spanStart, spanEnd, textSource, controller));
      }
    }

  }

  @Override
  public void readFromBratStandoff(
          File file, Map<Character, List<String[]>> annotationMap, String content) {
    String[] triple =
            annotationMap
                    .get(StandoffTags.TEXTBOUNDANNOTATION)
                    .get(0)[1]
                    .split(StandoffTags.textBoundAnnotationTripleDelimiter);
    int start = Integer.parseInt(triple[1]);
    for (int i = 2; i < triple.length; i++) {
      int end = Integer.parseInt(triple[i].split(StandoffTags.spanDelimiter)[0]);

      Span newSpan = new Span(null, start, end, textSource, controller);
      addSpan(newSpan);

      if (i != triple.length - 1) {
        start = Integer.parseInt(triple[i].split(StandoffTags.spanDelimiter)[1]);
      }
    }
  }

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  /*
  TRANSLATORS
   */

  /**
   * this needs to be moved out of this class
   *
   * @return an html representation of the annotation
   */
  public String toHTML() {
    StringBuilder sb = new StringBuilder();
    sb.append("<ul><li>").append(annotator.getId()).append("</li>");

    sb.append("<li>class = ").append(getOwlClass()).append("</li>");
    sb.append("<li>spanCollection = ");
    for (Span span : spanCollection) sb.append(span.toString()).append(" ");
    sb.append("</li>");

    sb.append("</ul>");
    return sb.toString();
  }

  @Override
  public String toString() {
    return String.format(
        "Annotation: %s owl class: %s annotation_type: %s", id, getOwlClass(), annotation_type);
  }


  /*
  ADDERS
   */
  void addSpan(Span newSpan) {
    spanCollection.add(newSpan);
    newSpan.setAnnotation(this);
  }

  void addOverlappingAnnotation(Annotation annotation) {
    overlappingAnnotations.add(annotation);
  }

  /*
  REMOVERS
   */
  void removeSpan(Span span) {
    spanCollection.remove(span);
  }


  public static int compare(Annotation annotation1, Annotation annotation2) {
    Iterator<Span> spanIterator1 = annotation1.getSpanCollection().iterator();
    Iterator<Span> spanIterator2 = annotation2.getSpanCollection().iterator();
    int result = 0;
    while(result == 0 && spanIterator1.hasNext() && spanIterator2.hasNext()) {
      Span span1 = spanIterator1.next();
      Span span2 = spanIterator2.next();
      if (span2 == null) {
        result = 1;
      }
      else if (span1 == null) {
        result = -1;
      } else {
        result = span1.getStart().compareTo(span2.getStart());
        if (result == 0) {
          result = span1.getEnd().compareTo(span2.getEnd());
        }
      }
    }
    if (result == 0) {
      result = annotation1.getId().compareTo(annotation2.getId());
    }
    return result;
  }

  public boolean contains(Integer loc) {
    for (Span span : spanCollection) {
      if (span.contains(loc)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public void dispose() {
    spanCollection.forEach(Span::dispose);
    spanCollection.getCollection().clear();

  }
}
