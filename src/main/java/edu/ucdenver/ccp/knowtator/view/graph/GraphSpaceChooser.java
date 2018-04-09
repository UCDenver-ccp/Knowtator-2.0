package edu.ucdenver.ccp.knowtator.view.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpacesListener;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;

import javax.swing.*;

public class GraphSpaceChooser extends JComboBox<GraphSpace> implements GraphSpacesListener, SelectionListener {
    private KnowtatorController controller;

    GraphSpaceChooser(KnowtatorController controller) {
        this.controller = controller;
    }

    @Override
    public void selectedAnnotationChanged() {

    }

    @Override
    public void selectedSpanChanged() {

    }

    @Override
    public void activeGraphSpaceChanged() {
        setSelectedItem(controller.getSelectionManager().getActiveGraphSpace());
    }

    @Override
    public void activeTextSourceChanged() {
        setModel(new DefaultComboBoxModel<>(controller.getSelectionManager().getActiveTextSource().getAnnotationManager().getGraphSpaces().getGraphSpaces().toArray(new GraphSpace[0])));
        controller.getSelectionManager().getActiveTextSource().getAnnotationManager().getGraphSpaces().addListener(this);
    }

    @Override
    public void currentProfileChange() {

    }

    @Override
    public void GraphSpacesChanged(GraphSpace graphSpace, Boolean wasAdded) {
        if (wasAdded) {
            addItem(graphSpace);
        } else {
            removeItem(graphSpace);
        }
    }
}
