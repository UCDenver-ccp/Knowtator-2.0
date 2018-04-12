package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.events.GraphSpaceChangeEvent;

public interface GraphSpaceSelectionListener {
	void activeGraphSpaceChanged(GraphSpaceChangeEvent e);
}
