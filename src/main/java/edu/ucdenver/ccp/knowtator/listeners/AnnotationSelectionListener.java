package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;

public interface AnnotationSelectionListener {
	void selectedAnnotationChanged(AnnotationChangeEvent e);
}
