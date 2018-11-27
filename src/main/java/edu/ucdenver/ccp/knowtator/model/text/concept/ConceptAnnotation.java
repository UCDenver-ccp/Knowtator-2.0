/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.text.concept;

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.AbstractKnowtatorTextBoundDataObject;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollection;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.*;

public class ConceptAnnotation extends AbstractKnowtatorTextBoundDataObject<ConceptAnnotation> implements KnowtatorXMLIO, BratStandoffIO {
    private static final Logger log = Logger.getLogger(ConceptAnnotation.class);
    private OWLClass owlClass;
    private final String annotation_type;

    private final Set<ConceptAnnotation> overlappingConceptAnnotations;
    private final Profile annotator;
    private String bratID;
    private String owlClassID;
    private final String owlClassLabel;
    private final KnowtatorModel controller;
    private final SpanCollection spanCollection;
    private String motivation;

    public ConceptAnnotation(
            KnowtatorModel controller,
            TextSource textSource, String annotationID,
            OWLClass owlClass,
            String owlClassID,
            String owlClassLabel,
            Profile annotator,
            String annotation_type,
            String motivation) {


        super(textSource, null);

        this.textSource = textSource;
        this.annotator = annotator;
        this.controller = controller;
        this.owlClassID = owlClassID;
        this.owlClassLabel = owlClassLabel;
        this.annotation_type = annotation_type;
        this.motivation = motivation;


        spanCollection = new SpanCollection(controller);
        //noinspection unchecked
        spanCollection.addCollectionListener(textSource);
        controller.verifyId(annotationID, this, false);

        overlappingConceptAnnotations = new HashSet<>();
        if (owlClass == null) setOWLClass();
    }

    void setOWLClass() {
        if (owlClass == null) {
            Optional<OWLClass> owlClass1 = controller.getOWLClassByID(owlClassID);
            if (owlClass1.isPresent()) {
                setOwlClass(owlClass1.get());
            } else {
                log.warn(String.format("OWL Class: %s not found for concept: %s", owlClassID, id));
            }
        }
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

    public Optional<OWLClass> getOwlClass() {
        return Optional.ofNullable(owlClass);
    }

    private String getOwlClassRendering() {
        return controller.getOWLEntityRendering(owlClass).orElse(owlClassID);

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
        sb.delete(sb.length() - 1, sb.length());
        return sb.toString();
    }

    @SuppressWarnings("unused")
    public Set<ConceptAnnotation> getOverlappingConceptAnnotations() {
        return overlappingConceptAnnotations;
    }

    private String getBratID() {
        return bratID;
    }

    public Color getColor() {
        return annotator.getColor(this);
    }


  /*
  SETTERS
   */


    public void setOwlClass(OWLClass owlClass) {

        this.owlClass = owlClass;
        controller.getOWLEntityRendering(owlClass).ifPresent(owlClassID -> this.owlClassID = owlClassID);
        modify(null);

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
            visualConfig.get("drawing").put(renderedOwlClassID, String.format("bgColor:%s", Profile.convertToHex(controller.getProfileCollection().getDefaultProfile().getColor(this))));

        }


        spanCollection.writeToBratStandoff(writer, annotationConfig, visualConfig);
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
        Element annotationElem = dom.createElement(KnowtatorXMLTags.ANNOTATION);
        annotationElem.setAttribute(KnowtatorXMLAttributes.ID, id);
        annotationElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, annotator.getId());
        annotationElem.setAttribute(KnowtatorXMLAttributes.TYPE, annotation_type);
	    annotationElem.setAttribute(KnowtatorXMLAttributes.MOTIVATION, motivation);

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

                Span span = new Span(controller, textSource, this, spanId, spanStart, spanEnd);
                spanCollection.add(span);
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
                Span span = new Span(controller, textSource, this, null, spanStart, spanEnd);
                spanCollection.add(span);
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
        int spanStart = Integer.parseInt(triple[1]);
        for (int i = 2; i < triple.length; i++) {
            int spanEnd = Integer.parseInt(triple[i].split(StandoffTags.spanDelimiter)[0]);

            Span span = new Span(controller, textSource, this, null, spanStart, spanEnd);
            spanCollection.add(span);

            if (i != triple.length - 1) {
                spanStart = Integer.parseInt(triple[i].split(StandoffTags.spanDelimiter)[1]);
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
                "%s OWL Class: %s", getSpannedText(), controller.getOWLEntityRendering(owlClass));
    }


  /*
  ADDERS
   */


    @SuppressWarnings("unused")
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
        spanCollection.dispose();
    }

    public KnowtatorModel getController() {
        return controller;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }
}
