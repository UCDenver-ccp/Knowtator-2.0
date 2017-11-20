package edu.ucdenver.ccp.knowtator.profile;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorView;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    private Profile currentProfile;
    private BasicKnowtatorView view;

    private Map<String, Profile> profiles;
    private KnowtatorManager manager;



    public ProfileManager(KnowtatorManager manager) {
        this.manager = manager;
        profiles = new HashMap<>();
        addNewProfile("Default");
    }

    private void setKnowtatorView(KnowtatorView view) {
        this.view = view;
    }

    public Profile addNewProfile(String profileID) {
        if (profiles.containsKey(profileID)) {
            return profiles.get(profileID);
        }

        Profile newProfile = new Profile(manager, profileID);
        profiles.put(profileID, newProfile);

        currentProfile = newProfile;

        if (view != null) view.profileAddedEvent(currentProfile);

        return newProfile;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void switchAnnotator(Profile profile) {
        currentProfile = profile;
        view.profileSelectionChangedEvent(currentProfile);
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile.getProfileID());
        view.profileRemovedEvent();

    }

    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    public void setView(BasicKnowtatorView view) {
        this.view = view;
    }
}
