package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;

public interface KnowtatorTextBoundObject<K extends KnowtatorObject> extends KnowtatorObject<K> {

	 TextSource getTextSource();
}
