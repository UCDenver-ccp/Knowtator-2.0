package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

public interface SelectableCollectionListener<K extends KnowtatorDataObjectInterface> extends CollectionListener<K> {
    void selected(SelectionChangeEvent<K> event);
}
