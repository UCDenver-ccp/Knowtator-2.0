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

package edu.ucdenver.ccp.knowtator.model.text.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxUndoManager;
import com.mxgraph.util.mxUndoableEdit;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.DataObjectModificationListener;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.graph.RelationOptionsDialog;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GraphSpace extends mxGraph implements KnowtatorTextBoundDataObjectInterface<GraphSpace>, KnowtatorXMLIO, BratStandoffIO, KnowtatorCollectionListener<ConceptAnnotation> {
    private final mxUndoManager undoManager;
    private mxIEventListener undoHandler;
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(GraphSpace.class);

    private final KnowtatorController controller;
    private boolean areListenersSet;
    private final TextSource textSource;
    private String id;
    private Window parentWindow;
    private final List<DataObjectModificationListener> modificationListeners;

    public GraphSpace(KnowtatorController controller, TextSource textSource, String id) {

        this.controller = controller;
        this.textSource = textSource;
        this.id = id;
        this.undoManager = new mxUndoManager();
        this.undoHandler = (sender, evt) -> undoManager.undoableEditHappened((mxUndoableEdit) evt.getProperty("edit"));
        modificationListeners = new ArrayList<>();


        controller.verifyId(id, this, false);
        textSource.getConceptAnnotationCollection().addCollectionListener(this);

        setCellsResizable(false);
        setEdgeLabelsMovable(false);
        setAllowDanglingEdges(false);
        setCellsEditable(false);
        setConnectableEdges(false);
        setCellsBendable(false);
        setResetEdgesOnMove(true);
        areListenersSet = false;
    }

    public void setParentWindow(Window parentWindow) {
        this.parentWindow = parentWindow;
    }

  /*
  COMPARISON
   */

    @Override
    public int compareTo(GraphSpace graphSpace2) {
        if (this == graphSpace2) {
            return 0;
        }
        if (graphSpace2 == null) {
            return 1;
        }

        int result = KnowtatorDataObjectInterface.extractInt(this.getId()) - KnowtatorDataObjectInterface.extractInt(graphSpace2.getId());
        if (result == 0) {
            return this.getId().compareTo(graphSpace2.getId());
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
        } finally {
            //      reDrawGraph();
            getModel().endUpdate();
            modify(null);
        }
        //    reDrawGraph();
    }

    private void addNode(String nodeId, ConceptAnnotation conceptAnnotation, double x, double y) {

        AnnotationNode newVertex = new AnnotationNode(controller, nodeId, conceptAnnotation, textSource, x, y);
        addCellToGraph(newVertex);

        modify(null);
    }

    public void addTriple(
            AnnotationNode source,
            AnnotationNode target,
            String id,
            Profile annotator,
            OWLObjectProperty property,
            String propertyID,
            String quantifier,
            String quantifierValue,
            Boolean isNegated) {
        id = textSource.getGraphSpaceCollection().verifyID(id, "edge");

        if (!(quantifier.equals("only")
                || quantifier.equals("exactly")
                || quantifier.equals("min")
                || quantifier.equals("max"))) {
            quantifier = "some";
        }

        RelationAnnotation newRelationAnnotation;
        newRelationAnnotation =
                new RelationAnnotation(
                        id,
                        source,
                        target,
                        property,
                        propertyID,
                        annotator,
                        quantifier,
                        quantifierValue,
                        isNegated,
                        controller,
                        textSource,
                        this);

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

    @Override
    public Object[] removeCells(Object[] cells, boolean includeEdges) {
        cells = super.removeCells(cells, true);
        modify(null);
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

            double x = 20;
            double y = 20;
            if (!x_string.equals("")) {
                x = Double.parseDouble(x_string);
            }
            if (!y_string.equals("")) {
                y = Double.parseDouble(y_string);
            }

            ConceptAnnotation conceptAnnotation = this.textSource.getConceptAnnotationCollection().get(annotationID);
            addNode(id, conceptAnnotation, x, y);
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

            Profile annotator = new Profile(controller, annotatorID);
            controller.getProfileCollection().add(annotator);
            AnnotationNode source =
                    (AnnotationNode) ((mxGraphModel) getModel()).getCells().get(subjectID);
            AnnotationNode target = (AnnotationNode) ((mxGraphModel) getModel()).getCells().get(objectID);

            if (target != null && source != null) {
                addTriple(
                        source,
                        target,
                        id,
                        annotator,
                        null,
                        propertyID,
                        quantifier,
                        quantifierValue,
                        propertyIsNegated.equals(KnowtatorXMLAttributes.IS_NEGATED_TRUE));
            }
        }

        for (Object cell : getChildVertices(getDefaultParent())) {
            ((mxGraphModel) getModel()).getCells().remove(((AnnotationNode) cell).getId(), cell);
            String nodeId =
                    textSource.getGraphSpaceCollection().verifyID(((AnnotationNode) cell).getId(), "node");
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
                .forEach(
                        annotation -> {
                            String id = annotation[0];

                            String[] relationTriple = annotation[1].split(StandoffTags.relationTripleDelimiter);
                            String propertyID = relationTriple[0];
                            String subjectAnnotationID =
                                    relationTriple[1].split(StandoffTags.relationTripleRoleIDDelimiter)[1];
                            String objectAnnotationID =
                                    relationTriple[2].split(StandoffTags.relationTripleRoleIDDelimiter)[1];

                            Profile annotator = controller.getProfileCollection().getDefaultProfile();

                            ConceptAnnotation subjectConceptAnnotation =
                                    textSource.getConceptAnnotationCollection().get(subjectAnnotationID);
                            List<Object> subjectAnnotationVertices = getVerticesForAnnotation(subjectConceptAnnotation);
                            AnnotationNode source = makeOrGetAnnotationNode(subjectConceptAnnotation, subjectAnnotationVertices);

                            ConceptAnnotation objectConceptAnnotation =
                                    textSource.getConceptAnnotationCollection().get(objectAnnotationID);
                            List<Object> objectAnnotationVertices = getVerticesForAnnotation(objectConceptAnnotation);
                            AnnotationNode target = makeOrGetAnnotationNode(subjectConceptAnnotation, objectAnnotationVertices);

                            addTriple(source, target, id, annotator, null, propertyID, "", null, false);
                        });
    }

    public AnnotationNode makeOrGetAnnotationNode(ConceptAnnotation subjectConceptAnnotation, List<Object> vertices) {
        AnnotationNode source;
        if (vertices == null || vertices.isEmpty()) {
            String nodeID = textSource.getGraphSpaceCollection().verifyID(null, "node");
            addNode(nodeID, subjectConceptAnnotation, 20, 20);
            source = (AnnotationNode) ((mxGraphModel) getModel()).getCells().get(nodeID);
        } else {
            source = (AnnotationNode) vertices.get(0);
        }
        return source;
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
    UPDATE
     */
    public void reDrawGraph() {
        getModel().beginUpdate();
        try {
            Arrays.stream(getChildVertices(getDefaultParent()))
                    .forEach(
                            vertex -> {
                                if (vertex instanceof AnnotationNode) {
                                    setVertexStyle((AnnotationNode) vertex);
                                }
                                updateCellSize(vertex);

                                getView().validateCell(vertex);
                            });
            Arrays.stream(getChildEdges(getDefaultParent()))
                    .forEach(
                            edge -> {
                                updateCellSize(edge);
                                //                if (edge instanceof RelationAnnotation) {
                                //                    ((RelationAnnotation) edge).setValue(((RelationAnnotation) edge).getValue());
                                //                }
                                getView().validateCell(edge);
                            });
        } finally {
            getModel().endUpdate();
            refresh();
        }
    }

    public void setupListeners() {
        // Handle drag and drop
        // Adds the current selected object property as the edge value
        if (!areListenersSet) {
            getModel().addListener(mxEvent.UNDO, undoHandler);
            getModel().addListener(mxEvent.REDO, undoHandler);

            undoHandler = (sender, evt) -> {
                List<mxUndoableEdit.mxUndoableChange> changes = ((mxUndoableEdit) evt.getProperty("edit")).getChanges();
                setSelectionCells(getSelectionCellsForChanges(changes));
            };

            undoManager.addListener(mxEvent.UNDO, undoHandler);
            undoManager.addListener(mxEvent.REDO, undoHandler);

            addListener(
                    mxEvent.ADD_CELLS,
                    (sender, evt) -> {
                        Object[] cells = (Object[]) evt.getProperty("cells");
                        if (cells != null && cells.length > 0) {
                            for (Object cell : cells) {
                                if (getModel().isEdge(cell) && "".equals(((mxCell) cell).getValue())) {
                                    mxCell edge = (mxCell) cell;
                                    OWLEntity owlEntity = controller.getOWLModel().getSelectedOWLEntity();
                                    if (owlEntity == null || owlEntity instanceof OWLObjectProperty) {
                                        String propertyID = controller.getOWLModel().getOWLEntityRendering(owlEntity);
                                        RelationOptionsDialog relationOptionsDialog = new RelationOptionsDialog(parentWindow, propertyID);
                                        relationOptionsDialog.pack();
                                        relationOptionsDialog.setAlwaysOnTop(true);
                                        relationOptionsDialog.setLocationRelativeTo(parentWindow);
                                        relationOptionsDialog.requestFocus();
                                        relationOptionsDialog.setVisible(true);
                                        if (relationOptionsDialog.getResult() == RelationOptionsDialog.OK_OPTION) {

                                            addTriple(
                                                    (AnnotationNode) edge.getSource(),
                                                    (AnnotationNode) edge.getTarget(),
                                                    null,
                                                    controller.getProfileCollection().getSelection(),
                                                    (OWLObjectProperty) owlEntity,
                                                    relationOptionsDialog.getPropertyID(),
                                                    relationOptionsDialog.getQuantifier(),
                                                    relationOptionsDialog.getQuantifierValue(),
                                                    relationOptionsDialog.getNegation());
                                        }
                                    }
                                    getModel().remove(edge);
                                }
                            }

                            reDrawGraph();
                        }
                    });

            addListener(mxEvent.MOVE_CELLS, (sender, evt) -> reDrawGraph());

            addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> reDrawGraph());

            getSelectionModel()
                    .addListener(
                            mxEvent.CHANGE,
                            (sender, evt) -> {
                                Collection selectedCells = (Collection) evt.getProperty("removed");
                                Collection deselectedCells = (Collection) evt.getProperty("added");
                                if (deselectedCells != null && deselectedCells.size() > 0) {
                                    for (Object cell : deselectedCells) {
                                        if (cell instanceof AnnotationNode) {
                                            setCellStyles(mxConstants.STYLE_STROKEWIDTH, "0", new Object[]{cell});
                                        }
                                    }
                                    //                Arrays.stream(graph.getChildVertices(graph.getDefaultParent()))
                                    //                    .forEach(
                                    //                        cell ->
                                    //                            graph.setCellStyles(
                                    //                                mxConstants.STYLE_STROKEWIDTH, "0", new Object[]
                                    // {cell}));
                                    reDrawGraph();
                                }

                                if (selectedCells != null && selectedCells.size() > 0) {
                                    for (Object cell : selectedCells) {
                                        if (cell instanceof AnnotationNode) {
                                            ConceptAnnotation conceptAnnotation = ((AnnotationNode) cell).getConceptAnnotation();

                                            textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);

                                            setCellStyles(mxConstants.STYLE_STROKEWIDTH, "4", new Object[]{cell});

                                        }
                                    }
                                    reDrawGraph();
                                }
                            });
            areListenersSet = true;
        }
    }


    /*
    GETTERS
     */
    public List<Object> getVerticesForAnnotation(ConceptAnnotation conceptAnnotation) {
        return Arrays.stream(getChildVertices(getDefaultParent()))
                .filter(
                        o ->
                                o instanceof AnnotationNode
                                        && conceptAnnotation.equals(((AnnotationNode) o).getConceptAnnotation()))
                .collect(Collectors.toList());
    }


    /*
  SETTERS
   */

    private void setVertexStyle(AnnotationNode vertex) {
        mxGeometry g = vertex.getGeometry();
        g.setHeight(g.getHeight() + 200);
        g.setWidth(g.getWidth() + 200);

        String color =
                Integer.toHexString(
                        controller
                                .getProfileCollection().getSelection()
                                .getColor(vertex.getConceptAnnotation())
                                .getRGB())
                        .substring(2);
        String shape = mxConstants.SHAPE_RECTANGLE;

        setCellStyles(mxConstants.STYLE_SHAPE, shape, new Object[]{vertex});
        setCellStyles(mxConstants.STYLE_FILLCOLOR, color, new Object[]{vertex});
    }

  /*
  TRANSLATORS
   */

    @Override
    public String toString() {
        return id;
    }

    @Override
    public void selected(SelectionEvent<ConceptAnnotation> changeEvent) {
        if (changeEvent.getNew() == null) {
            setSelectionCells(new Object[0]);
        } else {
            setSelectionCells(getVerticesForAnnotation(changeEvent.getNew()));
        }
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
        removeCells(getChildCells(getDefaultParent()));
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

    @Override
    public void addDataObjectModificationListener(DataObjectModificationListener listener) {
        modificationListeners.add(listener);
    }

    @Override
    public void modify(List<Integer> parameters) {
        modificationListeners.forEach(DataObjectModificationListener::modification);
    }

    @Override
    public void removeDataObjectModificationListener(DataObjectModificationListener listener) {
        modificationListeners.remove(listener);
    }

    @Override
    public void added() {

    }

    @Override
    public void removed() {

    }

    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

    }
}
