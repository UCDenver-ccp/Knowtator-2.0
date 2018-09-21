package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.selection.SelectionModel;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;

import java.util.TreeSet;

public class SpanCollection extends SelectionModel<Span, SpanCollectionListener> {
	public SpanCollection(KnowtatorController controller) {
		super(controller, new TreeSet<>());
	}
}
