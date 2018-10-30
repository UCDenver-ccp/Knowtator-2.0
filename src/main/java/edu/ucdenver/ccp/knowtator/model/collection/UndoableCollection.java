package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.actions.KnowtatorCollectionEdit;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

import java.util.TreeSet;

public class UndoableCollection<K extends KnowtatorDataObjectInterface, L extends SelectableCollectionListener<K>> extends SelectableCollection<K, L>{

    private KnowtatorController controller;

    UndoableCollection(KnowtatorController controller, TreeSet<K> collection) {
        super(controller, collection);
        this.controller = controller;
    }

    @Override
    public void add(K objectToAdd) {
        super.add(objectToAdd);
        KnowtatorCollectionEdit<K, L> edit = new KnowtatorCollectionEdit<>(KnowtatorCollectionEdit.ADD, this, objectToAdd, "Add");
        controller.addEdit(edit);
    }

    @Override
    public void remove(K objectToRemove) {
        super.remove(objectToRemove);
        KnowtatorCollectionEdit<K, L> edit = new KnowtatorCollectionEdit<>(KnowtatorCollectionEdit.REMOVE, this, objectToRemove, "Remove");
        controller.addEdit(edit);
    }
}
