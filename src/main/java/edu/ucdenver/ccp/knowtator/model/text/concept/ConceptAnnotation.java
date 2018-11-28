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
import edu.ucdenver.ccp.knowtator.model.text.DataObjectModificationListener;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollection;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.*;

public class ConceptAnnotation extends SpanCollection implements KnowtatorXMLIO, BratStandoffIO, KnowtatorTextBoundDataObjectInterface<ConceptAnnotation> {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(ConceptAnnotation.class);
    private final TextSource textSource;
    private OWLClass owlClass;
    private final String annotation_type;

    private final List<DataObjectModificationListener> modificationListeners;
    private final Set<ConceptAnnotation> overlappingConceptAnnotations;
    private final Profile annotator;
    private String bratID;
    private final KnowtatorModel controller;
    private final SpanCollection spanCollection;
    private String motivation;
    private String id;

    public ConceptAnnotation(
            KnowtatorModel controller,
            TextSource textSource,
            String annotationID,
            @Nonnull OWLClass owlClass,
            Profile annotator,
            String annotation_type,
            String motivation) {


        super(controller);

        this.annotator = annotator;
        this.controller = controller;
        this.annotation_type = annotation_type;
        this.motivation = motivation;
        this.owlClass = owlClass;
        this.textSource = textSource;

        this.modificationListeners = new ArrayList<>();

        spanCollection = new SpanCollection(controller);
        //noinspection unchecked
        spanCollection.addCollectionListener(textSource);
        controller.verifyId(annotationID, this, false);

        overlappingConceptAnnotations = new HashSet<>();

        getColor();
    }

    @Override
    public TextSource getTextSource() {
        return textSource;
    }

    @Override
    public void addDataObjectModificationListener(DataObjectModificationListener listener) {
        modificationListeners.add(listener);
    }

    @Override
    public void removeDataObjectModificationListener(DataObjectModificationListener listener) {
        modificationListeners.remove(listener);
    }

    @Override
    public void modify(List<Integer> parameters) {
        modificationListeners.forEach(DataObjectModificationListener::modification);
    }


  /*
  GETTERS
   */

    public Profile getAnnotator() {
        return annotator;
    }

    public OWLClass getOwlClass() {
        return owlClass;
    }

    /**
     * @return the getNumberOfGraphSpaces of the Span associated with the concept. If the concept has more than
     * one Span, then the sum of the getNumberOfGraphSpaces of the spanCollection is returned.
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
        return annotator.getColor(owlClass);
    }


  /*
  SETTERS
   */


    public void setOwlClass(OWLClass owlClass) {
        this.owlClass = owlClass;
        modify(null);

    }

    void setBratID(String bratID) {
        this.bratID = bratID;
    }


  /*
  WRITERS
   */

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) throws IOException {

	    String renderedOwlClassID = controller.getOWLEntityRendering(owlClass).replace(":", "_").replace(" ", "_");
        annotationConfig.get(StandoffTags.annotationsEntities).put(renderedOwlClassID, "");

        writer.append(String.format("%s\t%s ", getBratID(), renderedOwlClassID));

	    visualConfig.get("labels").put(renderedOwlClassID, getOWLClassLabel());
        visualConfig.get("drawing").put(renderedOwlClassID, String.format("bgColor:%s", Profile.convertToHex(annotator.getColor(owlClass))));

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

	    classElement.setAttribute(KnowtatorXMLAttributes.ID, controller.getOWLEntityRendering(owlClass));
        classElement.setAttribute(KnowtatorXMLAttributes.LABEL, getOWLClassLabel());
        annotationElem.appendChild(classElement);

        spanCollection.writeToKnowtatorXML(dom, annotationElem);

        textSourceElement.appendChild(annotationElem);
    }

    public String getOWLClassLabel() {
        return owlClass.getAnnotationPropertiesInSignature().stream()
                .filter(OWLAnnotationProperty::isLabel)
                .findFirst()
		        .map(OWLObject::toString).orElse(controller.getOWLEntityRendering(owlClass));
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
        Iterator<Span> spanIterator1 = iterator();
        Iterator<Span> spanIterator2 = conceptAnnotation2.iterator();
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
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
