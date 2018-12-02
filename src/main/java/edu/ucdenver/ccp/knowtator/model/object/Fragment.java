/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.object;

import java.util.HashMap;
import java.util.Map;

public class Fragment implements ModelObject<Fragment> {
	private final String type;
	private final Map<String, Integer> conceptCountMap;
	private String id;

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
	public int compareTo(Fragment o) {
		return 0;
	}
}
