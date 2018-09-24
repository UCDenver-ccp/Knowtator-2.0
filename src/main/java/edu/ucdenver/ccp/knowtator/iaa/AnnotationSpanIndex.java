package edu.ucdenver.ccp.knowtator.iaa;

import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;

import java.util.*;

/**
 * This class creates an index on a collection of annotations based on the proximity of the spans of
 * the annotations.
 */
public class AnnotationSpanIndex {

	private Map<Integer, Set<ConceptAnnotation>> window2AnnotationsMap;

	private int windowSize;

	public AnnotationSpanIndex(Collection<ConceptAnnotation> conceptAnnotations) {
		this(conceptAnnotations, 20);
	}

	private AnnotationSpanIndex(Collection<ConceptAnnotation> conceptAnnotations, int windowSize) {
		this.windowSize = windowSize;
		window2AnnotationsMap = new HashMap<>();
		for (ConceptAnnotation conceptAnnotation : conceptAnnotations) {
			for (Span span : conceptAnnotation.getSpanCollection()) {
				int startKey = span.getStart() / windowSize;
				int endKey = span.getEnd() / windowSize;
				for (int key = startKey; key <= endKey; key++) {
					addToMap(key, conceptAnnotation);
				}
			}
		}

		// for(Integer window : window2AnnotationsMap.keySet())
		// {
		// Set<concept> windowAnnotations =
		// window2AnnotationsMap.get(window);
		// for(concept windowAnnotation : windowAnnotations)
		// {
		// Collection<Span> spans = windowAnnotation.getSpanCollection();
		// }
		// }
	}

	private void addToMap(int key, ConceptAnnotation conceptAnnotation) {
		if (!window2AnnotationsMap.containsKey(key)) {
			window2AnnotationsMap.put(key, new HashSet<>());
		}
		window2AnnotationsMap.get(key).add(conceptAnnotation);
	}

	private Set<ConceptAnnotation> getNearbyAnnotations(ConceptAnnotation conceptAnnotation) {
		HashSet<Integer> windows = new HashSet<>();
		for (Span span : conceptAnnotation.getSpanCollection()) {
			windows.add(span.getStart() / windowSize);
			windows.add(span.getEnd() / windowSize);
		}

		HashSet<ConceptAnnotation> returnValues = new HashSet<>();
		for (Integer window : windows) {
			Set<ConceptAnnotation> windowConceptAnnotations = window2AnnotationsMap.get(window);
			if (windowConceptAnnotations != null) returnValues.addAll(windowConceptAnnotations);
		}

		returnValues.remove(conceptAnnotation);
		return returnValues;
	}

	public Set<ConceptAnnotation> getOverlappingAnnotations(ConceptAnnotation conceptAnnotation) {
		Set<ConceptAnnotation> nearbyConceptAnnotations = getNearbyAnnotations(conceptAnnotation);
		Set<ConceptAnnotation> returnValues = new HashSet<>();
		for (ConceptAnnotation nearbyConceptAnnotation : nearbyConceptAnnotations) {
			if (IAA.spansOverlap(conceptAnnotation, nearbyConceptAnnotation)) {
				returnValues.add(nearbyConceptAnnotation);
			}
		}
		return returnValues;
	}

	Set<ConceptAnnotation> getExactlyOverlappingAnnotations(ConceptAnnotation conceptAnnotation) {
		Set<ConceptAnnotation> nearbyConceptAnnotations = getNearbyAnnotations(conceptAnnotation);
		Set<ConceptAnnotation> returnValues = new HashSet<>();
		for (ConceptAnnotation nearbyConceptAnnotation : nearbyConceptAnnotations) {
			if (IAA.spansMatch(conceptAnnotation, nearbyConceptAnnotation)) {
				returnValues.add(nearbyConceptAnnotation);
			}
		}
		return returnValues;
	}
}
