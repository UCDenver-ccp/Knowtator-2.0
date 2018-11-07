package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;

import javax.swing.undo.UndoableEdit;

public abstract class AbstractKnowtatorCollectionAction<K extends KnowtatorDataObjectInterface> extends AbstractKnowtatorAction {
    public final static String ADD = "add";
    public static final String REMOVE = "remove";

    private final String actionName;
    KnowtatorCollectionEdit<K> edit;
    KnowtatorCollection<K> collection;
    K object;

    AbstractKnowtatorCollectionAction(String actionName, String presentationName) {
        super(String.format("%s %s",actionName, presentationName));
        this.actionName = actionName;
        this.edit = new KnowtatorCollectionEdit<>(actionName, collection, object, getPresentationName());
    }


    @Override
    public void execute() throws ActionUnperformableException {
        switch (actionName) {
            case ADD:
                prepareAdd();
                collection.add(object);
                cleanUpAdd();
                break;
            case REMOVE:
                prepareRemove();
                collection.remove(object);
                cleanUpRemove();
                break;
        }
    }

    abstract void prepareRemove() throws ActionUnperformableException;
    abstract void prepareAdd() throws ActionUnperformableException;

    abstract void cleanUpRemove();
    abstract void cleanUpAdd();


    @Override
    public UndoableEdit getEdit() {
        return edit;
    }

    public void setObject(K object) {
        this.object = object;
    }

    public void setCollection(KnowtatorCollection<K> collection) {
        this.collection = collection;
    }
}