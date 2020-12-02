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

package edu.ucdenver.ccp.knowtator.model.object;

import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxPerimeter;
import com.mxgraph.view.mxStylesheet;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLDeclarationAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.util.OWLEntityCollector;

/** The type Graph space. */
public class GraphSpace extends mxGraph
    implements OWLModelManagerListener,
        OWLOntologyChangeListener,
        TextBoundModelObject<GraphSpace> {
  @SuppressWarnings("unused")
  private final Logger log = Logger.getLogger(GraphSpace.class);

  private final KnowtatorModel knowtatorModel;
  private final TextSource textSource;
  private String id;

  /**
   * Instantiates a new Graph space.
   *
   * @param textSource the text source
   * @param id the id
   */
  public GraphSpace(@Nonnull TextSource textSource, String id) {

    this.knowtatorModel = textSource.getKnowtatorModel();
    this.textSource = textSource;

    // TODO: Make annotation nodes reflect changes in annotations
    this.id = knowtatorModel.verifyId(id, this, false);
    knowtatorModel.addOntologyChangeListener(this);
    knowtatorModel.addOwlModelManagerListener(this);

    setCellsResizable(false);
    setEdgeLabelsMovable(false);
    setAllowDanglingEdges(false);
    setCellsEditable(false);
    setConnectableEdges(false);
    setCellsBendable(false);
    setResetEdgesOnMove(true);

    Map<String, Object> edgeStyles = new HashMap<>();

    edgeStyles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
    edgeStyles.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
    edgeStyles.put(mxConstants.STYLE_STROKECOLOR, "#6482B9");
    edgeStyles.put(mxConstants.STYLE_FONTCOLOR, "#446299");
    edgeStyles.put(mxConstants.STYLE_STARTARROW, mxConstants.STYLE_DASHED);
    edgeStyles.put(mxConstants.STYLE_STARTSIZE, "12");
    edgeStyles.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_BLOCK);
    edgeStyles.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_TOP);
    edgeStyles.put(mxConstants.STYLE_VERTICAL_LABEL_POSITION, mxConstants.ALIGN_TOP);
    edgeStyles.put(mxConstants.STYLE_FONTSIZE, "16");

    mxStylesheet stylesheet = new mxStylesheet();
    stylesheet.setDefaultEdgeStyle(edgeStyles);

    Map<String, Object> vertexStyles = new HashMap<>();
    vertexStyles.put(mxConstants.STYLE_FONTSIZE, 16);
    vertexStyles.put(mxConstants.STYLE_FONTCOLOR, "black");
    vertexStyles.put(mxConstants.STYLE_STROKECOLOR, "black");
    vertexStyles.put(mxConstants.STYLE_PERIMETER, mxPerimeter.RectanglePerimeter);
    vertexStyles.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
    vertexStyles.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
    vertexStyles.put(mxConstants.STYLE_FILLCOLOR, "#C3D9FF");
    vertexStyles.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);
    vertexStyles.put(mxConstants.STYLE_STROKEWIDTH, "0");
    stylesheet.setDefaultVertexStyle(vertexStyles);

    Map<String, Object> selectedVertexStyles = new HashMap<>(vertexStyles);
    selectedVertexStyles.put(mxConstants.STYLE_STROKEWIDTH, "4");
    stylesheet.putCellStyle("selected", selectedVertexStyles);

    setStylesheet(stylesheet);

    getSelectionModel()
        .addListener(
            mxEvent.CHANGE,
            (sender, evt) -> {
              Collection selectedCells = (Collection) evt.getProperty("removed");
              // Collection deselectedCells = (Collection) evt.getProperty("added");
              if (selectedCells != null && selectedCells.size() > 0) {
                for (Object cell : selectedCells) {
                  if (cell instanceof AnnotationNode) {
                    ConceptAnnotation conceptAnnotation =
                        ((AnnotationNode) cell).getConceptAnnotation();
                    getTextSource()
                        .getSelectedAnnotation()
                        .filter(conceptAnnotation1 -> !conceptAnnotation.equals(conceptAnnotation1))
                        .ifPresent(
                            conceptAnnotation1 ->
                                getTextSource().setSelectedConceptAnnotation(conceptAnnotation));

                  } else if (cell instanceof RelationAnnotation) {
                    knowtatorModel.setSelectedOwlObjectProperty(
                        ((RelationAnnotation) cell).getProperty());
                  }
                }
              }
            });
  }

  @Override
  public int compareTo(GraphSpace o) {
    if (this.equals(o)) {
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

  /**
   * Add cell to graph.
   *
   * @param cell the cell
   */
  public void addCellToGraph(mxCell cell) {
    getModel().beginUpdate();
    try {
      addCell(cell);
      knowtatorModel.fireModelEvent(new ChangeEvent<>(knowtatorModel, null, this));
    } finally {
      //      reDrawGraph();
      getModel().endUpdate();
    }
    //    reDrawGraph();
  }

  /**
   * Adds a new triple.
   *
   * @param source The source node
   * @param target The target node
   * @param id The triple id
   * @param annotator The annotator
   * @param property The owl object property
   * @param quantifier The quantifier
   * @param quantifierValue A value for the quantifier
   * @param isNegated Negation
   * @param motivation A note about the motivation
   */
  public void addTriple(
      AnnotationNode source,
      AnnotationNode target,
      String id,
      Profile annotator,
      String property,
      Quantifier quantifier,
      String quantifierValue,
      Boolean isNegated,
      String motivation) {
    //    id = textSource.getGraphSpaces().verifyID(id, "edge");

    annotator = Optional.ofNullable(annotator).orElse(knowtatorModel.getDefaultProfile());
    RelationAnnotation newRelationAnnotation =
        new RelationAnnotation(
            this,
            id,
            source,
            target,
            property,
            annotator,
            quantifier,
            quantifierValue,
            isNegated,
            motivation);

    addCellToGraph(newRelationAnnotation);
  }

  /*
  REMOVERS
   */

  @SuppressWarnings("UnusedReturnValue")
  @Override
  public Object[] removeCells(Object[] cells, boolean includeEdges) {
    cells = super.removeCells(cells, true);
    Arrays.asList(cells)
        .forEach(
            o -> {
              if (o instanceof RelationAnnotation || o instanceof AnnotationNode) {
                ((ModelObject) o).dispose();
              }
            });
    if (cells.length != 0) {
      knowtatorModel.fireModelEvent(new ChangeEvent<>(knowtatorModel, null, this));
    }
    return cells;
  }

  /**
   * Gets annotation node for concept annotation.
   *
   * @param conceptAnnotation the concept annotation
   * @return the annotation node for concept annotation
   */
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

  /**
   * Make annotation node annotation node.
   *
   * @param conceptAnnotation the concept annotation
   * @return the annotation node
   */
  public AnnotationNode makeAnnotationNode(ConceptAnnotation conceptAnnotation) {
    return addAnnotationNode(null, conceptAnnotation, 20, 20);
  }

  public AnnotationNode addAnnotationNode(String id, ConceptAnnotation conceptAnnotation, double x, double y) {
    AnnotationNode newVertex = new AnnotationNode(id, conceptAnnotation, x, y, this);
    addCellToGraph(newVertex);
    return newVertex;
  }

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
    Arrays.stream(getChildCells(getDefaultParent()))
        .forEach(
            cell -> {
              if (cell instanceof RelationAnnotation) {
                ((RelationAnnotation) cell).dispose();
              }
              if (cell instanceof AnnotationNode) {
                ((AnnotationNode) cell).dispose();
              }
            });
    Arrays.asList(getChildCells(getDefaultParent()))
        .forEach(
            o -> {
              if (o instanceof RelationAnnotation || o instanceof AnnotationNode) {
                ((ModelObject) o).dispose();
              }
            });
    knowtatorModel.removeOntologyChangeListener(this);
    knowtatorModel.removeOwlModelManagerListener(this);
  }

  @Override
  public KnowtatorModel getKnowtatorModel() {
    return knowtatorModel;
  }

  /**
   * Contains annotation boolean.
   *
   * @param conceptAnnotation the concept annotation
   * @return the boolean
   */
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
              annotationsToChangeOrRemove.forEach(
                  relationAnnotation ->
                      relationAnnotation.setProperty(axiom.getEntity().toStringID()));
              annotationsToChangeOrRemove.clear();
            } else if (axChg instanceof RemoveAxiom) {
              axiom.accept(removedCollector);
              Arrays.asList(getChildEdges(getDefaultParent()))
                  .forEach(
                      o -> {
                        if (o instanceof RelationAnnotation) {
                          if (((RelationAnnotation) o).getProperty() != null) {
                            if (((RelationAnnotation) o)
                                .getProperty()
                                .equals(axiom.getEntity().toStringID())) {
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
      try {
        Arrays.asList(getChildCells(getDefaultParent()))
            .forEach(
                o -> {
                  if (o instanceof RelationAnnotation) {
                    ((RelationAnnotation) o).setLabel();
                  } else if (o instanceof AnnotationNode) {
                    ((AnnotationNode) o).setValue(((AnnotationNode) o).getConceptAnnotation().toMultilineString());
                  }
                });
        refresh();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Gets relation annotations.
   *
   * @return the relation annotations
   */
  public List<RelationAnnotation> getRelationAnnotations() {
    return Arrays.stream(getChildEdges(getDefaultParent()))
        .filter(o -> o instanceof RelationAnnotation)
        .map(o -> (RelationAnnotation) o)
        .collect(Collectors.toList());
  }

  /**
   * Gets annotation nodes.
   *
   * @param conceptAnnotation the concept annotation
   * @return the annotation nodes
   */
  public List<AnnotationNode> getAnnotationNodes(ConceptAnnotation conceptAnnotation) {
    return Arrays.stream(getChildVertices(getDefaultParent()))
        .filter(cell -> cell instanceof AnnotationNode)
        .map(cell -> (AnnotationNode) cell)
        .filter(annotationNode -> annotationNode.getConceptAnnotation().equals(conceptAnnotation))
        .collect(Collectors.toList());
  }
}
