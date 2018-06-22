package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotatorLabel extends JLabel implements ViewListener {


	private KnowtatorView view;

	AnnotatorLabel(KnowtatorView view) {
		this.view = view;
	}

	public void dispose() {

	}

	@Override
	public void viewChanged() {
		try {
			Annotation annotation = view.getController().getSelectionManager().getActiveTextSource().getAnnotationManager().getSelectedAnnotation();
			if (annotation != null) {
				setText(annotation.getAnnotator().getId());
			} else {
				setText("");
			}
		} catch (ActiveTextSourceNotSetException ignored) {
		}

	}
}
