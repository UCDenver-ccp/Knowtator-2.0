package edu.ucdenver.ccp.knowtator.model.text.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.*;
import edu.ucdenver.ccp.knowtator.listeners.OWLSetupListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.collection.AnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.SpanCollection;
import edu.ucdenver.ccp.knowtator.model.owl.OWLClassNotFoundException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.selection.SelectionModel;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.Triple;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class AnnotationManager extends AnnotationCollection implements OWLSetupListener, OWLOntologyChangeListener, ProjectListener, KnowtatorXMLIO, BratStandoffIO, KnowtatorManager {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AnnotationManager.class);

    private final KnowtatorController controller;

    private SpanCollection allSpanCollection;
    private TextSource textSource;

    public AnnotationManager(KnowtatorController controller, TextSource textSource) {
        this.controller = controller;
        this.textSource = textSource;

        controller.getOWLManager().addOWLSetupListener(this);
        controller.addProjectListener(this);

        annotationCollection = new AnnotationCollection(controller);
        allSpanCollection = new SpanCollection(controller);
    }

    /*
    ADDERS
     */

    public Annotation addAnnotation(String annotationID, OWLClass owlClass, String owlClassID, String owlClassLabel, Profile annotator, String annotation_type) {
        Annotation newAnnotation = new Annotation(controller, annotationID, owlClass, owlClassID, owlClassLabel, annotator, annotation_type, textSource);
        annotationCollection.add(newAnnotation);
        allSpanCollection.getCollection().addAll(newAnnotation.getSpanManager().getSpans().getCollection());
        setSelectedAnnotation(newAnnotation, null);
        return newAnnotation;
    }

    public void addSelectedAnnotation() {
        OWLEntity owlClass = controller.getSelectionManager().getSelectedOWLEntity();
        if (owlClass instanceof OWLClass) {
            Profile annotator = controller.getProfileManager().getSelection();
            int start = controller.getSelectionManager().getStart();
            int end = controller.getSelectionManager().getEnd();


            try {
                String owlClassID = controller.getOWLManager().getOWLEntityRendering(owlClass);
                Annotation newAnnotation = addAnnotation(null, (OWLClass) owlClass, owlClassID, null, annotator, "identity");
                newAnnotation.getSpanManager().addSpan(null, start, end);

                textSource.save();
            } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
                e.printStackTrace();
            }
        }

    }

    public void addSpanToSelectedAnnotation() {
        getSelection().getSpanManager().addSpan(null,
                controller.getSelectionManager().getStart(),
                controller.getSelectionManager().getEnd());
        textSource.save();
    }

    /*
    REMOVERS
     */

    public void removeAnnotation(Annotation annotationToRemove) {
        annotationCollection.remove(annotationToRemove);
        for (Span span : annotationToRemove.getSpanManager().getSpans()) {
            allSpanCollection.remove(span);
        }

        setSelectedAnnotation(null, null);
    }

    public void removeSelectedAnnotation() {
        textSource.getGraphSpaceManager().removeAnnotation(getSelection());
        removeAnnotation(getSelection());
        setSelection(null);
        textSource.save();
    }

    public void removeSpanFromAnnotation(Annotation annotation, Span span) {
        annotation.getSpanManager().removeSpan(span);
        allSpanCollection.remove(span);

    }

    public void removeSpanFromSelectedAnnotation() {
        Annotation annotation = getSelection();
        removeSpanFromAnnotation(annotation, annotation.getSpanManager().getSelection());
        textSource.save();
    }

    /*
    MODIFIERS
     */

    public void growSelectedSpanStart() {
        Span span = getSelection().getSpanManager().getSelection();
        allSpanCollection.remove(span);
        span.growStart();
        allSpanCollection.add(span);

        textSource.save();
    }

    public void growSelectedSpanEnd() {
        Span span = getSelection().getSpanManager().getSelection();
        allSpanCollection.remove(span);
        span.growEnd(textSource.getContent().length());
        allSpanCollection.add(span);

        textSource.save();
    }

    public void shrinkSelectedSpanEnd() {
        Span span = getSelection().getSpanManager().getSelection();
        allSpanCollection.remove(span);
        span.shrinkEnd();
        allSpanCollection.add(span);

        textSource.save();
    }

    public void shrinkSelectedSpanStart() {
        Span span = getSelection().getSpanManager().getSelection();
        allSpanCollection.remove(span);
        span.shrinkStart();
        allSpanCollection.add(span);

        textSource.save();
    }


    /*
    GETTERS
     */

    public AnnotationCollection getAnnotations() {
        return annotationCollection;
    }

    private AnnotationCollection getAnnotations(OWLClass owlClass) {
        AnnotationCollection annotationsForOwlClass = new AnnotationCollection(controller);

        try {
            String owlClassID = controller.getOWLManager().getOWLEntityRendering(owlClass);

            for (Annotation annotation : annotationCollection) {
                if (annotation.getOwlClass() == owlClass) {
                    annotationsForOwlClass.add(annotation);
                } else if (annotation.getOwlClassID().equals(owlClassID)) {
                    annotation.setOwlClass(owlClass);
                    annotationsForOwlClass.add(annotation);
                }
            }
        } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
            e.printStackTrace();
        }

        return annotationsForOwlClass;
    }

    /**
     * @param loc Location filter
     */
    public TreeSet<Span> getSpans(Integer loc, int start, int end) {
        Supplier<TreeSet<Span>> supplier = () -> new TreeSet<>(Span::compare);

        Set<OWLClass> activeOWLClassDescendents = new HashSet<>();
        if (controller.getSelectionManager().isFilterByOWLClass()) {
            try {
                activeOWLClassDescendents.addAll(controller.getOWLManager().getDescendants((OWLClass) controller.getSelectionManager().getSelectedOWLEntity()));
                activeOWLClassDescendents.add((OWLClass) controller.getSelectionManager().getSelectedOWLEntity());
            } catch (OWLWorkSpaceNotSetException ignored) {
            }
        }


        return allSpanCollection
                .stream()
                .filter(
                        span ->
                                (loc == null || span.contains(loc))
                                        && (start <= span.getStart() && span.getEnd() <= end)
                                        && (!controller.getSelectionManager().isFilterByOWLClass() || activeOWLClassDescendents.contains(span.getAnnotation().getOwlClass()))
                                        && (!controller.getSelectionManager().isFilterByProfile() || span.getAnnotation().getAnnotator().equals(controller.getProfileManager().getSelection())))
                .collect(Collectors.toCollection(supplier));
    }

    public TreeSet<Annotation> getAnnotations(int start, int end) {
        Supplier<TreeSet<Annotation>> supplier = () -> new TreeSet<>(Annotation::compare);
        return annotationCollection
                .stream()
                .filter(annotation -> (annotation.contains(start) && annotation.contains(end)))
                .collect(Collectors.toCollection(supplier));
    }

    @SuppressWarnings("unused")
    public void findOverlaps() {
        List<Span> overlappingSpans = new ArrayList<>();
        allSpanCollection.forEach(
                span -> {
                    List<Span> toRemove = new ArrayList<>();
                    overlappingSpans.forEach(
                            span1 -> {
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
        return annotationCollection.get(annotationID);
    }

    public void getNextSpan() {
        Span nextSpan = allSpanCollection.getNext(getSelection().getSpanManager().getSelection());
        setSelection(nextSpan.getAnnotation());
        nextSpan.getAnnotation().getSpanManager().setSelection(nextSpan);
    }

    public void getPreviousSpan() {
        Span previousSpan = allSpanCollection.getPrevious(getSelection().getSpanManager().getSelection());
        setSelection(previousSpan.getAnnotation());
        previousSpan.getAnnotation().getSpanManager().setSelection(previousSpan);

    }

    /*
    SETTERS
     */

    public void setSelectedAnnotation(Annotation newAnnotation, Span newSpan) {

        if (getSelection() != newAnnotation) {
            setSelection(newAnnotation);
            if (newAnnotation != null) {
                newAnnotation.getSpanManager().setSelection(newSpan);
                controller.getSelectionManager().setSelectedOWLEntity(newAnnotation.getOwlClass());
            }
        }

        controller.refreshView();
    }

    /*
    SETUP
     */

    @Override
    public void owlSetup() {
        try {
            controller.getOWLManager().getWorkSpace().getOWLModelManager().addOntologyChangeListener(this);
            for (Annotation annotation : annotationCollection) {
                annotation.setOwlClass(controller.getOWLManager().getOWLClassByID(annotation.getOwlClassID()));
            }
        } catch (OWLClassNotFoundException | OWLWorkSpaceNotSetException ignored) {

        }
    }

    /*
    WRITERS
     */

    @Override
    public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
        annotationCollection.forEach(
                annotation -> annotation.writeToKnowtatorXML(dom, textSourceElement));
    }

    @Override
    public void writeToBratStandoff(
            Writer writer,
            Map<String, Map<String, String>> annotationsConfig,
            Map<String, Map<String, String>> visualConfig)
            throws IOException {
        Iterator<Annotation> annotationIterator = annotationCollection.iterator();
        for (int i = 0; i < annotationCollection.size(); i++) {
            Annotation annotation = annotationIterator.next();
            annotation.setBratID(String.format("T%d", i));

            annotation.writeToBratStandoff(writer, annotationsConfig, visualConfig);
        }

        // Not adding relations due to complexity of relation types in Brat Standoff
    /*int lastNumTriples = 0;
    for (GraphSpace graphSpace : graphSpaceCollection) {
      Object[] edges = graphSpace.getChildEdges(graphSpace.getDefaultParent());
      int bound = edges.length;
      for (int i = 0; i < bound; i++) {
        Object edge = edges[i];
        Triple triple = (Triple) edge;
        triple.setBratID(String.format("R%d", lastNumTriples + i));
        String propertyID;
        try {
          propertyID =
              controller.getOWLAPIDataExtractor().getOWLEntityRendering(triple.getProperty());
        } catch (OWLEntityNullException | OWLWorkSpaceNotSetException e) {
          propertyID = triple.getValue().toString();
        }
        writer.append(
            String.format(
                "%s\t%s Arg1:%s Arg2:%s\n",
                triple.getBratID(),
                propertyID,
                ((AnnotationNode) triple.getSource()).getAnnotation().getBratID(),
                ((AnnotationNode) triple.getTarget()).getAnnotation().getBratID()));
      }
    }*/
    }

    /*
    READERS
     */

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        for (Node annotationNode :
                KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION))) {
            Element annotationElement = (Element) annotationNode;

            String annotationID = annotationElement.getAttribute(KnowtatorXMLAttributes.ID);
            String profileID = annotationElement.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
            String type = annotationElement.getAttribute(KnowtatorXMLAttributes.TYPE);

            Profile profile = controller.getProfileManager().getProfile(profileID);
            profile = profile == null ? controller.getProfileManager().getDefaultProfile() : profile;
            String owlClassID =
                    ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0))
                            .getAttribute(KnowtatorXMLAttributes.ID);
            String owlClassLabel =
                    ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0))
                            .getAttribute(KnowtatorXMLAttributes.LABEL);

            Annotation newAnnotation = addAnnotation(annotationID, null, owlClassID, owlClassLabel, profile, type);
            newAnnotation.readFromKnowtatorXML(null, annotationElement);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {

        Map<String, Element> slotToClassIDMap = KnowtatorXMLUtil.getslotsFromXml(parent);
        Map<String, Element> classMentionToClassIDMap = KnowtatorXMLUtil.getClassIDsFromXml(parent);
        Map<Annotation, Element> annotationToSlotMap = new HashMap<>();

        for (Node annotationNode :
                KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATION))) {
            Element annotationElement = (Element) annotationNode;

            Profile profile;
            try {
                String profileID =
                        annotationElement
                                .getElementsByTagName(OldKnowtatorXMLTags.ANNOTATOR)
                                .item(0)
                                .getTextContent();
                profile = controller.getProfileManager().addProfile(profileID);
            } catch (NullPointerException npe) {
                try {
                    String profileID = annotationElement.getAttribute(OldKnowtatorXMLAttributes.ANNOTATOR);
                    profile = controller.getProfileManager().addProfile(profileID);
                } catch (NullPointerException npe2) {
                    profile = controller.getProfileManager().getDefaultProfile();
                }
            }

            String annotationID =
                    ((Element) annotationElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION).item(0))
                            .getAttribute(OldKnowtatorXMLAttributes.ID);
            Element classElement = classMentionToClassIDMap.get(annotationID);

            String owlClassID =
                    ((Element) classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0))
                            .getAttribute(OldKnowtatorXMLAttributes.ID);
            String owlClassName =
                    classElement
                            .getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS)
                            .item(0)
                            .getTextContent();

            Annotation newAnnotation = addAnnotation(annotationID, null, owlClassID, owlClassName, profile, "identity");

            newAnnotation.readFromOldKnowtatorXML(null, annotationElement);

            // No need to keep annotations with no allSpanCollection
            if (newAnnotation.getSpanManager().getSpans().getCollection().isEmpty()) {
                removeAnnotation(newAnnotation);
            } else {
                for (Node slotMentionNode :
                        KnowtatorXMLUtil.asList(
                                classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION))) {
                    Element slotMentionElement = (Element) slotMentionNode;
                    String slotMentionID = slotMentionElement.getAttribute(OldKnowtatorXMLAttributes.ID);
                    Element slotElement = slotToClassIDMap.get(slotMentionID);
                    if (slotElement != null) {
                        annotationToSlotMap.put(newAnnotation, slotElement);
                    }
                }
            }
        }


        GraphSpace oldKnowtatorGraphSpace = new GraphSpace(controller, textSource, "Old Knowtator Relations");
        textSource.getGraphSpaceManager().addGraphSpace(oldKnowtatorGraphSpace);

        annotationToSlotMap.forEach(
                (annotation, slot) -> {
                    List<Object> vertices = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation);

                    AnnotationNode source = oldKnowtatorGraphSpace.makeOrGetAnnotationNode(annotation, vertices);

                    String propertyID =
                            ((Element) slot.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0))
                                    .getAttribute(OldKnowtatorXMLAttributes.ID);
                    for (Node slotMentionValueNode :
                            OldKnowatorUtil.asList(
                                    slot.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
                        Element slotMentionValueElement = (Element) slotMentionValueNode;
                        String value = slotMentionValueElement.getAttribute(OldKnowtatorXMLAttributes.VALUE);
                        Annotation annotation1 = textSource.getAnnotationManager().getAnnotation(value);

                        List<Object> vertices1 = oldKnowtatorGraphSpace.getVerticesForAnnotation(annotation1);


                        AnnotationNode target = oldKnowtatorGraphSpace.makeOrGetAnnotationNode(annotation1, vertices1);

                        oldKnowtatorGraphSpace.addTriple(
                                source,
                                target,
                                null,
                                controller.getProfileManager().getSelection(),
                                null,
                                propertyID,
                                "",
                                "",
                                false);
                    }
                });


    }

    @Override
    public void readFromBratStandoff(
            File file, Map<Character, List<String[]>> annotationCollection, String content) {

        Profile profile = controller.getProfileManager().getDefaultProfile();

        annotationCollection
                .get(StandoffTags.TEXTBOUNDANNOTATION)
                .forEach(
                        annotation -> {
                            Annotation newAnnotation = addAnnotation(annotation[0], null,
                                    annotation[1].split(StandoffTags.textBoundAnnotationTripleDelimiter)[0],
                                    null, profile, "identity");
                            Map<Character, List<String[]>> map = new HashMap<>();
                            List<String[]> list = new ArrayList<>();
                            list.add(annotation);
                            map.put(StandoffTags.TEXTBOUNDANNOTATION, list);
                            newAnnotation.readFromBratStandoff(null, map, content);
                        });

        annotationCollection
                .get(StandoffTags.NORMALIZATION)
                .forEach(
                        normalizaion -> {
                            String[] splitNormalization =
                                    normalizaion[1].split(StandoffTags.relationTripleDelimiter);
                            Annotation annotation = getAnnotation(splitNormalization[1]);
                            annotation.setOWLClassID(splitNormalization[2]);
                        });

        GraphSpace newGraphSpace = new GraphSpace(controller, textSource, "Brat Relation Graph");
        textSource.getGraphSpaceManager().addGraphSpace(newGraphSpace);
        newGraphSpace.readFromBratStandoff(null, annotationCollection, null);
    }


    @Override
    public void dispose() {
        super.dispose();
        annotationCollection.forEach(Annotation::dispose);
        annotationCollection.getCollection().clear();
        try {
            controller.getOWLManager().getWorkSpace().getOWLModelManager().removeOntologyChangeListener(this);
        } catch (OWLWorkSpaceNotSetException ignored) {
        }
    }

    @Override
    public void makeDirectory() {

    }

    @Override
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {
        Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
        Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
        OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
        OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

        Triple.processOntologyChanges(changes, addedCollector, removedCollector);

    /*
    For now, I will assume that entity removed is the one that existed and the one
    that is added is the new name for it.
     */
        if (!possiblyAddedEntities.isEmpty() && !possiblyRemovedEntities.isEmpty()) {
            OWLEntity oldOWLClass = possiblyRemovedEntities.iterator().next();
            OWLEntity newOWLClass = possiblyAddedEntities.iterator().next();

//            try {
//                log.warn(String.format("Old: %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(oldOWLClass)));
//                log.warn(String.format("New: %s", controller.getOWLAPIDataExtractor().getOWLEntityRendering(newOWLClass)));
//            } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
//                e.printStackTrace();
//            }

            AnnotationCollection annotationsForOwlClass = getAnnotations((OWLClass) oldOWLClass);
            for (Annotation annotation : annotationsForOwlClass) {
                annotation.setOwlClass((OWLClass) newOWLClass);
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

    public void reassignSelectedOWLClassToSelectedAnnotation() {
        OWLEntity selectedOWLEntity = controller.getSelectionManager().getSelectedOWLEntity();
        if (selectedOWLEntity instanceof OWLClass) {
            getSelection().setOwlClass((OWLClass) selectedOWLEntity);
            controller.refreshView();
        }
    }

    SpanCollection getAllSpanCollection() {
        return allSpanCollection;
    }
}
