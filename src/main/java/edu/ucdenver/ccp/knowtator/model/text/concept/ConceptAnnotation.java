package edu.ucdenver.ccp.knowtator.model.text.concept;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.AbstractKnowtatorTextBoundObject;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollection;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class ConceptAnnotation extends AbstractKnowtatorTextBoundObject<ConceptAnnotation> implements KnowtatorXMLIO, BratStandoffIO {

    private OWLClass owlClass;
    private final String annotation_type;

    private final Set<ConceptAnnotation> overlappingConceptAnnotations;
    private final Profile annotator;
    private String bratID;
    private String owlClassID;
    private final String owlClassLabel;
    private final KnowtatorController controller;
    private final SpanCollection spanCollection;

    ConceptAnnotation(
            KnowtatorController controller,
            String annotationID,
            OWLClass owlClass,
            String owlClassID,
            String owlClassLabel,
            Profile annotator,
            String annotation_type,
            TextSource textSource) {
        super(textSource, null);
        this.textSource = textSource;
        this.annotator = annotator;
        this.controller = controller;
        this.owlClass = owlClass;
        this.owlClassID = owlClassID;
        this.owlClassLabel = owlClassLabel;
        this.annotation_type = annotation_type;

        spanCollection = new SpanCollection(controller, textSource, this);
        //noinspection unchecked
        spanCollection.addCollectionListener(textSource);
        controller.verifyId(annotationID, this, false);

        overlappingConceptAnnotations = new HashSet<>();
    }

  /*
  GETTERS
   */

    public String getOwlClassID() {
        return owlClassID;
    }

    public String getOwlClassLabel() {
        return owlClassLabel;
    }

    public Profile getAnnotator() {
        return annotator;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    private String getOwlClassRendering() {
        if (controller.getOWLModel().isWorkSpaceSet()) {
            return controller.getOWLModel().getOWLEntityRendering(owlClass);
        } else {
            return owlClassID;
        }
    }

    public SpanCollection getSpanCollection() {
        return spanCollection;
    }

    /**
     * @return the size of the Span associated with the concept. If the concept has more than
     * one Span, then the sum of the size of the spanCollection is returned.
     */
    public int getSize() {
        int size = 0;
        for (Span span : spanCollection) {
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
    public Set<ConceptAnnotation> getOverlappingConceptAnnotations() {
        return overlappingConceptAnnotations;
    }

    private String getBratID() {
        return bratID;
    }


  /*
  SETTERS
   */


    void setOwlClass(OWLClass owlClass) {
        this.owlClass = owlClass;
        this.owlClassID = controller.getOWLModel().getOWLEntityRendering(owlClass);
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
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) throws IOException {
        String renderedOwlClassID = getOwlClassRendering();
        renderedOwlClassID = renderedOwlClassID.replace(":", "_").replace(" ", "_");
        annotationConfig.get(StandoffTags.annotationsEntities).put(renderedOwlClassID, "");

        writer.append(String.format("%s\t%s ", getBratID(), renderedOwlClassID));


        if (getOwlClassLabel() != null) {
            visualConfig.get("labels").put(renderedOwlClassID, getOwlClassLabel());
            visualConfig.get("drawing").put(renderedOwlClassID, String.format("bgColor:%s", Profile.convertToHex(controller.getProfileCollection().get("Default").getColor(this))));

        }


        spanCollection.writeToBratStandoff(writer, annotationConfig, visualConfig);
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
        Element annotationElem = dom.createElement(KnowtatorXMLTags.ANNOTATION);
        annotationElem.setAttribute(KnowtatorXMLAttributes.ID, id);
        annotationElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, annotator.getId());
        annotationElem.setAttribute(KnowtatorXMLAttributes.TYPE, annotation_type);

        Element classElement = dom.createElement(KnowtatorXMLTags.CLASS);

        classElement.setAttribute(KnowtatorXMLAttributes.ID, getOwlClassRendering());

        if (owlClassLabel != null) {
            classElement.setAttribute(KnowtatorXMLAttributes.LABEL, getOwlClassLabel());
        }

        annotationElem.appendChild(classElement);

        spanCollection.writeToKnowtatorXML(dom, annotationElem);

        textSourceElement.appendChild(annotationElem);
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

                spanCollection.addSpan(spanId, spanStart, spanEnd);
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

                spanCollection.addSpan(null, spanStart, spanEnd);
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

            spanCollection.addSpan(null, start, end);

            if (i != triple.length - 1) {
                start = Integer.parseInt(triple[i].split(StandoffTags.spanDelimiter)[1]);
            }
        }
    }

  /*
  TRANSLATORS
   */

    /**
     * this needs to be moved out of this class
     *
     * @return an html representation of the concept
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
                "ConceptAnnotation: %s owl class: %s annotation_type: %s", id, getOwlClass(), annotation_type);
    }


  /*
  ADDERS
   */


    void addOverlappingAnnotation(ConceptAnnotation conceptAnnotation) {
        overlappingConceptAnnotations.add(conceptAnnotation);
    }

  /*
  REMOVERS
   */

    @Override
    public int compareTo(ConceptAnnotation conceptAnnotation2) {
        Iterator<Span> spanIterator1 = getSpanCollection().iterator();
        Iterator<Span> spanIterator2 = conceptAnnotation2.getSpanCollection().iterator();
        int result = 0;
        while (result == 0 && spanIterator1.hasNext() && spanIterator2.hasNext()) {
            Span span1 = spanIterator1.next();
            Span span2 = spanIterator2.next();
            if (span2 == null) {
                result = 1;
            } else if (span1 == null) {
                result = -1;
            } else {
                result = span1.getStart().compareTo(span2.getStart());
                if (result == 0) {
                    result = span1.getEnd().compareTo(span2.getEnd());
                }
            }
        }
        if (result == 0) {
            result = this.getId().compareTo(conceptAnnotation2.getId());
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
        textSource.getGraphSpaceCollection().removeAnnotation(this);
        spanCollection.dispose();
    }

    public KnowtatorController getController() {
        return controller;
    }
}
