package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SwitchProfileCommand extends DisposableAction {

    public KnowtatorView view;

    public SwitchProfileCommand(KnowtatorView view) {
        super("Switch Annotator", KnowtatorIcons.getIcon(KnowtatorIcons.SWITCH_PROFILE_ICON));
        this.view = view;

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Switch between profiles");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        view.getAnnotatorManager().switchProfile();
    }


}
