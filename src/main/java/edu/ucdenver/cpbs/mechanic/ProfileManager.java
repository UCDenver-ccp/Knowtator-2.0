package edu.ucdenver.cpbs.mechanic;

import edu.ucdenver.cpbs.mechanic.Profiles.Profile;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICProfileViewer;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    private Profile currentProfile;

    private Map<String, Profile> profiles;
    private MechAnICProfileViewer profileViewer;
    private MechAnICView view;


    ProfileManager(MechAnICView view) {
        this.view = view;
        profiles = new HashMap<>();
    }

    void setProfileViewer(MechAnICProfileViewer profileViewer) {
        this.profileViewer = profileViewer;
    }

    void setupDefault() {
        newProfile("Default", "Default");
    }

    public void newProfile(String profileName, String profileID) {
        Profile newProfile = new Profile(profileName, profileID);
        profiles.put(profileName, newProfile);

        loadProfile(profileName);
    }

    public void addHighlighter(OWLClass cls, Color c, Profile profile) {
        DefaultHighlighter.DefaultHighlightPainter newHighlighter = new DefaultHighlighter.DefaultHighlightPainter(c);
        profile.addHighlighter(cls, newHighlighter);
        int i = 0;

        for(OWLClass decendent: view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls)) {
            i++;
            profile.addHighlighter(decendent, newHighlighter);
        }
        System.out.println(i);
    }

    private void removeCurrentProfile() {
    }

    public void loadProfile(String profileName) {
        if (currentProfile != null) {
            removeCurrentProfile();
        }

        // Change active profile
        currentProfile = profiles.get(profileName);
        profileViewer.getProfileLabel().setText(String.format("<html>Name: %s<br>ID: %s<html>", currentProfile.getAnnotatorName(), currentProfile.getAnnotatorID()));

        profileViewer.repaint();
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }


    public Map<String,Profile> getProfiles() {
        return profiles;
    }
}
