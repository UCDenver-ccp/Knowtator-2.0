package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public abstract class ListenableCollection<K extends KnowtatorObjectInterface, C extends Collection<K>, L extends CollectionListener<K>> implements Iterable<K> {
  final C collection;
  protected final List<L> collectionListeners;

  ListenableCollection(C collection) {
    this.collection = collection;
    collectionListeners = new ArrayList<>();
  }

  public void add(K objectToAdd) {
    collection.add(objectToAdd);

    AddEvent<K> event = new AddEvent<>(objectToAdd);

    collectionListeners.forEach(listener -> listener.added(event));
    if (collection.size() == 1) {
      collectionListeners.forEach(CollectionListener::firstAdded);
    }
  }

  public void remove(K objectToRemove) {
    collection.remove(objectToRemove);
    objectToRemove.dispose();

    RemoveEvent<K> event = new RemoveEvent<>(objectToRemove);

    collectionListeners.forEach(listener -> listener.removed(event));
    if (collection.size() == 0) {
      collectionListeners.forEach(CollectionListener::emptied);
    }
  }

  public K get(String id) {
    for (K obj : collection) {
      if (obj.getId().equals(id)) {
        return obj;
      }
    }
    return null;
  }

  @SuppressWarnings("unused")
  public boolean contains(K objToFind) {
    return containsID(objToFind.getId());
  }

  public boolean containsID(String idToFind) {
    for (K id : collection) {
      if (id.getId().equals(idToFind)) {
        return true;
      }
    }
    return false;
  }

  public void addCollectionListener(L listener) {
    if (!collectionListeners.contains(listener)) collectionListeners.add(listener);
  }

  public void removeCollectionListener(L listener) {
    collectionListeners.remove(listener);
  }

  @Override
  @Nonnull
  public Iterator<K> iterator() {
    return collection.iterator();
  }

  public Stream<K> stream() {
    return collection.stream();
  }

  public int size() {
    return collection.size();
  }

  void dispose() {
    collectionListeners.clear();
    collection.forEach(KnowtatorObjectInterface::dispose);
  }

  public K[] toArray(K[] newArray) {
    return collection.toArray(newArray);
  }

  public C getCollection() {
    return collection;
  }
}
