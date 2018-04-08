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
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;


public class GraphViewer implements ProfileListener, GraphListener {

    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(GraphViewer.class);
    private final KnowtatorController controller;
    private final KnowtatorView view;
    private JDialog dialog;

    private Map<GraphSpace, mxGraphComponent> graphComponentMap;
    private JScrollPane scrollPane;
    private JLabel graphLabel;
    private GraphViewMenu graphViewMenu;

    public GraphViewer(JFrame frame, KnowtatorController controller, KnowtatorView view) {
        this.controller = controller;
        this.view = view;
        graphComponentMap = new HashMap<>();
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        graphLabel = new JLabel();

        makeDialog(frame);

    }

    private void makeDialog(JFrame frame) {
        dialog = new JDialog(frame, "Graph Viewer");

        dialog.setLayout(new BorderLayout());

        dialog.setVisible(false);
        dialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        dialog.setSize(new Dimension(800, 800));
        dialog.setLocationRelativeTo(view);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GraphMenu(controller, this));

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
                    if (source instanceof AnnotationNode && target instanceof AnnotationNode) {
                        Object property = controller.getOWLAPIDataExtractor().getSelectedProperty();

                        if (property == null) {
                            log.warn("No Object property selected");
                            JTextField field1 = new JTextField();
                            Object[] message = {
                                    "Relationship ID", field1,
                            };
                            if (JOptionPane.showConfirmDialog(null, message, "Enter an ID for this property", JOptionPane.OK_CANCEL_OPTION) == JOptionPane.OK_OPTION) {
                                property = field1.getText();
                            }
                        }
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
                            int result = JOptionPane.showConfirmDialog(null, restrictionPanel,
                                    "Restriction options", JOptionPane.OK_CANCEL_OPTION);
                            if (result == JOptionPane.OK_OPTION) {
                                quantifier = quantifierField.getText();
                                value = valueField.getText();
                            }

                            graph.addTriple(
                                    (AnnotationNode) source,
                                    (AnnotationNode) target,
                                    null,
                                    controller.getProfileManager().getCurrentProfile(),
                                    property,
                                    quantifier,
                                    value);

                        }
                    }

                    graph.getModel().remove(edge);

                    graph.reDrawGraph();
//                    executeLayout(null);
                }
            }
        });

//        graph.addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> {
//            Object[] cells = (Object[]) evt.getProperty("cells");
//            for (Object cell : cells) {
//                if (graph.getModel().isEdge(cell)) {
//                    graph.removeCell((mxCell) cell);
//                    graph.reDrawGraph();
////                    executeLayout(null);
//                }
//            }
//        });

        graph.getSelectionModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
            Collection selectedCells = (Collection) evt.getProperty("removed");
            Arrays.stream(graph.getChildVertices(graph.getDefaultParent())).forEach(cell -> graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "0", new Object[]{cell}));

            if (selectedCells != null) {
                for (Object cell : selectedCells) {
                    if (cell instanceof AnnotationNode) {
                        //noinspection SuspiciousMethodCalls
                        Annotation annotation = ((AnnotationNode) cell).getAnnotation();

                        controller.getSelectionManager().setSelectedAnnotation(annotation);

                        if (annotation.isOwlClass()) {
                            view.owlEntitySelectionChanged((OWLClass) annotation.getOwlClass());
                        }
                        graph.setCellStyles(mxConstants.STYLE_STROKEWIDTH, "4", new Object[]{cell});

                    } else {
                        Object value = ((mxCell) cell).getValue();
                        if (value instanceof OWLProperty) {
                            view.owlEntitySelectionChanged((OWLObjectProperty) value);
                        } else if (value instanceof String) {
                            view.owlEntitySelectionChanged(controller.getOWLAPIDataExtractor().getOWLObjectPropertyByID((String) ((mxCell) cell).getValue()));
                        }
                    }
                }
            }
            graph.reDrawGraph();
        });
    }

    private void addAnnotationVertex(Annotation annotation) {
        mxCell vertex = controller.getSelectionManager().getActiveGraphSpace().addNode(null, annotation);

        goToVertex(vertex);
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

    private void goToVertex(Object vertex) {
        dialog.requestFocusInWindow();
        graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace()).scrollCellToVisible(vertex, true);

    }

    void deleteSelectedGraph() {
        if (graphComponentMap.size() > 0) {
            showGraph(graphComponentMap.keySet().iterator().next());
        } else {
            dialog.remove(graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace()));
        }

        controller.getSelectionManager().getActiveTextSource().getAnnotationManager().removeGraphSpace(controller.getSelectionManager().getActiveGraphSpace());
        graphComponentMap.remove(controller.getSelectionManager().getActiveGraphSpace());
        graphViewMenu.updateMenus();
    }

    void showGraph(GraphSpace graphSpace) {
        mxGraphComponent graphComponent = graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace());
        if (graphComponent != null) {
            dialog.remove(graphComponent);
        }

        controller.getSelectionManager().setActiveGraphSpace(graphSpace);
        dialog.add(graphComponentMap.get(graphSpace), BorderLayout.CENTER);
        graphLabel.setText(graphSpace.getId());
        graphSpace.reDrawGraph();
    }

    public void addGraph(GraphSpace graphSpace) {
        log.warn("Knowtator: GraphViewer: Adding graph space " + graphSpace.getId());

        mxGraphComponent graphComponent = new mxGraphComponent(graphSpace);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.white);
        graphComponent.setDragEnabled(false);
        graphComponent.setSize(new Dimension(800, 800));
        graphComponent.getGraphControl().add(scrollPane, 0);
        graphComponent.setName(graphSpace.getId());

        setupListeners(graphComponent);

        graphComponentMap.put(graphSpace, graphComponent);

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
                mxMorphing morph = new mxMorphing(graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace()), 20, 1.2, 20);

                morph.addListener(mxEvent.DONE, (arg0, arg1) -> graph.getModel().endUpdate());

                morph.startAnimation();
            }
        } finally {
            graph.getModel().endUpdate();
            graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace()).zoomAndCenter();
        }
    }

    public JDialog getDialog() {
        return dialog;
    }

    void renameCurrentGraph(String newGraphID) {
        (controller.getSelectionManager().getActiveGraphSpace()).setId(newGraphID);
    }

    Map<GraphSpace, mxGraphComponent> getGraphComponentMap() {
        return graphComponentMap;
    }

    void addSelectedAnnotationAsVertex() {
        addAnnotationVertex(controller.getSelectionManager().getSelectedAnnotation());
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
        graphComponentMap.keySet().forEach(GraphSpace::reDrawGraph);
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }

    @Override
    public void colorChanged() {
        graphComponentMap.keySet().forEach(GraphSpace::reDrawGraph);
    }

    @Override
    public void newGraph(GraphSpace graphSpace) {
        if (graphSpace.getTextSource() == controller.getSelectionManager().getActiveTextSource()) addGraph(graphSpace);
    }

    @Override
    public void removeGraph(GraphSpace graphSpace) {

    }

    public void removeAllGraphs() {
        mxGraphComponent graphComponent = graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace());
        if (graphComponent != null) {
            dialog.remove(graphComponent);
        }
        graphComponentMap = new HashMap<>();
    }

    void zoomIn() {
        graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace()).zoomIn();
    }

    void zoomOut() {
        graphComponentMap.get(controller.getSelectionManager().getActiveGraphSpace()).zoomOut();
    }
}
