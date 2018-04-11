package edu.ucdenver.ccp.knowtator.events;

public abstract class ChangeEvent<O> {

	private final O oldObject;
	private final O newObject;

	ChangeEvent(O oldObject, O newObject) {

		this.oldObject = oldObject;
		this.newObject = newObject;
	}

	public O getOld() {
		return oldObject;
	}

	public O getNew() {
		return newObject;
	}
}
