package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddTextAnnotationNodeCommand extends DisposableAction {

//    private static final Logger log = Logger.getLogger(KnowtatorView.class);
    private KnowtatorView view;

    public AddTextAnnotationNodeCommand(KnowtatorView view) {
        super("Add Node", KnowtatorIcons.getIcon(KnowtatorIcons.SHOW_NODE_ICON));
        this.view = view;


        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add a node");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addNode();
    }

    private void addNode() {
        TextAnnotation selectedTextAnnotation = view.getTextAnnotationManager().getSelectedTextAnnotation();
        if (selectedTextAnnotation != null) {
            view.getGraphViewer().addAnnotationNode(selectedTextAnnotation);
        }
    }
}
