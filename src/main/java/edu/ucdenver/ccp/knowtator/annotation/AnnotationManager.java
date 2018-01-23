package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


public final class AnnotationManager {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AnnotationManager.class);

    private final KnowtatorManager manager;

    private TreeMap<Span, ConceptAnnotation> spanMap;
    private BasicKnowtatorView view;
    private TextSource textSource;
    private Map<String, Annotation> annotationMap;

    AnnotationManager(KnowtatorManager manager, BasicKnowtatorView view, TextSource textSource) {
        this.manager = manager;
        this.view = view;
        this.textSource = textSource;
        annotationMap = new HashMap<>();
        spanMap = new TreeMap<>(Span::compare);

    }

    public void addConceptAnnotation(ConceptAnnotation newAnnotation, List<Span> spans) {

        for (Span span: spans) {
            addSpanToConceptAnnotation(newAnnotation, span);
        }
        addAnnotation(newAnnotation);

        if (view != null) view.conceptAnnotationAddedEvent(newAnnotation);

    }

    private void addAnnotation(Annotation newAnnotation) {
        String id = newAnnotation.getID();
        if (id == null) {
            id = String.format("annotation_%d", annotationMap.size());
        }

        while(annotationMap.containsKey(id)) {
            int annotationIDIndex = Integer.parseInt(id.split("annotation_")[1]);
            id = String.format("annotation_%d", ++annotationIDIndex);
        }
        newAnnotation.setID(id);
        annotationMap.put(newAnnotation.getID(), newAnnotation);

    }

    public void addSpanToConceptAnnotation(ConceptAnnotation annotation, Span newSpan){
        annotation.addSpan(newSpan);
        spanMap.put(newSpan, annotation);
        if (view != null) view.spanAddedEvent(newSpan);
    }

    private void removeConceptAnnotationFromSpanMap(ConceptAnnotation annotationToRemove) {
        annotationToRemove.getSpans().forEach(span -> spanMap.remove(span));
        if(view != null) view.annotationRemovedEvent(annotationToRemove);
    }

    public void removeAnnotation(String annotationToRemoveID) {
        Annotation annotationToRemove = annotationMap.get(annotationToRemoveID);
        annotationMap.remove(annotationToRemoveID);
        if (annotationToRemove instanceof ConceptAnnotation) removeConceptAnnotationFromSpanMap((ConceptAnnotation) annotationToRemove);
    }

    public void removeSpanFromConceptAnnotation(ConceptAnnotation annotation, Span span) {
        annotation.removeSpan(span);
        spanMap.remove(span);
        if (view != null) view.spanRemovedEvent();
    }

    public Collection<Annotation> getAnnotations(Set<Profile> profileFilters) {
        if (profileFilters == null) return annotationMap.values();
        return annotationMap.values().stream().filter(annotation -> profileFilters.contains(annotation.getAnnotator())).collect(Collectors.toSet());
    }

    public Set<ConceptAnnotation> getAllConceptAnnotations() {
        return annotationMap.values().stream().filter(annotation -> annotation instanceof ConceptAnnotation).map(annotation -> (ConceptAnnotation) annotation).collect(Collectors.toSet());
    }

    /**
     * @param loc  Location filter
     * @param profile  Profile filter
     *
     */
    public TreeMap<Span, ConceptAnnotation> getSpanMap(Integer loc, Profile profile) {
        TreeMap<Span, ConceptAnnotation> filteredAnnotations = new TreeMap<>(Span::compare);

        for (Map.Entry<Span, ConceptAnnotation> entry : spanMap.entrySet()) {
            Span span = entry.getKey();
            ConceptAnnotation annotation = entry.getValue();
            if ((loc == null || span.contains(loc)) && (profile == null || annotation.getAnnotator().equals(profile) )) {
                filteredAnnotations.put(span, annotation);
            }
        }
        return filteredAnnotations;
    }

    public void growSpanStart(Span span) {
        if (spanMap.containsKey(span)) {
            ConceptAnnotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.growStart();
            spanMap.put(span, annotation);
        } else {
            span.growStart();
        }
    }

    public void growSpanEnd(Span span, int limit) {
        if (spanMap.containsKey(span)) {
            ConceptAnnotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.growEnd(limit);
            spanMap.put(span, annotation);
        } else {
            span.growEnd(limit);
        }
    }

    public void shrinkSpanEnd(Span span) {
        if (spanMap.containsKey(span)) {
            ConceptAnnotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.shrinkEnd();
            spanMap.put(span, annotation);
        } else {
            span.shrinkEnd();
        }
    }

    public void shrinkSpanStart(Span span) {
        if (spanMap.containsKey(span)) {
            ConceptAnnotation annotation = spanMap.get(span);
            spanMap.remove(span);
            span.shrinkStart();
            spanMap.put(span, annotation);
        } else {
            span.shrinkStart();
        }
    }

    public void addCompositionalAnnotation(String graphTitle, String sourceAnnotationID, String targetAnnotationID, String relationship, String annotationID, Profile profile) {
        if (annotationMap.containsKey(sourceAnnotationID) && annotationMap.containsKey(targetAnnotationID)) {
            CompositionalAnnotation newCompositionalAnnotation = new CompositionalAnnotation(graphTitle, sourceAnnotationID, targetAnnotationID, relationship, annotationID, textSource, profile);
            addAnnotation(newCompositionalAnnotation);
            if (view != null) view.compositionalAnnotationAddedEvent(newCompositionalAnnotation);
        }
    }

    //TODO: Add annotations as subclasses of their assigned classes as well as of the AO
    public void addConceptAnnotation(Span span) {
        String classID = OWLAPIDataExtractor.getSelectedOwlClassID(view);
        String className = OWLAPIDataExtractor.getSelectedOwlClassName(view);
        List<Span> spans = Collections.singletonList(span);
        if (classID != null) {
            addConceptAnnotation(new ConceptAnnotation(classID, className, null, textSource, manager.getProfileManager().getCurrentProfile()), spans);
        }
    }

    public void findOverlaps() {
        Map<Span, ConceptAnnotation> overlappingSpans = new HashMap<>();
        spanMap.forEach((span, annotation) -> {
            List<Span> toRemove = new ArrayList<>();
            overlappingSpans.forEach((span1, annotation1) -> {
                if (span.intersects(span1)) {
                    annotation.addOverlappingAnnotation(
                            annotation1.getID());
                    annotation1.addOverlappingAnnotation(annotation.getID());
                } else {
                    toRemove.add(span1);
                }
            });
            overlappingSpans.keySet().removeAll(toRemove);

            overlappingSpans.put(span, annotation);
        });
    }

    public void setView(BasicKnowtatorView view) {
        this.view = view;
    }

    public Annotation getAnnotation(String annotationID) {
        return annotationMap.get(annotationID);
    }
}
