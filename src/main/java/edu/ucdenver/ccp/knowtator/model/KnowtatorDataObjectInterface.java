package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorObjectInterface;

public interface KnowtatorDataObjectInterface<K extends KnowtatorDataObjectInterface> extends Comparable<K>, KnowtatorObjectInterface {

	String getId();

	void setId(String id);
}
