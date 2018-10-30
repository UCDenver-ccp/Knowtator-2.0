package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;

public class KnowtatorCollectionAction<K extends KnowtatorDataObjectInterface> extends KnowtatorAction {
    final static String ADD = "add";
    static final String REMOVE = "remove";

    final String actionName;
    private KnowtatorCollection<K> collection;
    K object;

    KnowtatorCollectionAction(String actionName, KnowtatorCollection<K> collection, String presentationName) {
        super(presentationName);
        this.actionName = actionName;
        this.collection = collection;
    }


    @Override
    public void execute() {
        switch (actionName) {
            case ADD:
                collection.add(object);
                break;
            case REMOVE:
                collection.remove(object);
        }
    }

    public void setObject(K object) {
        this.object = object;
    }
}
