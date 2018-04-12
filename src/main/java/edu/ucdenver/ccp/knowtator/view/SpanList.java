package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.Span;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.util.Set;

public class SpanList extends JList<Span> implements SelectionListener {

	SpanList(KnowtatorView view) {
		view.getController().getSelectionManager().addListener(this);

	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
		if (e.getNew() != null) {
			Set<Span> spans = e.getNew().getSpanCollection().getCollection();
			setListData(spans.toArray(new Span[0]));
		}
	}

	@Override
	public void selectedSpanChanged(SpanChangeEvent e) {
		setSelectedValue(e.getNew(), true);
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
