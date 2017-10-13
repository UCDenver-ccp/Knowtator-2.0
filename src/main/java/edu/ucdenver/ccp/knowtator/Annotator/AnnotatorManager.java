package edu.ucdenver.ccp.knowtator.Annotator;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.ListDialog;

import java.util.HashMap;
import java.util.Map;

public class AnnotatorManager {

    public Annotator currentAnnotator;

    public Map<String, Annotator> annotatorMap;
    public KnowtatorManager manager;


    public AnnotatorManager(KnowtatorManager manager) {
        this.manager = manager;
        annotatorMap = new HashMap<>();
    }

    public void addNewAnnotator(String annotatorName, String annotatorID) {
        Annotator newAnnotator = new Annotator(manager, annotatorName, annotatorID);
        annotatorMap.put(annotatorName, newAnnotator);

        loadProfile(annotatorName);
    }

    public void removeCurrentProfile() {
    }

    public void loadProfile(String profileName) {
        if (currentAnnotator != null) removeCurrentProfile();

        // Change active annotator
        currentAnnotator = annotatorMap.get(profileName);
    }

    public Annotator getCurrentAnnotator() {
        return currentAnnotator;
    }

    public String[] getAnnotatorNames() {
        return annotatorMap.keySet().toArray(new String[annotatorMap.keySet().size()]);
    }

    public void switchAnnotator() {
        String annotatorName = ListDialog.showDialog(null, null, "Profiles", "Annotator Chooser", getAnnotatorNames(), getAnnotatorNames()[0], null);

        if (annotatorName != null)
        {
            loadProfile(annotatorName);
        }

    }
}
