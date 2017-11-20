package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.annotation.Span;

public interface SpanListener extends Listener {

    void spanAdded(Span newSpan);

    void spanRemoved();

    void spanSelectionChanged(Span span);
}