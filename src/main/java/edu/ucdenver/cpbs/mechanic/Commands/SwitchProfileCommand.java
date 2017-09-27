package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.ProfileManager;
import edu.ucdenver.cpbs.mechanic.ui.ListDialog;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SwitchProfileCommand extends DisposableAction {

    private ProfileManager profileManager;

    public SwitchProfileCommand(ProfileManager profileManager) {
        super("Switch Annotator", MechAnICIcons.getIcon(MechAnICIcons.SWITCH_PROFILE_ICON));
        this.profileManager = profileManager;

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Switch between profiles");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switchProfile();
    }

    private void switchProfile() {
        String[] profiles = profileManager.getProfiles().keySet().toArray(new String[profileManager.getProfiles().keySet().size()]);
        String profileName = ListDialog.showDialog(null, null, "Profiles", "Annotator Chooser", profiles, profiles[0], null);

        if (profileName != null)
        {
            profileManager.loadProfile(profileName);
        }

    }
}
