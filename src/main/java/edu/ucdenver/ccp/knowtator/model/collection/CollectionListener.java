package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public interface CollectionListener<K extends KnowtatorObjectInterface> {
	void added(AddEvent<K> event);

	void removed(RemoveEvent<K> event);

	void changed(ChangeEvent<K> event);

	void emptied();

	void firstAdded();
}
