package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.listeners.CollectionListener;

import java.util.TreeSet;

public class CyclableCollection<
		collectionType, listenerType extends CollectionListener<collectionType>>
		extends ListenableCollection<collectionType, TreeSet<collectionType>, listenerType> {

	CyclableCollection(TreeSet<collectionType> collection) {
		super(collection);
	}

	public collectionType getPrevious(collectionType current) {

		collectionType previous;
		try {
			previous = getData().contains(current) ? getData().lower(current) : getData().floor(current);
		} catch (NullPointerException npe) {
			previous = null;
		}
		if (previous == null) previous = getData().last();

		return previous;
	}

	public collectionType getNext(collectionType current) {
		collectionType next;
		try {
			next = getData().contains(current) ? getData().higher(current) : getData().ceiling(current);
		} catch (NullPointerException npe) {
			next = null;
		}
		if (next == null) next = getData().first();
		return next;
	}
}
