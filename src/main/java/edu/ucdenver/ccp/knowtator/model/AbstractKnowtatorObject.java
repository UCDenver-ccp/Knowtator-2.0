package edu.ucdenver.ccp.knowtator.model;

public abstract class AbstractKnowtatorObject<K extends KnowtatorObjectInterface<K>> implements KnowtatorObjectInterface<K> {
    protected String id;

    protected AbstractKnowtatorObject(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract void dispose();

}