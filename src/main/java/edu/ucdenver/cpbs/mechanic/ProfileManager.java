package edu.ucdenver.cpbs.mechanic;

import edu.ucdenver.cpbs.mechanic.Profiles.Profile;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICProfileViewer;

import javax.swing.*;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    private Profile currentProfile;
    private String currentHighlighterName;

    private Map<String, Profile> profiles;
    private ButtonGroup buttonGroup;
    private MechAnICProfileViewer profileViewer;


    ProfileManager() {
        profiles = new HashMap<>();
        buttonGroup = new ButtonGroup();
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

        addHighlighter("Default", Color.RED, newProfile);

        loadProfile(profileName);
    }

    public void addHighlighter(String newHighlighterName, Color c, Profile profile) {
        DefaultHighlighter.DefaultHighlightPainter newHighlighter = new DefaultHighlighter.DefaultHighlightPainter(c);
        JRadioButton btn = new JRadioButton(newHighlighterName);

        profile.addHighlighter(newHighlighterName, newHighlighter, btn);

        btn.setActionCommand(newHighlighterName);
        btn.setForeground(c);
        buttonGroup.add(btn);
        profileViewer.add(btn);
        btn.addActionListener(profileViewer);
        btn.setSelected(true);

        currentHighlighterName = newHighlighterName;
    }

    private void removeCurrentProfile() {
        // Remove current buttons
        for (JRadioButton btn : currentProfile.getRadioButtons()) {
            buttonGroup.remove(btn);
            profileViewer.remove(btn);
            btn.removeActionListener(profileViewer);
        }
    }

    public void loadProfile(String profileName) {
        if (currentProfile != null) {
            removeCurrentProfile();
        }

        // Change active profile
        currentProfile = profiles.get(profileName);
        profileViewer.getProfileLabel().setText(String.format("<html>Name: %s<br>ID: %s<html>", currentProfile.getAnnotatorName(), currentProfile.getAnnotatorID()));

        // Display buttons
        for (JRadioButton btn : currentProfile.getRadioButtons()) {
            buttonGroup.add(btn);
            profileViewer.add(btn);
            btn.addActionListener(profileViewer);
        }
        currentProfile.getRadioButtons().get(0).setSelected(true);
        profileViewer.repaint();
    }

    public DefaultHighlighter.DefaultHighlightPainter getCurrentHighlighter() {
        return currentProfile.getHighlighter(currentHighlighterName);
    }

    public void setCurrentHighlighterName(String highlighterName) {
        this.currentHighlighterName = highlighterName;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }


    public Map<String,Profile> getProfiles() {
        return profiles;
    }

    public String getCurrentHighlighterName() {
        return currentHighlighterName;
    }
}
