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

    AnnotationManager() {

        annotationMap = new TreeMap<>((span1, span2) -> {
            if (span1 == null) {
                return span2 == null ? 0 : -1;
            } else if (span2 == null) {
                return 1;
            }
            int compare = span1.getStart().compareTo(span2.getStart());
            if (compare == 0) {
                compare = span1.getEnd().compareTo(span2.getEnd());
            }
            return compare;
        });

        assertions = new HashSet<>();
    }

    public void addAssertion(Annotation source, Annotation target, String relationship) {
        assertions.add(new Assertion(source, target, relationship));
    }

    public void removeAssertion(Annotation annotation1, Annotation annotation2) {
        assertions.forEach(assertion -> {
            if (assertion.getSource().equals(annotation1) && assertion.getTarget().equals(annotation2)) {
                assertions.remove(assertion);
            }
        });
    }
    Annotation addAnnotation(TextSource textSource, Profile profile, String className, String classID, List<Span> spans) {


        Annotation newAnnotation = new Annotation(textSource, profile, className, classID);
        newAnnotation.addAllSpans(spans);



        newAnnotation.getSpans().forEach(span -> annotationMap.put(span, newAnnotation));
        return newAnnotation;
    }

    void addSpanToAnnotation(Annotation annotation, int start, int end) {
        Span newSpan = new Span(start, end);

        annotation.addSpan(newSpan);
        annotationMap.put(newSpan, annotation);
    }

    void removeAnnotation(Annotation annotationToRemove) {
        annotationToRemove.getSpans().forEach(span -> annotationMap.remove(span));
    }

    public void removeSpanFromSelectedAnnotation(Annotation annotation, int selectionStart, int selectionEnd) {
        Span toRemove = annotation.removeSpan(selectionStart, selectionEnd);
        annotationMap.remove(toRemove);
    }

    HashSet<Annotation> getAnnotations() {
        return new HashSet<>(annotationMap.values());
    }

    public Map<Span, Annotation> getAnnotationsContainingLocation(Integer loc) {
        return annotationMap.entrySet().stream()
                .filter(map -> map.getKey().contains(loc))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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

    public TreeMap<Span, Annotation> getAnnotationMap() {
        return annotationMap;
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

    Set<Assertion> getAssertions() {
        return assertions;
    }
}
