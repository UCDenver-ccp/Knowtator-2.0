package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class IncrementSelectionRightCommand extends DisposableAction {

    private final JTabbedPane tabbedPane;

    public IncrementSelectionRightCommand(MechAnICView view) {
        super("Increment right selection", MechAnICIcons.getIcon(MechAnICIcons.INCREMENT_RIGT));
        this.tabbedPane = view.getTextViewerTabbedPane();

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Increment selection right");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MechAnICTextViewer textViewer = (MechAnICTextViewer) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
        textViewer.setSelectionEnd(textViewer.getSelectionEnd() + 1);

    }
}
