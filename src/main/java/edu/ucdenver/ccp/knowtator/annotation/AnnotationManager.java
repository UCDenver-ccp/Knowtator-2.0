package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.profile.Profile;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


public final class AnnotationManager {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AnnotationManager.class);

    private TreeMap<Span, Annotation> annotationMap;
    private Set<Assertion> assertions;
    private int annotationCounter;
    private int assertionCounter;

    AnnotationManager() {
        annotationCounter = 0;
        assertionCounter = 0;
        annotationMap = new TreeMap<>(Span::compare);

        assertions = new HashSet<>();
    }

    Assertion addAssertion(String assertionID, Profile profile, Annotation source, Annotation target, String relationship) {
        if (assertionID == null) {
            assertionID = String.format("assertion_%d", assertionCounter++);
        }

        Assertion newAssertion = new Assertion(assertionID, profile, source, target, relationship);
        assertions.add(newAssertion);
        return newAssertion;
    }

    public void removeAssertion(Annotation annotation1, Annotation annotation2) {
        assertions.forEach(assertion -> {
            if (assertion.getSource().equals(annotation1) && assertion.getTarget().equals(annotation2)) {
                assertions.remove(assertion);
            }
        });
    }
    Annotation addAnnotation(String annotationID, TextSource textSource, String classID, Profile profile, String className, List<Span> spans) {

        if (annotationID == null) {
            annotationID = String.format("annotation_%d", annotationCounter++);
        }

        Annotation newAnnotation = new Annotation(annotationID, textSource, classID, profile, className);
        newAnnotation.addAllSpans(spans);



        newAnnotation.getSpans().forEach(span -> annotationMap.put(span, newAnnotation));
        return newAnnotation;
    }

    Span addSpanToAnnotation(Annotation annotation, int start, int end) {
        Span newSpan = new Span(start, end);

        annotation.addSpan(newSpan);
        annotationMap.put(newSpan, annotation);
        return newSpan;
    }

    void removeAnnotation(Annotation annotationToRemove) {
        annotationToRemove.getSpans().forEach(span -> annotationMap.remove(span));
    }

    public void removeSpanFromSelectedAnnotation(Annotation annotation, int selectionStart, int selectionEnd) {
        Span toRemove = annotation.removeSpan(selectionStart, selectionEnd);
        annotationMap.remove(toRemove);
    }

    Set<Annotation> getAnnotations(Set<Profile> profileFilters) {
        if (profileFilters == null) return new HashSet<>(annotationMap.values());
        return annotationMap.values().stream().filter(annotation -> profileFilters.contains(annotation.getAnnotator())).collect(Collectors.toSet());
    }


    Map.Entry<Span, Annotation> getNextSpan(Span span) {
        Map.Entry<Span, Annotation> next =
                annotationMap.containsKey(span) ? annotationMap.higherEntry(span) : annotationMap.ceilingEntry(span);

        if (next == null) next = annotationMap.firstEntry();
        return next;


    }

    Map.Entry<Span, Annotation> getPreviousSpan(Span span) {


        Map.Entry<Span, Annotation> previous =
                annotationMap.containsKey(span) ? annotationMap.lowerEntry(span) : annotationMap.floorEntry(span);

        if (previous == null) {
            previous = annotationMap.lastEntry();
        }

        return previous;


    }

    /**
     * @param loc  Location filter
     * @param profile  Profile filter
     *
     */
    TreeMap<Span, Annotation> getAnnotationMap(Integer loc, Profile profile) {
        TreeMap<Span, Annotation> filteredAnnotations = new TreeMap<>(Span::compare);

        for (Map.Entry<Span, Annotation> entry : annotationMap.entrySet()) {
            Span span = entry.getKey();
            Annotation annotation = entry.getValue();
            if ((loc == null || span.contains(loc)) && (profile == null || annotation.getAnnotator().equals(profile) )) {
                filteredAnnotations.put(entry.getKey(), entry.getValue());
            }
        }
        return filteredAnnotations;
    }

    public void growSpanStart(Span span) {
        if (annotationMap.containsKey(span)) {
            Annotation annotation = annotationMap.get(span);
            annotationMap.remove(span);
            span.growStart();
            annotationMap.put(span, annotation);
        } else {
            span.growStart();
        }
    }

    public void growSpanEnd(Span span, int limit) {
        if (annotationMap.containsKey(span)) {
            Annotation annotation = annotationMap.get(span);
            annotationMap.remove(span);
            span.growEnd(limit);
            annotationMap.put(span, annotation);
        } else {
            span.growEnd(limit);
        }
    }

    public void shrinkSpanEnd(Span span) {
        if (annotationMap.containsKey(span)) {
            Annotation annotation = annotationMap.get(span);
            annotationMap.remove(span);
            span.shrinkEnd();
            annotationMap.put(span, annotation);
        } else {
            span.shrinkEnd();
        }
    }

    public void shrinkSpanStart(Span span) {
        if (annotationMap.containsKey(span)) {
            Annotation annotation = annotationMap.get(span);
            annotationMap.remove(span);
            span.shrinkStart();
            annotationMap.put(span, annotation);
        } else {
            span.shrinkStart();
        }
    }

    Set<Assertion> getAssertions(Set<Profile> profileFilters) {
        return assertions.stream().filter(assertion -> profileFilters.contains(assertion.getAnnotator())).collect(Collectors.toSet());
    }

    Assertion addAssertion(String assertionID, Profile profile, String source, String target, String relationship) {
        Annotation sourceAnnotation = null, targetAnnotation = null;
        for(Annotation annotation : annotationMap.values()) {
            if (Objects.equals(annotation.getID(), source)) {
                sourceAnnotation = annotation;
            }
            if (annotation.getID().equals(target)) {
                targetAnnotation = annotation;
            }

        }

        if (sourceAnnotation != null && targetAnnotation != null) {
             return addAssertion(assertionID, profile, sourceAnnotation, targetAnnotation, relationship);
        }
        return null;
    }
}
