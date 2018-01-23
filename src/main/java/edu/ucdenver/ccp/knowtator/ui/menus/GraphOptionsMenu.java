package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.graph.GraphDialog;

import javax.swing.*;

public class GraphOptionsMenu extends JMenu {

    private GraphDialog graphDialog;

    public GraphOptionsMenu(GraphDialog graphDialog) {
        super("Options");

        this.graphDialog = graphDialog;

        add(displayAllAnnotationsCommand());
    }

    private JMenuItem displayAllAnnotationsCommand() {
        JMenuItem menuItem = new JMenuItem("Display all annotations");
        menuItem.addActionListener(e -> graphDialog.getGraphViewer().addAllAnnotations());

        return menuItem;
    }
}
