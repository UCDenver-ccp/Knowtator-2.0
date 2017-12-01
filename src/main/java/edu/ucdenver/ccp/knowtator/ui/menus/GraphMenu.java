package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;

public class GraphMenu extends JMenu {

    private BasicKnowtatorView view;

    public GraphMenu(BasicKnowtatorView view) {
        super("Graph");
        this.view = view;

        add(showGraphViewerCommand());
        addSeparator();
        add(addNewGraphCommand());

    }

    private JMenuItem showGraphViewerCommand() {
        JMenuItem menuItem = new JMenuItem("Show graph viewer");
        menuItem.addActionListener(e -> view.getGraphDialog().setVisible(true));

        return menuItem;
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
                view.getGraphDialog().getGraphViewer().addNewGraph(field1.getText());

            }

        });

        return addNewGraphMenuItem;
    }
}
