package edu.ucdenver.ccp.knowtator.view.annotation;

import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorLabel;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

public class AnnotationAnnotatorLabel extends KnowtatorLabel implements KnowtatorComponent {

    public AnnotationAnnotatorLabel(KnowtatorView view) {
    	super(view);
	}


	@Override
	protected void reactToConceptAnnotationSelectionChange(SelectionChangeEvent<ConceptAnnotation> event) {
		if (event.getNew() != null) {
			setText(event.getNew().getAnnotator().getId());
		} else {
			setText("");
		}
	}

	@Override
	public void reactToConceptAnnotationChange(ChangeEvent<ConceptAnnotation> event) {

	}


}
