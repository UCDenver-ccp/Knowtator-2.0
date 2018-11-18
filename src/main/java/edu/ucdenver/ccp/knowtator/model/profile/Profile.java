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

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.Savable;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class Profile implements KnowtatorDataObjectInterface<Profile>, Savable, KnowtatorXMLIO {
  @SuppressWarnings("unused")
  private static Logger log = LogManager.getLogger(Profile.class);

  private String id;
  private final HashMap<Object, Color> colors; // <ClassName, Highlighter>
  private final KnowtatorController controller;

  public Profile(KnowtatorController controller, String id) {
    colors = new HashMap<>();
    this.controller = controller;
    controller.verifyId(id, this, false);
  }

  /*
  COMPARRISON
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
    String owlClassID = conceptAnnotation.getOwlClassID();

    Color color = colors.get(owlClass);
    if (color != null) {
      return color;
    } else {
      color = colors.get(owlClassID);
      if (owlClass != null) {
        if (color == null) {
          color = Color.CYAN;
        }
        addColor(owlClass, color);
        controller.getProfileCollection().fireColorChanged();
        save();
        return color;
      } else if (color == null) {
        addColor(owlClassID, Color.CYAN);
        controller.getProfileCollection().fireColorChanged();
        save();
        return Color.CYAN;
      } else {
        return color;
      }
    }
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

  public void addColor(Object key, Color c) {
    colors.put(key, c);
    controller.getProfileCollection().fireColorChanged();
    save();
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
    colors.forEach(
        (owlEntity, c) -> {
          Element e = dom.createElement(KnowtatorXMLTags.HIGHLIGHTER);
          if (owlEntity instanceof OWLEntity) {
            e.setAttribute(
                KnowtatorXMLAttributes.CLASS_ID,
                controller
                    .getOWLModel()
                    .getOWLEntityRendering((OWLEntity) owlEntity));
          } else if (owlEntity instanceof String) {
            e.setAttribute(
                    KnowtatorXMLAttributes.CLASS_ID, (String) owlEntity);
          }
          e.setAttribute(
              KnowtatorXMLAttributes.COLOR, convertToHex(c));
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
      String color = highlighterElement.getAttribute(KnowtatorXMLAttributes.COLOR);
      Color c = Color.decode(color);
      c = new Color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);
      addColor(classID, c);
    }
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {}


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

  public Map<Object, Color> getColors() {
    return colors;
  }
}
