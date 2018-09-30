package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

public class SelectionChangeEvent<K extends KnowtatorDataObjectInterface> extends ChangeEvent<K> {
    SelectionChangeEvent(K oldObject, K newObject) {
        super(oldObject, newObject);
    }
}
