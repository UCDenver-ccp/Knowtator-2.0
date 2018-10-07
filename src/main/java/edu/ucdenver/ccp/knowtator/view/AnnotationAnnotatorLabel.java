package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

public class AnnotationAnnotatorLabel extends KnowtatorLabel implements KnowtatorComponent {

    AnnotationAnnotatorLabel(KnowtatorView view) {
    	super(view);
	}


	@Override
	void reactToConceptAnnotationSelectionChange(SelectionChangeEvent<ConceptAnnotation> event) {
		if (event.getNew() != null) {
			setText(event.getNew().getAnnotator().getId());
		} else {
			setText("");
		}
	}

	@Override
	void reactToConceptAnnotationChange(ChangeEvent<ConceptAnnotation> event) {

	}


}
