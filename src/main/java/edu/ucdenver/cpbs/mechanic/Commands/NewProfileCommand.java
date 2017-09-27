package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ProfileManager;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NewProfileCommand extends DisposableAction {

    private ProfileManager profileManager;

    public NewProfileCommand(ProfileManager profileManager) {
        super("New Annotator", MechAnICIcons.getIcon(MechAnICIcons.NEW_PROFILE_ICON));
        this.profileManager = profileManager;

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add new annotator profile");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        setAnnotator();
    }

    private void setAnnotator() {
        JTextField field1 = new JTextField();
        JTextField field2 = new JTextField();
        Object[] message = {
                "Annotator name", field1,
                "Annotator ID", field2,
        };
        int option = JOptionPane.showConfirmDialog(null, message, "Enter annotator name and ID", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION)
        {
            String annotator = field1.getText();
            String annotatorID = field2.getText();
            profileManager.newProfile(annotator, annotatorID);
        }

    }
}
