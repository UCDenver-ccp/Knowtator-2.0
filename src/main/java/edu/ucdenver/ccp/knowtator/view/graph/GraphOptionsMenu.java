package edu.ucdenver.ccp.knowtator.view.graph;

import javax.swing.*;

class GraphOptionsMenu extends JMenu {


    private GraphViewer graphViewer;

    GraphOptionsMenu(GraphViewer graphViewer) {
        super("Options");
        this.graphViewer = graphViewer;


//        add(displayAllAnnotationsCommand());
        add(applyLayoutCommand());
    }


    private JMenuItem applyLayoutCommand() {
        JMenuItem menuItem = new JMenuItem("Apply layout");
        menuItem.addActionListener(e -> graphViewer.executeLayout());
        return menuItem;
    }
}
