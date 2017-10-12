/*
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is Knowtator.
 *
 * The Initial Developer of the Original Code is University of Colorado.  
 * Copyright (C) 2005-2008.  All Rights Reserved.
 *
 * Knowtator was developed by the Center for Computational Pharmacology
 * (http://compbio.uchcs.edu) at the University of Colorado Health 
 *  Sciences Center School of Medicine with support from the National 
 *  Library of Medicine.  
 *
 * Current information about Knowtator can be obtained at 
 * http://knowtator.sourceforge.net/
 *
 * Contributor(s):
 *   Philip V. Ogren <philip@ogren.info> (Original Author)
 */

package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextSpan;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class creates an index on a collection of annotations based on the
 * proximity of the spans of the annotations.
 * 
 */

public class AnnotationSpanIndex {

	Map<Integer, Set<TextAnnotation>> window2AnnotationsMap;

	int windowSize;

	public AnnotationSpanIndex(Collection<TextAnnotation> textAnnotations) {
		this(textAnnotations, 20);
	}

	public AnnotationSpanIndex(Collection<TextAnnotation> textAnnotations, int windowSize) {
		this.windowSize = windowSize;
		window2AnnotationsMap = new HashMap<>();
		for (TextAnnotation textAnnotation : textAnnotations) {
			List<TextSpan> textSpans = textAnnotation.getTextSpans();
			for (TextSpan textSpan : textSpans) {
				int startKey = textSpan.getStart() / windowSize;
				int endKey = textSpan.getEnd() / windowSize;
				for (int key = startKey; key <= endKey; key++) {
					addToMap(key, textAnnotation);
				}
			}
		}

		// for(Integer window : window2AnnotationsMap.keySet())
		// {
		// Set<TextAnnotation> windowAnnotations =
		// window2AnnotationsMap.get(window);
		// for(TextAnnotation windowAnnotation : windowAnnotations)
		// {
		// Collection<TextSpan> spans = windowAnnotation.getSpans();
		// }
		// }
	}

	private void addToMap(int key, TextAnnotation textAnnotation) {
		if (!window2AnnotationsMap.containsKey(key)) {
			window2AnnotationsMap.put(key, new HashSet<>());
		}
		window2AnnotationsMap.get(key).add(textAnnotation);
	}

	public Set<TextAnnotation> getNearbyAnnotations(TextAnnotation textAnnotation) {
		Collection<TextSpan> textSpans = textAnnotation.getTextSpans();
		HashSet<Integer> windows = new HashSet<>();
		for (TextSpan textSpan : textSpans) {
			windows.add(textSpan.getStart() / windowSize);
			windows.add(textSpan.getEnd() / windowSize);
		}

		HashSet<TextAnnotation> returnValues = new HashSet<>();
		for (Integer window : windows) {
			Set<TextAnnotation> windowTextAnnotations = window2AnnotationsMap.get(window);
			if (windowTextAnnotations != null)
				returnValues.addAll(windowTextAnnotations);
		}

		returnValues.remove(textAnnotation);
		return returnValues;
	}

	public Set<TextAnnotation> getOverlappingAnnotations(TextAnnotation textAnnotation) {
		Set<TextAnnotation> nearbyTextAnnotations = getNearbyAnnotations(textAnnotation);
		Set<TextAnnotation> returnValues = new HashSet<>();
		for (TextAnnotation nearbyTextAnnotation : nearbyTextAnnotations) {
			if (TextAnnotation.spansOverlap(textAnnotation, nearbyTextAnnotation)) {
				returnValues.add(nearbyTextAnnotation);
			}
		}
		return returnValues;
	}

	public Set<TextAnnotation> getExactlyOverlappingAnnotations(TextAnnotation textAnnotation) {
		Set<TextAnnotation> nearbyTextAnnotations = getNearbyAnnotations(textAnnotation);
		Set<TextAnnotation> returnValues = new HashSet<>();
		for (TextAnnotation nearbyTextAnnotation : nearbyTextAnnotations) {
			if (TextAnnotation.spansMatch(textAnnotation, nearbyTextAnnotation)) {
				returnValues.add(nearbyTextAnnotation);
			}
		}
		return returnValues;
	}

}
