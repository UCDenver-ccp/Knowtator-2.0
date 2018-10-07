package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

public class AnnotationIDLabel extends KnowtatorLabel {
  AnnotationIDLabel(KnowtatorView view) {
    super(view);
    this.view = view;
  }

  @Override
  void reactToConceptAnnotationSelectionChange(SelectionChangeEvent<ConceptAnnotation> event) {
    if (event.getNew() != null) {
      setText(event.getNew().getId());
    } else {
      setText("");
    }
  }

  @Override
  void reactToConceptAnnotationChange(ChangeEvent<ConceptAnnotation> event) {

  }

}
