package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

public class RemoveEvent<K extends KnowtatorDataObjectInterface> {
    private final K removedOject;

    RemoveEvent(K newObject) {
        this.removedOject = newObject;
    }

    public K getRemoved() {
        return removedOject;
    }
}