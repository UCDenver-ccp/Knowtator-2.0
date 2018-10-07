package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorObjectInterface;

public interface KnowtatorDataObjectInterface<K extends KnowtatorDataObjectInterface> extends Comparable<K>, KnowtatorObjectInterface {

	String getId();

	void setId(String id);

	static int extractInt(String s) {
		String num = s.replaceAll("\\D", "");
		// return 0 if no digits found
		return num.isEmpty() ? 0 : Integer.parseInt(num);
	}
}
