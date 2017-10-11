package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.KnowtatorSelectionModel;
import edu.ucdenver.ccp.knowtator.ProfileManager;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NewHighlighterCommand extends DisposableAction {

    private KnowtatorView view;

    public NewHighlighterCommand(KnowtatorView view) {
        super("Add Highlighter Annotator", KnowtatorIcons.getIcon(KnowtatorIcons.NEW_HIGHLIGHTER_ICON));
        this.view = view;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add a new highlighter profile");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addHighlighterProfile();
    }

    public void addHighlighterProfile() {


        Color c = JColorChooser.showDialog(null, "Highlighter color", Color.BLUE);
        if (c != null) {
            view.getProfileManager().addHighlighter(view.getSelectionModel().getSelectedClass(), c, null);
        }
    }

    //TODO removeProfile
}