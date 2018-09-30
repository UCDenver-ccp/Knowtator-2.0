package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public interface CollectionListener<K extends KnowtatorObjectInterface> {
	void added(AddEvent<K> addedObject);

	void removed(RemoveEvent<K> removedObject);

	void changed();

	void emptied();

	void firstAdded();
}
