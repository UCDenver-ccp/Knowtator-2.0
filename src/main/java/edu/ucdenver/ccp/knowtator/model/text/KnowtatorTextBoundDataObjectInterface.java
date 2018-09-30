package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

public interface KnowtatorTextBoundDataObjectInterface<K extends KnowtatorDataObjectInterface> extends KnowtatorDataObjectInterface<K> {

	 TextSource getTextSource();
}
