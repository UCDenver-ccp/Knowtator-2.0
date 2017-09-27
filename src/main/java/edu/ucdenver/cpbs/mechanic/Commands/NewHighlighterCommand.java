package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICSelectionModel;
import edu.ucdenver.cpbs.mechanic.ProfileManager;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NewHighlighterCommand extends DisposableAction {

    private ProfileManager profileManager;
    private MechAnICSelectionModel selectionModel;

    public NewHighlighterCommand(MechAnICSelectionModel selectionModel, ProfileManager profileManager) {
        super("Add Highlighter Annotator", MechAnICIcons.getIcon(MechAnICIcons.NEW_HIGHLIGHTER_ICON));
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