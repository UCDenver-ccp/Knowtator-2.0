package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public abstract class KnowtatorCommand extends DisposableAction {

    public KnowtatorManager manager;

    public KnowtatorCommand(KnowtatorManager manager, String name, String iconName, String description) {
        super(name, KnowtatorIcons.getIcon(iconName));
        this.manager = manager;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, description);
    }

    @Override
    public void dispose() {

    }

    @Override
    public abstract void actionPerformed(ActionEvent e);
}
