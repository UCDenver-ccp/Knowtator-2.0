package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

public class AddEvent<K extends KnowtatorDataObjectInterface> {
    private final K newObject;

    AddEvent(K newObject) {
        this.newObject = newObject;
    }

    public K getAdded() {
        return newObject;
    }
}
