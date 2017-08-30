package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CloseDocumentCommand extends DisposableAction {

    private JTabbedPane tabbedPane;
    private MechAnICView view;

    public CloseDocumentCommand(MechAnICView view) {
        super("Close", MechAnICIcons.getIcon(MechAnICIcons.CLOSE_DOCUMENT_ICON));
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Close");
        this.tabbedPane = view.getTabbedPane();
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedComp = tabbedPane.getSelectedIndex();
        if (tabbedPane.getTabCount() > 1) {
            tabbedPane.remove(selectedComp);
        } else {
            tabbedPane.setComponentAt(0, new MechAnICTextViewer(view));
            tabbedPane.setTitleAt(0, "Untitled");
        }



    }


    @Override
    public void dispose() {

    }

}
