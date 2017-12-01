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

import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;

import java.util.*;

/**
 * This class creates an index on a collection of annotations based on the
 * proximity of the spans of the annotations.
 * 
 */

public class AnnotationSpanIndex {

	private Map<Integer, Set<ConceptAnnotation>> window2AnnotationsMap;

	private int windowSize;

	public AnnotationSpanIndex(Collection<ConceptAnnotation> annotations) {
		this(annotations, 20);
	}

	private AnnotationSpanIndex(Collection<ConceptAnnotation> annotations, int windowSize) {
		this.windowSize = windowSize;
		window2AnnotationsMap = new HashMap<>();
		for (ConceptAnnotation annotation : annotations) {
			TreeSet<Span> spans = annotation.getSpans();
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
		// Set<annotation> windowAnnotations =
		// window2AnnotationsMap.get(window);
		// for(annotation windowAnnotation : windowAnnotations)
		// {
		// Collection<Span> spans = windowAnnotation.getSpans();
		// }
		// }
	}

	private void addToMap(int key, ConceptAnnotation annotation) {
		if (!window2AnnotationsMap.containsKey(key)) {
			window2AnnotationsMap.put(key, new HashSet<>());
		}
		window2AnnotationsMap.get(key).add(annotation);
	}

	private Set<ConceptAnnotation> getNearbyAnnotations(ConceptAnnotation annotation) {
		Collection<Span> spans = annotation.getSpans();
		HashSet<Integer> windows = new HashSet<>();
		for (Span span : spans) {
			windows.add(span.getStart() / windowSize);
			windows.add(span.getEnd() / windowSize);
		}

		HashSet<ConceptAnnotation> returnValues = new HashSet<>();
		for (Integer window : windows) {
			Set<ConceptAnnotation> windowAnnotations = window2AnnotationsMap.get(window);
			if (windowAnnotations != null)
				returnValues.addAll(windowAnnotations);
		}

		returnValues.remove(annotation);
		return returnValues;
	}

	public Set<ConceptAnnotation> getOverlappingAnnotations(ConceptAnnotation annotation) {
		Set<ConceptAnnotation> nearbyAnnotations = getNearbyAnnotations(annotation);
		Set<ConceptAnnotation> returnValues = new HashSet<>();
		for (ConceptAnnotation nearbyAnnotation : nearbyAnnotations) {
			if (IAA.spansOverlap(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

	Set<ConceptAnnotation> getExactlyOverlappingAnnotations(ConceptAnnotation annotation) {
		Set<ConceptAnnotation> nearbyAnnotations = getNearbyAnnotations(annotation);
		Set<ConceptAnnotation> returnValues = new HashSet<>();
		for (ConceptAnnotation nearbyAnnotation : nearbyAnnotations) {
			if (IAA.spansMatch(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

}
