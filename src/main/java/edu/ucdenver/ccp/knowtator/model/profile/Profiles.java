package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.listeners.ProfilesListener;

import java.util.ArrayList;
import java.util.List;

public class Profiles {

    private final ArrayList<ProfilesListener> listeners;
    private ArrayList<Profile> profiles;

    public Profiles() {
        profiles = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public void add(Profile profile) {
        profiles.add(profile);
        listeners.forEach(profilesListener -> profilesListener.profilesChanged(profile, true));
    }

    public void remove(Profile profile) {
        profiles.remove(profile);
        listeners.forEach(profilesListener -> profilesListener.profilesChanged(profile, false));
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public void addListener(ProfilesListener listener) {
        listeners.add(listener);
    }
}
