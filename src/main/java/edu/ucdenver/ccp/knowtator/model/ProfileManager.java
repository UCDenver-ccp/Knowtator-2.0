package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class ProfileManager implements Savable {

	private static final Logger log = Logger.getLogger(KnowtatorController.class);
	private final Profile defaultProfile;

	private ProfileCollection profileCollection;
	private KnowtatorController controller;

	public ProfileManager(KnowtatorController controller) {
		this.controller = controller;
		profileCollection = new ProfileCollection(controller);
		defaultProfile = addProfile("Default");
	}

	public Profile addProfile(String profileID) {
		Profile newProfile = new Profile(profileID);
		profileCollection.add(newProfile);

		controller.getSelectionManager().setSelected(newProfile);
		return newProfile;
	}

	private void removeProfile(Profile profile) {
		profileCollection.remove(profile);
		controller.getSelectionManager().setSelected(profileCollection.getData().iterator().next());
	}

	public ProfileCollection getProfileCollection() {
		return profileCollection;
	}

	public void writeToKnowtatorXML(Document dom, Element root) {
		profileCollection.getData().forEach(profile -> profile.writeToKnowtatorXML(dom, root));
	}

	@Override
	public void readFromKnowtatorXML(File file, Element parent, String content) {
		for (Node profileNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.PROFILE))) {
			Element profileElement = (Element) profileNode;
			String profileID = profileElement.getAttribute(KnowtatorXMLAttributes.ID);

			Profile newProfile = addProfile(profileID);
			log.warn("\tXML: " + newProfile);
			newProfile.readFromKnowtatorXML(null, profileElement, content);
		}
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent, TextSource textSource) {
	}

	@Override
	public void readFromBratStandoff(
			File file, Map<Character, List<String[]>> annotationMap, String content) {
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

	Profile getDefaultProfile() {
		return defaultProfile;
	}

	public void removeActiveProfile() {
		if (controller.getSelectionManager().getActiveProfile() != defaultProfile) {
			removeProfile(controller.getSelectionManager().getActiveProfile());
		}
	}
}
