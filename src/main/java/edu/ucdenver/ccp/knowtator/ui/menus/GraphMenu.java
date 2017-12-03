package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.graph.GraphDialog;

import javax.swing.*;

public class GraphMenu extends JMenu {

    private GraphDialog graphDialog;

    public GraphMenu(GraphDialog graphDialog) {
        super("Graph");
        this.graphDialog = graphDialog;

        add(addNewGraphCommand());
        add(deleteGraphCommand());
    }

    private JMenuItem deleteGraphCommand() {
        JMenuItem deleteGraphMenuItem = new JMenuItem("Delete graph");
        deleteGraphMenuItem.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this graph?") == JOptionPane.YES_OPTION) {
                graphDialog.getGraphViewer().deleteSelectedGraph();
            }
        });

        return deleteGraphMenuItem;
    }

    private JMenuItem addNewGraphCommand() {
        JMenuItem addNewGraphMenuItem = new JMenuItem("Create new graph");
        addNewGraphMenuItem.addActionListener(e -> {
            JTextField field1 = new JTextField();
            Object[] message = {
                    "Graph Title", field1,
            };
            int option = JOptionPane.showConfirmDialog(null, message, "Enter a name for this graph", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                graphDialog.getGraphViewer().addNewGraph(field1.getText());

            }

        });

        return addNewGraphMenuItem;
    }
}
