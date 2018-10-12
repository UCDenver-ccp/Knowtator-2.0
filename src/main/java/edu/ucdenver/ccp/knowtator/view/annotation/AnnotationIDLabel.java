package edu.ucdenver.ccp.knowtator.view.annotation;

import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorLabel;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

public class AnnotationIDLabel extends KnowtatorLabel {
  public AnnotationIDLabel(KnowtatorView view) {
    super(view);
    this.view = view;
  }

  @Override
  protected void reactToConceptAnnotationSelectionChange(SelectionChangeEvent<ConceptAnnotation> event) {
    if (event.getNew() != null) {
      setText(event.getNew().getId());
    } else {
      setText("");
    }
  }

  @Override
  public void reactToConceptAnnotationChange(ChangeEvent<ConceptAnnotation> event) {

  }

}
