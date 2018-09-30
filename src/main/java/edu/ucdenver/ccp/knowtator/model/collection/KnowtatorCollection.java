package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;

import java.util.TreeSet;

public abstract class KnowtatorCollection<K extends KnowtatorDataObjectInterface> extends SelectableCollection<K, KnowtatorCollectionListener<K>> {
    protected KnowtatorCollection(KnowtatorController controller) {
        super(controller, new TreeSet<>());
    }
}
