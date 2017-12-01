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

    private TreeMap<Span, ConceptAnnotation> conceptAnnotationMap;
    private Set<CompositionalAnnotation> compositionalAnnotations;
    private int annotationCounter;
    private BasicKnowtatorView view;
    private TextSource textSource;

    AnnotationManager(KnowtatorManager manager, BasicKnowtatorView view, TextSource textSource) {
        this.manager = manager;
        this.view = view;
        this.textSource = textSource;
        annotationCounter = 0;
        conceptAnnotationMap = new TreeMap<>(Span::compare);

        compositionalAnnotations = new HashSet<>();
    }

    public void removeAssertion(ConceptAnnotation annotation1, ConceptAnnotation annotation2) {
        compositionalAnnotations.forEach(assertion -> {
            if (assertion.getSource().equals(annotation1) && assertion.getTarget().equals(annotation2)) {
                compositionalAnnotations.remove(assertion);
            }
        });
    }
    public void addAnnotation(String classID, String className, List<Span> spans, String annotationID, Profile profile) {


        if (annotationID == null) {
            annotationID = String.format("annotation_%d", annotationCounter++);
        } else {
            int annotationIDIndex = Integer.parseInt(annotationID.split("annotation_")[1]);
            if (annotationIDIndex > annotationCounter) {
                annotationCounter = annotationIDIndex + 1;
            }
        }

        ConceptAnnotation newAnnotation = new ConceptAnnotation( classID, className, annotationID, textSource, profile);
        for (Span span: spans) {
            addSpanToAnnotation(newAnnotation, span);
        }
        if (view != null) view.annotationAddedEvent(newAnnotation);

    }

    public Span addSpanToAnnotation(ConceptAnnotation annotation, Span newSpan){
        annotation.addSpan(newSpan);
        conceptAnnotationMap.put(newSpan, annotation);
        return newSpan;
    }

    public void removeAnnotation(ConceptAnnotation annotationToRemove) {
        annotationToRemove.getSpans().forEach(span -> conceptAnnotationMap.remove(span));
        if(view != null) view.annotationRemovedEvent(annotationToRemove);
    }

    public void removeSpanFromAnnotation(ConceptAnnotation annotation, Span span) {
        annotation.removeSpan(span);
        conceptAnnotationMap.remove(span);
        if (view != null) view.spanRemovedEvent();
    }

    public Set<ConceptAnnotation> getConceptAnnotations(Set<Profile> profileFilters) {
        if (profileFilters == null) return new HashSet<>(conceptAnnotationMap.values());
        return conceptAnnotationMap.values().stream().filter(annotation -> profileFilters.contains(annotation.getAnnotator())).collect(Collectors.toSet());
    }




    /**
     * @param loc  Location filter
     * @param profile  Profile filter
     *
     */
    public TreeMap<Span, ConceptAnnotation> getAnnotationMap(Integer loc, Profile profile) {
        TreeMap<Span, ConceptAnnotation> filteredAnnotations = new TreeMap<>(Span::compare);

        for (Map.Entry<Span, ConceptAnnotation> entry : conceptAnnotationMap.entrySet()) {
            Span span = entry.getKey();
            ConceptAnnotation annotation = entry.getValue();
            if ((loc == null || span.contains(loc)) && (profile == null || annotation.getAnnotator().equals(profile) )) {
                filteredAnnotations.put(span, annotation);
            }
        }
        return filteredAnnotations;
    }

    public void growSpanStart(Span span) {
        if (conceptAnnotationMap.containsKey(span)) {
            ConceptAnnotation annotation = conceptAnnotationMap.get(span);
            conceptAnnotationMap.remove(span);
            span.growStart();
            conceptAnnotationMap.put(span, annotation);
        } else {
            span.growStart();
        }
    }

    public void growSpanEnd(Span span, int limit) {
        if (conceptAnnotationMap.containsKey(span)) {
            ConceptAnnotation annotation = conceptAnnotationMap.get(span);
            conceptAnnotationMap.remove(span);
            span.growEnd(limit);
            conceptAnnotationMap.put(span, annotation);
        } else {
            span.growEnd(limit);
        }
    }

    public void shrinkSpanEnd(Span span) {
        if (conceptAnnotationMap.containsKey(span)) {
            ConceptAnnotation annotation = conceptAnnotationMap.get(span);
            conceptAnnotationMap.remove(span);
            span.shrinkEnd();
            conceptAnnotationMap.put(span, annotation);
        } else {
            span.shrinkEnd();
        }
    }

    public void shrinkSpanStart(Span span) {
        if (conceptAnnotationMap.containsKey(span)) {
            ConceptAnnotation annotation = conceptAnnotationMap.get(span);
            conceptAnnotationMap.remove(span);
            span.shrinkStart();
            conceptAnnotationMap.put(span, annotation);
        } else {
            span.shrinkStart();
        }
    }

    public Set<CompositionalAnnotation> getCompositionalAnnotations(Set<Profile> profileFilters) {
        return compositionalAnnotations.stream().filter(assertion -> profileFilters.contains(assertion.getAnnotator())).collect(Collectors.toSet());
    }

    public void addCompositionalAnnotation(String graphTitle, String source, String target, String relationship, String assertionID, Profile profile) {
        ConceptAnnotation sourceAnnotation = null, targetAnnotation = null;
        for(ConceptAnnotation annotation : conceptAnnotationMap.values()) {
            if (Objects.equals(annotation.getID(), source)) {
                sourceAnnotation = annotation;
            }
            if (annotation.getID().equals(target)) {
                targetAnnotation = annotation;
            }

        }

        if (sourceAnnotation != null && targetAnnotation != null) {
            addCompositionalAnnotation(graphTitle, sourceAnnotation, targetAnnotation, relationship, assertionID, profile);
        }
    }

    public void addCompositionalAnnotation(String graphTitle, Annotation source, Annotation target, String relationship, String assertionID, Profile profile) {
        if (assertionID == null) {
            assertionID = String.format("annotation_%d", annotationCounter++);
        }

        CompositionalAnnotation newCompositionalAnnotation = new CompositionalAnnotation(graphTitle, source, target, relationship, assertionID, textSource, profile);
        compositionalAnnotations.add(newCompositionalAnnotation);
        if(view != null) view.assertionAddedEvent(newCompositionalAnnotation);
    }

    //TODO: Add annotations as subclasses of their assigned classes as well as of the AO
    public void addAnnotation(Span span) {
        String classID = OWLAPIDataExtractor.getSelectedOwlClassID(view);
        String className = OWLAPIDataExtractor.getSelectedOwlClassName(view);
        List<Span> spans = Collections.singletonList(span);
        if (classID != null) {
            addAnnotation(classID, className, spans, null, manager.getProfileManager().getCurrentProfile());
        }
    }

    public void setView(BasicKnowtatorView view) {
        this.view = view;
    }
}
