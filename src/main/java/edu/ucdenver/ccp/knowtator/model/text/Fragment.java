package edu.ucdenver.ccp.knowtator.model.text;

import edu.ucdenver.ccp.knowtator.model.KnowtatorTextBoundObject;

import java.util.HashMap;
import java.util.Map;

public class Fragment implements KnowtatorTextBoundObject {
	private TextSource textSource;
	private String id;
	private String type;
	private Map<String, Integer> conceptCountMap;

	public Fragment(TextSource textSource, String id, String type) {
		this.textSource = textSource;
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

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
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
	public TextSource getTextSource() {
		return textSource;
	}
}
