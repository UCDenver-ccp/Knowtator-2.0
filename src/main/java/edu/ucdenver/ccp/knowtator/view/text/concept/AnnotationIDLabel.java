package edu.ucdenver.ccp.knowtator.view.text.concept;

import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;

class AnnotationIDLabel extends JLabel implements KnowtatorViewComponent {
  private TextSourceCollectionListener textSourceCollectionListener;
  private KnowtatorView view;


  AnnotationIDLabel(KnowtatorView view) {
    this.view = view;

    final ConceptAnnotationCollectionListener conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
      @Override
      public void added(AddEvent<ConceptAnnotation> addedObject) {

      }

      @Override
      public void removed(RemoveEvent<ConceptAnnotation> removedObject) {

      }

      @Override
      public void changed(ChangeEvent<ConceptAnnotation> changeEvent) {

      }

      @Override
      public void emptied(RemoveEvent<ConceptAnnotation> object) {

      }

      @Override
      public void firstAdded(AddEvent<ConceptAnnotation> object) {

      }

      @Override
      public void updated(ConceptAnnotation updatedItem) {

      }

      @Override
      public void noSelection(ConceptAnnotation previousSelection) {

      }

      @Override
      public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
        if (event.getNew() != null) {
          setText(event.getNew().getId());
        } else {
          setText("");
        }
      }

    };
    textSourceCollectionListener = new TextSourceCollectionListener() {
      @Override
      public void added(AddEvent<TextSource> addedObject) {

      }

      @Override
      public void removed(RemoveEvent<TextSource> removedObject) {

      }

      @Override
      public void changed(ChangeEvent<TextSource> changeEvent) {

      }

      @Override
      public void emptied(RemoveEvent<TextSource> object) {

      }

      @Override
      public void firstAdded(AddEvent<TextSource> object) {

      }

      @Override
      public void updated(TextSource updatedItem) {

      }

      @Override
      public void noSelection(TextSource previousSelection) {

      }

      @Override
      public void selected(SelectionChangeEvent<TextSource> event) {
        if (event.getOld() != null) {
          event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
      }
    };

    view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
  }

  @Override
  public void reset() {
    view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
  }

  public void dispose() {}
}
