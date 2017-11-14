package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;

public interface AnnotationListener {
    void annotationsChanged(Annotation annotation);
}
