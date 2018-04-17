package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public interface CollectionListener<K extends KnowtatorObject> {
	void added(K addedObject);

	void removed(K removedObject);
}
