package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;

public interface TextSourceSelectionListener {
	void activeTextSourceChanged(TextSourceChangeEvent e);
}
