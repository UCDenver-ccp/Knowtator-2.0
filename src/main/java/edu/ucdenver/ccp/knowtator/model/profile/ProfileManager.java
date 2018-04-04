/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileManager implements Savable {

    private static final Logger log = Logger.getLogger(KnowtatorManager.class);
    private final Profile defaultProfile;
    private Profile currentProfile;

    private Map<String, Profile> profiles;
    private KnowtatorManager manager;


    public ProfileManager(KnowtatorManager manager) {
        this.manager = manager;
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

        manager.profileAddedEvent(currentProfile);

        return newProfile;
    }

    public Profile getCurrentProfile() {
        return currentProfile;
    }

    public void switchAnnotator(Profile profile) {
        currentProfile = profile;
        manager.profileSelectionChangedEvent(currentProfile);
    }

    public void removeProfile(Profile profile) {
        profiles.remove(profile.getId());
        manager.profileRemovedEvent();

    }

    public Map<String, Profile> getProfiles() {
        return profiles;
    }

    public void writeToKnowtatorXML(Document dom, Element root) {
        profiles.values().forEach(profile -> profile.writeToKnowtatorXML(dom, root));
    }

    @Override
    public void readFromKnowtatorXML(Element parent, String content) {
        for (Node profileNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.PROFILE))) {
            Element profileElement = (Element) profileNode;
            String profileID = profileElement.getAttribute(KnowtatorXMLAttributes.ID);

            Profile newProfile = addNewProfile(profileID);
            log.warn("\tXML: " + newProfile);
            newProfile.readFromKnowtatorXML(profileElement, content);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(Element parent, String content) {

    }

    @Override
    public void readFromBratStandoff(Map<Character, List<String[]>> annotationMap, String content) {

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {

    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }
//
//    @Override
//    public void convertToUIMA(CAS cas) {
//
//    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    public Profile getDefaultProfile() {
        return defaultProfile;
    }
}
