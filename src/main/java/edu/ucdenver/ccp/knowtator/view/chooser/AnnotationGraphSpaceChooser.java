package edu.ucdenver.ccp.knowtator.view.chooser;

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
            public void updated(ConceptAnnotation updatedItem) {

            }

            @Override
            public void noSelection(ConceptAnnotation previousSelection) {

            }

            @Override
            public void selected(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {
                reactToAnnotationChange(currentSelection);
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
                reactToTextSourceChange(previousSelection, currentSelection);
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
    public void added(GraphSpace graphSpace) {
        if (graphSpace.containsAnnotation(
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection().getSelection())) {
            addItem(graphSpace);
        }
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
