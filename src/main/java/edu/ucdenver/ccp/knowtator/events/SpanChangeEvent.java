package edu.ucdenver.ccp.knowtator.events;

import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;

public class SpanChangeEvent extends ChangeEvent<Span> {

	public SpanChangeEvent(Span oldSpan, Span newSpan) {
		super(oldSpan, newSpan);
	}
}
