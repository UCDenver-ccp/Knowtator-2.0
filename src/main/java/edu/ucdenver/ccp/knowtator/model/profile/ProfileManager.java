package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.Savable;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileManager implements Savable {

    private static final Logger log = Logger.getLogger(KnowtatorController.class);
    private final Profile defaultProfile;
    private Profile currentProfile;

    private Map<String, Profile> profiles;
    private KnowtatorController controller;


    public ProfileManager(KnowtatorController controller) {
        this.controller = controller;
        profiles = new HashMap<>();
        defaultProfile = addNewProfile("Default");
    }

    public Profile addNewProfile(String profileID) {
        if (profiles.containsKey(profileID)) {
            return profiles.get(profileID);
        }

        Profile newProfile = new Profile(profileID);
        profiles.put(profileID, newProfile);

        currentProfile = newProfile;

        controller.profileAddedEvent(currentProfile);

        return newProfile;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void switchAnnotator(Profile profile) {
        currentProfile = profile;
        controller.profileSelectionChangedEvent(currentProfile);
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile.getId());
        controller.profileRemovedEvent();

    }

    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    public void writeToKnowtatorXML(Document dom, Element root) {
        profiles.values().forEach(profile -> profile.writeToKnowtatorXML(dom, root));
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent, String content) {
        for (Node profileNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.PROFILE))) {
            Element profileElement = (Element) profileNode;
            String profileID = profileElement.getAttribute(KnowtatorXMLAttributes.ID);

            Profile newProfile = addNewProfile(profileID);
            log.warn("\tXML: " + newProfile);
            newProfile.readFromKnowtatorXML(null, profileElement, content);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent, String content) {

    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {

    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    public Profile getDefaultProfile() {
        return defaultProfile;
    }
}
