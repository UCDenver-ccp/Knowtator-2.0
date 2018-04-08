package edu.ucdenver.ccp.knowtator.view.graph;

import javax.swing.*;

class GraphViewMenu extends JMenu {


    private GraphViewer graphViewer;

    GraphViewMenu(GraphViewer graphViewer) {
        super("View");
        this.graphViewer = graphViewer;

        updateMenus();
    }

    private JMenu goToGraphMenu() {
        JMenu menu = new JMenu("Go to graph");
        graphViewer.getGraphSpaceMap().keySet()
                .forEach(graphSpace -> {
                    JMenuItem menuItem = new JMenuItem(graphSpace.getId());
                    menuItem.addActionListener(e -> graphViewer.showGraph(graphSpace));
                    menu.add(menuItem);
                });
        return menu;
    }

    private JMenuItem zoomInCommand() {
        JMenuItem menuItem = new JMenuItem("Zoom In");
        menuItem.addActionListener(e -> graphViewer.zoomIn());
        return menuItem;
    }

    private JMenuItem zoomOutCommand() {
        JMenuItem menuItem = new JMenuItem("Zoom Out");
        menuItem.addActionListener(e -> graphViewer.zoomOut());
        return menuItem;
    }

    void updateMenus() {
        removeAll();
        add(zoomInCommand());
        add(zoomOutCommand());
        addSeparator();
        add(goToGraphMenu());
    }
}
