/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.model.xml.XmlTags;
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
import edu.ucdenver.ccp.knowtator.model.xml.forOld.OldXmlReader;
import edu.ucdenver.ccp.knowtator.model.xml.forOld.OldXmlTags;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.*;
import java.util.stream.Collectors;


public final class AnnotationManager implements Savable {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AnnotationManager.class);

    private final KnowtatorManager manager;

    private TreeMap<Span, Annotation> spanMap;
    private TextSource textSource;
    private Map<String, Annotation> annotationMap;
    private List<GraphSpace> graphSpaces;

    public AnnotationManager(KnowtatorManager manager, TextSource textSource) {
        this.manager = manager;
        this.textSource = textSource;
        annotationMap = new HashMap<>();
        spanMap = new TreeMap<>(Span::compare);
        graphSpaces = new ArrayList<>();
    }

    public void addAnnotation(Annotation newAnnotation) {
        String id = newAnnotation.getID();
        if (id == null) {
            id = String.format("mention_%d", annotationMap.size());
        }

        while (annotationMap.containsKey(id)) {
            int annotationIDIndex = Integer.parseInt(id.split("mention_")[1]);
            id = String.format("mention_%d", ++annotationIDIndex);
        }
        newAnnotation.setID(id);
        annotationMap.put(newAnnotation.getID(), newAnnotation);

        newAnnotation.getSpans().forEach(span -> spanMap.put(span, newAnnotation));
        manager.annotationAddedEvent(newAnnotation);
    }

    public void addSpanToAnnotation(Annotation annotation, Span newSpan) {
        annotation.addSpan(newSpan);
        spanMap.put(newSpan, annotation);
        manager.spanAddedEvent(newSpan);
    }

    private void removeAnnotationFromSpanMap(Annotation annotationToRemove) {
        annotationToRemove.getSpans().forEach(span -> spanMap.remove(span));
        manager.annotationRemovedEvent(annotationToRemove);

    }

    public void removeAnnotation(String annotationToRemoveID) {
        Annotation annotationToRemove = annotationMap.get(annotationToRemoveID);
        annotationMap.remove(annotationToRemoveID);
        if (annotationToRemove != null) removeAnnotationFromSpanMap(annotationToRemove);
        manager.annotationRemovedEvent(annotationToRemove);
    }

    public void removeSpanFromAnnotation(Annotation annotation, Span span) {
        annotation.removeSpan(span);
        spanMap.remove(span);
        manager.spanRemovedEvent();
    }

    public Collection<Annotation> getAnnotations() {
        return annotationMap.values();
    }

    public Set<Annotation> getAllAnnotations() {
        return annotationMap.values().stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * @param loc     Location filter
     * @param profile Profile filter
     */
    public TreeMap<Span, Annotation> getSpanMap(Integer loc, Profile profile) {
        TreeMap<Span, Annotation> filteredAnnotations = new TreeMap<>(Span::compare);

        for (Map.Entry<Span, Annotation> entry : spanMap.entrySet()) {
            Span span = entry.getKey();
            Annotation annotation = entry.getValue();
            if ((loc == null || span.contains(loc)) && (profile == null || annotation.getAnnotator().equals(profile))) {
                filteredAnnotations.put(span, annotation);
            }
        }
        return filteredAnnotations;
    }

    public void growSpanStart(Span span) {
        if (spanMap.containsKey(span)) {
            Annotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.growStart();
            spanMap.put(span, annotation);
        } else {
            span.growStart();
        }
    }

    public void growSpanEnd(Span span, int limit) {
        if (spanMap.containsKey(span)) {
            Annotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.growEnd(limit);
            spanMap.put(span, annotation);
        } else {
            span.growEnd(limit);
        }
    }

    public void shrinkSpanEnd(Span span) {
        if (spanMap.containsKey(span)) {
            Annotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.shrinkEnd();
            spanMap.put(span, annotation);
        } else {
            span.shrinkEnd();
        }
    }

    public void shrinkSpanStart(Span span) {
        if (spanMap.containsKey(span)) {
            Annotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.shrinkStart();
            spanMap.put(span, annotation);
        } else {
            span.shrinkStart();
        }
    }

    //TODO: Add annotations as subclasses of their assigned classes as well as of the AO
    public void addAnnotation(String className, String classID, Span span) {
        if (classID != null) {
            Annotation newAnnotation = new Annotation(classID, className, null, textSource, manager.getProfileManager().getCurrentProfile(), "identity");
            newAnnotation.addSpan(span);
            addAnnotation(newAnnotation);
        }
    }

    @SuppressWarnings("unused")
    public void findOverlaps() {
        List<Span> overlappingSpans = new ArrayList<>();
        spanMap.forEach((span, annotation) -> {
            List<Span> toRemove = new ArrayList<>();
            overlappingSpans.forEach(span1 -> {
                if (span.intersects(span1)) {
                    annotation.addOverlappingAnnotation(span1.getAnnotation().getID());
                    span1.getAnnotation().addOverlappingAnnotation(annotation.getID());
                } else {
                    toRemove.add(span1);
                }
            });
            overlappingSpans.removeAll(toRemove);

            overlappingSpans.add(span);
        });
    }

    public Annotation getAnnotation(String annotationID) {
        return annotationMap.get(annotationID);
    }

    public GraphSpace addGraphSpace(String title) {
        GraphSpace newGraphSpace = new GraphSpace(manager, textSource, title);
        graphSpaces.add(newGraphSpace);
        manager.newGraphEvent(newGraphSpace);

        return newGraphSpace;
    }

    public void writeToXml(Document dom, Element textSourceElement) {
        getAnnotations().forEach(annotation -> annotation.writeToXml(dom, textSourceElement));
        graphSpaces.forEach(graphSpace -> graphSpace.writeToXml(dom, textSourceElement));
    }

    @Override
    public void readFromXml(Element parent, String content) {
        Element annotationElement;
        String annotationID;
        String profileID;
        String type;
        Profile profile;
        String className;
        String classID;
        Annotation newAnnotation;
        Element graphSpaceElem;
        String id;
        GraphSpace graphSpace;
        for (Node annotationNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.ANNOTATION))) {
            annotationElement = (Element) annotationNode;

            annotationID = annotationElement.getAttribute(XmlTags.ID);
            profileID = annotationElement.getAttribute(XmlTags.ANNOTATOR);
            type = annotationElement.getAttribute(XmlTags.TYPE);

            profile = manager.getProfileManager().addNewProfile(profileID);
            className = annotationElement.getElementsByTagName(XmlTags.CLASS).item(0).getTextContent();
            classID = ((Element) annotationElement.getElementsByTagName(XmlTags.CLASS).item(0)).getAttribute(XmlTags.ID);

            newAnnotation = new Annotation(classID, className, annotationID, textSource, profile, type);
//            log.warn("\t\tXML: " + newAnnotation);
            newAnnotation.readFromXml(annotationElement, content);

            addAnnotation(newAnnotation);
        }

        for (Node graphSpaceNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.GRAPH))) {
            graphSpaceElem = (Element) graphSpaceNode;


            id = graphSpaceElem.getAttribute(XmlTags.ID);
            graphSpace = addGraphSpace(id);

            log.warn("\t\tXML: " + graphSpace);
            graphSpace.readFromXml(graphSpaceElem, content);
        }
    }

    @Override
    public void readFromOldXml(Element parent) {
        Map<String, Element> classMentionToClassIDMap = OldXmlReader.getClassIDsFromXml(parent);
//        Map<String, Element> complexSlotMentionToClassIDMap = getComplexSlotsFromXml(textSourceElement);

        for (Node annotationNode : XmlUtil.asList(parent.getElementsByTagName(OldXmlTags.ANNOTATION))) {
            Element annotationElement = (Element) annotationNode;

            String profileID;
            try {
                profileID = annotationElement.getElementsByTagName(OldXmlTags.ANNOTATOR).item(0).getTextContent();
            } catch (NullPointerException npe) {
                profileID = "Default";
            }

            Profile profile = manager.getProfileManager().addNewProfile(profileID);



            String annotationID = ((Element) annotationElement.getElementsByTagName(OldXmlTags.MENTION).item(0)).getAttribute(OldXmlTags.ID);
            Element classElement = classMentionToClassIDMap.get(annotationID);

            String className = classElement.getElementsByTagName(OldXmlTags.MENTION_CLASS).item(0).getTextContent();
            String classID = ((Element) classElement.getElementsByTagName(OldXmlTags.MENTION_CLASS).item(0)).getAttribute(OldXmlTags.ID);

            Annotation newAnnotation;
//                    if (classID.equals(OldXmlTags.MENTION_CLASS_ID_IDENTITY)) {
//                        newAnnotation = new Annotation(annotationID, textSource, profile);
//                        Element complexSlotElement = complexSlotMentionToClassIDMap.get(((Element) classElement.getElementsByTagName(OldXmlTags.HAS_SLOT_MENTION).item(0)).getAttribute(OldXmlTags.HAS_SLOT_MENTION_ID));
//                        if (((Element) complexSlotElement.getElementsByTagName(OldXmlTags.MENTION_SLOT).item(0)).getAttribute(OldXmlTags.MENTION_SLOT_ID).equals(OldXmlTags.MENTION_SLOT_ID_COREFERENCE)) {
//                            for (Node complexSlotMentionValueNode : XmlUtil.asList(complexSlotElement.getElementsByTagName(OldXmlTags.COMPLEX_SLOT_MENTION_VALUE))) {
//                                ((IdentityChainAnnotation) newAnnotation).addCoreferringAnnotation(((Element) complexSlotMentionValueNode).getAttribute(OldXmlTags.COMPLEX_SLOT_MENTION_VALUE_VALUE));
//                            }
//                        }
//
//                    } else {
//
            newAnnotation = new Annotation(classID, className, annotationID, textSource, profile, "identity");

            newAnnotation.readFromOldXml(annotationElement);
//            spans.forEach(newAnnotation::addSpan);
            addAnnotation(newAnnotation);

//            log.warn("OLD XML: " + newAnnotation);

        }
    }

    public List<GraphSpace> getGraphSpaces() {
        return graphSpaces;
    }

    public void removeGraphSpace(GraphSpace graphSpace) {
        graphSpaces.remove(graphSpace);
        manager.removeGraphEvent(graphSpace);
    }
}
