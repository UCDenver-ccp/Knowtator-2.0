package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotationGraphSpaceChooser extends Chooser<GraphSpace> {
    private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;
    private TextSourceCollectionListener textSourceCollectionListener;


    public AnnotationGraphSpaceChooser(KnowtatorView view) {
        super(view);
        conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
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
                reactToAnnotationChange(event.getNew());
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
                reactToTextSourceChange(event.getOld(), event.getNew());
            }

        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    @Override
    public void added(AddEvent<GraphSpace> event) {
        if (event.getAdded().containsAnnotation(
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection().getSelection())) {
            addItem(event.getAdded());
        }
    }

    @Override
    public void updated(GraphSpace updatedItem) {
        //TODO: This should update when a node is added
        reactToAnnotationChange(updatedItem.getTextSource().getConceptAnnotationCollection().getSelection());
    }

    private void reactToTextSourceChange(TextSource previousSelection, TextSource currentSelection) {
        if (previousSelection != null) {
            previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        currentSelection.getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
    }

    private void reactToAnnotationChange(ConceptAnnotation currentSelection) {
        setModel(new DefaultComboBoxModel<>(currentSelection.getTextSource()
                .getGraphSpaceCollection()
                .stream().filter(graphSpace -> graphSpace.containsAnnotation(currentSelection))
                .toArray(GraphSpace[]::new)));
    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }
}
