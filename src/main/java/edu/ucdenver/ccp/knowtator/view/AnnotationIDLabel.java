package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

public class AnnotationIDLabel extends KnowtatorLabel {
  AnnotationIDLabel(KnowtatorView view) {
    super(view);
    this.view = view;
  }

  @Override
  protected void reactToConceptAnnotationUpdated() {

  }

  @Override
  void reactToConceptAnnotationChange(SelectionChangeEvent<ConceptAnnotation> event) {
    if (event.getNew() != null) {
      setText(event.getNew().getId());
    } else {
      setText("");
    }
  }

}
