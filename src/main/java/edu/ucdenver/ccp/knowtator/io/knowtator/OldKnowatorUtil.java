package edu.ucdenver.ccp.knowtator.io.knowtator;

import edu.ucdenver.ccp.knowtator.io.XMLUtil;
import edu.ucdenver.ccp.knowtator.model.Span;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OldKnowatorUtil extends XMLUtil {

	public static List<Span> getSpanInfo(Element annotationElement, String content) {
		List<Span> spans = new ArrayList<>();

		for (Node spanNode :
				KnowtatorXMLUtil.asList(annotationElement.getElementsByTagName(OldKnowtatorXMLTags.SPAN))) {
			if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
				Element spanElement = (Element) spanNode;
				int spanStart =
						Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_START));
				int spanEnd =
						Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_END));
				String spannedText = content.substring(spanStart, spanEnd);

				spans.add(new Span(spanStart, spanEnd, spannedText));
			}
		}
		return spans;
	}

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
