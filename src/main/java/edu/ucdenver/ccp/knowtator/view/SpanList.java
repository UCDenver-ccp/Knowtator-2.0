package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationSelectionListener;
import edu.ucdenver.ccp.knowtator.model.Span;

import javax.swing.*;
import java.util.Set;

public class SpanList extends JList<Span> implements AnnotationSelectionListener {

	SpanList(KnowtatorView view) {
		view.getController().getSelectionManager().addAnnotationListener(this);

	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
		if (e.getNew() != null) {
			Set<Span> spans = e.getNew().getSpanCollection().getCollection();
			setListData(spans.toArray(new Span[0]));
		} else {
			setListData(new Span[0]);
		}
	}

}
