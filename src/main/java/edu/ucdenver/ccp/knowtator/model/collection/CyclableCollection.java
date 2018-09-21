package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

import java.util.TreeSet;

public class CyclableCollection<K extends KnowtatorObject, L extends CollectionListener<K>>
    extends ListenableCollection<K, TreeSet<K>, L> {

  CyclableCollection(KnowtatorController controller, TreeSet<K> collection) {
    super(controller, collection);
  }

  public K getPrevious(K current) {

    K previous;
    try {
      previous =
          collection.contains(current) ? collection.lower(current) : collection.floor(current);
    } catch (NullPointerException npe) {
      previous = null;
    }
    if (previous == null) previous = collection.last();


    return previous;
  }

  public K getNext(K current) {
    K next;
    try {
      next =
          collection.contains(current) ? collection.higher(current) : collection.ceiling(current);
    } catch (NullPointerException npe) {
      next = null;
    }
    if (next == null) next = collection.first();

    return next;
  }
}
