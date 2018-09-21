package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

public interface SelectionListener<K extends KnowtatorObject> {
    void noSelection(K previousSelection);
    void selected(K previousSelection, K currentSelection);
}
