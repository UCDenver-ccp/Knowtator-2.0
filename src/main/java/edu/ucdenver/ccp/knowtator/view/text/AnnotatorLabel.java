package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotatorLabel extends JLabel implements ViewListener {


	private KnowtatorView view;

	AnnotatorLabel(KnowtatorView view) {
		this.view = view;
		view.getController().addViewListener(this);
	}

	public void dispose() {

	}

	@Override
	public void viewChanged() {
		Annotation annotation = view.getController()
				.getTextSourceManager().getSelection()
				.getAnnotationManager().getSelection();
		if (annotation != null) {
			setText(annotation.getAnnotator().getId());
		} else {
			setText("");
		}

	}
}
