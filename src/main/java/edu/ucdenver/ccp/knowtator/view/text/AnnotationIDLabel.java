package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

class AnnotationIDLabel extends JLabel {

  AnnotationIDLabel(KnowtatorView view) {

    final ConceptAnnotationCollectionListener conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
      @Override
      public void updated(ConceptAnnotation updatedItem) {

      }

      @Override
      public void noSelection(ConceptAnnotation previousSelection) {

      }

      @Override
      public void selected(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {
        if (currentSelection != null) {
          setText(currentSelection.getId());
        } else {
          setText("");
        }
      }

      @Override
      public void added(ConceptAnnotation addedObject) {

      }

      @Override
      public void removed(ConceptAnnotation removedObject) {

      }

      @Override
      public void emptied(ConceptAnnotation object) {

      }

      @Override
      public void firstAdded(ConceptAnnotation object) {

      }
    };
    TextSourceCollectionListener textSourceCollectionListener = new TextSourceCollectionListener() {
      @Override
      public void updated(TextSource updatedItem) {

      }

      @Override
      public void noSelection(TextSource previousSelection) {

      }

      @Override
      public void selected(TextSource previousSelection, TextSource currentSelection) {
        if (previousSelection != null) {
          previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        currentSelection.getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
      }

      @Override
      public void added(TextSource addedObject) {

      }

      @Override
      public void removed(TextSource removedObject) {

      }

      @Override
      public void emptied(TextSource object) {

      }

      @Override
      public void firstAdded(TextSource object) {

      }
    };

    view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
  }

  public void dispose() {}
}
