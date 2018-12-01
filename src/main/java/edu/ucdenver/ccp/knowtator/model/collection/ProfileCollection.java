/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.BaseKnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Optional;

public class ProfileCollection extends KnowtatorCollection<Profile> implements KnowtatorXMLIO, BaseKnowtatorManager {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(KnowtatorModel.class);

	private final BaseModel model;
	private final Profile defaultProfile;

	public ProfileCollection(BaseModel model) {
		super(model);
		defaultProfile = new Profile(model, "Default");
		add(defaultProfile);
		this.model = model;
	}

	public Profile getDefaultProfile() {
		return defaultProfile;
	}

	@Override
	public void save() {
//		if (model.isNotLoading()) {
		forEach(Profile::save);
//		}
	}

	@Override
	public Optional<Profile> getSelection() {
		//Profile should never be null
		if (super.getSelection().isPresent()) {
			return super.getSelection();
		} else {
			setSelection(defaultProfile);
			return Optional.of(defaultProfile);
		}
	}

	@Override
	public void add(Profile profile) {
		if (!get(profile.getId()).isPresent()) {
			super.add(profile);
		}
	}

	@Override
	public void remove(Profile profile) {
		if (profile.equals(defaultProfile)) {
			return;
		}
		super.remove(profile);
		selectNext();
	}

	public void writeToKnowtatorXML(Document dom, Element root) {
		forEach(profile -> profile.writeToKnowtatorXML(dom, root));
	}

	@Override
	public void readFromKnowtatorXML(File file, Element parent) {
		for (Node profileNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.PROFILE))) {
			Element profileElement = (Element) profileNode;
			String profileID = profileElement.getAttribute(KnowtatorXMLAttributes.ID);

			Profile newProfile = new Profile(model, profileID);
			add(newProfile);
			get(profileID).ifPresent(profile -> profile.readFromKnowtatorXML(null, profileElement));
		}
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {
	}

	@Override
	public void dispose() {
		super.dispose();
	}

//	@Override
//	public File getSaveLocation() {
//		return profilesLocation;
//	}
//
//	@Override
//	public void setSaveLocation(File saveLocation) throws IOException {
//		profilesLocation = (new File(saveLocation, "Profiles"));
//		Files.createDirectories(profilesLocation.toPath());
//	}

	@Override
	public void load() throws IOException {
		log.info("Loading profiles");
		KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
		Files.list(model.getProfilesLocation().toPath())
				.filter(path -> path.toString().endsWith(".xml"))
				.forEach(inputFile -> xmlUtil.read(this, inputFile.toFile()));
	}
}
