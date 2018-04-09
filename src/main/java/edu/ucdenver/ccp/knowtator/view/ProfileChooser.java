package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.ProfilesListener;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;

import javax.swing.*;

public class ProfileChooser extends JComboBox<Profile> implements ProfilesListener, SelectionListener {

    private KnowtatorController controller;

    ProfileChooser(KnowtatorController controller) {
        super(controller.getProfileManager().getProfiles().getProfiles().toArray(new Profile[0]));
        this.controller = controller;
        controller.getProfileManager().getProfiles().addListener(this);
    }

    @Override
    public void profilesChanged(Profile profile, Boolean wasAdded) {
        if (wasAdded) {
            addItem(profile);
        } else {
            removeItem(profile);
        }
    }

    @Override
    public void selectedAnnotationChanged() {

    }

    @Override
    public void selectedSpanChanged() {

    }

    @Override
    public void activeGraphSpaceChanged() {

    }

    @Override
    public void activeTextSourceChanged() {

    }

    @Override
    public void currentProfileChange() {
        setSelectedItem(controller.getSelectionManager().getActiveProfile());
    }
}
