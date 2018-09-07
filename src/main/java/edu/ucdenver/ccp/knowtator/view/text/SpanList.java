package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import java.util.Set;

public class SpanList extends JList<Span> implements ViewListener {

	private KnowtatorView view;

	SpanList(KnowtatorView view) {
		this.view = view;
		view.getController().addViewListener(this);
	}

	public void dispose() {

	}

	@Override
	public void viewChanged() {
		try {
			if (view.getController().getSelectionManager().getActiveTextSource().getAnnotationManager().getSelectedAnnotation() != null) {
				Set<Span> spans = view.getController().getSelectionManager().getActiveTextSource().getAnnotationManager().getSelectedAnnotation().getSpanCollection().getCollection();
				setListData(spans.toArray(new Span[0]));
			} else {
				setListData(new Span[0]);
			}
		} catch (ActiveTextSourceNotSetException ignored) {

		}
	}
}
