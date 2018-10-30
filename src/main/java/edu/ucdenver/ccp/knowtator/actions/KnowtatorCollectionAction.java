package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;

public class KnowtatorCollectionAction<K extends KnowtatorDataObjectInterface> extends KnowtatorAction {
    final static String ADD = "add";
    static final String REMOVE = "remove";

    final String actionName;
    private KnowtatorCollection<K> collection;
    K object;
    KnowtatorController controller;

    KnowtatorCollectionAction(String actionName, KnowtatorCollection<K> collection, String presentationName, KnowtatorController controller) {
        super(presentationName);
        this.actionName = actionName;
        this.collection = collection;
        this.controller = controller;
    }


    @Override
    public void execute() {
        KnowtatorCollectionEdit<K> edit = null;
        switch (actionName) {
            case ADD:
                collection.add(object);
                edit = new KnowtatorCollectionEdit<>(actionName, collection, object, "Add");
                break;
            case REMOVE:
                collection.remove(object);
                edit = new KnowtatorCollectionEdit<>(KnowtatorCollectionEdit.REMOVE, collection, object, "Remove");
                break;
        }
        if (edit != null) {
            controller.addEdit(edit);
        }
    }

    public void setObject(K object) {
        this.object = object;
    }
}
