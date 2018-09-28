package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public interface CollectionListener<K extends KnowtatorObjectInterface> {
	void added(AddEvent<K> addedObject);

	void removed(RemoveEvent<K> removedObject);

	void changed(ChangeEvent<K> changeEvent);

	void emptied(RemoveEvent<K> object);

	void firstAdded(AddEvent<K> object);
}
