package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public interface CollectionListener<K extends KnowtatorObject> {
	void added(K addedObject);

	void removed(K removedObject);
}
