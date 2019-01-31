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

import com.mxgraph.model.mxGraphModel;
import edu.ucdenver.ccp.knowtator.io.XMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.model.object.*;
import org.semanticweb.owlapi.model.OWLClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public final class KnowtatorXMLUtil extends XMLUtil {
	private static final Logger log = LoggerFactory.getLogger(KnowtatorXMLUtil.class);

	private Optional<Element> getRootElement(File file) {
		Optional<Document> docOptional = startRead(file);

		if (docOptional.isPresent()) {
			Document doc = docOptional.get();
			List<Node> knowtatorNodes = asList(doc.getElementsByTagName(KnowtatorXMLTags.KNOWTATOR_PROJECT));
			if (knowtatorNodes.size() > 0) {
				log.info(String.format("Reading from %s", file));
				return Optional.ofNullable((Element) knowtatorNodes.get(0));
			}
		}
		return Optional.empty();
	}


	public void readToTextSourceCollection(KnowtatorModel model, File file) {
		Optional<Element> parentOptional = getRootElement(file);
		parentOptional.ifPresent(parent -> {
			for (Node documentNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.DOCUMENT))) {
				Element documentElement = (Element) documentNode;
				String textSourceId = documentElement.getAttribute(KnowtatorXMLAttributes.ID);
				String textFileName = documentElement.getAttribute(KnowtatorXMLAttributes.FILE);
				if (textFileName == null || textFileName.equals("")) {
					textFileName = textSourceId;
				}
				TextSource newTextSource = new TextSource(model, file, textFileName);
				model.getTextSources().add(newTextSource);
				readToTextSource(newTextSource, documentElement);
			}
		});
	}

	private void readToTextSource(TextSource textSource, Element parent) {
		textSource.getKnowtatorModel().removeModelListener(textSource);
		readToConceptAnnotationCollection(textSource.getKnowtatorModel(), textSource, textSource.getConceptAnnotations(), parent);
		readToGraphSpaceCollection(textSource, textSource.getGraphSpaces(), parent);
		textSource.getKnowtatorModel().addModelListener(textSource);
	}

	private void readToConceptAnnotationCollection(KnowtatorModel model, TextSource textSource, ConceptAnnotationCollection conceptAnnotationCollection, Element parent) {
		HashSet<String> classIDs = KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION)).stream().map(node -> (Element) node).map(annotationElement -> ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.ID)).collect(Collectors.toCollection(HashSet::new));

		Map<String, OWLClass> owlClassMap = model.getOWLClassesByIDs(classIDs);

		KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.ANNOTATION)).stream().map(node -> (Element) node)
				.map(annotationElement -> {
					String annotationID = annotationElement.getAttribute(KnowtatorXMLAttributes.ID);
					String profileID = annotationElement.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
					String type = annotationElement.getAttribute(KnowtatorXMLAttributes.TYPE);
					String owlClassID = ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.ID);
//					String owlClassLabel = ((Element) annotationElement.getElementsByTagName(KnowtatorXMLTags.CLASS).item(0)).getAttribute(KnowtatorXMLAttributes.LABEL);
					String motivation = annotationElement.getAttribute(KnowtatorXMLAttributes.MOTIVATION);

					Profile profile = model.getProfile(profileID).orElse(model.getDefaultProfile());

					Optional<OWLClass> owlClass = Optional.ofNullable(owlClassMap.get(owlClassID));
					if (owlClass.isPresent()) {
						ConceptAnnotation newConceptAnnotation = new ConceptAnnotation(textSource, annotationID, owlClass.get(), profile, type, motivation);
						readToConceptAnnotation(newConceptAnnotation, annotationElement);
						if (newConceptAnnotation.size() == 0) {
							return Optional.empty();
						} else {
							return Optional.of(newConceptAnnotation);
						}
					} else {
						log.warn(String.format("OWL Class: %s not found for concept: %s", owlClassID, annotationID));
						return Optional.empty();

					}
				}).forEach(conceptAnnotationOptional -> conceptAnnotationOptional.map(o -> (ConceptAnnotation) o).ifPresent(conceptAnnotationCollection::add));

	}

	private void readToConceptAnnotation(ConceptAnnotation conceptAnnotation, Element parent) {
		Element spanElement;
		String spanId;
		int spanStart;
		int spanEnd;
		for (Node spanNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.SPAN))) {
			if (spanNode.getNodeType() == Node.ELEMENT_NODE) {
				spanElement = (Element) spanNode;
				spanStart = Integer.parseInt(spanElement.getAttribute(KnowtatorXMLAttributes.SPAN_START));
				spanEnd = Integer.parseInt(spanElement.getAttribute(KnowtatorXMLAttributes.SPAN_END));
				spanId = spanElement.getAttribute(KnowtatorXMLAttributes.ID);

				Span span = new Span(conceptAnnotation, spanId, spanStart, spanEnd);
				conceptAnnotation.add(span);
			}
		}
	}

	private void readToGraphSpaceCollection(TextSource textSource, GraphSpaceCollection graphSpaceCollection, Element parent) {
		for (Node graphSpaceNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.GRAPH_SPACE))) {
			Element graphSpaceElem = (Element) graphSpaceNode;

			String id = graphSpaceElem.getAttribute(KnowtatorXMLAttributes.ID);

			GraphSpace graphSpace = new GraphSpace(textSource, id);
			graphSpaceCollection.add(graphSpace);

			readToGraphSpace(graphSpace, graphSpaceElem);
		}

	}

	private void readToGraphSpace(GraphSpace graphSpace, Element parent) {
		for (Node graphVertexNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.VERTEX))) {
			Element graphVertexElem = (Element) graphVertexNode;

			String id = graphVertexElem.getAttribute(KnowtatorXMLAttributes.ID);
			String annotationID = graphVertexElem.getAttribute(KnowtatorXMLTags.ANNOTATION);
			String x_string = graphVertexElem.getAttribute(KnowtatorXMLAttributes.X_LOCATION);
			String y_string = graphVertexElem.getAttribute(KnowtatorXMLAttributes.Y_LOCATION);

			double x = x_string.equals("") ? 20 : Double.parseDouble(x_string);
			double y = y_string.equals("") ? 20 : Double.parseDouble(y_string);

			graphSpace.getTextSource().getAnnotation(annotationID).ifPresent(conceptAnnotation -> {
				AnnotationNode newVertex = new AnnotationNode(id, conceptAnnotation, x, y, graphSpace);
				graphSpace.addCellToGraph(newVertex);
			});
		}

		for (Node tripleNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.TRIPLE))) {
			Element tripleElem = (Element) tripleNode;

			String id = tripleElem.getAttribute(KnowtatorXMLAttributes.ID);
			String annotatorID = tripleElem.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
			String subjectID = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_SUBJECT);
			String objectID = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_OBJECT);
			String propertyID = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_PROPERTY);
			String quantifier = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_QUANTIFIER);
			String quantifierValue = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_VALUE);
			String propertyIsNegated = tripleElem.getAttribute(KnowtatorXMLAttributes.IS_NEGATED);
			String motivation = tripleElem.getAttribute(KnowtatorXMLAttributes.MOTIVATION);

			Profile annotator = graphSpace.getKnowtatorModel().getProfile(annotatorID).orElse(graphSpace.getKnowtatorModel().getDefaultProfile());
			AnnotationNode source =
					(AnnotationNode) ((mxGraphModel) graphSpace.getModel()).getCells().get(subjectID);
			AnnotationNode target = (AnnotationNode) ((mxGraphModel) graphSpace.getModel()).getCells().get(objectID);


			if (target != null && source != null) {
				graphSpace.getKnowtatorModel().getOWLObjectPropertyByID(propertyID)
						.ifPresent(owlObjectProperty -> graphSpace.addTriple(source, target, id, annotator, owlObjectProperty, quantifier, quantifierValue, propertyIsNegated.equals(KnowtatorXMLAttributes.IS_NEGATED_TRUE), motivation));
			}
		}

		for (Object cell : graphSpace.getChildVertices(graphSpace.getDefaultParent())) {
			((mxGraphModel) graphSpace.getModel()).getCells().remove(((AnnotationNode) cell).getId(), cell);
			String nodeId =
					graphSpace.getTextSource().getGraphSpaces().verifyID(((AnnotationNode) cell).getId(), "node");
			((AnnotationNode) cell).setId(nodeId);
			((mxGraphModel) graphSpace.getModel()).getCells().put(nodeId, cell);
		}
	}

	public void readToProfileCollection(KnowtatorModel model, File file) {
		Optional<Element> parentOptional = getRootElement(file);
		parentOptional.ifPresent(parent -> {
			for (Node profileNode :
					KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.PROFILE))) {
				Element profileElement = (Element) profileNode;
				String profileID = profileElement.getAttribute(KnowtatorXMLAttributes.ID);

				Profile newProfile = new Profile(model, profileID);
				model.getProfiles().add(newProfile);
				model.getProfile(profileID).ifPresent(profile -> readToProfile(model, profile, profileElement));
			}
		});
	}

	private void readToProfile(KnowtatorModel model, Profile profile, Element parent) {
		Map<String, Color> colorMap = new HashMap<>();
		for (Node highlighterNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.HIGHLIGHTER))) {
			Element highlighterElement = (Element) highlighterNode;

			String classID = highlighterElement.getAttribute(KnowtatorXMLAttributes.CLASS_ID);
			Color c = Color.decode(highlighterElement.getAttribute(KnowtatorXMLAttributes.COLOR));

			Color color = new Color((float) c.getRed() / 255, (float) c.getGreen() / 255, (float) c.getBlue() / 255, 1f);
			colorMap.put(classID, color);
		}
		model.getOWLClassesByIDs(colorMap.keySet()).entrySet().parallelStream()
				.forEach(entry -> profile.addColor(entry.getValue(), colorMap.get(entry.getKey())));

	}

	public void writeFromTextSource(TextSource textSource) {
		Optional<Document> domOptional = startWrite(textSource.getSaveLocation());
		domOptional.ifPresent(dom -> {
			Element root = dom.createElement(KnowtatorXMLTags.KNOWTATOR_PROJECT);
			dom.appendChild(root);
			try {
				Element textSourceElement = dom.createElement(KnowtatorXMLTags.DOCUMENT);
				textSourceElement.setAttribute(KnowtatorXMLAttributes.ID, textSource.getId());
				textSourceElement.setAttribute(KnowtatorXMLAttributes.FILE, textSource.getTextFile().getName());
				writeFromConceptAnnotationCollection(textSource.getConceptAnnotations(), dom, textSourceElement);
				writeFromGraphSpaceCollection(textSource.getGraphSpaces(), dom, textSourceElement);
				root.appendChild(textSourceElement);
			} finally {
				XMLUtil.finishWritingXML(dom, textSource.getSaveLocation());
			}
		});

	}

	private void writeFromGraphSpaceCollection(GraphSpaceCollection graphSpaces, Document dom, Element parent) {
		graphSpaces.forEach(graphSpace -> writeFromGraphSpace(graphSpace, dom, parent));
	}

	private void writeFromGraphSpace(GraphSpace graphSpace, Document dom, Element parent) {
		Element graphElem = dom.createElement(KnowtatorXMLTags.GRAPH_SPACE);
		graphElem.setAttribute(KnowtatorXMLAttributes.ID, graphSpace.getId());
		Arrays.stream(graphSpace.getChildVertices(graphSpace.getDefaultParent())).forEach(vertex -> {
			if (vertex instanceof AnnotationNode) {
				writeFromAnnotationNode((AnnotationNode) vertex, dom, graphElem);
			}
		});
		Arrays.stream(graphSpace.getChildEdges(graphSpace.getDefaultParent())).forEach(edge -> {
			if (edge instanceof RelationAnnotation) {
				writeFromRelationAnnotation((RelationAnnotation) edge, dom, graphElem);
			}
		});
		parent.appendChild(graphElem);

	}

	private void writeFromRelationAnnotation(RelationAnnotation relationAnnotation, Document dom, Element parent) {
		Element tripleElem = dom.createElement(KnowtatorXMLTags.TRIPLE);
		tripleElem.setAttribute(KnowtatorXMLAttributes.ID, relationAnnotation.getId());
		try {
			tripleElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, relationAnnotation.getAnnotator().getId());
			tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_SUBJECT, relationAnnotation.getSource().getId());
			tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_OBJECT, relationAnnotation.getTarget().getId());
		} catch (NullPointerException ignore) {

		}

		String propertyID = relationAnnotation.getOwlPropertyRendering();

		tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_PROPERTY, propertyID);

		tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_QUANTIFIER, relationAnnotation.getQuantifier());
		tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_VALUE, relationAnnotation.getQuantifierValue());
		tripleElem.setAttribute(KnowtatorXMLAttributes.IS_NEGATED, relationAnnotation.getIsNegated() ? KnowtatorXMLAttributes.IS_NEGATED_TRUE : KnowtatorXMLAttributes.IS_NEGATED_FALSE);
		tripleElem.setAttribute(KnowtatorXMLAttributes.MOTIVATION, relationAnnotation.getMotivation());

		parent.appendChild(tripleElem);
	}

	private void writeFromAnnotationNode(AnnotationNode annotationNode, Document dom, Element parent) {
		Element vertexElem = dom.createElement(KnowtatorXMLTags.VERTEX);
		vertexElem.setAttribute(KnowtatorXMLAttributes.ID, annotationNode.getId());
		vertexElem.setAttribute(KnowtatorXMLTags.ANNOTATION, annotationNode.getConceptAnnotation().getId());
		vertexElem.setAttribute(KnowtatorXMLAttributes.X_LOCATION, String.valueOf(annotationNode.getGeometry().getX()));
		vertexElem.setAttribute(KnowtatorXMLAttributes.Y_LOCATION, String.valueOf(annotationNode.getGeometry().getY()));
		parent.appendChild(vertexElem);
	}

	private void writeFromConceptAnnotationCollection(ConceptAnnotationCollection conceptAnnotationCollection, Document dom, Element parent) {
		conceptAnnotationCollection.forEach(
				conceptAnnotation -> writeFromConceptAnnotation(conceptAnnotation, dom, parent));
	}

	private void writeFromConceptAnnotation(ConceptAnnotation conceptAnnotation, Document dom, Element parent) {
		Element annotationElem = dom.createElement(KnowtatorXMLTags.ANNOTATION);
		annotationElem.setAttribute(KnowtatorXMLAttributes.ID, conceptAnnotation.getId());
		annotationElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, conceptAnnotation.getAnnotator().getId());
		annotationElem.setAttribute(KnowtatorXMLAttributes.TYPE, conceptAnnotation.getAnnotationType());
		annotationElem.setAttribute(KnowtatorXMLAttributes.MOTIVATION, conceptAnnotation.getMotivation());

		Element classElement = dom.createElement(KnowtatorXMLTags.CLASS);

		classElement.setAttribute(KnowtatorXMLAttributes.ID, conceptAnnotation.getKnowtatorModel().getOWLEntityRendering(conceptAnnotation.getOwlClass()));
		classElement.setAttribute(KnowtatorXMLAttributes.LABEL, conceptAnnotation.getOWLClassLabel());
		annotationElem.appendChild(classElement);

		conceptAnnotation.forEach(span -> writeFromSpan(span, dom, annotationElem));

		parent.appendChild(annotationElem);
	}

	private void writeFromSpan(Span span, Document dom, Element parent) {
		Element spanElement = dom.createElement(KnowtatorXMLTags.SPAN);
		spanElement.setAttribute(KnowtatorXMLAttributes.SPAN_START, String.valueOf(span.getStart()));
		spanElement.setAttribute(KnowtatorXMLAttributes.SPAN_END, String.valueOf(span.getEnd()));
		spanElement.setAttribute(KnowtatorXMLAttributes.ID, span.getId());
		spanElement.setTextContent(span.getSpannedText());
		parent.appendChild(spanElement);
	}

	public void writeFromProfile(Profile profile) {
		Optional<Document> domOptional = startWrite(profile.getSaveLocation());
		domOptional.ifPresent(dom -> {
			Element root = dom.createElement(KnowtatorXMLTags.KNOWTATOR_PROJECT);
			dom.appendChild(root);
			try {
				Element profileElem = dom.createElement(KnowtatorXMLTags.PROFILE);
				profileElem.setAttribute(KnowtatorXMLAttributes.ID, profile.getId());
				profile.getColors().forEach((owlEntity, c) -> {
					Element e = dom.createElement(KnowtatorXMLTags.HIGHLIGHTER);

					e.setAttribute(KnowtatorXMLAttributes.CLASS_ID, profile.getKnowtatorModel().getOWLEntityRendering(owlEntity));

					e.setAttribute(KnowtatorXMLAttributes.COLOR, Profile.convertToHex(c));
					profileElem.appendChild(e);

				});

				root.appendChild(profileElem);
			} finally {
				XMLUtil.finishWritingXML(dom, profile.getSaveLocation());
			}
		});
	}
}
