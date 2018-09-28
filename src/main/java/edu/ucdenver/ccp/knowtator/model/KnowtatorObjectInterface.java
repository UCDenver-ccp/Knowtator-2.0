package edu.ucdenver.ccp.knowtator.model;

public interface KnowtatorObjectInterface<K extends KnowtatorObjectInterface> extends Comparable<K>{

	String getId();

	void setId(String id);

	void dispose();
}
