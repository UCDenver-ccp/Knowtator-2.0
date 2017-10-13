package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;

import java.awt.event.ActionEvent;

public class GraphCommands {

    private KnowtatorManager manager;

    public GraphCommands(KnowtatorManager manager) {

        this.manager = manager;
    }

    public KnowtatorCommand getAddTextAnnotationNodeCommand() {
        return new KnowtatorCommand(manager, "Add Node", KnowtatorIcons.SHOW_NODE_ICON, "Add a node") {


            @Override
            public void actionPerformed(ActionEvent e) {
                TextAnnotation selectedTextAnnotation = manager.getTextAnnotationManager().getSelectedTextAnnotation();
                if (selectedTextAnnotation != null) {
                    manager.getKnowtatorView().getGraphViewer().addAnnotationNode(selectedTextAnnotation);
                }
            }
        };
    }
}
