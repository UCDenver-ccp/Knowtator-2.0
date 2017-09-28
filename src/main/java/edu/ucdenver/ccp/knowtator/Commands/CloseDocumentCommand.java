package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class CloseDocumentCommand extends DisposableAction {

    private JTabbedPane tabbedPane;
    private KnowtatorView view;

    public CloseDocumentCommand(KnowtatorView view) {
        super("Close", KnowtatorIcons.getIcon(KnowtatorIcons.CLOSE_DOCUMENT_ICON));
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Close");
        this.tabbedPane = view.getTextViewer();
        this.view = view;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int selectedComp = tabbedPane.getSelectedIndex();
        if (tabbedPane.getTabCount() > 1) {
            tabbedPane.remove(selectedComp);
        } else {
            tabbedPane.setComponentAt(0, new KnowtatorTextPane(view));
            tabbedPane.setTitleAt(0, "Untitled");
        }



    }


    @Override
    public void dispose() {

    }

}
