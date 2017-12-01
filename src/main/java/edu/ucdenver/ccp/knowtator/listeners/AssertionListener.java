package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.CompositionalAnnotation;

public interface AssertionListener extends Listener {
    void assertionAdded(CompositionalAnnotation compositionalAnnotation);
}
