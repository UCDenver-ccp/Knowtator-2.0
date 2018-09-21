package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

import java.util.ArrayList;
import java.util.List;

public abstract class SelectionModel<K extends KnowtatorObject> {

    private K selection;
    private List<SelectionListener<K>> selectionListeners;

    protected SelectionModel() {
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
        selectionListeners.clear();
    }
}
