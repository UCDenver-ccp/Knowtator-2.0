package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.Annotation;

public interface AnnotationListener extends Listener {
    void annotationAdded(Annotation newAnnotation);

    void annotationRemoved(Annotation removedAnnotation);

    void annotationSelectionChanged(Annotation annotation);
}
