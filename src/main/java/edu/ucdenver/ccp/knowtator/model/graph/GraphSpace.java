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
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.model.xml.XmlTags;
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GraphSpace extends mxGraph implements Savable {
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(GraphSpace.class);

    private Map<mxCell, Triple> edgeToTripleMap;
    private Map<mxCell, Annotation> vertexToAnnotationMap;
    private String id;
    private KnowtatorManager manager;
    private TextSource textSource;


    public GraphSpace(KnowtatorManager manager, TextSource textSource, String id) {
        this.manager = manager;
        this.textSource = textSource;
        this.id = id;
        edgeToTripleMap = new HashMap<>();
        vertexToAnnotationMap = new HashMap<>();
        setCellsResizable(false);
        setEdgeLabelsMovable(false);
        setAllowDanglingEdges(false);
        setCellsEditable(false);
        setConnectableEdges(false);
        setCellsBendable(false);
    }

    public void addTriple(mxCell edge, String id, Profile annotator, String property, String quantifier, String value) {
        if (id == null) {
            id = String.format("edge_%d", edgeToTripleMap.size());
        }
        while (tripleIdExists(id)) {
            int vertexIDIndex = Integer.parseInt(id.split("edge_")[1]);
            id = String.format("edge_%d", ++vertexIDIndex);
        }

        edge.setValue(property);
        edge.setId(id);
        setCellStyles(mxConstants.STYLE_STARTARROW, "dash", new Object[]{edge});
        setCellStyles(mxConstants.STYLE_STARTSIZE, "12", new Object[]{edge});
        setCellStyles(mxConstants.STYLE_ENDARROW, "block", new Object[]{edge});
        setCellStyles(mxConstants.STYLE_VERTICAL_ALIGN, "top", new Object[]{edge});
        setCellStyles(mxConstants.STYLE_VERTICAL_LABEL_POSITION, "top", new Object[]{edge});
        setCellStyles(mxConstants.STYLE_FONTSIZE, "16", new Object[]{edge});

        Triple newTriple = new Triple(edge, id, annotator,
                quantifier,
                value);

        edgeToTripleMap.put(edge, newTriple);
    }

    private boolean tripleIdExists(String id) {
        for (mxCell edge : edgeToTripleMap.keySet()) {
            if (edge.getId().equals(id)) {
                return true;
            }
        }
        return false;
    }

    public mxCell addVertex(String id, Annotation annotation) {

        mxCell newVertex = new mxCell(annotation.getSpannedText(), new mxGeometry(20, 20, 80, 80), "fontSize=16;fontColor=black;strokeColor=black");
        newVertex.setVertex(true);
        newVertex.setConnectable(true);

        if (id == null) {
            id = String.format("node_%d", vertexToAnnotationMap.size());
        }
        while (((mxGraphModel) getModel()).getCells().containsKey(id)) {
            int vertexIDIndex = Integer.parseInt(id.split("node_")[1]);
            id = String.format("node_%d", ++vertexIDIndex);
        }
        newVertex.setId(id);

        vertexToAnnotationMap.put(newVertex, annotation);
        addCell(newVertex, getDefaultParent());
        return newVertex;
    }

    public void removeCell(mxCell cell) {
        getModel().remove(cell);
        if (cell.isVertex()) vertexToAnnotationMap.remove(cell);
        if (cell.isEdge()) edgeToTripleMap.remove(cell);

        reDrawVertices();
    }

    public void writeToXml(Document dom, Element textSourceElement) {
        Element graphElem = dom.createElement(XmlTags.GRAPH);
        graphElem.setAttribute(XmlTags.ID, id);
        vertexToAnnotationMap.forEach((vertex, annotation) -> {
            Element vertexElem = dom.createElement(XmlTags.VERTEX);
            vertexElem.setAttribute(XmlTags.ID, vertex.getId());
            vertexElem.setAttribute(XmlTags.ANNOTATION, annotation.getID());
            graphElem.appendChild(vertexElem);
        });
        edgeToTripleMap.values().forEach(triple -> triple.writeToXml(dom, graphElem));
        textSourceElement.appendChild(graphElem);
    }

    private void setVertexStyle(mxCell vertex) {
        String classID = vertexToAnnotationMap.get(vertex).getClassID();
        Profile profile = manager.getProfileManager().getCurrentProfile();
        String color = Integer.toHexString(profile.getColor(classID).getRGB()).substring(2);
        String shape = mxConstants.SHAPE_RECTANGLE;

        setCellStyles(mxConstants.STYLE_SHAPE, shape, new Object[]{vertex});
        setCellStyles(mxConstants.STYLE_FILLCOLOR, color, new Object[]{vertex});

    }

    @Override
    public void readFromXml(Element parent, String content) {
        for (Node graphVertexNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.VERTEX))) {
            Element graphVertexElem = (Element) graphVertexNode;

            String id = graphVertexElem.getAttribute(XmlTags.ID);
            String annotationID = graphVertexElem.getAttribute(XmlTags.ANNOTATION);

            Annotation annotation = textSource.getAnnotationManager().getAnnotation(annotationID);
            addVertex(id, annotation);
        }

        for (Node tripleNode : XmlUtil.asList(parent.getElementsByTagName(XmlTags.TRIPLE))) {
            Element tripleElem = (Element) tripleNode;

            String id = tripleElem.getAttribute(XmlTags.ID);
            String annotator = tripleElem.getAttribute(XmlTags.ANNOTATOR);
            String subject = tripleElem.getAttribute(XmlTags.TRIPLE_SUBJECT);
            String object = tripleElem.getAttribute(XmlTags.TRIPLE_OBJECT);
            String property = tripleElem.getAttribute(XmlTags.TRIPLE_PROPERTY);
            String quantifier = tripleElem.getAttribute(XmlTags.TRIPLE_QUANTIFIER);
            String value = tripleElem.getAttribute(XmlTags.TRIPLE_VALUE);

            addTriple(id, subject, object, property, quantifier, value, annotator);


        }
    }

    @Override
    public void readFromOldXml(Element parent) {

    }

    public void addTriple(String id, String subject, String object, String property, String quantifier, String value, String annotator) {
        Profile annotator1 = manager.getProfileManager().addNewProfile(annotator);
        mxCell subject1 = (mxCell) ((mxGraphModel) getModel()).getCells().get(subject);
        mxCell object1 = (mxCell) ((mxGraphModel) getModel()).getCells().get(object);
        mxCell edge = (mxCell) insertEdge(getDefaultParent(), id, property, subject1, object1);
        addTriple(edge,
                id,
                annotator1,
                property,
                quantifier, value);
    }


    public Map<mxCell, Triple> getTriples() {
        return edgeToTripleMap;
    }

    public String getId() {
        return id;
    }

    public void reDrawVertices() {
        getModel().beginUpdate();
        try {
            getVertices().keySet().forEach(v -> {
                setVertexStyle(v);
                updateCellSize(v);
                mxGeometry g = v.getGeometry();
                g.setHeight(g.getHeight() + 50);
                g.setWidth(g.getWidth() + 50);

                getView().validateCell(v);
            });
        } finally {
            getModel().endUpdate();
            refresh();
        }
    }

    public Map<mxCell, Annotation> getVertices() {
        return vertexToAnnotationMap;
    }

    public mxCell containsVertexCorrespondingToAnnotation(Annotation selectedAnnotation) {
        for (Map.Entry<mxCell, Annotation> entry : vertexToAnnotationMap.entrySet()) {
            if (entry.getValue().equals(selectedAnnotation)) {
                return entry.getKey();
            }
        }
        return null;
    }

    public List<Object> getVerticesForAnnotation(Annotation annotation) {
        return vertexToAnnotationMap.entrySet().stream()
                .filter(map -> annotation.equals(map.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "GraphSpace: " + id;
    }

    public TextSource getTextSource() {
        return textSource;
    }
}
