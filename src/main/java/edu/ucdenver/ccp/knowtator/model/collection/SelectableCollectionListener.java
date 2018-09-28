package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public interface SelectableCollectionListener<K extends KnowtatorObjectInterface> extends CollectionListener<K> {
    void selected(SelectionChangeEvent<K> event);
}
