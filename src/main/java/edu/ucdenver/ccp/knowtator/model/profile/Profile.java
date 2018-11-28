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

package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorDefaultSettings;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Profile implements KnowtatorDataObjectInterface<Profile>, Savable, KnowtatorXMLIO {
	@SuppressWarnings("unused")
	private static Logger log = LogManager.getLogger(Profile.class);

	private String id;
	private final HashMap<OWLClass, Color> colors; // <ClassName, Highlighter>
	private final KnowtatorModel controller;

	public Profile(KnowtatorModel controller, String id) {
		colors = new HashMap<>();
		this.controller = controller;
		controller.verifyId(id, this, false);
	}

  /*
  COMPARISON
   */

	@Override
	public int compareTo(Profile profile2) {
		if (this == profile2) {
			return 0;
		}
		if (profile2 == null) {
			return 1;
		}
		return this.getId().toLowerCase().compareTo(profile2.getId().toLowerCase());
	}

  /*
  GETTERS
   */

	@Override
	public String getId() {
		return id;
	}

	public Color getColor(ConceptAnnotation conceptAnnotation) {
		OWLClass owlClass = conceptAnnotation.getOwlClass();

		return colors.getOrDefault(owlClass, KnowtatorDefaultSettings.COLORS.get(0));
	}

  /*
  SETTERS
   */

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void dispose() {
		colors.clear();
	}

  /*
  ADDERS
   */

	public void addColor(OWLClass owlClass, Color c) {
		colors.put(owlClass, c);
		controller.getProfileCollection().fireColorChanged();
	}


	/*
	TRANSLATORS
	 */
	public static String convertToHex(Color c) {
		return String.format("#%06x", c.getRGB() & 0x00FFFFFF);
	}

	public String toString() {
		return id;
	}

  /*
  WRITERS
   */


	@Override
	public void writeToKnowtatorXML(Document dom, Element root) {
		Element profileElem = dom.createElement(KnowtatorXMLTags.PROFILE);
		profileElem.setAttribute(KnowtatorXMLAttributes.ID, id);
		colors.forEach((owlEntity, c) -> {
			Element e = dom.createElement(KnowtatorXMLTags.HIGHLIGHTER);
			controller.getOWLEntityRendering(owlEntity)
					.ifPresent(owlClassID -> e.setAttribute(KnowtatorXMLAttributes.CLASS_ID, owlClassID));

			e.setAttribute(KnowtatorXMLAttributes.COLOR, convertToHex(c));
			profileElem.appendChild(e);

		});
		root.appendChild(profileElem);
	}


  /*
  READERS
   */

	@Override
	public void readFromKnowtatorXML(File file, Element parent) {
		for (Node highlighterNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.HIGHLIGHTER))) {
			Element highlighterElement = (Element) highlighterNode;

			String classID = highlighterElement.getAttribute(KnowtatorXMLAttributes.CLASS_ID);
			Color c = Color.decode(highlighterElement.getAttribute(KnowtatorXMLAttributes.COLOR));

			Optional<OWLClass> owlClassOptional = controller.getOWLClassByID(classID);
			Color color = new Color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);
			owlClassOptional.ifPresent(owlClass -> addColor(owlClass, color));
		}
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {
	}


	@Override
	public void save() {
		if (controller.isNotLoading())
			controller.saveToFormat(KnowtatorXMLUtil.class, this, getSaveLocation());
	}

	@Override
	public void load() {

	}

	@SuppressWarnings("WeakerAccess")
	@Override
	public File getSaveLocation() {
		return new File(controller.getProfileCollection().getSaveLocation().getAbsolutePath(), id + ".xml");
	}

	@Override
	public void setSaveLocation(File saveLocation) {

	}

	public Map<OWLClass, Color> getColors() {
		return colors;
	}
}
