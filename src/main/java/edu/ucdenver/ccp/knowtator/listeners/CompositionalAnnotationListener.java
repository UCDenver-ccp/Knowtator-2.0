package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.CompositionalAnnotation;

public interface CompositionalAnnotationListener extends Listener {
    void compositionalAnnotationAdded(CompositionalAnnotation compositionalAnnotation);
}
