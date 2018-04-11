package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.listeners.CollectionListener;

import java.util.ArrayList;
import java.util.Collection;

public class ListenableCollection<
		collectionType,
		underlyingCollection extends Collection<collectionType>,
		listenerType extends CollectionListener<collectionType>> {
	public final underlyingCollection collection;
	private final ArrayList<listenerType> listeners;

	ListenableCollection(underlyingCollection collection) {
		this.collection = collection;
		listeners = new ArrayList<>();
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
}
