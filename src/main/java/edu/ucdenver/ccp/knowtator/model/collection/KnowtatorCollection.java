package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

import java.util.TreeSet;

public abstract class KnowtatorCollection<K extends KnowtatorObjectInterface> extends SelectableCollection<K, KnowtatorCollectionListener<K>> {
    protected KnowtatorCollection(KnowtatorController controller) {
        super(controller, new TreeSet<>());
    }

    public void dispose() {
        super.dispose();
        forEach(KnowtatorObjectInterface::dispose);
    }

    public void update(K updatedItem) {
        collectionListeners.forEach(l -> l.updated(updatedItem));
    }
}
