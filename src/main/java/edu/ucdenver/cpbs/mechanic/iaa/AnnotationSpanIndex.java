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

package edu.ucdenver.cpbs.mechanic.iaa;

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

	Map<Integer, Set<Annotation>> window2AnnotationsMap;

	int windowSize;

	public AnnotationSpanIndex(Collection<Annotation> annotations) {
		this(annotations, 20);
	}

	public AnnotationSpanIndex(Collection<Annotation> annotations, int windowSize) {
		this.windowSize = windowSize;
		window2AnnotationsMap = new HashMap<Integer, Set<Annotation>>();
		for (Annotation annotation : annotations) {
			List<Span> spans = annotation.getSpans();
			for (Span span : spans) {
				int startKey = span.getStart() / windowSize;
				int endKey = span.getEnd() / windowSize;
				for (int key = startKey; key <= endKey; key++) {
					addToMap(key, annotation);
				}
			}
		}

		// for(Integer window : window2AnnotationsMap.keySet())
		// {
		// Set<Annotation> windowAnnotations =
		// window2AnnotationsMap.get(window);
		// for(Annotation windowAnnotation : windowAnnotations)
		// {
		// Collection<Span> spans = windowAnnotation.getSpans();
		// }
		// }
	}

	private void addToMap(int key, Annotation annotation) {
		if (!window2AnnotationsMap.containsKey(key)) {
			window2AnnotationsMap.put(key, new HashSet<Annotation>());
		}
		window2AnnotationsMap.get(key).add(annotation);
	}

	public Set<Annotation> getNearbyAnnotations(Annotation annotation) {
		Collection<Span> spans = annotation.getSpans();
		HashSet<Integer> windows = new HashSet<Integer>();
		for (Span span : spans) {
			windows.add(new Integer(span.getStart() / windowSize));
			windows.add(new Integer(span.getEnd() / windowSize));
		}

		HashSet<Annotation> returnValues = new HashSet<Annotation>();
		for (Integer window : windows) {
			Set<Annotation> windowAnnotations = window2AnnotationsMap.get(window);
			if (windowAnnotations != null)
				returnValues.addAll(windowAnnotations);
		}

		returnValues.remove(annotation);
		return returnValues;
	}

	public Set<Annotation> getOverlappingAnnotations(Annotation annotation) {
		Set<Annotation> nearbyAnnotations = getNearbyAnnotations(annotation);
		Set<Annotation> returnValues = new HashSet<Annotation>();
		for (Annotation nearbyAnnotation : nearbyAnnotations) {
			if (Annotation.spansOverlap(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

	public Set<Annotation> getExactlyOverlappingAnnotations(Annotation annotation) {
		Set<Annotation> nearbyAnnotations = getNearbyAnnotations(annotation);
		Set<Annotation> returnValues = new HashSet<Annotation>();
		for (Annotation nearbyAnnotation : nearbyAnnotations) {
			if (Annotation.spansMatch(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

}
