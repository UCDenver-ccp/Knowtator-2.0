package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

import java.util.TreeSet;

public abstract class SelectableCollection<K extends KnowtatorDataObjectInterface, L extends SelectableCollectionListener<K>> extends CyclableCollection<K, L> {

    private final KnowtatorController controller;
    private K selection;

    SelectableCollection(KnowtatorController controller, TreeSet<K> collection) {
        super(collection);
        this.controller = controller;
        selection = null;
    }

    public K getSelection() throws NoSelectionException {
        if (selection == null) {
            throw new NoSelectionException();
        }
        return selection;

    }

    public void selectNext() {
        setSelection(getNext(selection));
    }

    public void selectPrevious() {
        setSelection(getPrevious(selection));
    }

    public void setSelection(K newSelection) {
        if (controller.isNotLoading() && this.selection != newSelection) {
            SelectionChangeEvent<K> selectionChangeEvent = new SelectionChangeEvent<>(this.selection, newSelection);
            this.selection = newSelection;
            collectionListeners.forEach(selectionListener -> selectionListener.selected(selectionChangeEvent));

        }
    }

    @Override
    public void add(K item) {
        super.add(item);
        setSelection(item);
    }

    public void removeSelected() {
        remove(selection);
        setSelection(null);
    }
}
