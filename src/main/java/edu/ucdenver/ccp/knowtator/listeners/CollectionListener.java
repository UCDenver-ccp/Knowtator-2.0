package edu.ucdenver.ccp.knowtator.listeners;

public interface CollectionListener<T1> {
	void added(T1 addedObject);

	void removed(T1 removedObject);
}
