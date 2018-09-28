package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

public class AddEvent<K extends KnowtatorObjectInterface> {
    private final K newObject;

    AddEvent(K newObject) {
        this.newObject = newObject;
    }

    public K getAdded() {
        return newObject;
    }
}
