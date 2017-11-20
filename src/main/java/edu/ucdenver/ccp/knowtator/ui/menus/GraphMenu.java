package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;

public class GraphMenu extends JMenu {

    private BasicKnowtatorView view;

    public GraphMenu(BasicKnowtatorView view) {
        super("Graph");
        this.view = view;

        add(displayAnnotationAsNode());
    }

    private JMenuItem displayAnnotationAsNode() {
        JMenuItem menuItem = new JMenuItem("Add Node");
        menuItem.addActionListener(e -> {

            Annotation selectedAnnotation = view.getTextViewer().getSelectedTextPane().getSelectedAnnotation();
            if (selectedAnnotation != null) {
                view.getGraphViewer().addAnnotationNode(selectedAnnotation);
            }
        });

        return menuItem;
    }

    //TODO: Go to annotation node
    //TODO: Show graph viewer/ hide graph viewer
    //TODO: Display node does not appear to be working
}
