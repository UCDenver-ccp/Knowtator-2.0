package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.iaa.Annotation;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddTextAnnotationNodeCommand extends DisposableAction {

//    private static final Logger log = Logger.getLogger(MechAnICView.class);
    private MechAnICView view;

    public AddTextAnnotationNodeCommand(MechAnICView view) {
        super("Add Node", MechAnICIcons.getIcon("None"));
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
        Annotation selectedAnnotation = ((MechAnICTextViewer) ((JScrollPane) (view.getTextViewerTabbedPane().getSelectedComponent())).getViewport().getView()).getTextAnnotationManager().getSelectedAnnotation();
        if (selectedAnnotation != null) {
            view.getGraphViewer().addNode(selectedAnnotation);
        }
    }
}
