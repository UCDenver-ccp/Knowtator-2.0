package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;

import java.util.*;

/**
 * This class creates an index on a collection of annotations based on the proximity of the spans of
 * the annotations.
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
			for (Span span : annotation.getSpanManager().getSpans()) {
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
		// Collection<Span> spans = windowAnnotation.getSpanCollection();
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
		HashSet<Integer> windows = new HashSet<>();
		for (Span span : annotation.getSpanManager().getSpans()) {
			windows.add(span.getStart() / windowSize);
			windows.add(span.getEnd() / windowSize);
		}

		HashSet<Annotation> returnValues = new HashSet<>();
		for (Integer window : windows) {
			Set<Annotation> windowAnnotations = window2AnnotationsMap.get(window);
			if (windowAnnotations != null) returnValues.addAll(windowAnnotations);
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
