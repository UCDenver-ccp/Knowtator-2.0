package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.Span;

import javax.swing.*;
import java.util.Set;

public class SpanList extends JList<Span> implements SelectionListener {

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
		if (e.getNew() != null) {
			Set<Span> spans = e.getNew().getSpanCollection().getData();
			setListData(spans.toArray(new Span[0]));
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
}
