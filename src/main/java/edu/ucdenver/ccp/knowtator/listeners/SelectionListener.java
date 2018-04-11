package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.events.*;

public interface SelectionListener {

	void selectedAnnotationChanged(AnnotationChangeEvent e);

	void selectedSpanChanged(SpanChangeEvent e);

	void activeGraphSpaceChanged(GraphSpaceChangeEvent e);

	void activeTextSourceChanged(TextSourceChangeEvent e);

	void activeProfileChange(ProfileChangeEvent e);
}
