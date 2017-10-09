package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class IncrementSelectionRightCommand extends DisposableAction {

    private final KnowtatorTextViewer textViewer;

    public IncrementSelectionRightCommand(KnowtatorView view) {
        super("Increment right selection", KnowtatorIcons.getIcon(KnowtatorIcons.INCREMENT_LEFT));
        this.textViewer = view.getTextViewer();

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Increment selection right");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        KnowtatorTextPane textPane = textViewer.getSelectedTextPane();
        textPane.setSelectionEnd(textPane.getSelectionEnd() + 1);

    }
}
