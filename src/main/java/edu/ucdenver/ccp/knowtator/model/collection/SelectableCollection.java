package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

import java.util.TreeSet;

public abstract class SelectableCollection<K extends KnowtatorObject, L extends SelectableCollectionListener<K>> extends CyclableCollection<K, L> {

    private K selection;

    SelectableCollection(KnowtatorController controller, TreeSet<K> collection) {
        super(controller, collection);
        selection = null;
    }

    public K getSelection() {
        return selection;

    }

    public void selectNext() {
        setSelection(getNext(getSelection()));
    }

    public void selectPrevious() {
        setSelection(getPrevious(getSelection()));
    }

    public void setSelection(K newSelection) {
        if (this.selection != newSelection) {
            SelectionChangeEvent<K> selectionChangeEvent = new SelectionChangeEvent<>(this.selection, newSelection);
            this.selection = newSelection;
            collectionListeners.forEach(kSelectionListener -> kSelectionListener.selected(selectionChangeEvent));

        }
    }
}
