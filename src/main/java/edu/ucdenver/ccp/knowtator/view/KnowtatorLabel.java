package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import javax.swing.*;

public abstract class KnowtatorLabel extends JLabel implements KnowtatorComponent {

    private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
    private final KnowtatorCollectionListener<TextSource> textSourceCollectionListener;
    KnowtatorView view;

    KnowtatorLabel(KnowtatorView view) {
        this.view = view;
        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void added(AddEvent<ConceptAnnotation> event) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> event) {

            }

            @Override
            public void changed(ChangeEvent<ConceptAnnotation> event) {
                reactToConceptAnnotationChanged();
            }

            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }


            @Override
            public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
                reactToConceptAnnotationChange(event);

            }
        };
        textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
            @Override
            public void added(AddEvent<TextSource> event) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> event) {

            }

            @Override
            public void changed(ChangeEvent<TextSource> event) {
            }

            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }

            @Override
            public void selected(SelectionChangeEvent<TextSource> event) {
                reactToTextSourceChange(event);
            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    protected abstract void reactToConceptAnnotationChanged();

    private void reactToTextSourceChange(SelectionChangeEvent<TextSource> event) {
        if (event.getOld() != null) {
            event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
    }

    abstract void reactToConceptAnnotationChange(SelectionChangeEvent<ConceptAnnotation> event);


    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    public void dispose() {

    }
}
