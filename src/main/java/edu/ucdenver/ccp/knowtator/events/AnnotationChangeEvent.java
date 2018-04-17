package edu.ucdenver.ccp.knowtator.events;

import edu.ucdenver.ccp.knowtator.model.Annotation;

public class AnnotationChangeEvent extends ChangeEvent<Annotation> {

	public AnnotationChangeEvent(Annotation oldAnnotation, Annotation newAnnotation) {
		super(oldAnnotation, newAnnotation);
	}
}
