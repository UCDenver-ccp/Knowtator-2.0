package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public class SelectionChangeEvent<K extends KnowtatorObjectInterface> extends ChangeEvent<K> {
    SelectionChangeEvent(K oldObject, K newObject) {
        super(oldObject, newObject);
    }
}
