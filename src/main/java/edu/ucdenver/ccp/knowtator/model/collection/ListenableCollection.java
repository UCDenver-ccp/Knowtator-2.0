package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.CollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;

import java.util.ArrayList;
import java.util.Collection;

public class ListenableCollection<
        collectionType,
        underlyingCollection extends Collection<collectionType>,
        listenerType extends CollectionListener<collectionType>>
        implements ProjectListener {
  public final underlyingCollection collection;
  private final ArrayList<listenerType> listeners;

  ListenableCollection(KnowtatorController controller, underlyingCollection collection) {
    this.collection = collection;
    listeners = new ArrayList<>();
    controller.getProjectManager().addListener(this);
  }

  public void add(collectionType objectToAdd) {
    collection.add(objectToAdd);
    listeners.forEach(profilesListener -> profilesListener.added(objectToAdd));
  }

  public void remove(collectionType objectToRemove) {
    collection.remove(objectToRemove);
    listeners.forEach(listener -> listener.removed(objectToRemove));
  }

  public underlyingCollection getData() {
    return collection;
  }

  public void addListener(listenerType listener) {
    listeners.add(listener);
  }

  public void removeListener(listenerType listener) {
    listeners.remove(listener);
  }

  @Override
  public void projectClosed() {
    collection.clear();
    listeners.clear();
  }

  @Override
  public void projectLoaded() {
  }
}
