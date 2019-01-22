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
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.object.*;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class OldKnowtatorXMLUtil extends XMLUtil {

	private static final Logger log = LoggerFactory.getLogger(OldKnowtatorXMLUtil.class);

	private static HashMap<String, Element> getClassIDsFromXml(Element textSourceElement) {
    /*
    Next parse classes and addProfile the annotations
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

	private static HashMap<String, Element> getSlotsFromXml(Element textSourceElement) {
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

	private Optional<Element> getRootElement(File file) {
		Optional<Document> docOptional = startRead(file);
		if (docOptional.isPresent()) {
			Document doc = docOptional.get();
			List<Node> annotationNodes = asList(doc.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATIONS));
			if (annotationNodes.size() > 0) {
				return Optional.ofNullable(doc.getDocumentElement());
			}
		}
		return Optional.empty();

	}

	public void readToTextSourceCollection(KnowtatorModel model, File file) {
		Optional<Element> parentOptional = getRootElement(file);
		parentOptional.ifPresent(parent -> {
			String textSourceId = parent.getAttribute(OldKnowtatorXMLAttributes.TEXT_SOURCE).replace(".txt", "");
			TextSource newTextSource = new TextSource(model, file, textSourceId);
			model.getTextSources().add(newTextSource);
			model.getTextSources().get(newTextSource.getId())
					.ifPresent(textSource -> readToTextSource(textSource, parent));
		});
	}

	private void readToTextSource(TextSource textSource, Element parent) {
		readToConceptAnnotationCollection(textSource.getKnowtatorModel(), textSource, textSource.getConceptAnnotations(), parent);
//		readToGraphSpaceCollection();
	}

	private void readToConceptAnnotationCollection(KnowtatorModel model, TextSource textSource, ConceptAnnotationCollection conceptAnnotationCollection, Element parent) {
		Map<String, Element> slotToClassIDMap = getSlotsFromXml(parent);
		Map<String, Element> classMentionToClassIDMap = getClassIDsFromXml(parent);
		Map<ConceptAnnotation, Element> annotationToSlotMap = new HashMap<>();

		KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATION)).stream().map(node -> (Element) node)
				.map(annotationElement -> {
					String annotationID = ((Element) annotationElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
					Element classElement = classMentionToClassIDMap.get(annotationID);

					String owlClassID = ((Element) classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);
//					String owlClassName = classElement.getElementsByTagName(OldKnowtatorXMLTags.MENTION_CLASS).item(0).getTextContent();
					String profileID = null;
					try {
						profileID = annotationElement.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATOR).item(0).getTextContent();
						model.addProfile(new Profile(model, profileID));
					} catch (NullPointerException npe) {
						try {
							profileID = annotationElement.getAttribute(OldKnowtatorXMLAttributes.ANNOTATOR);
							model.addProfile(new Profile(model, profileID));
						} catch (NullPointerException ignored) {
						}
					}
					Profile profile = model.getProfile(profileID).orElse(model.getDefaultProfile());

					Optional<OWLClass> owlClass = model.getOWLClassByID(owlClassID);
					if (owlClass.isPresent()) {
						ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(textSource, annotationID, owlClass.get(), profile, "identity", "");
						if (conceptAnnotationCollection.containsID(annotationID)) model.verifyId(null, newConceptAnnotation, false);
						readToConceptAnnotation(newConceptAnnotation, annotationElement);

						// No need to keep annotations with no allSpanCollection
						if (newConceptAnnotation.size() == 0) {
							return Optional.empty();
						} else {
							KnowtatorXMLUtil.asList(classElement.getElementsByTagName(OldKnowtatorXMLTags.HAS_SLOT_MENTION)).stream()
									.map(node -> (Element) node).forEach(slotMentionElement -> {
								String slotMentionID = slotMentionElement.getAttribute(OldKnowtatorXMLAttributes.ID);
								Element slotElement = slotToClassIDMap.get(slotMentionID);
								if (slotElement != null) {
									annotationToSlotMap.put(newConceptAnnotation, slotElement);
								}
							});
							return Optional.of(newConceptAnnotation);
						}
					} else {
						log.warn(String.format("OWL Class: %s not found for concept: %s", owlClassID, annotationID));
						return Optional.empty();

					}


				}).forEach(conceptAnnotationOptional -> conceptAnnotationOptional.map(o -> (ConceptAnnotation) o).ifPresent(conceptAnnotationCollection::add));


		GraphSpace oldKnowtatorGraphSpace = new GraphSpace(textSource, "Old Knowtator Relations");
		textSource.add(oldKnowtatorGraphSpace);

		annotationToSlotMap.forEach((annotation, slot) -> {
					String propertyID = ((Element) slot.getElementsByTagName(OldKnowtatorXMLTags.MENTION_SLOT).item(0)).getAttribute(OldKnowtatorXMLAttributes.ID);

					AnnotationNode source = oldKnowtatorGraphSpace.getAnnotationNodeForConceptAnnotation(annotation);

					for (Node slotMentionValueNode : OldKnowtatorXMLUtil.asList(slot.getElementsByTagName(OldKnowtatorXMLTags.COMPLEX_SLOT_MENTION_VALUE))) {
						Element slotMentionValueElement = (Element) slotMentionValueNode;
						String value = slotMentionValueElement.getAttribute(OldKnowtatorXMLAttributes.VALUE);
						conceptAnnotationCollection.get(value).map(oldKnowtatorGraphSpace::getAnnotationNodeForConceptAnnotation)
								.ifPresent(target -> model.getOWLObjectPropertyByID(propertyID)
										.ifPresent(property -> oldKnowtatorGraphSpace.addTriple(source, target, null, model.getDefaultProfile(), property, "", "", false, "")));

					}
				}
		);

	}

	private void readToConceptAnnotation(ConceptAnnotation conceptAnnotation, Element parent) {
		for (Node spanNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(OldKnowtatorXMLTags.SPAN))) {
			if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
				Element spanElement = (Element) spanNode;
				int spanStart = Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_START));
				int spanEnd = Integer.parseInt(spanElement.getAttribute(OldKnowtatorXMLAttributes.SPAN_END));
				Span span = new Span(conceptAnnotation, null, spanStart, spanEnd);
				conceptAnnotation.add(span);
			}
		}
	}
}
