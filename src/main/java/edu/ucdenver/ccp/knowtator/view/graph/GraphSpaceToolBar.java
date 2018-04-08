package edu.ucdenver.ccp.knowtator.view.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;

import javax.swing.*;

class GraphSpaceToolBar extends JToolBar {

    private GraphViewer graphViewer;
    private JComboBox<GraphSpace> graphSpaceChooser;
    private KnowtatorController controller;

    GraphSpaceToolBar(GraphViewer graphViewer, KnowtatorController controller) {
        this.graphViewer = graphViewer;
        this.controller = controller;

        setFloatable(false);

        add(previousGraphSpaceCommand());
        add(nextGraphSpaceCommand());
        add(graphSpaceChooserCommand());

    }

    private JComboBox<GraphSpace> graphSpaceChooserCommand() {
        graphSpaceChooser = new JComboBox<>();

        graphSpaceChooser.addActionListener(e -> {
            JComboBox comboBox = (JComboBox) e.getSource();
            if (comboBox.getSelectedItem() != null && comboBox.getSelectedItem() != controller.getSelectionManager().getActiveTextSource()) {
                graphViewer.showGraph((GraphSpace) comboBox.getSelectedItem());
            }
        });

        return graphSpaceChooser;
    }

    void update(GraphSpace graphSpace) {
        graphSpaceChooser.removeAllItems();
        graphViewer.getGraphSpaceMap().keySet().forEach(textSource1 -> graphSpaceChooser.addItem(textSource1));
        graphSpaceChooser.setSelectedItem(graphSpace);
    }

    private JButton previousGraphSpaceCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.UP_ICON));
        command.setToolTipText("Previous graph space");
        command.addActionListener(e -> graphViewer.showPreviousGraphSpace());
        return command;
    }

    private JButton nextGraphSpaceCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.DOWN_ICON));
        command.setToolTipText("Next graph space");
        command.addActionListener(e -> graphViewer.showNextGraphSpace());
        return command;
    }
}
