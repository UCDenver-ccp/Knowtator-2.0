package edu.ucdenver.ccp.knowtator.view.text.concept;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;

public class AnnotatorLabel extends JLabel implements KnowtatorViewComponent {

	private TextSourceCollectionListener textSourceCollectionListener;
	private KnowtatorView view;


	AnnotatorLabel(KnowtatorView view) {
		this.view = view;
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
					setText(currentSelection.getAnnotator().getId());
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
		textSourceCollectionListener = new TextSourceCollectionListener() {
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

	@Override
	public void reset() {
		view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
	}

	public void dispose() {

	}

}
