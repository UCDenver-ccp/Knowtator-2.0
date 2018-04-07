package edu.ucdenver.ccp.knowtator.model.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.graph.Triple;
import edu.ucdenver.ccp.knowtator.model.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;


public class AnnotationManager implements Savable {

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

    void addAnnotation(Annotation newAnnotation) {
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

        OWLClass owlClass = manager.getOWLAPIDataExtractor().getOWLClassByID((String) newAnnotation.getOwlClass());
        if (owlClass != null) {
            newAnnotation.setOwlClass(owlClass);
        }
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

    public void addAnnotation(String classID, Span span) {
        if (classID != null) {
            Annotation newAnnotation = new Annotation(classID, null, textSource, manager.getProfileManager().getCurrentProfile(), "identity");
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

    public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
        getAnnotations().forEach(annotation -> annotation.writeToKnowtatorXML(dom, textSourceElement));
        graphSpaces.forEach(graphSpace -> graphSpace.writeToKnowtatorXML(dom, textSourceElement));
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent, String content) {
        Element annotationElement;
        String annotationID;
        String profileID;
        String type;
        Profile profile;
//        String className;
        String classID;
        Annotation newAnnotation;
        Element graphSpaceElem;
        String id;
        GraphSpace graphSpace;
        for (Node annotationNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION))) {
            annotationElement = (Element) annotationNode;

            annotationID = annotationElement.getAttribute(KnowtatorXMLAttributes.ID);
            profileID = annotationElement.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
            type = annotationElement.getAttribute(KnowtatorXMLAttributes.TYPE);

            profile = manager.getProfileManager().addNewProfile(profileID);
//            className = annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0).getTextContent();
            classID = ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.ID);

            newAnnotation = new Annotation(classID, annotationID, textSource, profile, type);
//            log.warn("\t\tXML: " + newAnnotation);
            newAnnotation.readFromKnowtatorXML(null, annotationElement, content);

            addAnnotation(newAnnotation);
        }

        for (Node graphSpaceNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.GRAPH_SPACE))) {
            graphSpaceElem = (Element) graphSpaceNode;


            id = graphSpaceElem.getAttribute(KnowtatorXMLAttributes.ID);
            graphSpace = addGraphSpace(id);

            log.warn("\t\tXML: " + graphSpace);
            graphSpace.readFromKnowtatorXML(null, graphSpaceElem, content);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent, String content) {
        Map<String, Element> complexSlotMentionToClassIDMap = KnowtatorXMLUtil.getComplexSlotsFromXml(parent);
        Map<String, Element> classMentionToClassIDMap = KnowtatorXMLUtil.getClassIDsFromXml(parent);

        Element annotationElement;
        String annotationID;
        Element classElement;
//        String className;
        String classID;
        Annotation newAnnotation;
        Map<Annotation, Element> annotationToComplexSlotMap = new HashMap<>();

        for (Node annotationNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATION))) {
            annotationElement = (Element) annotationNode;

            Profile profile;
            try {
                String profileID = annotationElement.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATOR).item(0).getTextContent();
                profile = manager.getProfileManager().addNewProfile(profileID);
            } catch (NullPointerException npe) {
                profile = manager.getProfileManager().getDefaultProfile();
            }

            annotationID = ((Element) annotationElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
            classElement = classMentionToClassIDMap.get(annotationID);

//            className = classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0).getTextContent();
            classID = ((Element) classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);

            newAnnotation = new Annotation(classID, annotationID, textSource, profile, "identity");

            newAnnotation.readFromOldKnowtatorXML(null, annotationElement, content);

            // No need to keep annotations with no spans
            if (!newAnnotation.getSpans().isEmpty()) {
                addAnnotation(newAnnotation);

                log.warn("\t\tOLD KNOWATOTOR: added ANNOTATION " + newAnnotation);

                for (Node slotMentionNode : KnowtatorXMLUtil.asList(classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION))) {
                    Element slotMentionElement = (Element) slotMentionNode;
                    String slotMentionID = slotMentionElement.getAttribute(OldKnowtatorXMLAttributes.ID);
                    Element complexSlotMentionElement = complexSlotMentionToClassIDMap.get(slotMentionID);
                    annotationToComplexSlotMap.put(newAnnotation, complexSlotMentionElement);
                }
            }

//            if (classID.equals(OldKnowtatorXMLAttributes.IDENTITY_CHAIN)) {
//                Element complexSlotElement = complexSlotMentionToClassIDMap.get(((Element) classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID));
//                if (((Element) complexSlotElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID).equals(OldKnowtatorXMLAttributes.COREFERENCE)) {
//                    for (Node complexSlotMentionValueNode : KnowtatorXMLUtil.asList(complexSlotElement.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
//                        newAnnotation.addCoreferringAnnotation(((Element) complexSlotMentionValueNode).getAttribute(OldKnowtatorXMLAttributes.VALUE));
//                    }
//                }
//
//            }



//            log.warn("OLD XML: " + newAnnotation);

        }

        GraphSpace oldKnowtatorGraphSpace = addGraphSpace("Old Knowtator Relations");
        annotationToComplexSlotMap.forEach((annotation, slotMention) -> {
            List<Object> vertices = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation);
            AnnotationNode source;
            if (vertices.isEmpty()) {
                source = oldKnowtatorGraphSpace.addNode(null, annotation);
                log.warn("\t\tOLD KNOWTATOR: added NODE: " + source);
            } else {
                source = (AnnotationNode) vertices.get(0);
            }


            String property = ((Element) slotMention.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
            for (Node slotMentionValueNode : OldKnowatorUtil.asList(slotMention.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
                Element slotMentionValueElement = (Element) slotMentionValueNode;
                String value = slotMentionValueElement.getAttribute(OldKnowtatorXMLAttributes.VALUE);
                Annotation annotation1 = getAnnotation(value);

                List<Object> vertices1 = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation1);
                AnnotationNode target;
                if (vertices1.isEmpty()) {
                    target = oldKnowtatorGraphSpace.addNode(null, annotation1);
                    log.warn("\t\t\tOLD KNOWTATOR: added NODE: " + target);
                } else {
                    target = (AnnotationNode) vertices1.get(0);
                }
                Triple triple = oldKnowtatorGraphSpace.addTriple(source, target, null, manager.getProfileManager().getCurrentProfile(), property, "", "");
                log.warn("\t\t\tOLD KNOWTATOR: added TRIPLE: " + triple);
            }
        });

    }

    //TODO: Should probably also read in Events, Modifiers, etc.
    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

        Profile profile = manager.getProfileManager().getDefaultProfile();

        annotationMap.get(StandoffTags.TEXTBOUNDANNOTATION).forEach(annotation -> {
            Annotation newAnnotation = new Annotation(annotation[1].split(StandoffTags.textBoundAnnotationTripleDelimiter)[0], annotation[0], textSource, profile, "identity");
            Map<Character, List<String[]>> map = new HashMap<>();
            List<String[]> list = new ArrayList<>();
            list.add(annotation);
            map.put(StandoffTags.TEXTBOUNDANNOTATION, list);
            newAnnotation.readFromBratStandoff(null, map, content);

            addAnnotation(newAnnotation);
        });

        GraphSpace newGraphSpace = addGraphSpace("Brat Relation Graph");
        newGraphSpace.readFromBratStandoff(null, annotationMap, null);
    }


    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {
        Iterator<Annotation> annotationIterator = annotationMap.values().iterator();
        for (int i=0; i<annotationMap.values().size(); i++) {
            Annotation annotation = annotationIterator.next();
            annotation.setBratID(String.format("T%d", i));
            writer.append(String.format("%s\t%s ", annotation.getBratID(), annotation.getOwlClass()));
            annotation.writeToBratStandoff(writer);

            writer.append(String.format("\t%s\n", annotation.getSpans().first().getSpannedText()));
        }

        int lastNumTriples = 0;
        for (GraphSpace graphSpace : graphSpaces) {
            Object[] edges = graphSpace.getChildEdges(graphSpace.getDefaultParent());
            int bound = edges.length;
            for (int i = 0; i < bound; i++) {
                Object edge = edges[i];
                Triple triple = (Triple) edge;
                triple.setBratID(String.format("R%d", lastNumTriples + i));
                writer.append(String.format("%s\t%s Arg1:%s Arg2:%s\n",
                        triple.getBratID(),
                        triple.getValue(),
                        ((AnnotationNode) triple.getSource()).getAnnotation().getBratID(),
                        ((AnnotationNode) triple.getTarget()).getAnnotation().getBratID())
                );
            }
        }
    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

//    @Override
//    public void convertToUIMA(CAS cas) {
//        annotationMap.values().forEach(annotation -> annotation.convertToUIMA(cas));
//    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    public List<GraphSpace> getGraphSpaces() {
        return graphSpaces;
    }

    public void removeGraphSpace(GraphSpace graphSpace) {
        graphSpaces.remove(graphSpace);
        manager.removeGraphEvent(graphSpace);
    }

}
