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

import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Profile implements Savable {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(Profile.class);

    private String profileID;
    private HashMap<String, Color> colors;  //<ClassName, Highlighter>

    public Profile(String profileID) {
        this.profileID = profileID;

        colors = new HashMap<>();
    }

    public String getId() {
        return profileID;
    }

    public Color getColor(String classID) {
        colors.putIfAbsent(classID, Color.CYAN);

        return colors.get(classID);
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
    public void writeToKnowtatorXml(Document dom, Element root) {
        Element profileElem = dom.createElement(KnowtatorXMLTags.PROFILE);
        profileElem.setAttribute(KnowtatorXMLTags.ID, profileID);
        colors.forEach((classID, c) -> {
            Element e = dom.createElement(KnowtatorXMLTags.HIGHLIGHTER);
            e.setAttribute(KnowtatorXMLTags.CLASS, classID);
            e.setAttribute(KnowtatorXMLTags.COLOR, String.format("#%06x", c.getRGB() & 0x00FFFFFF));
            profileElem.appendChild(e);
        });
        root.appendChild(profileElem);
    }

    @Override
    public void readFromKnowtatorXml(Element parent, String content) {
        for (Node highlighterNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.HIGHLIGHTER))) {
            Element highlighterElement = (Element) highlighterNode;

            String classID = highlighterElement.getAttribute(KnowtatorXMLTags.CLASS);
            String color = highlighterElement.getAttribute(KnowtatorXMLTags.COLOR);
            addColor(classID, color);
        }
    }

    @Override
    public void readFromOldKnowtatorXml(Element parent) {

    }

    @Override
    public void readFromBratStandoff(Map<Character, List<String[]>> annotationMap, String content) {

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {

    }

    @Override
    public void readFromGeniaXml(Element parent, String content) {

    }
}
