package edu.ucdenver.ccp.knowtator.view.graph;

import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;
import org.apache.log4j.Logger;

import javax.swing.*;

class GraphToolBar extends JToolBar {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(GraphToolBar.class);

    private GraphViewer graphViewer;

    GraphToolBar(GraphViewer graphViewer) {

        this.graphViewer = graphViewer;

        setFloatable(false);

        add(addGraphNodeCommand());
        add(removeCellCommand());
        //TODO: Add arrows to switch between graph spaces
    }

    private JButton removeCellCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE));
        button.setToolTipText("Remove vertex or edge");

        button.addActionListener(e -> graphViewer.removeSelectedCell());

        return button;
    }

    private JButton addGraphNodeCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.ADD));
        button.setToolTipText("Add annotation as node");

        button.addActionListener(e -> graphViewer.addSelectedAnnotationAsVertex());

        return button;
    }


}
