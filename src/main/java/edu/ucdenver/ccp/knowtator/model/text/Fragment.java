package edu.ucdenver.ccp.knowtator.model.text;

import java.util.HashMap;
import java.util.Map;

public class Fragment {
	private String id;
	private String type;
	private Map<String, Integer> conceptCountMap;

	public Fragment(String id, String type) {
		this.id = id;
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

	public String getId() {
		return id;
	}

	public String getType() {
		return type;
	}

	public Map<String, Integer> getConceptCountMap() {
		return conceptCountMap;
	}
}
