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

package edu.ucdenver.ccp.knowtator.io.knowtator;

import edu.ucdenver.ccp.knowtator.io.XMLUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;

public class OldKnowtatorUtil extends XMLUtil {

	public static HashMap<String, Element> getClassIDsFromXml(Element textSourceElement) {
    /*
    Next parse classes and add the annotations
     */
		HashMap<String, Element> mentionTracker = new HashMap<>();

		for (Node classNode :
				KnowtatorXMLUtil.asList(
						textSourceElement.getElementsByTagName(OldKnowtatorXMLTags.CLASS_MENTION))) {
			if (classNode.getNodeType() == Node.ELEMENT_NODE) {
				Element classElement = (Element) classNode;

				String annotationID = classElement.getAttribute(OldKnowtatorXMLAttributes.ID);
				mentionTracker.put(annotationID, classElement);
			}
		}

		return mentionTracker;
	}

	public static HashMap<String, Element> getslotsFromXml(Element textSourceElement) {
		HashMap<String, Element> slotMap = new HashMap<>();
		String slotID;
		Element slotElement;
		for (Node complexSlotNode :
				asList(textSourceElement.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION))) {
			slotElement = (Element) complexSlotNode;
			slotID = slotElement.getAttribute(OldKnowtatorXMLAttributes.ID);
			slotMap.put(slotID, slotElement);
		}
		//        for (Node stringSlotNode :
		// asList(textSourceElement.getElementsByTagName(OldKnowtatorXMLTags.STRING_SLOT_MENTION))) {
		//            slotElement = (Element) stringSlotNode;
		//            slotID = slotElement.getAttribute(OldKnowtatorXMLAttributes.ID);
		//            slotMap.put(slotID, slotElement);
		//        }

		return slotMap;
	}
}
