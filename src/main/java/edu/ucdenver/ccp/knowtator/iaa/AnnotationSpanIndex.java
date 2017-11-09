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

import java.util.*;

/**
 * This class creates an index on a collection of annotations based on the
 * proximity of the spans of the annotations.
 * 
 */

public class AnnotationSpanIndex {

	Map<Integer, Set<TextAnnotation>> window2AnnotationsMap;

	int windowSize;

	public AnnotationSpanIndex(Collection<TextAnnotation> annotations) {
		this(annotations, 20);
	}

	public AnnotationSpanIndex(Collection<TextAnnotation> annotations, int windowSize) {
		this.windowSize = windowSize;
		window2AnnotationsMap = new HashMap<>();
		for (TextAnnotation annotation : annotations) {
			List<TextSpan> spans = annotation.getTextSpans();
			for (TextSpan span : spans) {
				int startKey = span.getStart() / windowSize;
				int endKey = span.getEnd() / windowSize;
				for (int key = startKey; key <= endKey; key++) {
					addToMap(key, annotation);
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

	private void addToMap(int key, TextAnnotation annotation) {
		if (!window2AnnotationsMap.containsKey(key)) {
			window2AnnotationsMap.put(key, new HashSet<>());
		}
		window2AnnotationsMap.get(key).add(annotation);
	}

	public Set<TextAnnotation> getNearbyAnnotations(TextAnnotation annotation) {
		Collection<TextSpan> spans = annotation.getTextSpans();
		HashSet<Integer> windows = new HashSet<>();
		for (TextSpan span : spans) {
			windows.add(span.getStart() / windowSize);
			windows.add(span.getEnd() / windowSize);
		}

		HashSet<TextAnnotation> returnValues = new HashSet<>();
		for (Integer window : windows) {
			Set<TextAnnotation> windowAnnotations = window2AnnotationsMap.get(window);
			if (windowAnnotations != null)
				returnValues.addAll(windowAnnotations);
		}

		returnValues.remove(annotation);
		return returnValues;
	}

	public Set<TextAnnotation> getOverlappingAnnotations(TextAnnotation annotation) {
		Set<TextAnnotation> nearbyAnnotations = getNearbyAnnotations(annotation);
		Set<TextAnnotation> returnValues = new HashSet<>();
		for (TextAnnotation nearbyAnnotation : nearbyAnnotations) {
			if (TextAnnotation.spansOverlap(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

	public Set<TextAnnotation> getExactlyOverlappingAnnotations(TextAnnotation annotation) {
		Set<TextAnnotation> nearbyAnnotations = getNearbyAnnotations(annotation);
		Set<TextAnnotation> returnValues = new HashSet<>();
		for (TextAnnotation nearbyAnnotation : nearbyAnnotations) {
			if (TextAnnotation.spansMatch(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

}
