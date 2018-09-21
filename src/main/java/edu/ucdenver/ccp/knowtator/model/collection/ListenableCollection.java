package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.ProjectManager;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorObject;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.stream.Stream;

public class ListenableCollection<
        K extends KnowtatorObject, C extends Collection<K>, L extends CollectionListener<K>>
        implements ProjectListener, Iterable<K> {
  public final C collection;
  private final ArrayList<L> listeners;

  ListenableCollection(ProjectManager controller, C collection) {
    this.collection = collection;
    listeners = new ArrayList<>();
    controller.addProjectListener(this);
  }

  public void add(K objectToAdd) {
    collection.add(objectToAdd);
    listeners.forEach(listener -> listener.added(objectToAdd));
    if (collection.size() == 1) {
      listeners.forEach(listener -> listener.firstAdded(objectToAdd));
    }
  }

  public void remove(K objectToRemove) {
    collection.remove(objectToRemove);
    listeners.forEach(listener -> listener.removed(objectToRemove));
    if (collection.size() == 0) {
      listeners.forEach(listener -> listener.emptied(objectToRemove));
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

  public void addListener(L listener) {
    listeners.add(listener);
  }

  public void removeListener(L listener) {
    listeners.remove(listener);
  }

  @Override
  public void projectClosed() {
    collection.clear();
    listeners.clear();
  }

  @Override
  public void projectLoaded() {}

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

  public C getCollection() {
    return collection;
  }
}
