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
import edu.ucdenver.ccp.knowtator.model.xml.XmlTags;
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
import edu.ucdenver.ccp.knowtator.model.owl.OWLAPIDataExtractor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Set;

public class Profile implements Savable {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(Profile.class);

    private String profileID;
    private HashMap<String, Color> colors;  //<ClassName, Highlighter>
    private KnowtatorManager manager;

    public Profile(String profileID, KnowtatorManager manager) {
        this.profileID = profileID;
        this.manager = manager;

        colors = new HashMap<>();
    }

    public String getId() {
        return profileID;
    }

    public Color getColor(String classID, String className) {
        if (colors.containsKey(classID)) {
            return colors.get(classID);
        } else {
            Color c = JColorChooser.showDialog(null, String.format("Pick a color for %s", className == null ? classID : className), Color.CYAN);
            if (c != null) {
                addColor(classID, c);

                if (JOptionPane.showConfirmDialog(null, String.format("Assign color to descendents of %s?", className == null ? classID : className)) == JOptionPane.OK_OPTION) {
                    Set<OWLClass> descendents = manager.getOWLAPIDataExtractor().getSelectedOWLClassDescendents();
                    if (descendents != null) {
                        for (OWLClass descendent : descendents) {
                            String decClassID = OWLAPIDataExtractor.getOwlEntID(descendent);

                            addColor(decClassID, c);
                        }
                    }
                }
            } else {
                return getColor(classID, className);
            }
            return colors.get(classID);
        }

    }

    private void addColor(String classID, String color) {
        Color c = Color.decode(color);
        c = new Color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);

        colors.put(classID, c);
    }

    private void addColor(String classID, Color c) {
        colors.put(classID, c);
    }

    public String toString() {
        return String.format("Profile: ID: %s", profileID);
    }

    public void reassignColor(String classID, String className) {
        colors.remove(classID);
        getColor(classID, className);
        manager.colorChangedEvent();
    }

    @Override
    public void writeToXml(Document dom, Element root) {
        Element profileElem = dom.createElement(XmlTags.PROFILE);
        profileElem.setAttribute(XmlTags.ID, profileID);
        colors.forEach((classID, c) -> {
            Element e = dom.createElement(XmlTags.HIGHLIGHTER);
            e.setAttribute(XmlTags.CLASS, classID);
            e.setAttribute(XmlTags.COLOR, String.format("#%06x", c.getRGB() & 0x00FFFFFF));
            profileElem.appendChild(e);
        });
        root.appendChild(profileElem);
    }

    @Override
    public void readFromXml(Element parent) {
        for (Node highlighterNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.HIGHLIGHTER))) {
            Element highlighterElement = (Element) highlighterNode;

            String classID = highlighterElement.getAttribute(XmlTags.CLASS);
            String color = highlighterElement.getAttribute(XmlTags.COLOR);
            addColor(classID, color);
        }
    }
}
