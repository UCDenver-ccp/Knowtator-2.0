package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.stream.Collectors;


public final class AnnotationManager {
    private static final Logger log = Logger.getLogger(KnowtatorManager.class);

    private TreeMap<Span, Annotation> annotationMap;

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
        Map.Entry<Span, Annotation> next = annotationMap.higherEntry(span);

        if (next == null) next = annotationMap.firstEntry();
        return next;


    }

    Map.Entry<Span, Annotation> getPreviousSpan(Span span) {
        Map.Entry<Span, Annotation> next = annotationMap.lowerEntry(span);

        if (next == null) {
            next = annotationMap.lastEntry();
        }

        return next;


    }

    TreeMap<Span, Annotation> getSpanMap() {
        return annotationMap;
    }
}
