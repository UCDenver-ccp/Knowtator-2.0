package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.listeners.SpanCollectionListener;
import edu.ucdenver.ccp.knowtator.model.Span;

import java.util.TreeSet;

public class SpanCollection extends CyclableCollection<Span, SpanCollectionListener> {
	public SpanCollection() {
		super(new TreeSet<>(Span::compare));
	}
}
