package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.Profiles.Annotator;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    private Annotator currentAnnotator;

    private Map<String, Annotator> profiles;
    private KnowtatorView view;


    ProfileManager(KnowtatorView view) {
        this.view = view;
        profiles = new HashMap<>();
    }

    void setupDefault() {
        addNewProfile("Default", "Default");
    }

    public void addNewProfile(String profileName, String profileID) {
        Annotator newAnnotator = new Annotator(profileName, profileID);
        profiles.put(profileName, newAnnotator);

        loadProfile(profileName);
    }

    public void addHighlighter(OWLClass cls, Color c, Annotator annotator) {
        DefaultHighlighter.DefaultHighlightPainter newHighlighter = new DefaultHighlighter.DefaultHighlightPainter(c);
        annotator.addHighlighter(cls, newHighlighter);

        for(OWLClass decendent: view.getOWLModelManager().getOWLHierarchyManager().getOWLClassHierarchyProvider().getDescendants(cls)) {
            annotator.addHighlighter(decendent, newHighlighter);
        }
    }

    private void removeCurrentProfile() {
    }

    public void loadProfile(String profileName) {
        if (currentAnnotator != null) removeCurrentProfile();

        // Change active profile
        currentAnnotator = profiles.get(profileName);
    }

    public Annotator getCurrentAnnotator() {
        return currentAnnotator;
    }


    public Map<String,Annotator> getProfiles() {
        return profiles;
    }
}
