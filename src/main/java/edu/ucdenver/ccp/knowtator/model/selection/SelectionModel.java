package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;
import edu.ucdenver.ccp.knowtator.model.collection.CollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.CyclableCollection;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public abstract class SelectionModel<K extends KnowtatorObject, L extends CollectionListener<K>> extends CyclableCollection<K, L> {

    private K selection;
    private List<SelectionListener<K>> selectionListeners;

    protected SelectionModel(KnowtatorController controller, TreeSet<K> collection) {
        super(controller, collection);
        selection = null;
        selectionListeners = new ArrayList<>();
    }

    public void addSelectionListener(SelectionListener<K> listener) {
        selectionListeners.add(listener);
    }

    public void removeSelectionListener(SelectionListener listener) {
        selectionListeners.remove(listener);
    }

    public K getSelection() {
        return selection;

    }
    public void setSelection(K selection) {
        if (this.selection != selection) {
            K previousSelection = this.selection;
            this.selection = selection;
            if (selection == null) {
                selectionListeners.forEach(kSelectionListener -> kSelectionListener.noSelection(previousSelection));
            } else {
                selectionListeners.forEach(kSelectionListener -> kSelectionListener.selected(previousSelection, this.selection));
            }
        }
    }

    public void dispose() {
        forEach(KnowtatorObject::dispose);
        getCollection().clear();
        selectionListeners.clear();
    }
}
