package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

public class AnnotationActions {

    public static void removeSpan(TextSource textSource, ConceptAnnotation annotation, Span span) {
        textSource.getAnnotationManager().removeSpanFromAnnotation(annotation, span);
    }

    public static void addSpan(BasicKnowtatorView view, TextSource textSource, ConceptAnnotation annotation, Span span) {
        Span newSpan = textSource.getAnnotationManager().addSpanToAnnotation(annotation, span);
        view.spanAddedEvent(newSpan);
    }

    public static void addSelectedAnnotation(TextSource textSource, Span span) {
        textSource.getAnnotationManager().addAnnotation(span);
    }

}
