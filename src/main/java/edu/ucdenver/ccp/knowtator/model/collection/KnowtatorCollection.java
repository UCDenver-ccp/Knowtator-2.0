package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

import java.util.TreeSet;

public abstract class KnowtatorCollection<K extends KnowtatorObject> extends SelectableCollection<K, KnowtatorCollectionListener<K>> {
    protected KnowtatorCollection(KnowtatorController controller) {
        super(controller, new TreeSet<>());
    }

    public void dispose() {
        super.dispose();
        forEach(KnowtatorObject::dispose);
    }

    public void update(K updatedItem) {
        collectionListeners.forEach(l -> l.updated(updatedItem));
    }
}
