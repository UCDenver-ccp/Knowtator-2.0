package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;

public interface AnnotationListener extends Listener {
    void annotationAdded(Annotation newAnnotation);

    void annotationRemoved(Annotation removedAnnotation);

    void annotationSelectionChanged(Annotation annotation);
}
