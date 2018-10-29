package edu.ucdenver.ccp.knowtator;

public abstract class UndoEvent {
    public KnowtatorObjectInterface getOldObject() {
        return oldObject;
    }

    public KnowtatorObjectInterface getNewObject() {
        return newObject;
    }

    private final KnowtatorObjectInterface oldObject;
    private final KnowtatorObjectInterface newObject;

    UndoEvent(KnowtatorObjectInterface oldObject, KnowtatorObjectInterface newObject) {

        this.oldObject = oldObject;
        this.newObject = newObject;
    }

    abstract void reverse();

    abstract void execute();
}
