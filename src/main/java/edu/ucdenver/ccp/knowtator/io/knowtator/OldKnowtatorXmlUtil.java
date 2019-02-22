/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.io.knowtator;

import edu.ucdenver.ccp.knowtator.io.XmlUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** The type Old knowtator xml util. */
public class OldKnowtatorXmlUtil extends XmlUtil {

  private static final Logger log = LoggerFactory.getLogger(OldKnowtatorXmlUtil.class);

  private static HashMap<String, Element> getClassIDsFromXml(Element textSourceElement) {
    /*
    Next parse classes and addProfile the annotations
     */
    HashMap<String, Element> mentionTracker = new HashMap<>();

    for (Node classNode :
        KnowtatorXmlUtil.asList(
            textSourceElement.getElementsByTagName(OldKnowtatorXmlTags.CLASS_MENTION))) {
      if (classNode.getNodeType() == Node.ELEMENT_NODE) {
        Element classElement = (Element) classNode;

        String annotationID = classElement.getAttribute(OldKnowtatorXmlAttributes.ID);
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
        asList(textSourceElement.getElementsByTagName(OldKnowtatorXmlTags.COMPLEX_SLOT_MENTION))) {
      slotElement = (Element) complexSlotNode;
      slotID = slotElement.getAttribute(OldKnowtatorXmlAttributes.ID);
      slotMap.put(slotID, slotElement);
    }
    //        for (Node stringSlotNode :
    // asList(textSourceElement.getElementsByTagName(OldKnowtatorXmlTags.STRING_SLOT_MENTION))) {
    //            slotElement = (Element) stringSlotNode;
    //            slotID = slotElement.getAttribute(OldKnowtatorXmlAttributes.ID);
    //            slotMap.put(slotID, slotElement);
    //        }

    return slotMap;
  }

  private Optional<Element> getRootElement(File file) {
    Optional<Document> docOptional = startRead(file);
    if (docOptional.isPresent()) {
      Document doc = docOptional.get();
      List<Node> annotationNodes =
          asList(doc.getElementsByTagName(OldKnowtatorXmlTags.ANNOTATIONS));
      if (annotationNodes.size() > 0) {
        log.info(String.format("Reading from %s", file));
        return Optional.ofNullable(doc.getDocumentElement());
      }
    }
    return Optional.empty();
  }

  /**
   * Read to text source collection.
   *
   * @param model the model
   * @param file the file
   */
  public void readToTextSourceCollection(KnowtatorModel model, File file) {
    Optional<Element> parentOptional = getRootElement(file);
    parentOptional.ifPresent(
        parent -> {
          String textSourceId =
              parent.getAttribute(OldKnowtatorXmlAttributes.TEXT_SOURCE).replace(".txt", "");
          TextSource newTextSource = new TextSource(model, file, textSourceId);
          model.getTextSources().add(newTextSource);
          model
              .getTextSources()
              .get(newTextSource.getId())
              .ifPresent(textSource -> readToTextSource(textSource, parent));
        });
  }

  private void readToTextSource(TextSource textSource, Element parent) {
    readToConceptAnnotationCollection(
        textSource.getKnowtatorModel(), textSource, textSource.getConceptAnnotations(), parent);
  }

  private void readToConceptAnnotationCollection(
      KnowtatorModel model,
      TextSource textSource,
      ConceptAnnotationCollection conceptAnnotationCollection,
      Element parent) {
    Map<String, Element> slotToClassIdMap = getSlotsFromXml(parent);
    Map<String, Element> classMentionToClassIdMap = getClassIDsFromXml(parent);
    Map<ConceptAnnotation, Element> annotationToSlotMap = new HashMap<>();

    KnowtatorXmlUtil.asList(parent.getElementsByTagName(OldKnowtatorXmlTags.ANNOTATION)).stream()
        .map(node -> (Element) node)
        .map(
            annotationElement -> {
              String annotationID =
                  ((Element)
                          annotationElement
                              .getElementsByTagName(OldKnowtatorXmlTags.MENTION)
                              .item(0))
                      .getAttribute(OldKnowtatorXmlAttributes.ID);
              Element classElement = classMentionToClassIdMap.get(annotationID);

              String owlClassID =
                  ((Element)
                          classElement
                              .getElementsByTagName(OldKnowtatorXmlTags.MENTION_CLASS)
                              .item(0))
                      .getAttribute(OldKnowtatorXmlAttributes.ID);

              String profileID = null;
              try {
                profileID =
                    annotationElement
                        .getElementsByTagName(OldKnowtatorXmlTags.ANNOTATOR)
                        .item(0)
                        .getTextContent();
                model.addProfile(new Profile(model, profileID));
              } catch (NullPointerException npe) {
                try {
                  profileID = annotationElement.getAttribute(OldKnowtatorXmlAttributes.ANNOTATOR);
                  model.addProfile(new Profile(model, profileID));
                } catch (NullPointerException ignored) {
                  // Ok if element doesn't have attribute
                }
              }
              Profile profile = model.getProfile(profileID).orElse(model.getDefaultProfile());

              Optional<OWLClass> owlClass = model.getOwlClassById(owlClassID);
              if (owlClass.isPresent()) {
                ConceptAnnotation newConceptAnnotation =
                    new ConceptAnnotation(
                        textSource, annotationID, owlClass.get(), profile, "identity", "");
                if (conceptAnnotationCollection.containsID(annotationID)) {
                  model.verifyId(null, newConceptAnnotation, false);
                }
                readToConceptAnnotation(newConceptAnnotation, annotationElement);

                // No need to keep annotations with no allSpanCollection
                if (newConceptAnnotation.size() == 0) {
                  return Optional.empty();
                } else {
                  KnowtatorXmlUtil.asList(
                          classElement.getElementsByTagName(OldKnowtatorXmlTags.HAS_SLOT_MENTION))
                      .stream()
                      .map(node -> (Element) node)
                      .forEach(
                          slotMentionElement -> {
                            String slotMentionID =
                                slotMentionElement.getAttribute(OldKnowtatorXmlAttributes.ID);
                            Element slotElement = slotToClassIdMap.get(slotMentionID);
                            if (slotElement != null) {
                              annotationToSlotMap.put(newConceptAnnotation, slotElement);
                            }
                          });
                  return Optional.of(newConceptAnnotation);
                }
              } else {
                log.warn(
                    String.format(
                        "OWL Class: %s not found for concept: %s", owlClassID, annotationID));
                return Optional.empty();
              }
            })
        .forEach(
            conceptAnnotationOptional ->
                conceptAnnotationOptional
                    .map(o -> (ConceptAnnotation) o)
                    .ifPresent(conceptAnnotationCollection::add));

    GraphSpace oldKnowtatorGraphSpace = new GraphSpace(textSource, "Old Knowtator Relations");
    textSource.add(oldKnowtatorGraphSpace);

    annotationToSlotMap.forEach(
        (annotation, slot) -> {
          String propertyID =
              ((Element) slot.getElementsByTagName(OldKnowtatorXmlTags.MENTION_SLOT).item(0))
                  .getAttribute(OldKnowtatorXmlAttributes.ID);

          AnnotationNode source =
              oldKnowtatorGraphSpace.getAnnotationNodeForConceptAnnotation(annotation);

          for (Node slotMentionValueNode :
              OldKnowtatorXmlUtil.asList(
                  slot.getElementsByTagName(OldKnowtatorXmlTags.COMPLEX_SLOT_MENTION_VALUE))) {
            Element slotMentionValueElement = (Element) slotMentionValueNode;
            String value = slotMentionValueElement.getAttribute(OldKnowtatorXmlAttributes.VALUE);
            conceptAnnotationCollection
                .get(value)
                .map(oldKnowtatorGraphSpace::getAnnotationNodeForConceptAnnotation)
                .ifPresent(
                    target ->
                        model
                            .getOwlObjectPropertyById(propertyID)
                            .ifPresent(
                                property ->
                                    oldKnowtatorGraphSpace.addTriple(
                                        source,
                                        target,
                                        null,
                                        model.getDefaultProfile(),
                                        property,
                                        "",
                                        "",
                                        false,
                                        "")));
          }
        });
  }

  private void readToConceptAnnotation(ConceptAnnotation conceptAnnotation, Element parent) {
    for (Node spanNode :
        KnowtatorXmlUtil.asList(parent.getElementsByTagName(OldKnowtatorXmlTags.SPAN))) {
      if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
        Element spanElement = (Element) spanNode;
        int spanStart =
            Integer.parseInt(spanElement.getAttribute(OldKnowtatorXmlAttributes.SPAN_START));
        int spanEnd =
            Integer.parseInt(spanElement.getAttribute(OldKnowtatorXmlAttributes.SPAN_END));
        Span span = new Span(conceptAnnotation, null, spanStart, spanEnd);
        conceptAnnotation.add(span);
      }
    }
  }
}
