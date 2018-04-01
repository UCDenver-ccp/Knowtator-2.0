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

package edu.ucdenver.ccp.knowtator.model.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import org.apache.log4j.Logger;
import org.apache.uima.cas.CAS;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphSpace extends mxGraph implements Savable {
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(GraphSpace.class);

    private String id;
    private KnowtatorManager manager;
    private TextSource textSource;

    public GraphSpace(KnowtatorManager manager, TextSource textSource, String id) {
        this.manager = manager;
        this.textSource = textSource;
        this.id = id;
        setCellsResizable(false);
        setEdgeLabelsMovable(false);
        setAllowDanglingEdges(false);
        setCellsEditable(false);
        setConnectableEdges(false);
        setCellsBendable(false);
        setResetEdgesOnMove(true);
    }

    /*
    ADDERS
     */

    private void addCellToGraph(mxCell cell) {
        getModel().beginUpdate();
        try {
            addCell(cell);
//            log.warn(String.format("Cell: %s", cell));
        } finally {
            reDrawGraph();
            getModel().endUpdate();
        }

        reDrawGraph();
    }

    public AnnotationNode addNode(String id, Annotation annotation) {
        id = verifyID(id, "node");

        AnnotationNode newVertex = new AnnotationNode(id, annotation);
        addCellToGraph(newVertex);

        return newVertex;
    }

    public void addTriple(AnnotationNode source, AnnotationNode target, String id, Profile annotator, Object property, String quantifier, String quantifierValue) {
        id = verifyID(id, "edge");

        Triple newTriple = new Triple(id, source, target, property, annotator, quantifier, quantifierValue);

        setCellStyles(mxConstants.STYLE_STARTARROW, "dash", new Object[]{newTriple});
        setCellStyles(mxConstants.STYLE_STARTSIZE, "12", new Object[]{newTriple});
        setCellStyles(mxConstants.STYLE_ENDARROW, "block", new Object[]{newTriple});
        setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN, "top", new Object[]{newTriple});
        setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, "top", new Object[]{newTriple});
        setCellStyles(mxConstants.STYLE_FONTSIZE, "16", new Object[]{newTriple});

        addCellToGraph(newTriple);
    }

    private String verifyID(String id, String idPrefix) {
        if (id == null) {
            id = String.format("%s_%d", idPrefix, getChildCells(getDefaultParent()).length);
        }
        while (((mxGraphModel) getModel()).getCells().containsKey(id)) {
            int vertexIDIndex = Integer.parseInt(id.split(String.format("%s_", idPrefix))[1]);
            id = String.format("%s_%d", idPrefix, ++vertexIDIndex);
        }

        return id;
    }

    /*
    REMOVERS
     */

    public void removeSelectedCell() {
        Object cell = getSelectionModel().getCell();
        Arrays.stream(getEdges(cell)).forEach(edge -> getModel().remove(edge));
        getModel().remove(cell);
        reDrawGraph();
    }

    /*
    I/O
     */

    @Override
    public void readFromKnowtatorXML(Element parent, String content) {
        for (Node graphVertexNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.VERTEX))) {
            Element graphVertexElem = (Element) graphVertexNode;

            String id = graphVertexElem.getAttribute(KnowtatorXMLAttributes.ID);
            String annotationID = graphVertexElem.getAttribute(KnowtatorXMLTags.ANNOTATION);

            Annotation annotation = textSource.getAnnotationManager().getAnnotation(annotationID);
            addNode(id, annotation);
        }

        for (Node tripleNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.TRIPLE))) {
            Element tripleElem = (Element) tripleNode;

            String id = tripleElem.getAttribute(KnowtatorXMLAttributes.ID);
            String annotatorID = tripleElem.getAttribute(KnowtatorXMLAttributes.ANNOTATOR);
            String subjectID = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_SUBJECT);
            String objectID = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_OBJECT);
            String propertyID = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_PROPERTY);
            String quantifier = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_QUANTIFIER);
            String quantifierValue = tripleElem.getAttribute(KnowtatorXMLAttributes.TRIPLE_VALUE);

            Profile annotator = manager.getProfileManager().addNewProfile(annotatorID);
            AnnotationNode source = (AnnotationNode) ((mxGraphModel) getModel()).getCells().get(subjectID);
            AnnotationNode target = (AnnotationNode) ((mxGraphModel) getModel()).getCells().get(objectID);

            if (target != null && source != null) {
                addTriple(source, target, id, annotator, propertyID, quantifier, quantifierValue);
            }

        }
    }

    @Override
    public void readFromOldKnowtatorXML(Element parent) {

    }

    @Override
    public void readFromBratStandoff(Map<Character, List<String[]>> annotationMap, String content) {
        annotationMap.get(StandoffTags.RELATION).forEach(annotation -> {
            String id = annotation[0];

            String[] relationTriple = annotation[1].split(StandoffTags.relationTripleDelimiter);
            String propertyID = relationTriple[0];
            String subjectAnnotationID = relationTriple[1].split(StandoffTags.relationTripleRoleIDDelimiter)[1];
            String objectAnnotationID = relationTriple[2].split(StandoffTags.relationTripleRoleIDDelimiter)[1];

            Profile annotator = manager.getProfileManager().getDefaultProfile();

            Annotation subjectAnnotation = textSource.getAnnotationManager().getAnnotation(subjectAnnotationID);
            List<Object> subjectAnnotationVertices = getVerticesForAnnotation(subjectAnnotation);
            AnnotationNode source;
            if (subjectAnnotationVertices.size() == 0) {
                source = addNode(null, subjectAnnotation);
            } else {
                source = (AnnotationNode) subjectAnnotationVertices.get(0);
            }

            Annotation objectAnnotation = textSource.getAnnotationManager().getAnnotation(objectAnnotationID);
            List<Object> objectAnnotationVertices = getVerticesForAnnotation(objectAnnotation);
            AnnotationNode target;
            if (objectAnnotationVertices.size() == 0) {
                target = addNode(null, objectAnnotation);
            } else {
                target = (AnnotationNode) objectAnnotationVertices.get(0);
            }

            addTriple(source, target, id, annotator, propertyID, "", "");


        });
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {

    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

    @Override
    public void readFromUIMAXMI(Element parent, String content) {

    }

    @Override
    public void writeToUIMAXMI(Document dom, Element parent) {

    }

    @Override
    public void convertToUIMA(CAS cas) {

    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element textSourceElement) {
        Element graphElem = dom.createElement(KnowtatorXMLTags.GRAPH_SPACE);
        graphElem.setAttribute(KnowtatorXMLAttributes.ID, id);
        Arrays.stream(getChildVertices(getDefaultParent())).forEach(vertex -> {
            if (vertex instanceof  AnnotationNode) {
                ((AnnotationNode) vertex).writeToKnowtatorXML(dom, graphElem);
            }
        });
        Arrays.stream(getChildEdges(getDefaultParent())).forEach(edge -> {
            if (edge instanceof Triple) {
                ((Triple) edge).writeToKnowtatorXML(dom, graphElem);
            }
        });
        textSourceElement.appendChild(graphElem);
    }

    @Override
    public String toString() {
        return "GraphSpace: " + id;
    }

    public void connectEdgesToProperties() {
        Arrays.stream(getChildEdges(getDefaultParent())).forEach(edge -> {
            if (edge instanceof Triple) {
                Object value = ((Triple) edge).getValue();
                if (value instanceof String) {
                    OWLObjectProperty property = manager.getOWLAPIDataExtractor().getOWLObjectPropertyByID((String) value);
                    if (property != null) {
                        ((Triple) edge).setValue(property);
                    } else {
                        log.warn(String.format("Edge not found in ontology: %s", ((Triple) edge).getValue()));
                    }
                }
            }
        });
    }

    /*
    UPDATE
     */
    public void reDrawGraph() {
//        log.warn("Redrawing Graph");
        getModel().beginUpdate();
        try {
            Arrays.stream(getChildVertices(getDefaultParent())).forEach(vertex -> {

                if (vertex instanceof AnnotationNode) {
                    setVertexStyle((AnnotationNode) vertex);
                }
                updateCellSize(vertex);

                getView().validateCell(vertex);
            });
            Arrays.stream(getChildEdges(getDefaultParent())).forEach(edge -> {
                updateCellSize(edge);
//                if (edge instanceof Triple) {
//                    ((Triple) edge).setValue(((Triple) edge).getValue());
//                }
                getView().validateCell(edge);
            });
        } finally {
            getModel().endUpdate();
            refresh();
        }
    }

    private void setVertexStyle(AnnotationNode vertex) {
        mxGeometry g = vertex.getGeometry();
        g.setHeight(g.getHeight() + 200);
        g.setWidth(g.getWidth() + 200);

        String classID = vertex.getAnnotation().getClassID();
        Profile profile = manager.getProfileManager().getCurrentProfile();
        String color = Integer.toHexString(profile.getColor(classID).getRGB()).substring(2);
        String shape = mxConstants.SHAPE_RECTANGLE;

        setCellStyles(mxConstants.STYLE_SHAPE, shape, new Object[]{vertex});
        setCellStyles(mxConstants.STYLE_FILLCOLOR, color, new Object[]{vertex});

    }

    /*
    GETTERS, CHECKERS, SETTERS
     */

    public String getId() {
        return id;
    }

    public AnnotationNode containsVertexCorrespondingToAnnotation(Annotation selectedAnnotation) {
        for (Object o : getChildVertices(getDefaultParent())) {
            if (o instanceof AnnotationNode && ((AnnotationNode) o).getAnnotation().equals(selectedAnnotation)) {
                return (AnnotationNode) o;
            }
        }
        return null;
    }

    public List<Object> getVerticesForAnnotation(Annotation annotation) {
        return Arrays.stream(getChildVertices(getDefaultParent()))
                .filter(o -> o instanceof AnnotationNode && annotation.equals(((AnnotationNode) o).getAnnotation()))
                .collect(Collectors.toList());
    }

    public void setId(String id) { this.id = id; }

    public TextSource getTextSource() {
        return textSource;
    }

    public void reassignProperty(OWLEntity oldProperty, OWLEntity newProperty) {
        log.warn(String.format("Old Property: %s, New Property: %s", oldProperty, newProperty));

        List<Triple> edges = getTriplesCorrespondingToProperty(oldProperty);
        edges.forEach(edge -> edge.setValue(newProperty));
    }

    private List<Triple> getTriplesCorrespondingToProperty(OWLEntity property) {
        List<Triple> edges = new ArrayList<>();
        Arrays.stream(getChildEdges(getDefaultParent())).forEach(edge -> {
            if (((Triple) edge).getValue() == property) {
                edges.add((Triple) edge);
            }
        });

        return edges;
    }
}
