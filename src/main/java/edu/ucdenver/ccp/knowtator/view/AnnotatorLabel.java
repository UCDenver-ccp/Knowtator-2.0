package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.Annotation;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;

public class AnnotatorLabel extends JLabel implements SelectionListener {
	private KnowtatorController controller;

	AnnotatorLabel(KnowtatorController controller) {
		this.controller = controller;
	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
		Annotation annotation = controller.getSelectionManager().getSelectedAnnotation();
		if (annotation != null) {
			setText(annotation.getAnnotator().getId());
		}
	}

	@Override
	public void selectedSpanChanged(SpanChangeEvent e) {
	}

	@Override
	public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {
	}

	@Override
	public void activeTextSourceChanged(TextSourceChangeEvent e) {
	}

	@Override
	public void activeProfileChange(ProfileChangeEvent e) {
	}

	@Override
	public void owlPropertyChangedEvent(OWLObjectProperty value) {

	}
}
