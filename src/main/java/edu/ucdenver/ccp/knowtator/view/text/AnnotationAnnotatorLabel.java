package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

public class AnnotationAnnotatorLabel extends KnowtatorLabel implements KnowtatorViewComponent {

    public AnnotationAnnotatorLabel(KnowtatorView view) {
    	super(view);
	}

	@Override
	protected void reactToConceptAnnotationUpdated(ConceptAnnotation updatedItem) {

	}

	@Override
	void reactToConceptAnnotationChange(SelectionChangeEvent<ConceptAnnotation> event) {
		if (event.getNew() != null) {
			setText(event.getNew().getAnnotator().getId());
		} else {
			setText("");
		}
	}


}
