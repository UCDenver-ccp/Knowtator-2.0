package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotationGraphSpaceChooser extends Chooser<GraphSpace> implements ViewListener {

    private KnowtatorView view;

    public AnnotationGraphSpaceChooser(KnowtatorView view) {
        super(view);
        this.view = view;
        view.getController().addViewListener(this);
    }

    @Override
    public void added(GraphSpace graphSpace) {
        if (graphSpace.containsAnnotation(
                view.getController()
                        .getTextSourceManager().getSelection()
                        .getAnnotationManager().getSelection())) {
            addItem(graphSpace);
        }
    }

    @Override
    public void viewChanged() {
        setModel(
                new DefaultComboBoxModel<>(
                        view.getController()
                                .getTextSourceManager().getSelection()
                                .getGraphSpaceManager()
                                .stream().filter(
                                graphSpace ->
                                        graphSpace.containsAnnotation(
                                                view.getController()
                                                        .getTextSourceManager().getSelection()
                                                        .getAnnotationManager().getSelection()))
                                .toArray(GraphSpace[]::new)));
    }
}
