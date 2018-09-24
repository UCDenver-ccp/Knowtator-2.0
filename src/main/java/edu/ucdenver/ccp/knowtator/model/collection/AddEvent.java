package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public class AddEvent<K extends KnowtatorObject> {
    private final K newObject;

    AddEvent(K newObject) {
        this.newObject = newObject;
    }

    public K getAdded() {
        return newObject;
    }
}
