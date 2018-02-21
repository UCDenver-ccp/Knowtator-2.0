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
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.xml.XmlTags;
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
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

    AnnotationManager(KnowtatorManager manager, TextSource textSource) {
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
    public void addAnnotation(Span span) {
        String[] clsInfo = manager.getOWLAPIDataExtractor().getSelectedOwlClassInfo();
        String className = clsInfo[0];
        String classID = clsInfo[1];

        if (classID != null) {
            Annotation newAnnotation = new Annotation(classID, className, null, textSource, manager.getProfileManager().getCurrentProfile(), "identity");
            newAnnotation.addSpan(span);
            addAnnotation(newAnnotation);
        }
    }

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

    public void addGraphSpace(GraphSpace graphSpace) {
        graphSpaces.add(graphSpace);
        manager.newGraphEvent(graphSpace);
    }

    public void writeToXml(Document dom, Element textSourceElement) {
        getAnnotations().forEach(annotation -> annotation.writeToXml(dom, textSourceElement));
        graphSpaces.forEach(graphSpace -> graphSpace.writeToXml(dom, textSourceElement));
    }

    @Override
    public void readFromXml(Element parent) {
        for (Node annotationNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.ANNOTATION))) {
            Element annotationElement = (Element) annotationNode;

            String annotationID = annotationElement.getAttribute(XmlTags.ID);
            String profileID = annotationElement.getAttribute(XmlTags.ANNOTATOR);
            String type = annotationElement.getAttribute(XmlTags.TYPE);

            Profile profile = manager.getProfileManager().addNewProfile(profileID);
            String className = annotationElement.getElementsByTagName(XmlTags.CLASS).item(0).getTextContent();
            String classID = ((Element) annotationElement.getElementsByTagName(XmlTags.CLASS).item(0)).getAttribute(XmlTags.ID);

            Annotation newAnnotation = new Annotation(classID, className, annotationID, textSource, profile, type);
            log.warn("\t\tXML: " + newAnnotation);
            newAnnotation.readFromXml(annotationElement);

            addAnnotation(newAnnotation);
        }

        for (Node graphSpaceNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.GRAPH))) {
            Element graphSpaceElem = (Element) graphSpaceNode;

            String id = graphSpaceElem.getAttribute(XmlTags.ID);
            GraphSpace graphSpace = new GraphSpace(manager, this, id);
            addGraphSpace(graphSpace);

            graphSpace.readFromXml(graphSpaceElem);
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
