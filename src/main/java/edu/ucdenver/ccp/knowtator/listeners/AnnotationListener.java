package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;

public interface AnnotationListener extends Listener {
    void annotationAdded(ConceptAnnotation newAnnotation);

    void annotationRemoved(ConceptAnnotation removedAnnotation);

    void annotationSelectionChanged(ConceptAnnotation annotation);
}
