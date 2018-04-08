package edu.ucdenver.ccp.knowtator.model.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.graph.Triple;
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
import java.util.function.Supplier;
import java.util.stream.Collectors;


public class AnnotationManager implements Savable, ProjectListener {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AnnotationManager.class);

    private final KnowtatorController controller;

    private TreeSet<Span> spanTreeSet;
    private TextSource textSource;
    private Map<String, Annotation> annotationMap;
    private List<GraphSpace> graphSpaces;

    public AnnotationManager(KnowtatorController controller, TextSource textSource) {
        this.controller = controller;
        this.textSource = textSource;
        annotationMap = new HashMap<>();
        spanTreeSet = new TreeSet<>(Span::compare);
        graphSpaces = new ArrayList<>();
    }

    void addAnnotation(Annotation newAnnotation) {
        String id = newAnnotation.getID();
        if (id == null || annotationMap.containsKey(id)) {
            id = String.format("mention_%d", annotationMap.size());
        }

        while (annotationMap.containsKey(id)) {
            int annotationIDIndex = Integer.parseInt(id.split("mention_")[1]);
            id = String.format("mention_%d", ++annotationIDIndex);
        }
        newAnnotation.setID(id);
        annotationMap.put(newAnnotation.getID(), newAnnotation);

        spanTreeSet.addAll(newAnnotation.getSpans());

        controller.getSelectionManager().setSelectedAnnotation(newAnnotation, null);
        controller.annotationAddedEvent(newAnnotation);
    }

    public void addSpanToAnnotation(Annotation annotation, Span newSpan) {
        annotation.addSpan(newSpan);
        spanTreeSet.add(newSpan);
        controller.spanAddedEvent(newSpan);
    }

    public void removeAnnotation(Annotation annotationToRemove) {
        annotationMap.remove(annotationToRemove.getID());
        for (Span span : annotationToRemove.getSpans()) {
            spanTreeSet.remove(span);
        }
        for (GraphSpace graphSpace : graphSpaces) {
            for (Object vertex : graphSpace.getVerticesForAnnotation(annotationToRemove)) {
                graphSpace.setSelectionCell(vertex);
                graphSpace.removeSelectedCell();
            }
        }
        controller.getSelectionManager().setSelectedAnnotation(null, null);
        controller.annotationRemovedEvent(annotationToRemove);
    }

    public void removeSpanFromAnnotation(Annotation annotation, Span span) {
        annotation.removeSpan(span);
        spanTreeSet.remove(span);
        controller.spanRemovedEvent();
    }

    public Collection<Annotation> getAnnotations() {
        return annotationMap.values();
    }

    public Set<Annotation> getAllAnnotations() {
        return annotationMap.values().stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    /**
     * @param loc     Location filter
     */
    public TreeSet<Span> getSpanSet(Integer loc) {
        Supplier<TreeSet<Span>> supplier = () -> new TreeSet<>(Span::compare);
        return spanTreeSet.stream().filter(span ->
                (loc == null || span.contains(loc)) &&
                        (!controller.getSelectionManager().isFilterByProfile() ||
                                span.getAnnotation().getAnnotator().equals(controller.getSelectionManager().getActiveProfile())))
                .collect(Collectors.toCollection(supplier));
    }

    public void growSpanStart(Span span) {
        if (spanTreeSet.contains(span)) {
            spanTreeSet.remove(span);
            span.growStart();
            spanTreeSet.add(span);
        } else {
            span.growStart();
        }
    }

    public void growSpanEnd(Span span, int limit) {
        if (spanTreeSet.contains(span)) {
            spanTreeSet.remove(span);
            span.growEnd(limit);
            spanTreeSet.add(span);
        } else {
            span.growEnd(limit);
        }
    }

    public void shrinkSpanEnd(Span span) {
        if (spanTreeSet.contains(span)) {
            spanTreeSet.remove(span);
            span.shrinkEnd();
            spanTreeSet.add(span);
        } else {
            span.shrinkEnd();
        }
    }

    public void shrinkSpanStart(Span span) {
        if (spanTreeSet.contains(span)) {
            spanTreeSet.remove(span);
            span.shrinkStart();
            spanTreeSet.add(span);
        } else {
            span.shrinkStart();
        }
    }

    public void addAnnotation(OWLClass owlClass, String owlClassID, Span span) {
        if (owlClass != null) {
            Annotation newAnnotation = new Annotation(
                    owlClass,
                    owlClassID,
                    null,
                    textSource,
                    controller.getSelectionManager().getActiveProfile(),
                    "identity",
                    controller);
            newAnnotation.addSpan(span);
            addAnnotation(newAnnotation);
        }
    }

    @SuppressWarnings("unused")
    public void findOverlaps() {
        List<Span> overlappingSpans = new ArrayList<>();
        spanTreeSet.forEach(span -> {
            List<Span> toRemove = new ArrayList<>();
            overlappingSpans.forEach(span1 -> {
                if (span.intersects(span1)) {
                    span.getAnnotation().addOverlappingAnnotation(span1.getAnnotation());
                    span1.getAnnotation().addOverlappingAnnotation(span.getAnnotation());
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
        GraphSpace newGraphSpace = new GraphSpace(controller, textSource, title);
        graphSpaces.add(newGraphSpace);
        controller.newGraphEvent(newGraphSpace);

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
        String owlClassID;
        Annotation newAnnotation;
        Element graphSpaceElem;
        String id;
        GraphSpace graphSpace;
        for (Node annotationNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION))) {
            annotationElement = (Element) annotationNode;

            annotationID = annotationElement.getAttribute(KnowtatorXMLAttributes.ID);
            profileID = annotationElement.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
            type = annotationElement.getAttribute(KnowtatorXMLAttributes.TYPE);

            profile = controller.getProfileManager().addNewProfile(profileID);
            owlClassID = ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.ID);

            newAnnotation = new Annotation(null, owlClassID, annotationID, textSource, profile, type, controller);
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

        Map<String, Element> slotToClassIDMap = KnowtatorXMLUtil.getslotsFromXml(parent);
        Map<String, Element> classMentionToClassIDMap = KnowtatorXMLUtil.getClassIDsFromXml(parent);
        Map<Annotation, Element> annotationToSlotMap = new HashMap<>();

        for (Node annotationNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATION))) {
            Element annotationElement = (Element) annotationNode;

            Profile profile;
            try {
                String profileID = annotationElement.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATOR).item(0).getTextContent();
                profile = controller.getProfileManager().addNewProfile(profileID);
            } catch (NullPointerException npe) {
                profile = controller.getProfileManager().getDefaultProfile();
            }

            String annotationID = ((Element) annotationElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
            Element classElement = classMentionToClassIDMap.get(annotationID);

            String owlClassID = ((Element) classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);

            Annotation newAnnotation = new Annotation(null, owlClassID, annotationID, textSource, profile, "identity", controller);

            newAnnotation.readFromOldKnowtatorXML(null, annotationElement, content);

            // No need to keep annotations with no spanTreeSet
            if (!newAnnotation.getSpans().isEmpty()) {
                addAnnotation(newAnnotation);

//                log.warn("OLD KNOWTATOR: added ANNOTATION " + newAnnotation);

                for (Node slotMentionNode : KnowtatorXMLUtil.asList(classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION))) {
                    Element slotMentionElement = (Element) slotMentionNode;
                    String slotMentionID = slotMentionElement.getAttribute(OldKnowtatorXMLAttributes.ID);
                    Element slotElement = slotToClassIDMap.get(slotMentionID);
                    annotationToSlotMap.put(newAnnotation, slotElement);
                }
            }
        }

        GraphSpace oldKnowtatorGraphSpace = addGraphSpace("Old Knowtator Relations");
//        log.warn("OLD KNOWTATOR: added GRAPHSPACE " + oldKnowtatorGraphSpace);

        annotationToSlotMap.forEach((annotation, slot) -> {
            List<Object> vertices = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation);

            AnnotationNode source;
            if (vertices.isEmpty()) {
                source = oldKnowtatorGraphSpace.addNode(null, annotation);
//                log.warn("OLD KNOWTATOR: added NODE: " + source);
            } else {
                source = (AnnotationNode) vertices.get(0);
            }

            String property = ((Element) slot.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
            for (Node slotMentionValueNode : OldKnowatorUtil.asList(slot.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
                Element slotMentionValueElement = (Element) slotMentionValueNode;
                String value = slotMentionValueElement.getAttribute(OldKnowtatorXMLAttributes.VALUE);
                Annotation annotation1 = getAnnotation(value);

                List<Object> vertices1 = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation1);

                AnnotationNode target;
                if (vertices1.isEmpty()) {
                    target = oldKnowtatorGraphSpace.addNode(null, annotation1);
//                        log.warn("OLD KNOWTATOR: added NODE: " + target);
                } else {
                    target = (AnnotationNode) vertices1.get(0);
                }

                Triple triple = oldKnowtatorGraphSpace.addTriple(
                        source,
                        target,
                        null,
                        controller.getSelectionManager().getActiveProfile(),
                        property,
                        "",
                        "");
                log.warn("OLD KNOWTATOR: added TRIPLE: " + triple);
            }
        });

    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

        Profile profile = controller.getProfileManager().getDefaultProfile();

        annotationMap.get(StandoffTags.TEXTBOUNDANNOTATION).forEach(annotation -> {
            Annotation newAnnotation = new Annotation(null, annotation[1].split(StandoffTags.textBoundAnnotationTripleDelimiter)[0], annotation[0], textSource, profile, "identity", controller);
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

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    public List<GraphSpace> getGraphSpaces() {
        return graphSpaces;
    }

    public void removeGraphSpace(GraphSpace graphSpace) {
        graphSpaces.remove(graphSpace);
        controller.removeGraphEvent(graphSpace);
    }

    public Span getPreviousSpan() {
        Span selectedSpan = controller.getSelectionManager().getSelectedSpan();
        TreeSet<Span> annotationMap = textSource.getAnnotationManager().getSpanSet(null);
        Span previousSpan;
        try {
            previousSpan = annotationMap.contains(selectedSpan) ? annotationMap.lower(selectedSpan) : annotationMap.floor(selectedSpan);
        } catch (NullPointerException npe) {
            previousSpan = null;
        }
        if (previousSpan == null) previousSpan = annotationMap.last();
        return previousSpan;
    }

    public Span getNextSpan() {
        Span selectedSpan = controller.getSelectionManager().getSelectedSpan();
        TreeSet<Span> annotationMap = textSource.getAnnotationManager().getSpanSet(null);

        Span nextSpan;
        try {
            nextSpan = annotationMap.contains(selectedSpan) ? annotationMap.higher(selectedSpan) : annotationMap.ceiling(selectedSpan);
        } catch (NullPointerException npe) {
            nextSpan = null;
        }
        if (nextSpan == null) nextSpan = annotationMap.first();

        return nextSpan;
    }

    @Override
    public void projectLoaded() {
        for (Annotation annotation : annotationMap.values()) {
            annotation.setOwlClass(controller.getOWLAPIDataExtractor().getOWLClassByID(annotation.getOwlClassID()));
        }
    }
}
