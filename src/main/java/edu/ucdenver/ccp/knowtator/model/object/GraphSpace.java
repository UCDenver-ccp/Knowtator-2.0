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

package edu.ucdenver.ccp.knowtator.model.object;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

public class GraphSpace extends mxGraph implements OWLModelManagerListener, OWLOntologyChangeListener, TextBoundModelObject<GraphSpace>, KnowtatorXMLIO, BratStandoffIO {
	@SuppressWarnings("unused")
	private Logger log = Logger.getLogger(GraphSpace.class);

	private final BaseModel baseModel;
	private final TextSource textSource;
	private String id;

	public GraphSpace(BaseModel baseModel, @Nonnull TextSource textSource, String id) {

		this.baseModel = baseModel;
		this.textSource = textSource;
		this.id = id;


		baseModel.verifyId(id, this, false);
		baseModel.addOntologyChangeListener(this);
		baseModel.addOWLModelManagerListener(this);

		setCellsResizable(false);
		setEdgeLabelsMovable(false);
		setAllowDanglingEdges(false);
		setCellsEditable(false);
		setConnectableEdges(false);
		setCellsBendable(false);
		setResetEdgesOnMove(true);

		getSelectionModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
			Collection selectedCells = (Collection) evt.getProperty("removed");
//			Collection deselectedCells = (Collection) evt.getProperty("added");
			if (selectedCells != null && selectedCells.size() > 0) {
				for (Object cell : selectedCells) {
					if (cell instanceof AnnotationNode) {
						ConceptAnnotation conceptAnnotation = ((AnnotationNode) cell).getConceptAnnotation();
						getTextSource().getSelectedAnnotation()
								.filter(conceptAnnotation1 -> !conceptAnnotation.equals(conceptAnnotation1))
								.ifPresent(conceptAnnotation1 -> getTextSource().setSelectedConceptAnnotation(conceptAnnotation));

					} else if (cell instanceof RelationAnnotation) {
						baseModel.setSelectedOWLEntity(((RelationAnnotation) cell).getProperty());
					}
				}

			}
		});
	}

	@Override
	public int compareTo(GraphSpace o) {
		if (this == o) {
			return 0;
		}
		if (o == null) {
			return 1;
		}

		int result = ModelObject.extractInt(this.getId()) - ModelObject.extractInt(o.getId());
		if (result == 0) {
			return this.getId().compareTo(o.getId());
		} else {
			return result;
		}

	}

  /*
  ADDERS
   */

	private void addCellToGraph(mxCell cell) {
		getModel().beginUpdate();
		try {
			addCell(cell);
			baseModel.fireModelEvent(new ChangeEvent<>(baseModel, null, this));
		} finally {
			//      reDrawGraph();
			getModel().endUpdate();
		}
		//    reDrawGraph();
	}

	public void addTriple(
			AnnotationNode source,
			AnnotationNode target,
			String id,
			Profile annotator,
			OWLObjectProperty property,
			String quantifier,
			String quantifierValue,
			Boolean isNegated,
			String motivation) {
		id = textSource.getGraphSpaces().verifyID(id, "edge");

		if (!(quantifier.equals("only")
				|| quantifier.equals("exactly")
				|| quantifier.equals("min")
				|| quantifier.equals("max"))) {
			quantifier = "some";
		}

		RelationAnnotation newRelationAnnotation;
		newRelationAnnotation =
				new RelationAnnotation(
						baseModel, textSource, this, id,
						source,
						target,
						property,
						Optional.ofNullable(annotator).orElse(baseModel.getDefaultProfile()),
						quantifier,
						quantifierValue,
						isNegated,
						motivation
				);

		setCellStyles(mxConstants.STYLE_STARTARROW, "dash", new Object[]{newRelationAnnotation});
		setCellStyles(mxConstants.STYLE_STARTSIZE, "12", new Object[]{newRelationAnnotation});
		setCellStyles(mxConstants.STYLE_ENDARROW, "block", new Object[]{newRelationAnnotation});
		setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN, "top", new Object[]{newRelationAnnotation});
		setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, "top", new Object[]{newRelationAnnotation});
		setCellStyles(mxConstants.STYLE_FONTSIZE, "16", new Object[]{newRelationAnnotation});

		addCellToGraph(newRelationAnnotation);
	}

  /*
  REMOVERS
   */

	@SuppressWarnings("UnusedReturnValue")
	@Override
	public Object[] removeCells(Object[] cells, boolean includeEdges) {
		cells = super.removeCells(cells, true);
		Arrays.asList(cells).forEach(o -> {
			if (o instanceof RelationAnnotation || o instanceof AnnotationNode) {
				((ModelObject) o).dispose();
			}
		});
		if (cells.length != 0) baseModel.fireModelEvent(new ChangeEvent<>(baseModel, null, this));
		return cells;
	}

  /*
  READERS
   */

	@Override
	public void readFromKnowtatorXML(File file, Element parent) {
		for (Node graphVertexNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.VERTEX))) {
			Element graphVertexElem = (Element) graphVertexNode;

			String id = graphVertexElem.getAttribute(KnowtatorXMLAttributes.ID);
			String annotationID = graphVertexElem.getAttribute(KnowtatorXMLTags.ANNOTATION);
			String x_string = graphVertexElem.getAttribute(KnowtatorXMLAttributes.X_LOCATION);
			String y_string = graphVertexElem.getAttribute(KnowtatorXMLAttributes.Y_LOCATION);

			double x = x_string.equals("") ? 20 : Double.parseDouble(x_string);
			double y = y_string.equals("") ? 20 : Double.parseDouble(y_string);

			this.textSource.getAnnotation(annotationID).ifPresent(conceptAnnotation -> {
				AnnotationNode newVertex = new AnnotationNode(baseModel, id, conceptAnnotation, textSource, x, y, this);
				addCellToGraph(newVertex);
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

			Profile annotator = baseModel.getProfile(annotatorID).orElse(baseModel.getDefaultProfile());
			AnnotationNode source =
					(AnnotationNode) ((mxGraphModel) getModel()).getCells().get(subjectID);
			AnnotationNode target = (AnnotationNode) ((mxGraphModel) getModel()).getCells().get(objectID);


			if (target != null && source != null) {
				baseModel.getOWLObjectPropertyByID(propertyID).ifPresent(owlObjectProperty -> addTriple(
						source,
						target,
						id,
						annotator,
						owlObjectProperty,
						quantifier,
						quantifierValue,
						propertyIsNegated.equals(KnowtatorXMLAttributes.IS_NEGATED_TRUE),
						motivation));
			}
		}

		for (Object cell : getChildVertices(getDefaultParent())) {
			((mxGraphModel) getModel()).getCells().remove(((AnnotationNode) cell).getId(), cell);
			String nodeId =
					textSource.getGraphSpaces().verifyID(((AnnotationNode) cell).getId(), "node");
			((AnnotationNode) cell).setId(nodeId);
			((mxGraphModel) getModel()).getCells().put(nodeId, cell);
		}
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {
	}

	@Override
	public void readFromBratStandoff(
			File file, Map<Character, List<String[]>> annotationMap, String content) {
		annotationMap
				.get(StandoffTags.RELATION)
				.forEach(annotation -> {
					String id = annotation[0];
					String[] relationTriple = annotation[1].split(StandoffTags.relationTripleDelimiter);
					String propertyID = relationTriple[0];
					String subjectAnnotationID = relationTriple[1].split(StandoffTags.relationTripleRoleIDDelimiter)[1];
					String objectAnnotationID = relationTriple[2].split(StandoffTags.relationTripleRoleIDDelimiter)[1];

					Profile annotator = baseModel.getDefaultProfile();

					textSource.getAnnotation(subjectAnnotationID).ifPresent(subjectConceptAnnotation -> {
						AnnotationNode source = getAnnotationNodeForConceptAnnotation(subjectConceptAnnotation);

						textSource.getAnnotation(objectAnnotationID).ifPresent(objectConceptAnnotation -> {
							AnnotationNode target = getAnnotationNodeForConceptAnnotation(objectConceptAnnotation);

							baseModel.getOWLObjectPropertyByID(propertyID).ifPresent(owlObjectProperty -> addTriple(source, target, id, annotator, owlObjectProperty, "", null, false, ""));
						});
					});


				});
	}

	public AnnotationNode getAnnotationNodeForConceptAnnotation(ConceptAnnotation conceptAnnotation) {
		List<AnnotationNode> nodes = getAnnotationNodes(conceptAnnotation);
		AnnotationNode source;
		if (nodes.isEmpty()) {
			source = makeAnnotationNode(conceptAnnotation);
		} else {
			source = nodes.get(0);
		}
		return source;
	}

	public AnnotationNode makeAnnotationNode(ConceptAnnotation conceptAnnotation) {
		String nodeId = textSource.getGraphSpaces().verifyID(null, "node");
		AnnotationNode newVertex = new AnnotationNode(baseModel, nodeId, conceptAnnotation, textSource, 20, 20, this);
		addCellToGraph(newVertex);
		return (AnnotationNode) ((mxGraphModel) getModel()).getCells().get(nodeId);
	}

  /*
  WRITERS
   */

	@SuppressWarnings("RedundantThrows")
	@Override
	public void writeToBratStandoff(
			Writer writer,
			Map<String, Map<String, String>> annotationsConfig,
			Map<String, Map<String, String>> visualConfig)
			throws IOException {
	}

	@Override
	public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
		Element graphElem = dom.createElement(KnowtatorXMLTags.GRAPH_SPACE);
		graphElem.setAttribute(KnowtatorXMLAttributes.ID, id);
		Arrays.stream(getChildVertices(getDefaultParent()))
				.forEach(
						vertex -> {
							if (vertex instanceof AnnotationNode) {
								((AnnotationNode) vertex).writeToKnowtatorXML(dom, graphElem);
							}
						});
		Arrays.stream(getChildEdges(getDefaultParent()))
				.forEach(
						edge -> {
							if (edge instanceof RelationAnnotation) {
								((RelationAnnotation) edge).writeToKnowtatorXML(dom, graphElem);
							}
						});
		textSourceElement.appendChild(graphElem);
	}


    /*
  SETTERS
   */

	public void setVertexStyle(AnnotationNode vertex) {
		mxGeometry g = vertex.getGeometry();
		g.setHeight(g.getHeight() + 200);
		g.setWidth(g.getWidth() + 200);


		String colorString = Integer.toHexString(vertex.getConceptAnnotation().getColor().getRGB()).substring(2);
		String shape = mxConstants.SHAPE_RECTANGLE;

		setCellStyles(mxConstants.STYLE_SHAPE, shape, new Object[]{vertex});
		setCellStyles(mxConstants.STYLE_FILLCOLOR, colorString, new Object[]{vertex});

	}

  /*
  TRANSLATORS
   */

	@Override
	public String toString() {
		return id;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void dispose() {
		Arrays.stream(getChildCells(getDefaultParent())).forEach(cell -> {
			if (cell instanceof RelationAnnotation) {
				((RelationAnnotation) cell).dispose();
			}
			if (cell instanceof AnnotationNode) {
				((AnnotationNode) cell).dispose();
			}
		});
		Arrays.asList(getChildCells(getDefaultParent())).forEach(o -> {
			if (o instanceof RelationAnnotation || o instanceof AnnotationNode) {
				((ModelObject) o).dispose();
			}
		});
		baseModel.removeOntologyChangeListener(this);
		baseModel.removeOWLModelManagerListener(this);
	}

	public boolean containsAnnotation(ConceptAnnotation conceptAnnotation) {
		for (Object vertex : getChildVertices(getDefaultParent())) {
			if (vertex instanceof AnnotationNode) {
				if (((AnnotationNode) vertex).getConceptAnnotation().equals(conceptAnnotation)) {
					return true;
				}
			}
		}
		return false;
	}


	@Override
	public TextSource getTextSource() {
		return textSource;
	}

	@SuppressWarnings("Duplicates")
	@Override
	public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {
		Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
		Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
		OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
		OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

		Set<RelationAnnotation> annotationsToChangeOrRemove = new HashSet<>();

		for (OWLOntologyChange chg : changes) {
			AxiomType type = chg.getAxiom().getAxiomType();
			if (chg.isAxiomChange()) {
				OWLAxiomChange axChg = (OWLAxiomChange) chg;
				if (AxiomType.DECLARATION == type) {
					OWLDeclarationAxiom axiom = ((OWLDeclarationAxiom) axChg.getAxiom());
					if (axiom.getEntity() instanceof OWLObjectProperty) {
						if (axChg instanceof AddAxiom) {
							axiom.accept(addedCollector);
							annotationsToChangeOrRemove.forEach(relationAnnotation -> relationAnnotation.setProperty((OWLObjectProperty) axiom.getEntity()));
							annotationsToChangeOrRemove.clear();
						} else if (axChg instanceof RemoveAxiom) {
							axiom.accept(removedCollector);
							Arrays.asList(getChildEdges(getDefaultParent())).forEach(o -> {
								if (o instanceof RelationAnnotation) {
									if (((RelationAnnotation) o).getProperty() != null) {
										if (((RelationAnnotation) o).getProperty().equals(axiom.getEntity())) {
											annotationsToChangeOrRemove.add((RelationAnnotation) o);
										}
									}
								}
							});
						}
					}

				}
				if (AxiomType.SUBCLASS_OF == type) {
					if (axChg instanceof AddAxiom) {
						axChg.getAxiom().accept(addedCollector);
					} else {
						axChg.getAxiom().accept(removedCollector);
					}
				}
			}
		}

		removeCells(annotationsToChangeOrRemove.toArray(), true);
	}

	@Override
	public void handleChange(OWLModelManagerChangeEvent event) {
		if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
			Arrays.asList(getChildEdges(getDefaultParent())).forEach(o -> {
				if (o instanceof RelationAnnotation) {
					((RelationAnnotation) o).setValue(((RelationAnnotation) o).getProperty());
				}
			});
		}
	}

	public List<RelationAnnotation> getRelationAnnotations() {
		return Arrays.stream(getChildEdges(getDefaultParent())).filter(o -> o instanceof RelationAnnotation).map(o -> (RelationAnnotation) o).collect(Collectors.toList());
	}

	public List<AnnotationNode> getAnnotationNodes(ConceptAnnotation conceptAnnotation) {
		return Arrays.stream(getChildVertices(getDefaultParent()))
				.filter(cell -> cell instanceof AnnotationNode)
				.map(cell -> (AnnotationNode) cell)
				.filter(annotationNode -> annotationNode.getConceptAnnotation().equals(conceptAnnotation))
				.collect(Collectors.toList());
	}
}
