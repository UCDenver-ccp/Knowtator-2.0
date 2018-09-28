package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public interface SelectableCollectionListener<K extends KnowtatorObjectInterface> extends CollectionListener<K> {
    void noSelection(K previousSelection);

    void selected(SelectionChangeEvent<K> event);
}
