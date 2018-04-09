package edu.ucdenver.ccp.knowtator.listeners;

import edu.ucdenver.ccp.knowtator.model.profile.Profile;

public interface ProfilesListener {
    void profilesChanged(Profile profile, Boolean wasAdded);
}
