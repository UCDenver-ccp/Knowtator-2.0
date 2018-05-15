package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ColorListener;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileManager implements Savable {

  private static final Logger log = Logger.getLogger(KnowtatorController.class);

  private ProfileCollection profileCollection;
  private KnowtatorController controller;
  private List<ColorListener> colorListeners;

  public ProfileManager(KnowtatorController controller) {
    this.controller = controller;
    colorListeners = new ArrayList<>();
    profileCollection = new ProfileCollection(controller);
  }

  public void addColorListener(ColorListener listener) {
    colorListeners.add(listener);
  }

  public Profile addProfile(String profileID) {
    Profile newProfile = profileCollection.get(profileID);
    if (newProfile == null) {
      newProfile = new Profile(controller, profileID);
      profileCollection.add(newProfile);
    }
    controller.getSelectionManager().setSelectedProfile(newProfile);
    return newProfile;
  }

  private void removeProfile(Profile profile) {
    profileCollection.remove(profile);
    controller.getSelectionManager().setSelectedProfile(profileCollection.iterator().next());
  }

  public ProfileCollection getProfileCollection() {
    return profileCollection;
  }

  public void writeToKnowtatorXML(Document dom, Element root) {
    profileCollection.forEach(profile -> profile.writeToKnowtatorXML(dom, root));
  }

  @Override
  public void readFromKnowtatorXML(File file, Element parent) {
    for (Node profileNode :
        KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.PROFILE))) {
      Element profileElement = (Element) profileNode;
      String profileID = profileElement.getAttribute(KnowtatorXMLAttributes.ID);

      Profile newProfile = addProfile(profileID);
      newProfile.readFromKnowtatorXML(null, profileElement);
    }
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {}

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {}

  @SuppressWarnings("RedundantThrows")
  @Override
  public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> config) throws IOException {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

  Profile getDefaultProfile() {
    return profileCollection.getDefaultProfile();
  }

  public void removeActiveProfile() {
    if (controller.getSelectionManager().getActiveProfile() != profileCollection.getDefaultProfile()) {
      removeProfile(controller.getSelectionManager().getActiveProfile());
    }
  }

  Profile getProfile(String profileID) {
    return profileCollection.get(profileID);
  }

  void fireColorChanged() {
    colorListeners.forEach(ColorListener::colorChanged);
  }
}
