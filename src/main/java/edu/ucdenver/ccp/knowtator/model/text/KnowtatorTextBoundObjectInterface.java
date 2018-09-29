package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public interface KnowtatorTextBoundObjectInterface<K extends KnowtatorObjectInterface> extends KnowtatorObjectInterface<K> {

	 TextSource getTextSource();
}
