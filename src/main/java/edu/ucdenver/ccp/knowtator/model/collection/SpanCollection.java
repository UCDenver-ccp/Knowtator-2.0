package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.SpanCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;

import java.util.TreeSet;

public class SpanCollection extends CyclableCollection<Span, SpanCollectionListener> {
	public SpanCollection(KnowtatorController controller) {
		super(controller, new TreeSet<>());
	}
}
