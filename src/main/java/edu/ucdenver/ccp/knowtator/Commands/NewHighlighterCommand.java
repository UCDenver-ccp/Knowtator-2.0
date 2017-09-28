package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.KnowtatorSelectionModel;
import edu.ucdenver.ccp.knowtator.ProfileManager;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NewHighlighterCommand extends DisposableAction {

    private ProfileManager profileManager;
    private KnowtatorSelectionModel selectionModel;

    public NewHighlighterCommand(KnowtatorSelectionModel selectionModel, ProfileManager profileManager) {
        super("Add Highlighter Annotator", KnowtatorIcons.getIcon(KnowtatorIcons.NEW_HIGHLIGHTER_ICON));
        this.selectionModel = selectionModel;
        this.profileManager = profileManager;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add a new highlighter profile");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addHighlighterProfile();
    }

    private void addHighlighterProfile() {


        Color c = JColorChooser.showDialog(null, "Highlighter color", Color.BLUE);
        if (c != null) {
            profileManager.addHighlighter(selectionModel.getSelectedClass(), c, profileManager.getCurrentAnnotator());
        }
    }

    //TODO removeProfile
}