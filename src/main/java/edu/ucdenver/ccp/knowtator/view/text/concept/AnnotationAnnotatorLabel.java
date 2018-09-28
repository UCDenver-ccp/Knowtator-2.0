package edu.ucdenver.ccp.knowtator.view.text.concept;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;

public class AnnotationAnnotatorLabel extends JLabel implements KnowtatorViewComponent {

	private KnowtatorCollectionListener<TextSource> textSourceCollectionListener;
	private KnowtatorView view;


    public AnnotationAnnotatorLabel(KnowtatorView view) {
		this.view = view;
		final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
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
					setText(event.getNew().getAnnotator().getId());
				} else {
					setText("");
				}

			}
		};
		textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
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

	public void dispose() {

	}

}
