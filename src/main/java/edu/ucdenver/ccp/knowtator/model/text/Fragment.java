package edu.ucdenver.ccp.knowtator.model.text;

import java.util.HashMap;
import java.util.Map;

public class Fragment extends AbstractKnowtatorTextBoundObject {
	private String type;
	private Map<String, Integer> conceptCountMap;

	public Fragment(TextSource textSource, String id, String type) {
		super(textSource, id);
		this.type = type;

		conceptCountMap = new HashMap<>();
	}

	public void add(String concept) {
		if (conceptCountMap.containsKey(concept)) {
			conceptCountMap.put(concept, conceptCountMap.get(concept) + 1);
		}
		else {
			conceptCountMap.put(concept, 1);
		}
	}

	@Override
	public void dispose() {

	}

	public String getType() {
		return type;
	}

	public Map<String, Integer> getConceptCountMap() {
		return conceptCountMap;
	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
