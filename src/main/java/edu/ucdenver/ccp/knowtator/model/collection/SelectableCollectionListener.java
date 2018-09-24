package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public interface SelectableCollectionListener<K extends KnowtatorObject> extends CollectionListener<K> {
    void noSelection(K previousSelection);

    void selected(SelectionChangeEvent<K> event);
}
