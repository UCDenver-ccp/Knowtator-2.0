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

import com.mxgraph.model.mxGraphModel;
import edu.ucdenver.ccp.knowtator.io.XmlUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.awt.Color;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/** The type Knowtator xml util. */
public final class KnowtatorXmlUtil extends XmlUtil {
  private static final Logger log = LoggerFactory.getLogger(KnowtatorXmlUtil.class);

  private Optional<Element> getRootElement(File file) {
    Optional<Document> docOptional = startRead(file);

    if (docOptional.isPresent()) {
      Document doc = docOptional.get();
      List<Node> knowtatorNodes =
          asList(doc.getElementsByTagName(KnowtatorXmlTags.KNOWTATOR_PROJECT));
      if (knowtatorNodes.size() > 0) {
        log.info(String.format("Reading from %s", file));
        return Optional.ofNullable((Element) knowtatorNodes.get(0));
      }
    }
    return Optional.empty();
  }

  /**
   * Read to text source.
   *
   * @param textSource the text source
   * @param file the file
   */
  public void readToTextSource(TextSource textSource, File file) {
    Optional<Element> parentOptional = getRootElement(file);
    parentOptional.ifPresent(
        parent -> {
          for (Node documentNode :
              KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.DOCUMENT))) {
            Element documentElement = (Element) documentNode;
            readToTextSource(textSource, documentElement);
          }
        });
  }

  private void readToTextSource(TextSource textSource, Element parent) {
    textSource.getKnowtatorModel().removeModelListener(textSource);
    readToConceptAnnotationCollection(
        textSource.getKnowtatorModel(), textSource, textSource.getConceptAnnotations(), parent);
    readToGraphSpaceCollection(textSource, textSource.getGraphSpaces(), parent);
    textSource.getKnowtatorModel().addModelListener(textSource);
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
          for (Node documentNode :
              KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.DOCUMENT))) {
            Element documentElement = (Element) documentNode;
            String textSourceId = documentElement.getAttribute(KnowtatorXmlAttributes.ID);
            String textFileName = documentElement.getAttribute(KnowtatorXmlAttributes.FILE);
            if (textFileName == null || textFileName.equals("")) {
              textFileName = textSourceId;
            }
            TextSource newTextSource = new TextSource(model, file, textFileName);
            model.getTextSources().add(newTextSource);
            readToTextSource(newTextSource, documentElement);
          }
        });
  }

  private void readToConceptAnnotationCollection(
      KnowtatorModel model,
      TextSource textSource,
      ConceptAnnotationCollection conceptAnnotationCollection,
      Element parent) {
    HashSet<String> classIDs =
        KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.ANNOTATION)).stream()
            .map(node -> (Element) node)
            .map(
                annotationElement ->
                    ((Element)
                            annotationElement.getElementsByTagName(KnowtatorXmlTags.CLASS).item(0))
                        .getAttribute(KnowtatorXmlAttributes.ID))
            .collect(Collectors.toCollection(HashSet::new));

    Map<String, OWLClass> owlClassMap = model.getOwlClassesByIds(classIDs);

    KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.ANNOTATION)).stream()
        .map(node -> (Element) node)
        .map(
            annotationElement -> {
              String annotationID = annotationElement.getAttribute(KnowtatorXmlAttributes.ID);
              String profileID = annotationElement.getAttribute(KnowtatorXmlAttributes.ANNOTATOR);
              String type = annotationElement.getAttribute(KnowtatorXmlAttributes.TYPE);
              String owlClassID =
                  ((Element) annotationElement.getElementsByTagName(KnowtatorXmlTags.CLASS).item(0))
                      .getAttribute(KnowtatorXmlAttributes.ID);
              String motivation = annotationElement.getAttribute(KnowtatorXmlAttributes.MOTIVATION);

              Profile profile = model.getProfile(profileID).orElse(model.getDefaultProfile());

              Optional<OWLClass> owlClass = Optional.ofNullable(owlClassMap.get(owlClassID));
              if (owlClass.isPresent()) {
                ConceptAnnotation newConceptAnnotation =
                    new ConceptAnnotation(
                        textSource, annotationID, owlClass.get(), profile, type, motivation);
                readToConceptAnnotation(newConceptAnnotation, annotationElement);
                if (newConceptAnnotation.size() == 0) {
                  return Optional.empty();
                } else {
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
  }

  private void readToConceptAnnotation(ConceptAnnotation conceptAnnotation, Element parent) {
    Element spanElement;
    String spanId;
    int spanStart;
    int spanEnd;
    for (Node spanNode :
        KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.SPAN))) {
      if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
        spanElement = (Element) spanNode;
        spanStart = Integer.parseInt(spanElement.getAttribute(KnowtatorXmlAttributes.SPAN_START));
        spanEnd = Integer.parseInt(spanElement.getAttribute(KnowtatorXmlAttributes.SPAN_END));
        spanId = spanElement.getAttribute(KnowtatorXmlAttributes.ID);

        Span span = new Span(conceptAnnotation, spanId, spanStart, spanEnd);
        conceptAnnotation.add(span);
      }
    }
  }

  private void readToGraphSpaceCollection(
      TextSource textSource, GraphSpaceCollection graphSpaceCollection, Element parent) {
    for (Node graphSpaceNode :
        KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.GRAPH_SPACE))) {
      Element graphSpaceElem = (Element) graphSpaceNode;

      String id = graphSpaceElem.getAttribute(KnowtatorXmlAttributes.ID);

      GraphSpace graphSpace = new GraphSpace(textSource, id);
      graphSpaceCollection.add(graphSpace);

      readToGraphSpace(graphSpace, graphSpaceElem);
    }
  }

  private void readToGraphSpace(GraphSpace graphSpace, Element parent) {
    for (Node graphVertexNode :
        KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.VERTEX))) {
      Element graphVertexElem = (Element) graphVertexNode;

      String id = graphVertexElem.getAttribute(KnowtatorXmlAttributes.ID);
      String annotationID = graphVertexElem.getAttribute(KnowtatorXmlTags.ANNOTATION);
      String xstring = graphVertexElem.getAttribute(KnowtatorXmlAttributes.X_LOCATION);
      String ystring = graphVertexElem.getAttribute(KnowtatorXmlAttributes.Y_LOCATION);

      double x = xstring.equals("") ? 20 : Double.parseDouble(xstring);
      double y = ystring.equals("") ? 20 : Double.parseDouble(ystring);

      graphSpace
          .getTextSource()
          .getAnnotation(annotationID)
          .ifPresent(
              conceptAnnotation -> {
                AnnotationNode newVertex =
                    new AnnotationNode(id, conceptAnnotation, x, y, graphSpace);
                graphSpace.addCellToGraph(newVertex);
              });
    }

    for (Node tripleNode :
        KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.TRIPLE))) {
      Element tripleElem = (Element) tripleNode;

      String id = tripleElem.getAttribute(KnowtatorXmlAttributes.ID);
      String annotatorID = tripleElem.getAttribute(KnowtatorXmlAttributes.ANNOTATOR);
      String subjectID = tripleElem.getAttribute(KnowtatorXmlAttributes.TRIPLE_SUBJECT);
      String objectID = tripleElem.getAttribute(KnowtatorXmlAttributes.TRIPLE_OBJECT);
      String propertyID = tripleElem.getAttribute(KnowtatorXmlAttributes.TRIPLE_PROPERTY);
      String quantifier = tripleElem.getAttribute(KnowtatorXmlAttributes.TRIPLE_QUANTIFIER);
      String quantifierValue = tripleElem.getAttribute(KnowtatorXmlAttributes.TRIPLE_VALUE);
      String propertyIsNegated = tripleElem.getAttribute(KnowtatorXmlAttributes.IS_NEGATED);
      String motivation = tripleElem.getAttribute(KnowtatorXmlAttributes.MOTIVATION);

      Profile annotator =
          graphSpace
              .getKnowtatorModel()
              .getProfile(annotatorID)
              .orElse(graphSpace.getKnowtatorModel().getDefaultProfile());
      AnnotationNode source =
          (AnnotationNode) ((mxGraphModel) graphSpace.getModel()).getCells().get(subjectID);
      AnnotationNode target =
          (AnnotationNode) ((mxGraphModel) graphSpace.getModel()).getCells().get(objectID);

      if (target != null && source != null) {
        graphSpace
            .getKnowtatorModel()
            .getOwlObjectPropertyById(propertyID)
            .ifPresent(
                owlObjectProperty ->
                    graphSpace.addTriple(
                        source,
                        target,
                        id,
                        annotator,
                        owlObjectProperty,
                        quantifier,
                        quantifierValue,
                        propertyIsNegated.equals(KnowtatorXmlAttributes.IS_NEGATED_TRUE),
                        motivation));
      }
    }

    for (Object cell : graphSpace.getChildVertices(graphSpace.getDefaultParent())) {
      ((mxGraphModel) graphSpace.getModel())
          .getCells()
          .remove(((AnnotationNode) cell).getId(), cell);
      String nodeId =
          graphSpace
              .getTextSource()
              .getGraphSpaces()
              .verifyID(((AnnotationNode) cell).getId(), "node");
      ((AnnotationNode) cell).setId(nodeId);
      ((mxGraphModel) graphSpace.getModel()).getCells().put(nodeId, cell);
    }
  }

  /**
   * Read to profile collection.
   *
   * @param model the model
   * @param file the file
   */
  public void readToProfileCollection(KnowtatorModel model, File file) {
    Optional<Element> parentOptional = getRootElement(file);
    parentOptional.ifPresent(
        parent -> {
          for (Node profileNode :
              KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.PROFILE))) {
            Element profileElement = (Element) profileNode;
            String profileID = profileElement.getAttribute(KnowtatorXmlAttributes.ID);

            Profile newProfile = new Profile(model, profileID);
            model.getProfiles().add(newProfile);
            model
                .getProfile(profileID)
                .ifPresent(profile -> readToProfile(model, profile, profileElement));
          }
        });
  }

  private void readToProfile(KnowtatorModel model, Profile profile, Element parent) {
    Map<String, Color> colorMap = new HashMap<>();
    for (Node highlighterNode :
        KnowtatorXmlUtil.asList(parent.getElementsByTagName(KnowtatorXmlTags.HIGHLIGHTER))) {
      Element highlighterElement = (Element) highlighterNode;

      String classID = highlighterElement.getAttribute(KnowtatorXmlAttributes.CLASS_ID);
      Color c = Color.decode(highlighterElement.getAttribute(KnowtatorXmlAttributes.COLOR));

      Color color =
          new Color(
              (float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);
      colorMap.put(classID, color);
    }
    model
        .getOwlClassesByIds(colorMap.keySet())
        .entrySet()
        .parallelStream()
        .forEach(entry -> profile.addColor(entry.getValue(), colorMap.get(entry.getKey())));
  }

  /**
   * Write from text source.
   *
   * @param textSource the text source
   */
  public void writeFromTextSource(TextSource textSource) {
    Optional<Document> domOptional = startWrite(textSource.getSaveLocation());
    domOptional.ifPresent(
        dom -> {
          Element root = dom.createElement(KnowtatorXmlTags.KNOWTATOR_PROJECT);
          dom.appendChild(root);
          try {
            Element textSourceElement = dom.createElement(KnowtatorXmlTags.DOCUMENT);
            textSourceElement.setAttribute(KnowtatorXmlAttributes.ID, textSource.getId());
            textSourceElement.setAttribute(
                KnowtatorXmlAttributes.FILE, textSource.getTextFile().getName());
            writeFromConceptAnnotationCollection(
                textSource.getConceptAnnotations(), dom, textSourceElement);
            writeFromGraphSpaceCollection(textSource.getGraphSpaces(), dom, textSourceElement);
            root.appendChild(textSourceElement);
          } finally {
            XmlUtil.finishWritingXml(dom, textSource.getSaveLocation());
          }
        });
  }

  private void writeFromGraphSpaceCollection(
      GraphSpaceCollection graphSpaces, Document dom, Element parent) {
    graphSpaces.forEach(graphSpace -> writeFromGraphSpace(graphSpace, dom, parent));
  }

  private void writeFromGraphSpace(GraphSpace graphSpace, Document dom, Element parent) {
    Element graphElem = dom.createElement(KnowtatorXmlTags.GRAPH_SPACE);
    graphElem.setAttribute(KnowtatorXmlAttributes.ID, graphSpace.getId());
    Arrays.stream(graphSpace.getChildVertices(graphSpace.getDefaultParent()))
        .forEach(
            vertex -> {
              if (vertex instanceof AnnotationNode) {
                writeFromAnnotationNode((AnnotationNode) vertex, dom, graphElem);
              }
            });
    Arrays.stream(graphSpace.getChildEdges(graphSpace.getDefaultParent()))
        .forEach(
            edge -> {
              if (edge instanceof RelationAnnotation) {
                writeFromRelationAnnotation((RelationAnnotation) edge, dom, graphElem);
              }
            });
    parent.appendChild(graphElem);
  }

  private void writeFromRelationAnnotation(
      RelationAnnotation relationAnnotation, Document dom, Element parent) {
    Element tripleElem = dom.createElement(KnowtatorXmlTags.TRIPLE);
    tripleElem.setAttribute(KnowtatorXmlAttributes.ID, relationAnnotation.getId());
    try {
      tripleElem.setAttribute(
          KnowtatorXmlAttributes.ANNOTATOR, relationAnnotation.getAnnotator().getId());
      tripleElem.setAttribute(
          KnowtatorXmlAttributes.TRIPLE_SUBJECT, relationAnnotation.getSource().getId());
      tripleElem.setAttribute(
          KnowtatorXmlAttributes.TRIPLE_OBJECT, relationAnnotation.getTarget().getId());
    } catch (NullPointerException ignore) {
      // Ok to ignore if an attribute not present
    }

    String propertyID = relationAnnotation.getOwlPropertyRendering();

    tripleElem.setAttribute(KnowtatorXmlAttributes.TRIPLE_PROPERTY, propertyID);

    tripleElem.setAttribute(
        KnowtatorXmlAttributes.TRIPLE_QUANTIFIER, relationAnnotation.getQuantifier());
    tripleElem.setAttribute(
        KnowtatorXmlAttributes.TRIPLE_VALUE, relationAnnotation.getQuantifierValue());
    tripleElem.setAttribute(
        KnowtatorXmlAttributes.IS_NEGATED,
        relationAnnotation.getIsNegated()
            ? KnowtatorXmlAttributes.IS_NEGATED_TRUE
            : KnowtatorXmlAttributes.IS_NEGATED_FALSE);
    tripleElem.setAttribute(KnowtatorXmlAttributes.MOTIVATION, relationAnnotation.getMotivation());

    parent.appendChild(tripleElem);
  }

  private void writeFromAnnotationNode(
      AnnotationNode annotationNode, Document dom, Element parent) {
    Element vertexElem = dom.createElement(KnowtatorXmlTags.VERTEX);
    vertexElem.setAttribute(KnowtatorXmlAttributes.ID, annotationNode.getId());
    vertexElem.setAttribute(
        KnowtatorXmlTags.ANNOTATION, annotationNode.getConceptAnnotation().getId());
    vertexElem.setAttribute(
        KnowtatorXmlAttributes.X_LOCATION, String.valueOf(annotationNode.getGeometry().getX()));
    vertexElem.setAttribute(
        KnowtatorXmlAttributes.Y_LOCATION, String.valueOf(annotationNode.getGeometry().getY()));
    parent.appendChild(vertexElem);
  }

  private void writeFromConceptAnnotationCollection(
      ConceptAnnotationCollection conceptAnnotationCollection, Document dom, Element parent) {
    conceptAnnotationCollection.forEach(
        conceptAnnotation -> writeFromConceptAnnotation(conceptAnnotation, dom, parent));
  }

  private void writeFromConceptAnnotation(
      ConceptAnnotation conceptAnnotation, Document dom, Element parent) {
    Element annotationElem = dom.createElement(KnowtatorXmlTags.ANNOTATION);
    annotationElem.setAttribute(KnowtatorXmlAttributes.ID, conceptAnnotation.getId());
    annotationElem.setAttribute(
        KnowtatorXmlAttributes.ANNOTATOR, conceptAnnotation.getAnnotator().getId());
    annotationElem.setAttribute(KnowtatorXmlAttributes.TYPE, conceptAnnotation.getAnnotationType());
    annotationElem.setAttribute(
        KnowtatorXmlAttributes.MOTIVATION, conceptAnnotation.getMotivation());

    Element classElement = dom.createElement(KnowtatorXmlTags.CLASS);

    classElement.setAttribute(
        KnowtatorXmlAttributes.ID,
        conceptAnnotation
            .getKnowtatorModel()
            .getOwlEntityRendering(conceptAnnotation.getOwlClass()));
    classElement.setAttribute(KnowtatorXmlAttributes.LABEL, conceptAnnotation.getOwlClassLabel());
    annotationElem.appendChild(classElement);

    conceptAnnotation.forEach(span -> writeFromSpan(span, dom, annotationElem));

    parent.appendChild(annotationElem);
  }

  private void writeFromSpan(Span span, Document dom, Element parent) {
    Element spanElement = dom.createElement(KnowtatorXmlTags.SPAN);
    spanElement.setAttribute(KnowtatorXmlAttributes.SPAN_START, String.valueOf(span.getStart()));
    spanElement.setAttribute(KnowtatorXmlAttributes.SPAN_END, String.valueOf(span.getEnd()));
    spanElement.setAttribute(KnowtatorXmlAttributes.ID, span.getId());
    spanElement.setTextContent(span.getSpannedText());
    parent.appendChild(spanElement);
  }

  /**
   * Write from profile.
   *
   * @param profile the profile
   */
  public void writeFromProfile(Profile profile) {
    Optional<Document> domOptional = startWrite(profile.getSaveLocation());
    domOptional.ifPresent(
        dom -> {
          Element root = dom.createElement(KnowtatorXmlTags.KNOWTATOR_PROJECT);
          dom.appendChild(root);
          try {
            Element profileElem = dom.createElement(KnowtatorXmlTags.PROFILE);
            profileElem.setAttribute(KnowtatorXmlAttributes.ID, profile.getId());
            profile
                .getColors()
                .forEach(
                    (owlEntity, c) -> {
                      Element e = dom.createElement(KnowtatorXmlTags.HIGHLIGHTER);

                      e.setAttribute(
                          KnowtatorXmlAttributes.CLASS_ID,
                          profile.getKnowtatorModel().getOwlEntityRendering(owlEntity));

                      e.setAttribute(KnowtatorXmlAttributes.COLOR, Profile.convertToHex(c));
                      profileElem.appendChild(e);
                    });

            root.appendChild(profileElem);
          } finally {
            XmlUtil.finishWritingXml(dom, profile.getSaveLocation());
          }
        });
  }
}
