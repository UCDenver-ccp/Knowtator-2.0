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

package edu.ucdenver.ccp.knowtator.model.xml.forOld;

import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSourceManager;
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OldXmlReader {

    private static final Logger log = Logger.getLogger(OldXmlReader.class);

    public static void readAnnotations(TextSourceManager textSourceManager, List<Node> textSourceNodes) {
        /*
        Get annotations by document
         */
        for (Node textSourceNode : textSourceNodes) {
            Element textSourceElement = (Element) textSourceNode;
            String docID = textSourceElement.getAttribute(OldXmlTags.TEXT_SOURCE).replace(".txt", "");

            TextSource newTextSource = textSourceManager.addTextSource(docID);
            getAnnotationsFromXml_OLD(textSourceManager, newTextSource, textSourceElement);
        }

    }

    private static void getAnnotationsFromXml_OLD(TextSourceManager textSourceManager, TextSource textSource, Element textSourceElement) {

        Map<String, Element> classMentionToClassIDMap = getClassIDsFromXml(textSourceElement);
//        Map<String, Element> complexSlotMentionToClassIDMap = getComplexSlotsFromXml(textSourceElement);

        for (Node annotationNode : XmlUtil.asList(textSourceElement.getElementsByTagName(OldXmlTags.ANNOTATION))) {
            Element annotationElement = (Element) annotationNode;

            String profileID;
            try {
                profileID = annotationElement.getElementsByTagName(OldXmlTags.ANNOTATOR).item(0).getTextContent();
            } catch (NullPointerException npe) {
                profileID = "Default";
            }

            Profile profile = textSourceManager.getManager().getProfileManager().addNewProfile(profileID);
            List<Span> spans = getSpanInfo(textSource, annotationElement);


            String annotationID = ((Element) annotationElement.getElementsByTagName(OldXmlTags.MENTION).item(0)).getAttribute(OldXmlTags.ID);
            Element classElement = classMentionToClassIDMap.get(annotationID);

            String className = classElement.getElementsByTagName(OldXmlTags.MENTION_CLASS).item(0).getTextContent();
            String classID = ((Element) classElement.getElementsByTagName(OldXmlTags.MENTION_CLASS).item(0)).getAttribute(OldXmlTags.ID);

            Annotation newAnnotation;
//                    if (classID.equals(OldXmlTags.MENTION_CLASS_ID_IDENTITY)) {
//                        newAnnotation = new Annotation(annotationID, textSource, profile);
//                        Element complexSlotElement = complexSlotMentionToClassIDMap.get(((Element) classElement.getElementsByTagName(OldXmlTags.HAS_SLOT_MENTION).item(0)).getAttribute(OldXmlTags.HAS_SLOT_MENTION_ID));
//                        if (((Element) complexSlotElement.getElementsByTagName(OldXmlTags.MENTION_SLOT).item(0)).getAttribute(OldXmlTags.MENTION_SLOT_ID).equals(OldXmlTags.MENTION_SLOT_ID_COREFERENCE)) {
//                            for (Node complexSlotMentionValueNode : XmlUtil.asList(complexSlotElement.getElementsByTagName(OldXmlTags.COMPLEX_SLOT_MENTION_VALUE))) {
//                                ((IdentityChainAnnotation) newAnnotation).addCoreferringAnnotation(((Element) complexSlotMentionValueNode).getAttribute(OldXmlTags.COMPLEX_SLOT_MENTION_VALUE_VALUE));
//                            }
//                        }
//
//                    } else {
//                        
            newAnnotation = new Annotation(classID, className, annotationID, textSource, profile, "identity");
            spans.forEach(newAnnotation::addSpan);
            textSource.getAnnotationManager().addAnnotation(newAnnotation);

            log.warn("OLD XML: " + newAnnotation);

        }
    }

    private static List<Span> getSpanInfo(TextSource textSource, Element annotationElement) {
        List<Span> spans = new ArrayList<>();

        for (Node spanNode : XmlUtil.asList(annotationElement.getElementsByTagName(OldXmlTags.SPAN))) {
            if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
                Element spanElement = (Element) spanNode;
                String spanStart = spanElement.getAttribute(OldXmlTags.SPAN_START);
                String spanEnd = spanElement.getAttribute(OldXmlTags.SPAN_END);

                spans.add(new Span(textSource, Integer.parseInt(spanStart), Integer.parseInt(spanEnd)));
            }
        }
        return spans;
    }

    private static HashMap<String, Element> getClassIDsFromXml(Element textSourceElement) {
        /*
        Next parse classes and add the annotations
         */
        HashMap<String, Element> mentionTracker = new HashMap<>();

        for (Node classNode : XmlUtil.asList(textSourceElement.getElementsByTagName(OldXmlTags.CLASS_MENTION))) {
            if (classNode.getNodeType() == Node.ELEMENT_NODE) {
                Element classElement = (Element) classNode;

                String annotationID = classElement.getAttribute(OldXmlTags.ID);
                mentionTracker.put(annotationID, classElement);
            }
        }

        return mentionTracker;
    }

//    static String getMentionSourceFromXML(String fullMention) {
//        String mentionSource;
//        if(fullMention.indexOf("_new_Instance") < fullMention.indexOf("Instance") && fullMention.contains("_new_Instance")) {
//            mentionSource = fullMention.substring(0, fullMention.indexOf("_new_Instance"));
//        } else {
//            mentionSource = fullMention.substring(0, fullMention.indexOf("_Instance"));
//        }
//        return mentionSource;
//    }
//
//    static Integer getMentionIDFromXML(String fullMention) {
//        return Integer.parseInt(fullMention.substring(fullMention.indexOf("_Instance_")+10));
//    }
}
