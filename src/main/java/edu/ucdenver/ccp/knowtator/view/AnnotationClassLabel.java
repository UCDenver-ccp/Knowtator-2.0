package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationSelectionListener;
import edu.ucdenver.ccp.knowtator.model.Annotation;

import javax.swing.*;

public class AnnotationClassLabel extends JLabel implements AnnotationSelectionListener {


	private KnowtatorView view;

	AnnotationClassLabel(KnowtatorView view) {
		this.view = view;
		view.getController().getSelectionManager().addAnnotationListener(this);
	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
		Annotation annotation = view.getController().getSelectionManager().getSelectedAnnotation();
		if (annotation != null) {
			setText(annotation.getOwlClassID());
		}
	}
}
