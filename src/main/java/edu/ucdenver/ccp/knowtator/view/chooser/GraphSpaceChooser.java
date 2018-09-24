package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class GraphSpaceChooser extends Chooser<GraphSpace> {


    public GraphSpaceChooser(KnowtatorView view) {
        super(view);
    }

    public void reactToTextSourceChange(TextSource previousSelection, TextSource currentSelection) {
        setModel(new DefaultComboBoxModel<>(currentSelection
                .getGraphSpaceCollection().toArray(new GraphSpace[0])));
    }

    @Override
    void reactToAnnotationChange(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {

    }
}
