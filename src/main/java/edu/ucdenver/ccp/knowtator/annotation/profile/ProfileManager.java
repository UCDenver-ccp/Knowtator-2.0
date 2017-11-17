package edu.ucdenver.ccp.knowtator.annotation.profile;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class ProfileManager {

    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    public Profile currentProfile;

    public Map<String, Profile> annotatorMap;
    public KnowtatorManager manager;


    public ProfileManager(KnowtatorManager manager) {
        this.manager = manager;
        annotatorMap = new HashMap<>();
    }

    public Profile addNewAnnotator(String annotatorName, String annotatorID) {

        if (annotatorMap.containsKey(annotatorName)) {
            return annotatorMap.get(annotatorName);
        }

        Profile newProfile = new Profile(manager, annotatorName, annotatorID);
        annotatorMap.put(annotatorName, newProfile);

        currentProfile = newProfile;

        manager.profileChangedEvent(currentProfile);

        return newProfile;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public String[] getAnnotatorNames() {
        return annotatorMap.keySet().toArray(new String[annotatorMap.keySet().size()]);
    }

    public void switchAnnotator(String annotatorName) {
        if (annotatorName != null) {
            currentProfile = annotatorMap.get(annotatorName);
            manager.profileChangedEvent(currentProfile);
        }

    }

    public void removeAnnotator(String annotatorName) {
        if (annotatorName != null) {
            annotatorMap.remove(annotatorName);
            manager.profileChangedEvent(null);
        }
    }

    public Map<String, Profile> getAnnotators() {
        return annotatorMap;
    }
}
