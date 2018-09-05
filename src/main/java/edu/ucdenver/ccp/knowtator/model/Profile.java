package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile implements Savable, KnowtatorObject {
  @SuppressWarnings("unused")
  private static Logger log = LogManager.getLogger(Profile.class);

  private String id;
  private HashMap<Object, Color> colors; // <ClassName, Highlighter>
  private KnowtatorController controller;

  public Profile(KnowtatorController controller, String id) {
    colors = new HashMap<>();
    this.controller = controller;
    controller.verifyId(id, this, false);
  }

  private void save() {
    if (controller.getProjectManager().isProjectLoaded()) {
      controller.getProfileManager().save();
    }
  }

  /*
  COMPARRISON
   */

  public static int compare(Profile profile1, Profile profile2) {
    if (profile1 == profile2) {
      return 0;
    }
    if (profile2 == null) {
      return 1;
    }
    if (profile1 == null) {
      return -1;
    }
    return profile1.getId().toLowerCase().compareTo(profile2.getId().toLowerCase());
  }

  /*
  GETTERS
   */

  @Override
  public String getId() {
    return id;
  }

  public Color getColor(Annotation annotation) {
    OWLClass owlClass = annotation.getOwlClass();
    String owlClassID = annotation.getOwlClassID();

    Color color = colors.get(owlClass);
    if (color != null) {
      return color;
    } else {
      color = colors.get(owlClassID);
      if (owlClass != null) {
        if (color == null) {
          color = Color.CYAN;
        }
        colors.put(owlClass, color);
        controller.getProfileManager().fireColorChanged();
        return color;
      } else if (color == null) {
        colors.put(owlClassID, Color.CYAN);
        controller.getProfileManager().fireColorChanged();
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

  /*
  ADDERS
   */

  private void addColor(String classID, String color) {
    Color c = Color.decode(color);
    c =
        new Color(
            (float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);

    addColor(classID, c);
  }

  public void addColor(Object key, Color c) {
    colors.put(key, c);
    controller.getProfileManager().fireColorChanged();
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
          try {
            if (owlEntity instanceof OWLEntity) {
              e.setAttribute(
                  KnowtatorXMLAttributes.CLASS_ID,
                  controller
                      .getOWLAPIDataExtractor()
                      .getOWLEntityRendering((OWLEntity) owlEntity));
            } else if (owlEntity instanceof String) {
              e.setAttribute(
                      KnowtatorXMLAttributes.CLASS_ID, (String) owlEntity);
            }
            e.setAttribute(
                KnowtatorXMLAttributes.COLOR, convertToHex(c));
            profileElem.appendChild(e);

          } catch (OWLWorkSpaceNotSetException | OWLEntityNullException ignored) {

          }
        });
    root.appendChild(profileElem);
  }

  @SuppressWarnings("RedundantThrows")
  @Override
  public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationsConfig, Map<String, Map<String, String>> visualConfig) throws IOException {}

  @Override
  public void writeToGeniaXML(Document dom, Element parent) {}

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
      addColor(classID, color);
    }
  }

  @Override
  public void readFromOldKnowtatorXML(File file, Element parent) {}

  @Override
  public void readFromBratStandoff(
      File file, Map<Character, List<String[]>> annotationMap, String content) {}

  @Override
  public void readFromGeniaXML(Element parent, String content) {}
}
