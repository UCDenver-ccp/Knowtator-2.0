package edu.ucdenver.ccp.knowtator.model;

public abstract class AbstractKnowtatorDataObject<K extends KnowtatorDataObjectInterface<K>> implements KnowtatorDataObjectInterface<K> {
    protected String id;

    protected AbstractKnowtatorDataObject(String id) {
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
