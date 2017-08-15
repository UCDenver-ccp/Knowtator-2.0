package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ProfileManager;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class NewHighlighterCommand extends DisposableAction {

    private ProfileManager profileManager;

    public NewHighlighterCommand(ProfileManager profileManager) {
        super("Add Highlighter Profile", MechAnICIcons.getIcon(MechAnICIcons.NEW_HIGHLIGHTER_ICON));
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
        JTextField field1 = new JTextField();
        Object[] message = {
                "Highlighter profile name", field1,
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Enter name for the new highlighter profile", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION)
        {
            String profileName = field1.getText();

            Color c = JColorChooser.showDialog(null, "Highlighter color", Color.BLUE);
            if (c != null) {
                profileManager.addHighlighter(profileName, c, profileManager.getCurrentProfile());
            }
        }

    }

    //TODO removeProfile
}