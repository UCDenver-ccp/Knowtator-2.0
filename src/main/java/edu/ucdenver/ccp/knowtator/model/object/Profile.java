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

package edu.ucdenver.ccp.knowtator.model.object;

import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.Savable;
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

public class Profile implements ModelObject<Profile>, Savable, KnowtatorXMLIO {
	@SuppressWarnings("unused")
	private static Logger log = LogManager.getLogger(Profile.class);

	private String id;
	private final HashMap<OWLClass, Color> colors; // <ClassName, Highlighter>
	private final KnowtatorModel model;

	public Profile(KnowtatorModel model, String id) {
		colors = new HashMap<>();
		this.model = model;
		model.verifyId(id, this, false);
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

	public Color getColor(OWLClass owlClass) {
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
		model.fireColorChanged();
	}


	/*
	TRANSLATORS
	 */
	static String convertToHex(Color c) {
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

			e.setAttribute(KnowtatorXMLAttributes.CLASS_ID, model.getOWLEntityRendering(owlEntity));

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
		Map<String, Color> colorMap = new HashMap<>();
		for (Node highlighterNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.HIGHLIGHTER))) {
			Element highlighterElement = (Element) highlighterNode;

			String classID = highlighterElement.getAttribute(KnowtatorXMLAttributes.CLASS_ID);
			Color c = Color.decode(highlighterElement.getAttribute(KnowtatorXMLAttributes.COLOR));


			Color color = new Color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);
			colorMap.put(classID, color);
		}
		model.getOWLClassesByIDs(colorMap.keySet()).entrySet().parallelStream()
				.forEach(entry -> addColor(entry.getValue(), colorMap.get(entry.getKey())));
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {
	}


	@Override
	public void save() {
		model.saveToFormat(KnowtatorXMLUtil.class, this, getSaveLocation());
	}

	private File getSaveLocation() {
		return new File(model.getSaveLocation().getAbsolutePath(), String.format("%s.xml", id));
	}

	public Map<OWLClass, Color> getColors() {
		return colors;
	}
}
