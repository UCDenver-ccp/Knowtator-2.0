package edu.ucdenver.ccp.knowtator.ui.menus;

import com.mxgraph.swing.mxGraphComponent;
import edu.ucdenver.ccp.knowtator.ui.graph.GraphDialog;

import javax.swing.*;

public class GraphViewMenu extends JMenu {

    private GraphDialog graphDialog;

    public GraphViewMenu(GraphDialog graphDialog) {
        super("View");
        this.graphDialog = graphDialog;

        add(zoomInCommand());
        add(zoomOutCommand());
    }

    private JMenuItem zoomInCommand() {
        JMenuItem menuItem = new JMenuItem("Zoom In");
        menuItem.addActionListener(e -> ((mxGraphComponent) graphDialog.getGraphViewer().getSelectedComponent()).zoomIn());
        return menuItem;
    }

    private JMenuItem zoomOutCommand() {
        JMenuItem menuItem = new JMenuItem("Zoom Out");
        menuItem.addActionListener(e -> ((mxGraphComponent) graphDialog.getGraphViewer().getSelectedComponent()).zoomOut());
        return menuItem;
    }

}
