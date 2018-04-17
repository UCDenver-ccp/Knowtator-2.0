package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.events.SpanChangeEvent;

public interface SpanSelectionListener {

	void selectedSpanChanged(SpanChangeEvent e);
}
