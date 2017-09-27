package edu.ucdenver.cpbs.mechanic;

import edu.ucdenver.cpbs.mechanic.Profiles.Annotator;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICProfileViewer;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    private Annotator currentAnnotator;

    private Map<String, Annotator> profiles;
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
        Annotator newAnnotator = new Annotator(profileName, profileID);
        profiles.put(profileName, newAnnotator);

        loadProfile(profileName);
    }

    public void addHighlighter(OWLClass cls, Color c, Annotator annotator) {
        DefaultHighlighter.DefaultHighlightPainter newHighlighter = new DefaultHighlighter.DefaultHighlightPainter(c);
        annotator.addHighlighter(cls, newHighlighter);
        int i = 0;

        for(OWLClass decendent: view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls)) {
            i++;
            annotator.addHighlighter(decendent, newHighlighter);
        }
    }

    private void removeCurrentProfile() {
    }

    public void loadProfile(String profileName) {
        if (currentAnnotator != null) {
            removeCurrentProfile();
        }

        // Change active profile
        currentAnnotator = profiles.get(profileName);
        profileViewer.getProfileLabel().setText(String.format("<html>Name: %s<br>ID: %s<html>", currentAnnotator.getAnnotatorName(), currentAnnotator.getAnnotatorID()));

        profileViewer.repaint();
    }

    public Annotator getCurrentAnnotator() {
        return currentAnnotator;
    }


    public Map<String,Annotator> getProfiles() {
        return profiles;
    }
}
