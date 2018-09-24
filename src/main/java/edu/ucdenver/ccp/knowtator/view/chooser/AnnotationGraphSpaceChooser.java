package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotationGraphSpaceChooser extends Chooser<GraphSpace> {

    private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;


    public AnnotationGraphSpaceChooser(KnowtatorView view) {
        super(view);
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

    @Override
    void reactToTextSourceChange(TextSource previousSelection, TextSource currentSelection) {
        if (previousSelection != null) {
            previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        currentSelection.getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
    }

    @Override
    void reactToAnnotationChange(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {
        setModel(new DefaultComboBoxModel<>(currentSelection.getTextSource()
                .getGraphSpaceCollection()
                .stream().filter(graphSpace -> graphSpace.containsAnnotation(currentSelection))
                .toArray(GraphSpace[]::new)));
    }

}
