package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public class SelectionChangeEvent<K extends KnowtatorObject> extends ChangeEvent<K> {
    SelectionChangeEvent(K oldObject, K newObject) {
        super(oldObject, newObject);
    }
}
