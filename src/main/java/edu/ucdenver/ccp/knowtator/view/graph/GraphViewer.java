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
import edu.ucdenver.ccp.knowtator.view.text.TextPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;


public class GraphViewer implements ProfileListener, GraphListener {

    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(GraphViewer.class);
    private final KnowtatorController controller;
    private final KnowtatorView view;
    private TextPane textPane;
    private JDialog dialog;

    private mxGraphComponent currentGraphComponent;
    private List<mxGraphComponent> graphComponentList;
    private JScrollPane scrollPane;
    private JLabel graphLabel;
    private GraphViewMenu graphViewMenu;

    public GraphViewer(JFrame frame, KnowtatorController controller, KnowtatorView view, TextPane textPane) {
        this.controller = controller;
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
        textPane.getTextSource().getAnnotationManager().addGraphSpace(graphID);
    }

    mxGraphComponent getCurrentGraphComponent() {
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

                        controller.annotationSelectionChangedEvent(annotation);

                        if (annotation.isOwlClassSet()) {
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
//            executeLayout(null);
        });
    }

    private void addAnnotationVertex(Annotation annotation) {
        if (currentGraphComponent != null && annotation != null) {
            GraphSpace graph = (GraphSpace) currentGraphComponent.getGraph();

            mxCell vertex = graph.addNode(null, annotation);
            goToVertex(vertex);
        }

    }

    public void goToAnnotationVertex(String graphID, Annotation annotation) {
        if (annotation != null) {
            showGraph(getGraphComponent(graphID));
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

    void deleteSelectedGraph() {
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

    void showGraph(mxGraphComponent graphComponent) {
        if (graphComponent != null) {
            if (currentGraphComponent != null) {
                dialog.remove(currentGraphComponent);
            }
            currentGraphComponent = graphComponent;
            dialog.add(currentGraphComponent, BorderLayout.CENTER);
            graphLabel.setText(currentGraphComponent.getName());
            ((GraphSpace) graphComponent.getGraph()).reDrawGraph();
        }
    }

    public void addGraph(GraphSpace graph) {
        log.warn("Knowtator: GraphViewer: Adding graph " + graph.getId());

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
        executeLayout();

        graphViewMenu.updateMenus();
    }

    void executeLayout() {
        if (currentGraphComponent != null) {
            GraphSpace graph = (GraphSpace) currentGraphComponent.getGraph();
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

    void renameCurrentGraph(String newGraphID) {
        ((GraphSpace) currentGraphComponent.getGraph()).setId(newGraphID);
        currentGraphComponent.setName(newGraphID);
    }

    List<mxGraphComponent> getGraphComponentList() {
        return graphComponentList;
    }

    void addSelectedAnnotationAsVertex() {
        addAnnotationVertex(textPane.getSelectedAnnotation());
    }

    void removeSelectedCell() {
        if (currentGraphComponent != null) {
            GraphSpace graphSpace = (GraphSpace) currentGraphComponent.getGraph();
            graphSpace.removeSelectedCell();
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
        graphComponentList.forEach(graphComponent -> ((GraphSpace) graphComponent.getGraph()).reDrawGraph());
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }

    @Override
    public void colorChanged() {
        graphComponentList.forEach(graphComponent -> ((GraphSpace) graphComponent.getGraph()).reDrawGraph());
    }

    @Override
    public void newGraph(GraphSpace graphSpace) {
        if(graphSpace.getTextSource() == textPane.getTextSource()) addGraph(graphSpace);
    }

    @Override
    public void removeGraph(GraphSpace graphSpace) {

    }
}
