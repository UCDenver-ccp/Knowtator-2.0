package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

import javax.swing.*;

public class AnnotationClassLabel extends JLabel implements OWLModelManagerListener {

    private KnowtatorView view;
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(AnnotationClassLabel.class);

    AnnotationClassLabel(KnowtatorView view) {
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
                displayAnnotation(currentSelection);
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

    private void displayAnnotation(ConceptAnnotation conceptAnnotation) {
        if (conceptAnnotation != null) {
            try {
                setText(view.getController().getOWLManager().getOWLEntityRendering(conceptAnnotation.getOwlClass()));
            } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
                setText(String.format("ID: %s Label: %s", conceptAnnotation.getOwlClassID(), conceptAnnotation.getOwlClassLabel()));
            }
        } else {
            setText("");
        }
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
            ConceptAnnotation conceptAnnotation = view.getController()
                    .getTextSourceCollection().getSelection()
                    .getConceptAnnotationCollection().getSelection();
            displayAnnotation(conceptAnnotation);
        }
    }

    public void dispose() {
        try {
            view.getController().getOWLManager().getWorkSpace().getOWLModelManager().removeListener(this);
        } catch (OWLWorkSpaceNotSetException ignored) {
        }
    }
}
