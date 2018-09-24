package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public interface KnowtatorCollectionListener<K extends KnowtatorObject> extends SelectableCollectionListener<K> {

    void updated(K updatedItem);
}
