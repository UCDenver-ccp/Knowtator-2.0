/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;

import java.util.*;

/**
 * This class creates an index on a collection of annotations based on the
 * proximity of the spans of the annotations.
 * 
 */

public class AnnotationSpanIndex {

	private Map<Integer, Set<Annotation>> window2AnnotationsMap;

	private int windowSize;

	public AnnotationSpanIndex(Collection<Annotation> annotations) {
		this(annotations, 20);
	}

	private AnnotationSpanIndex(Collection<Annotation> annotations, int windowSize) {
		this.windowSize = windowSize;
		window2AnnotationsMap = new HashMap<>();
		for (Annotation annotation : annotations) {
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

	private void addToMap(int key, Annotation annotation) {
		if (!window2AnnotationsMap.containsKey(key)) {
			window2AnnotationsMap.put(key, new HashSet<>());
		}
		window2AnnotationsMap.get(key).add(annotation);
	}

	private Set<Annotation> getNearbyAnnotations(Annotation annotation) {
		Collection<Span> spans = annotation.getSpans();
		HashSet<Integer> windows = new HashSet<>();
		for (Span span : spans) {
			windows.add(span.getStart() / windowSize);
			windows.add(span.getEnd() / windowSize);
		}

		HashSet<Annotation> returnValues = new HashSet<>();
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
		Set<Annotation> returnValues = new HashSet<>();
		for (Annotation nearbyAnnotation : nearbyAnnotations) {
			if (IAA.spansOverlap(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

	Set<Annotation> getExactlyOverlappingAnnotations(Annotation annotation) {
		Set<Annotation> nearbyAnnotations = getNearbyAnnotations(annotation);
		Set<Annotation> returnValues = new HashSet<>();
		for (Annotation nearbyAnnotation : nearbyAnnotations) {
			if (IAA.spansMatch(annotation, nearbyAnnotation)) {
				returnValues.add(nearbyAnnotation);
			}
		}
		return returnValues;
	}

}
