package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;

public class KnowtatorCollectionEdit<K extends KnowtatorDataObjectInterface> extends KnowtatorEdit {
    public final static String ADD = "add";
    public static final String REMOVE = "remove";

    private final String actionName;
    private final KnowtatorCollection<K> collection;
    private final K object;

    public KnowtatorCollectionEdit(String actionName, KnowtatorCollection<K> collection, K object, String presentationName) {
        super(presentationName);
        this.actionName = actionName;
        this.collection = collection;
        this.object = object;
    }

    @Override
    public void undo() {
        super.undo();
        switch (actionName) {
            case ADD:
                collection.remove(object);
                break;
            case REMOVE:
                collection.add(object);
                break;
        }
    }

    @Override
    public void redo() {
        super.redo();
        switch (actionName) {
            case ADD:
                collection.add(object);
                break;
            case REMOVE:
                collection.remove(object);
                break;
        }
    }


}
