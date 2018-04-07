package edu.ucdenver.ccp.knowtator.model.profile;

import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
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

public class Profile implements Savable {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(Profile.class);

    private String profileID;
    private HashMap<Object, Color> colors;  //<ClassName, Highlighter>

    public Profile(String profileID) {
        this.profileID = profileID;

        colors = new HashMap<>();
    }

    public String getId() {
        return profileID;
    }

    public Color getColor(Object owlClass) {
        colors.putIfAbsent(owlClass, Color.CYAN);

        return colors.get(owlClass);
    }

    private void addColor(String classID, String color) {
        Color c = Color.decode(color);
        c = new Color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);

        colors.put(classID, c);
    }

    public void addColor(String classID, Color c) {
        colors.put(classID, c);
    }

    public String toString() {
        return String.format("Profile: ID: %s", profileID);
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element root) {
        Element profileElem = dom.createElement(KnowtatorXMLTags.PROFILE);
        profileElem.setAttribute(KnowtatorXMLAttributes.ID, profileID);
        colors.forEach((owlClass, c) -> {
            Element e = dom.createElement(KnowtatorXMLTags.HIGHLIGHTER);
            e.setAttribute(KnowtatorXMLAttributes.CLASS_ID, owlClass.toString());
            e.setAttribute(KnowtatorXMLAttributes.COLOR, String.format("#%06x", c.getRGB() & 0x00FFFFFF));
            profileElem.appendChild(e);
        });
        root.appendChild(profileElem);
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent, String content) {
        for (Node highlighterNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.HIGHLIGHTER))) {
            Element highlighterElement = (Element) highlighterNode;

            String classID = highlighterElement.getAttribute(KnowtatorXMLAttributes.CLASS_ID);
            String color = highlighterElement.getAttribute(KnowtatorXMLAttributes.COLOR);
            addColor(classID, color);
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
}
