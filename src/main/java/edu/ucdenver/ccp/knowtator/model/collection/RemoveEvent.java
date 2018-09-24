package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public class RemoveEvent<K extends KnowtatorObject> {
    private final K removedOject;

    RemoveEvent(K newObject) {
        this.removedOject = newObject;
    }

    public K getRemoved() {
        return removedOject;
    }
}