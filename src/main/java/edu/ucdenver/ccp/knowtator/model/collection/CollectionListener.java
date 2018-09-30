package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

public interface CollectionListener<K extends KnowtatorDataObjectInterface> {
	void added(AddEvent<K> event);

	void removed(RemoveEvent<K> event);

	void changed(ChangeEvent<K> event);

	void emptied();

	void firstAdded();
}
