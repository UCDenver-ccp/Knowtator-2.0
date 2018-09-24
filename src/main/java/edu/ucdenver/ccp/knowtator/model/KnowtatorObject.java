package edu.ucdenver.ccp.knowtator.model;

public interface KnowtatorObject<K extends KnowtatorObject> extends Comparable<K>{

	String getId();

	void setId(String id);

	void dispose();
}
