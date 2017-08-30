package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import org.apache.log4j.Logger;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class AddNode extends DisposableAction {

    private static final Logger log = Logger.getLogger(MechAnICView.class);
    private MechAnICView view;

    public AddNode(MechAnICView view) {
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
        view.getGraphViewer().addNode("Default");
    }
}
