package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.GraphListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class GraphViewer implements ProfileListener, GraphListener, TextSourceListener {

    private static Logger log = LogManager.getLogger(GraphViewer.class);
    private final KnowtatorController controller;
    private JDialog dialog;

    private TreeMap<GraphSpace, mxGraphComponent> graphSpaceMap;
    private JScrollPane scrollPane;
    private GraphSpaceToolBar graphSpaceToolBar;
    private GraphViewMenu graphViewMenu;

    public GraphViewer(JFrame frame, KnowtatorController controller) {
        this.controller = controller;
        graphSpaceMap = new TreeMap<>(GraphSpace::compare);
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        graphSpaceToolBar = new GraphSpaceToolBar(this, controller);

        makeDialog(frame);

    }

    private void makeDialog(JFrame frame) {
        dialog = new JDialog(frame, "Graph Viewer");

        dialog.setLayout(new BorderLayout());

        dialog.setVisible(false);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.setSize(new Dimension(800, 800));
        dialog.setLocationRelativeTo(frame);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GraphMenu(controller, this));

        graphViewMenu = new GraphViewMenu(this);
        menuBar.add(graphViewMenu);
        menuBar.add(new GraphOptionsMenu(this));
        dialog.setJMenuBar(menuBar);

        JPanel subPanel = new JPanel();
        subPanel.setLayout(new BorderLayout());
        subPanel.add(graphSpaceToolBar, BorderLayout.NORTH);
        subPanel.add(new GraphEditorToolBar(this, controller), BorderLayout.SOUTH);

        dialog.add(subPanel, BorderLayout.NORTH);

//        dialog.add(this);
    }


    void addNewGraphSpace(String graphID) {
        controller.getSelectionManager().getActiveTextSource().getAnnotationManager().addGraphSpace(graphID);
    }

    private void setupListeners(mxGraphComponent graphComponent) {
        GraphSpace graph = (GraphSpace) graphComponent.getGraph();
        //Handle drag and drop
        //Adds the current selected object property as the edge value
        graph.addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
            Object[] cells = (Object[]) evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell) && ((mxCell) cell).getValue() != null && ((mxCell) cell).getValue().equals("")) {
                    mxCell edge = (mxCell) cell;
                    mxICell source = edge.getSource();
                    mxICell target = edge.getTarget();
                    Object property = controller.getOWLAPIDataExtractor().getSelectedProperty();

                    if (property != null) {
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
                        int result = JOptionPane.showConfirmDialog(controller.getView(), restrictionPanel,
                                "Restriction options", JOptionPane.OK_CANCEL_OPTION);
                        if (result == JOptionPane.OK_OPTION) {
                            quantifier = quantifierField.getText();
                            value = valueField.getText();
                        }

                        graph.addTriple(
                                (AnnotationNode) source,
                                (AnnotationNode) target,
                                null,
                                controller.getSelectionManager().getActiveProfile(),
                                property,
                                quantifier,
                                value);

                    }

                    graph.getModel().remove(edge);

                    graph.reDrawGraph();
                }
            }
        });

        graph.addListener(mxEvent.MOVE_CELLS, (sender, evt) -> graph.reDrawGraph());

        graph.getSelectionModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
            Collection selectedCells = (Collection) evt.getProperty("removed");
            Arrays.stream(graph.getChildVertices(graph.getDefaultParent())).forEach(cell -> graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "0", new Object[]{cell}));

            if (selectedCells != null) {
                for (Object cell : selectedCells) {
                    if (cell instanceof AnnotationNode) {
                        Annotation annotation = ((AnnotationNode) cell).getAnnotation();

                        controller.getSelectionManager().setSelectedAnnotation(annotation, null);

                        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "4", new Object[]{cell});

                    } else {
                        Object value = ((mxCell) cell).getValue();
                        controller.propertyChangedEvent(value);
                    }
                }
            }
            graph.reDrawGraph();
        });
    }

    public void goToAnnotationVertex(GraphSpace graphSpace, Annotation annotation) {
        if (annotation != null && graphSpace != null) {
            showGraph(graphSpace);
            List<Object> vertices = graphSpace.getVerticesForAnnotation(annotation);
            if (vertices.size() > 0) {
                graphSpace.setSelectionCells(vertices);
                goToVertex(vertices.get(0));
            }
        }
    }

    void goToVertex(Object vertex) {
        dialog.requestFocusInWindow();
        graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace()).scrollCellToVisible(vertex, true);

    }

    void deleteSelectedGraph() {
        if (graphSpaceMap.size() > 0) {
            showGraph(graphSpaceMap.keySet().iterator().next());
        } else {
            dialog.remove(graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace()));
        }

        controller.getSelectionManager().getActiveTextSource().getAnnotationManager().removeGraphSpace(controller.getSelectionManager().getActiveGraphSpace());
        graphSpaceMap.remove(controller.getSelectionManager().getActiveGraphSpace());
        graphViewMenu.updateMenus();
    }

    void showGraph(GraphSpace graphSpace) {
        mxGraphComponent graphComponent = graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace());
        if (graphComponent != null && graphSpace != controller.getSelectionManager().getActiveGraphSpace()) {
            dialog.remove(graphComponent);
        }

        controller.getSelectionManager().setActiveGraphSpace(graphSpace);
        graphComponent = graphSpaceMap.get(graphSpace);
        if (graphComponent != null) {
            dialog.add(graphSpaceMap.get(graphSpace), BorderLayout.CENTER);

            graphSpace.reDrawGraph();

            graphSpaceToolBar.update(graphSpace);
        }
    }

    private void addGraph(GraphSpace graphSpace) {
        log.warn("Knowtator: GraphViewer: Adding graph space " + graphSpace.getId());

        mxGraphComponent graphComponent = new mxGraphComponent(graphSpace);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.white);
        graphComponent.setDragEnabled(false);
        graphComponent.setSize(new Dimension(800, 800));
        graphComponent.getGraphControl().add(scrollPane, 0);
        graphComponent.setName(graphSpace.getId());

        setupListeners(graphComponent);

        graphSpaceMap.put(graphSpace, graphComponent);

        showGraph(graphSpace);
        executeLayout();

        graphViewMenu.updateMenus();
    }

    void executeLayout() {
        GraphSpace graph = controller.getSelectionManager().getActiveGraphSpace();
        graph.reDrawGraph();
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
                mxMorphing morph = new mxMorphing(graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace()), 20, 1.2, 20);

                morph.addListener(mxEvent.DONE, (arg0, arg1) -> graph.getModel().endUpdate());

                morph.startAnimation();
            }
        } finally {
            graph.getModel().endUpdate();
            graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace()).zoomAndCenter();
        }
    }

    public JDialog getDialog() {
        return dialog;
    }

    void renameCurrentGraph(String newGraphID) {
        (controller.getSelectionManager().getActiveGraphSpace()).setId(newGraphID);
    }

    public Map<GraphSpace, mxGraphComponent> getGraphSpaceMap() {
        return graphSpaceMap;
    }

    void removeSelectedCell() {
        controller.getSelectionManager().getActiveGraphSpace().removeSelectedCell();
    }

    @Override
    public void profileAdded(Profile profile) {

    }

    @Override
    public void profileRemoved() {

    }

    @Override
    public void profileSelectionChanged(Profile profile) {
        graphSpaceMap.keySet().forEach(GraphSpace::reDrawGraph);
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }

    @Override
    public void colorChanged() {
        graphSpaceMap.keySet().forEach(GraphSpace::reDrawGraph);
    }

    @Override
    public void newGraph(GraphSpace graphSpace) {
        if (graphSpace.getTextSource() == controller.getSelectionManager().getActiveTextSource()) addGraph(graphSpace);
    }

    @Override
    public void graphSpaceRemoved(GraphSpace graphSpace) {

    }

    private void removeAllGraphs() {
        mxGraphComponent graphComponent = graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace());
        if (graphComponent != null) {
            dialog.remove(graphComponent);
        }
        graphSpaceMap = new TreeMap<>(GraphSpace::compare);
    }

    void zoomIn() {
        graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace()).zoomIn();
    }

    void zoomOut() {
        graphSpaceMap.get(controller.getSelectionManager().getActiveGraphSpace()).zoomOut();
    }

    @Override
    public void textSourceAdded(TextSource textSource) {

    }

    @Override
    public void activeTextSourceChanged(TextSource textSource) {
        removeAllGraphs();
        for (GraphSpace graphSpace : textSource.getAnnotationManager().getGraphSpaces()) {
            addGraph(graphSpace);
        }
    }

    void showPreviousGraphSpace() {
        GraphSpace graphSpace = controller.getSelectionManager().getActiveGraphSpace();

        GraphSpace previousGraphSpace;
        try {
            previousGraphSpace = graphSpaceMap.containsKey(graphSpace) ? graphSpaceMap.lowerKey(graphSpace) : graphSpaceMap.floorKey(graphSpace);
        } catch (NullPointerException npe) {
            previousGraphSpace = null;
        }
        if (previousGraphSpace == null) previousGraphSpace = graphSpaceMap.lastKey();
        showGraph(previousGraphSpace);
    }

    void showNextGraphSpace() {
        GraphSpace graphSpace = controller.getSelectionManager().getActiveGraphSpace();

        GraphSpace nextGraphSpace;
        try {
            nextGraphSpace = graphSpaceMap.containsKey(graphSpace) ? graphSpaceMap.higherKey(graphSpace) : graphSpaceMap.ceilingKey(graphSpace);
        } catch (NullPointerException npe) {
            nextGraphSpace = null;
        }
        if (nextGraphSpace == null) nextGraphSpace = graphSpaceMap.firstKey();
        showGraph(nextGraphSpace);
    }
}
