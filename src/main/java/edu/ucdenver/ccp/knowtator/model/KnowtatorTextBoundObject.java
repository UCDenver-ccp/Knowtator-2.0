package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;

public interface KnowtatorTextBoundObject extends KnowtatorObject {
	TextSource getTextSource();
}
