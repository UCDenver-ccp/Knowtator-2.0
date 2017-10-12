package edu.ucdenver.ccp.knowtator.Profiles;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.Profiles.Annotator;
import edu.ucdenver.ccp.knowtator.ui.ListDialog;

import java.util.HashMap;
import java.util.Map;

public class AnnotatorManager {

    public Annotator currentAnnotator;

    public Map<String, Annotator> profiles;
    public KnowtatorView view;


    public AnnotatorManager(KnowtatorView view) {
        this.view = view;
        profiles = new HashMap<>();
    }

    public void setupDefault() {
        addNewProfile("Default", "Default");
    }

    public void addNewProfile(String profileName, String profileID) {
        Annotator newAnnotator = new Annotator(view, profileName, profileID);
        profiles.put(profileName, newAnnotator);

        loadProfile(profileName);
    }

    public void removeCurrentProfile() {
    }

    public void loadProfile(String profileName) {
        if (currentAnnotator != null) removeCurrentProfile();

        // Change active profile
        currentAnnotator = profiles.get(profileName);
    }

    public Annotator getCurrentAnnotator() {
        return currentAnnotator;
    }

    public String[] getProfileNames() {
        return profiles.keySet().toArray(new String[profiles.keySet().size()]);
    }

    public void switchProfile() {
        String[] profiles = getProfileNames();
        String profileName = ListDialog.showDialog(null, null, "Profiles", "Annotator Chooser", profiles, profiles[0], null);

        if (profileName != null)
        {
            loadProfile(profileName);
        }

    }
}
