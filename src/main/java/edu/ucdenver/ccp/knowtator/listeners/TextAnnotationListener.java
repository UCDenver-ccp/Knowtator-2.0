package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;

public interface TextAnnotationListener {
    void textAnnotationsChanged();

    void textAnnotationsChanged(TextAnnotation newAnnotation);
}
