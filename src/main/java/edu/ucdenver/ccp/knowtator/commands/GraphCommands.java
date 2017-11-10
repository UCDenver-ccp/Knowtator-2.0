package edu.ucdenver.ccp.knowtator.commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
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
                Annotation selectedAnnotation = manager.getAnnotationManager().getSelectedAnnotation();
                if (selectedAnnotation != null) {
                    manager.getKnowtatorView().getGraphViewer().addAnnotationNode(selectedAnnotation);
                }
            }
        };
    }
}
