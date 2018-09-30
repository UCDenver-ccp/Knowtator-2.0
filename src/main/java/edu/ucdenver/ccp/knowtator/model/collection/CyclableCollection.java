package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

import java.util.TreeSet;

public abstract class CyclableCollection<K extends KnowtatorObjectInterface, L extends CollectionListener<K>>
    extends ListenableCollection<K, TreeSet<K>, L> {

  CyclableCollection(TreeSet<K> collection) {
    super(collection);
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

  public K first() {
    return collection.first();
  }

  void dispose() {
    super.dispose();
    collection.clear();
  }
}
