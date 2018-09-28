package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public class RemoveEvent<K extends KnowtatorObjectInterface> {
    private final K removedOject;

    RemoveEvent(K newObject) {
        this.removedOject = newObject;
    }

    public K getRemoved() {
        return removedOject;
    }
}