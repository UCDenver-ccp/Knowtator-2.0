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

package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.listeners.GraphListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.text.TextPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

//TODO: Display coreferences
//TODO: Expand to show ontology terms as nodes
public class GraphViewer implements ProfileListener, GraphListener {

    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(GraphViewer.class);
    private final KnowtatorManager manager;
    private final KnowtatorView view;
    private TextPane textPane;
    private JDialog dialog;

    private mxGraphComponent currentGraphComponent;
    private List<mxGraphComponent> graphComponentList;
    private JScrollPane scrollPane;
    private JLabel graphLabel;
    private GraphViewMenu graphViewMenu;

    public GraphViewer(JFrame frame, KnowtatorManager manager, KnowtatorView view, TextPane textPane) {
        this.manager = manager;
        this.view = view;
        this.textPane = textPane;
        graphComponentList = new ArrayList<>();
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        graphLabel = new JLabel();

        makeDialog(frame);

    }

    private void makeDialog(JFrame frame) {
        dialog = new JDialog(frame, "Graph Viewer - " + textPane.getTextSource().getDocID());

        dialog.setLayout(new BorderLayout());

        dialog.setVisible(false);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.setSize(new Dimension(800, 800));
        dialog.setLocationRelativeTo(view);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GraphMenu(manager, this));

        graphViewMenu = new GraphViewMenu(this);
        menuBar.add(graphViewMenu);
        menuBar.add(new GraphOptionsMenu(this));
        dialog.setJMenuBar(menuBar);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BorderLayout());
        subPanel.add(new GraphToolBar(this), BorderLayout.NORTH);
        subPanel.add(graphLabel, BorderLayout.SOUTH);

        dialog.add(subPanel, BorderLayout.NORTH);

//        dialog.add(this);
    }


    public void addNewGraphSpace(String graphID) {
        textPane.getTextSource().getAnnotationManager().addGraphSpace(graphID);
    }

    public mxGraphComponent getCurrentGraphComponent() {
        return currentGraphComponent;
    }

    private mxGraphComponent getGraphComponent(String graphID) {
        if (graphID == null) return currentGraphComponent;
        for (mxGraphComponent graphComponent : graphComponentList) {
            if (graphComponent.getName().equals(graphID)) {
                return graphComponent;
            }
        }
        return null;
    }

    private void setupListeners(mxGraphComponent graphComponent) {
        GraphSpace graph = (GraphSpace) graphComponent.getGraph();
        //Handle drag and drop
        //Adds the current selected object property as the edge value
        graph.addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
            Object[] cells = (Object[]) evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell) && ((mxCell) cell).getValue() != null && ((mxCell) cell).getValue().equals("")) {
                    String propertyName = manager.getOWLAPIDataExtractor().getSelectedPropertyID();

                    if (propertyName == null) {
                        log.warn("No Object property selected");
                        JTextField field1 = new JTextField();
                        Object[] message = {
                                "Relationship ID", field1,
                        };
                        if (JOptionPane.showConfirmDialog(null, message, "Enter an ID for this property", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                            propertyName = field1.getText();
                        }
                    }
                    if (propertyName == null) {
                        graph.getModel().remove(cell);
                    } else {
                        JTextField quantifierField = new JTextField(10);
                        JTextField valueField = new JTextField(10);
                        JPanel restrictionPanel = new JPanel();
                        restrictionPanel.add(new JLabel("Quantifier:"));
                        restrictionPanel.add(quantifierField);
                        restrictionPanel.add(Box.createHorizontalStrut(15));
                        restrictionPanel.add(new JLabel("Value:"));
                        restrictionPanel.add(valueField);

                        String quantifier = "";
                        String value = "";
                        int result = JOptionPane.showConfirmDialog(null, restrictionPanel,
                                "Restriction options", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            quantifier = quantifierField.getText();
                            value = valueField.getText();
                        }
                        graph.addTriple(
                                (mxCell) cell,
                                null,
                                manager.getProfileManager().getCurrentProfile(),
                                propertyName,
                                quantifier,
                                value);
                    }
                    graph.reDrawVertices();
//                    executeLayout(null);
                }
            }
        });

        graph.addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> {
            Object[] cells = (Object[]) evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
                    graph.removeVertex((mxCell) cell);
                    graph.reDrawVertices();
//                    executeLayout(null);
                }
            }
        });

        graph.getSelectionModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
            Collection selectedCells = (Collection) evt.getProperty("removed");
            graph.getVertices().keySet().forEach(cell -> graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "0", new Object[]{cell}));

            if (selectedCells != null) {
                for (Object cell : selectedCells) {
                    if (graph.getModel().isVertex(cell)) {
                        //noinspection SuspiciousMethodCalls
                        Annotation annotation = graph.getVertices().get(cell);

                        manager.annotationSelectionChangedEvent(annotation);

                        view.owlEntitySelectionChanged(manager.getOWLAPIDataExtractor().getOWLClassByID(annotation.getClassID()));
                        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "4", new Object[]{cell});
                    } else {
                        view.owlEntitySelectionChanged(manager.getOWLAPIDataExtractor().getOWLObjectPropertyByID((String) ((mxCell) cell).getValue()));
                    }
                }
            }
            graph.reDrawVertices();
//            executeLayout(null);
        });
    }

    private void addAnnotationVertex(Annotation annotation) {
        if (currentGraphComponent != null) {
            GraphSpace graph = (GraphSpace) currentGraphComponent.getGraph();
            graph.getModel().beginUpdate();
            try {

                if (annotation != null) {

                    mxCell vertex = graph.addVertex(null, annotation);
                    goToVertex(vertex);
                }

            } finally {
                graph.reDrawVertices();
//            executeLayout(null);
                graph.getModel().endUpdate();
            }
        }

    }

    public void goToAnnotationVertex(String graphID, Annotation annotation) {
        if (annotation != null) {
            currentGraphComponent = getGraphComponent(graphID);
            if (currentGraphComponent != null) {
                GraphSpace graphSpace = (GraphSpace) currentGraphComponent.getGraph();
                List<Object> vertices = graphSpace.getVerticesForAnnotation(annotation);
                if (vertices.size() > 0) {
                    graphSpace.setSelectionCells(vertices);
                    goToVertex(vertices.get(0));
                }
            }
        }
    }

    private void goToVertex(Object vertex) {
        if (currentGraphComponent != null) {
//            GraphSpace graph = (GraphSpace) currentGraphComponent.getGraph();

//            if (!graph.getSelectionModel().isSelected(vertex)) graph.setSelectionCell(vertex);
            dialog.requestFocusInWindow();
            currentGraphComponent.scrollCellToVisible(vertex, true);

        }
    }

    public void deleteSelectedGraph() {
        if (currentGraphComponent != null) {
            GraphSpace graph = (GraphSpace) currentGraphComponent.getGraph();

            textPane.getTextSource().getAnnotationManager().removeGraphSpace(graph);

            int index = graphComponentList.indexOf(currentGraphComponent);
            graphComponentList.remove(currentGraphComponent);
            if (graphComponentList.size() > 0) {
                showGraph(graphComponentList.get(index == 0 ? 0 : index - 1));
            }
            graphViewMenu.updateMenus();
        }

    }

    public void showGraph(mxGraphComponent graphComponent) {
        if (currentGraphComponent != null) {
            dialog.remove(currentGraphComponent);
        }
        currentGraphComponent = graphComponent;
        dialog.add(currentGraphComponent, BorderLayout.CENTER);
        graphLabel.setText(currentGraphComponent.getName());
        ((GraphSpace) graphComponent.getGraph()).reDrawVertices();
    }

    private void addGraph(GraphSpace graph) {


        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.white);
        graphComponent.setDragEnabled(false);
        graphComponent.setSize(new Dimension(800, 800));
        graphComponent.getGraphControl().add(scrollPane, 0);
        graphComponent.setName(graph.getId());

        setupListeners(graphComponent);


        graphComponentList.add(graphComponent);

        showGraph(graphComponent);

        graphViewMenu.updateMenus();
    }

    void executeLayout() {
        if (currentGraphComponent != null) {
            GraphSpace graph = (GraphSpace) currentGraphComponent.getGraph();
            graph.reDrawVertices();
            mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
            layout.setOrientation(SwingConstants.WEST);
            layout.setIntraCellSpacing(50);
            layout.setInterRankCellSpacing(125);
            layout.setOrientation(SwingConstants.NORTH);

            try {
                graph.getModel().beginUpdate();
                try {
                    layout.execute(graph.getDefaultParent());
                } finally {
                    mxMorphing morph = new mxMorphing(currentGraphComponent, 20, 1.2, 20);

                    morph.addListener(mxEvent.DONE, (arg0, arg1) -> graph.getModel().endUpdate());

                    morph.startAnimation();
                }
            } finally {
                graph.getModel().endUpdate();
                currentGraphComponent.zoomAndCenter();
            }
        }
    }

    public JDialog getDialog() {
        return dialog;
    }

    public void renameCurrentGraph(String newGraphID) {
        ((GraphSpace) currentGraphComponent.getGraph()).setId(newGraphID);
        currentGraphComponent.setName(newGraphID);
    }

    public List<mxGraphComponent> getGraphComponentList() {
        return graphComponentList;
    }

    public void addSelectedAnnotationAsVertex() {
        addAnnotationVertex(textPane.getSelectedAnnotation());
    }

    public void removeSelectedVertex() {
        if (currentGraphComponent != null) {
            GraphSpace graphSpace = (GraphSpace) currentGraphComponent.getGraph();
            Object cell = graphSpace.getSelectionModel().getCell();
            graphSpace.removeVertex((mxCell) cell);
            graphSpace.reDrawVertices();
        }
    }

    @Override
    public void profileAdded(Profile profile) {

    }

    @Override
    public void profileRemoved() {

    }

    @Override
    public void profileSelectionChanged(Profile profile) {
        graphComponentList.forEach(graphComponent -> ((GraphSpace) graphComponent.getGraph()).reDrawVertices());
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }

    @Override
    public void colorChanged() {
        graphComponentList.forEach(graphComponent -> ((GraphSpace) graphComponent.getGraph()).reDrawVertices());
    }

    @Override
    public void newGraph(GraphSpace graphSpace) {
        addGraph(graphSpace);
    }

    @Override
    public void removeGraph(GraphSpace graphSpace) {

    }
}
