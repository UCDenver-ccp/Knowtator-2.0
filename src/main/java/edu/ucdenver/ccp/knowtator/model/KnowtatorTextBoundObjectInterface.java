package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;

public interface KnowtatorTextBoundObjectInterface<K extends KnowtatorObjectInterface> extends KnowtatorObjectInterface<K> {

	 TextSource getTextSource();
}
