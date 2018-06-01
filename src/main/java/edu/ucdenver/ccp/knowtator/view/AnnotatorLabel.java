package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationSelectionListener;
import edu.ucdenver.ccp.knowtator.model.Annotation;

import javax.swing.*;

public class AnnotatorLabel extends JLabel implements AnnotationSelectionListener {


	private KnowtatorView view;

	AnnotatorLabel(KnowtatorView view) {
		this.view = view;
	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
		Annotation annotation = view.getController().getSelectionManager().getSelectedAnnotation();
		if (annotation != null) {
			setText(annotation.getAnnotator().getId());
		} else {
			setText("");
		}
	}

	public void dispose() {

	}
}
