package edu.ucdenver.ccp.knowtator.events;

import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

public class AnnotationChangeEvent extends ChangeEvent<ConceptAnnotation> {

	public AnnotationChangeEvent(ConceptAnnotation oldConceptAnnotation, ConceptAnnotation newConceptAnnotation) {
		super(oldConceptAnnotation, newConceptAnnotation);
	}
}
