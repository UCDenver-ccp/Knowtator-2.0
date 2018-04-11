package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.TextSource;

public interface GraphSpaceListener {
	void graphTextChanged(TextSource textSource, int start, int end);
}
