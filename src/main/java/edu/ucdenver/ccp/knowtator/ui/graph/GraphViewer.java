package edu.ucdenver.ccp.knowtator.ui.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.*;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import other.DnDTabbedPane;
import other.GraphPopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

//TODO: Display coreferences
//TODO: Expand to show ontology terms as nodes
public class GraphViewer extends DnDTabbedPane {

    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(GraphViewer.class);
    private final KnowtatorManager manager;
    private final BasicKnowtatorView view;
    private TextSource textSource;

    private int graphCounter;
    private boolean includeOntologyTerms;
    private boolean includeCoreferences;
    private boolean includeOverlaps;

    private String edgeOptions = "straight=1;startArrow=dash;startSize=12;endArrow=block;verticalAlign=top;verticalLabelPosition=top;fontSize=%d";

    public GraphViewer(KnowtatorManager manager, BasicKnowtatorView view, TextSource textSource) {

        this.manager = manager;
        this.view = view;
        this.textSource = textSource;

        graphCounter = 0;
        addNewGraph(String.format("Graph %d", graphCounter++));
        includeOntologyTerms = true;
        includeCoreferences = true;
        includeOverlaps = true;
    }


    public mxGraphComponent addNewGraph(String title) {

        mxGraph graph = new mxGraph();

        graph.setCellsResizable(false);
        graph.setEdgeLabelsMovable(false);
        graph.setAllowDanglingEdges(false);
        graph.setCellsEditable(false);
        graph.setConnectableEdges(false);
        graph.setCellsBendable(false);

        JScrollPane sp = new JScrollPane();
        sp.getVerticalScrollBar().setUnitIncrement(20);

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.white);
        graphComponent.setDragEnabled(false);
        graphComponent.setSize(new Dimension(800, 800));
        graphComponent.getGraphControl().add(sp, 0);
        graphComponent.setName(title);

        setupListeners(graphComponent);

        addTab(title, graphComponent);

        int index = indexOfTab(title);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        JButton btnClose = new JButton("x");

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;
        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);

        setTabComponentAt(index, pnlTab);

        btnClose.addActionListener(evt -> closeGraph(graphComponent));

        setSelectedComponent(graphComponent);

        return graphComponent;
    }

    private void closeGraph(mxGraphComponent graphComponent) {
        if (getTabCount() == 1) {
            addNewGraph(String.format("Graph %d", graphCounter++));
        }
        remove(graphComponent);
    }

    public mxGraphComponent getSelectedGraphComponent() {
        if (getSelectedComponent() != null) {
            if (getSelectedComponent().getClass() == mxGraphComponent.class) {
                return ((mxGraphComponent) getSelectedComponent());
            }
        }
        return null;
    }

    private mxGraphComponent getGraphComponent(String title) {
        for (Component component : getComponents()) {
            if (Objects.equals(component.getName(), title)) {
                setSelectedComponent(component);
                return ((mxGraphComponent) getSelectedComponent());
            }
        }
        return addNewGraph(title);
    }

    private void setupListeners(mxGraphComponent graphComponent) {
        mxGraph graph = graphComponent.getGraph();
        //Handle drag and drop
        //Adds the current selected object property as the edge value
        graph.addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
            Object[] cells = (Object[])evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
                    if (((mxCell) cell).getValue() != null) {
                        if (((mxCell) cell).getValue().equals("")) {
                            String propertyName = OWLAPIDataExtractor.getSelectedPropertyID(view);

                            String sourceAnnotationID = ((mxCell) cell).getSource().getId();
                            String targetAnnotationID = ((mxCell) cell).getTarget().getId();

                            textSource.getAnnotationManager().addCompositionalAnnotation(
                                    getSelectedComponent().getName(),
                                    sourceAnnotationID,
                                    targetAnnotationID,
                                    propertyName,
                                    null,
                                    manager.getProfileManager().getCurrentProfile()
                            );

                            graph.getModel().remove(cell);
                        }
                    }
                }
            }
        });

        //TODO: Check if I really want removing a cell to remove the annotation
        graph.addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> {
            Object[] cells = (Object[])evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
                    graph.getModel().beginUpdate();
                    try {
                        textSource.getAnnotationManager().removeAnnotation(((mxCell) cell).getId());
                        graph.getModel().remove(cell);
                    }
                    finally {
                        executeLayout(graphComponent);
                        graph.getModel().endUpdate();
                    }

                }
            }
        });

        graph.getSelectionModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
            Collection deselectedCells = (Collection) evt.getProperty("added");
            Collection selectedCells = (Collection) evt.getProperty("removed");

            if (selectedCells != null) {
                for (Object cell : selectedCells) {
                    if (graph.getModel().isVertex(cell)) {
                        graph.getModel().beginUpdate();
                        try {
                            setVertexStyle(graph, (mxCell) cell, true);
                        } finally {
                            graph.getModel().endUpdate();
                        }
                        view.annotationSelectionChangedEvent(textSource.getAnnotationManager().getAnnotation(((mxCell) cell).getId()));
                        view.owlEntitySelectionChanged(OWLAPIDataExtractor.getOWLClassByID(view, ((mxCell) cell).getId()));
                    }
                    if (graph.getModel().isEdge(cell)) {
                        view.owlEntitySelectionChanged(OWLAPIDataExtractor.getOWLObjectPropertyByID(view, (String) ((mxCell) cell).getValue()));
                    }
                    executeLayout(graphComponent);
                }
            }

            if (deselectedCells != null) {
                for (Object cell : deselectedCells) {
                    if (graph.getModel().isVertex(cell)) {
                            graph.getModel().beginUpdate();
                            try {
                                setVertexStyle(graph, (mxCell) cell, false);
                            } finally {
                                graph.refresh();
                            }
                        }
                }
            }
     });

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                // Handles context menu on the Mac where the trigger is on mousepressed
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showGraphPopupMenu(graphComponent, e);
                }
            }
        });
    }

    public Object addAnnotationVertex(String annotationID) {
        Annotation annotation = textSource.getAnnotationManager().getAnnotation(annotationID);
        mxGraphComponent graphComponent = getSelectedGraphComponent();
        mxGraph graph = graphComponent.getGraph();
        if (((mxGraphModel) graph.getModel()).getCell(annotationID) == null) {
            graph.getModel().beginUpdate();
            try {

                if (annotation instanceof ConceptAnnotation) {
                    Object annotationVertex = graph.insertVertex(
                            graph.getDefaultParent(),
                            annotationID,
                            includeOntologyTerms ? ((ConceptAnnotation) annotation).getSpannedText():
                                    String.format ("%s\n%s\n%s",
                                            ((ConceptAnnotation) annotation).getSpannedText(),
                                            ((ConceptAnnotation) annotation).getClassID(),
                                            ((ConceptAnnotation) annotation).getClassName()
                                    ),
                            20, 20,
                            80, 30
                    );
                    setVertexStyle(graph, (mxCell) annotationVertex, false);
                    graph.updateCellSize(annotationVertex);

                    if (includeOverlaps) {
                        ((ConceptAnnotation) annotation).getOverlappingAnnotations().forEach(overlappingAnnotationID -> {
                            Object overlappingAnnotationVertex = ((mxGraphModel) graph.getModel()).getCell(overlappingAnnotationID);
                            if (overlappingAnnotationVertex == null) {
                                overlappingAnnotationVertex = addAnnotationVertex(overlappingAnnotationID);
                            }
                            Boolean addOverlapEdge = true;
                            for(Object edge : graph.getEdges(overlappingAnnotationVertex)) {
                                if (((mxCell) edge).getValue().equals("overlaps_with")) {
                                    if (((mxCell) edge).getSource().equals(annotationVertex) || ((mxCell) edge).getTarget().equals(annotationVertex)) {
                                        addOverlapEdge = false;
                                    }
                                }
                            }
                            if (addOverlapEdge) {
                                graph.insertEdge(graph.getDefaultParent(),
                                        null,
                                        "overlaps_with",
                                        annotationVertex,
                                        overlappingAnnotationVertex,
                                        String.format("straight=1;dashed=1;startArrow=dash;startSize=12;endArrow=none;verticalAlign=top;verticalLabelPosition=top;fontSize=%d",
                                                16
                                        )
                                );
                            }
                        });
                    }

                    if (annotation instanceof IdentityChainAnnotation) {
                        if (includeCoreferences) {
                            ((IdentityChainAnnotation) annotation).getCoreferringAnnotations().forEach(coreferringAnnotationID -> {
                                Object coreferringAnnotationVertex = ((mxGraphModel) graph.getModel()).getCell(coreferringAnnotationID);
                                if (coreferringAnnotationVertex == null) {
                                    coreferringAnnotationVertex = addAnnotationVertex(coreferringAnnotationID);
                                }
                                graph.insertEdge(graph.getDefaultParent(),
                                        null,
                                        "refers_to",
                                        annotationVertex,
                                        coreferringAnnotationVertex,
                                        String.format(edgeOptions,
                                                16
                                        )
                                );
                            });
                        }
                    }
                    else if (includeOntologyTerms) {
                        Object conceptVertex = ((mxGraphModel) graph.getModel()).getCell(((ConceptAnnotation) annotation).getClassID());
                        if (conceptVertex == null) {
                            conceptVertex = graph.insertVertex(graph.getDefaultParent(),
                                    ((ConceptAnnotation) annotation).getClassID(),
                                    String.format("%s\n%s", ((ConceptAnnotation) annotation).getClassID(), ((ConceptAnnotation) annotation).getClassName()), 20, 20,
                                    80, 30
                            );
                            setVertexStyle(graph, (mxCell) conceptVertex, false);
                            graph.updateCellSize(conceptVertex);
                        }
                        graph.insertEdge(graph.getDefaultParent(),
                                null,
                                "denotes_concept",
                                annotationVertex,
                                conceptVertex,
                                String.format(edgeOptions,
                                    16
                                )
                        );
                    }


                }

            } finally {
                executeLayout(graphComponent);
                graph.getModel().endUpdate();
            }

        }
        return goToVertex(annotation);
    }

    void addEdge(CompositionalAnnotation compositionalAnnotation, boolean ignoreGraphTitle) {

        mxGraphComponent graphComponent = ignoreGraphTitle ? getSelectedGraphComponent() : getGraphComponent(compositionalAnnotation.getGraphTitle());
        mxGraph graph = graphComponent.getGraph();
        graph.getModel().beginUpdate();
        try {
            Object source = ((mxGraphModel) graph.getModel()).getCell(compositionalAnnotation.getSourceAnnotationID());
            Object target =  ((mxGraphModel) graph.getModel()).getCell(compositionalAnnotation.getTargetAnnotationID());

            if (source == null) {
                source = addAnnotationVertex(compositionalAnnotation.getSourceAnnotationID());
            }
            if (target == null) {
                target = addAnnotationVertex(compositionalAnnotation.getTargetAnnotationID());
            }


            graph.insertEdge(graph.getDefaultParent(),
                    compositionalAnnotation.getID(),
                    compositionalAnnotation,
                    source,
                    target,
                    "straight=1;" +
                            "startArrow=dash;" +
                            "startSize=12;" +
                            "endArrow=block;" +
                            "verticalAlign=top;" +
                            "verticalLabelPosition=top;" +
                            "fontSize=16"
            );

        } finally {
            executeLayout(graphComponent);
            graph.getModel().endUpdate();
        }
    }

    public Action bind(mxGraphComponent graphComponent, String name, final Action action) {
        AbstractAction newAction = new AbstractAction(name) {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(graphComponent, e.getID(), e.getActionCommand()));
            }
        };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
    }

    private void showGraphPopupMenu(mxGraphComponent graphComponent, MouseEvent e) {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                graphComponent);
        GraphPopupMenu menu = new GraphPopupMenu(this);
        menu.show(graphComponent, pt.x, pt.y);

        e.consume();
    }


    public Object goToVertex(Annotation annotation) {
        mxGraph graph = getSelectedGraphComponent().getGraph();
        Object cellToGoTo = ((mxGraphModel) graph.getModel()).getCell(annotation.getID());

        if (!graph.getSelectionModel().isSelected(cellToGoTo)) graph.setSelectionCell(cellToGoTo);
        if (cellToGoTo != null) {
            requestFocusInWindow();
            ((mxGraphComponent) getSelectedComponent()).scrollCellToVisible(cellToGoTo, true);
        }

        return cellToGoTo;
    }

    void removeVertex(ConceptAnnotation removedAnnotation) {
        mxGraph graph = getSelectedGraphComponent().getGraph();
        graph.getModel().remove(((mxGraphModel) graph.getModel()).getCell(removedAnnotation.getID()));
    }

    public void deleteSelectedGraph() {
        mxGraph graph = getSelectedGraphComponent().getGraph();

        for (Object cell : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
            textSource.getAnnotationManager().removeAnnotation(((mxCell) cell).getId());
        }

        closeGraph((mxGraphComponent) getSelectedComponent());

    }

    java.util.List<mxGraphComponent> getAllGraphComponents() {
        List<mxGraphComponent> graphComponentList = new ArrayList<>();
        for (Component component : getComponents()) {
            if (component.getClass() == mxGraphComponent.class) {
                graphComponentList.add((mxGraphComponent) component);
            }
        }
        return graphComponentList;
    }

    void reDrawVertices(mxGraphComponent graphComponent) {
        mxGraph graph = graphComponent.getGraph();

        graph.getModel().beginUpdate();
        try {
            for (Object c : graph.getChildVertices(graph.getDefaultParent())) {
                mxCell cell = (mxCell) c;
                setVertexStyle(graph, cell, graph.isCellSelected(cell));
                graph.updateCellSize(cell);
//                mxRectangle bounds = graph.getView().getState(cell).getLabelBounds();
//                graph.resizeCell(cell,
//                        new mxRectangle(
//                                bounds.getCenterX(),
//                                bounds.getCenterY(),
//                                bounds.getWidth(),
//                                bounds.getHeight() + 20
//                        )
//                );

                graph.getView().validateCell(cell);
            }
        } finally {
            graph.getModel().endUpdate();
            graph.refresh();
        }
    }

    private void executeLayout(mxGraphComponent graphComponent) {
        reDrawVertices(graphComponent);
        mxHierarchicalLayout layout = new mxHierarchicalLayout(graphComponent.getGraph());
        layout.setOrientation(SwingConstants.WEST);
        layout.setIntraCellSpacing(50);
        layout.setInterRankCellSpacing(250);

//        mxFastOrganicLayout layout = new mxFastOrganicLayout(graphComponent.getGraph());
//        layout.setForceConstant(250); // the higher, the more separated
//        layout.setMinDistanceLimit(500);
//        layout.setDisableEdgeStyle( false); // true transforms the edges and makes them direct lines

//        layout.execute(graphComponent.getGraph().getDefaultParent());

        // layout using morphing
        try {
            graphComponent.getGraph().getModel().beginUpdate();
            try {
                layout.execute(graphComponent.getGraph().getDefaultParent());
            } finally {
                mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

                morph.addListener(mxEvent.DONE, (arg0, arg1) -> {
                    graphComponent.getGraph().getModel().endUpdate();
                    // fitViewport();
                });

                morph.startAnimation();
            }
        } finally {
            graphComponent.getGraph().getModel().endUpdate();
            graphComponent.zoomAndCenter();
        }
    }

    public void addAllAnnotations() {
        getGraphComponent("All annotations");

        textSource.getAnnotationManager().findOverlaps();
        Collection<Annotation> annotations = textSource.getAnnotationManager().getAnnotations(null);

        //TODO: Fix progress bar
        for(Annotation annotation : annotations) {
            if (annotation instanceof ConceptAnnotation) {
                addAnnotationVertex(annotation.getID());
            }
            if (annotation instanceof CompositionalAnnotation) addEdge((CompositionalAnnotation) annotation, true);
        }
    }

    private void setVertexStyle(mxGraph graph, mxCell cell, boolean isSelected) {
        String vertexType = getVertexTypeFromCellID(cell.getId());

        String defaultVertexColor = "D3D3D3";
        String vertexColor = defaultVertexColor;
        String vertexShape = mxConstants.SHAPE_ELLIPSE;

        switch (vertexType) {
            case "NA":
                vertexColor = Integer.toHexString(manager.getProfileManager().getCurrentProfile().getColor(cell.getId(), null).getRGB()).substring(2);
                vertexShape = mxConstants.SHAPE_ELLIPSE;
                break;
            case "IA":
                vertexColor = defaultVertexColor;
                vertexShape = mxConstants.SHAPE_HEXAGON;
                break;
            case "CA":
                vertexColor = includeOntologyTerms ? defaultVertexColor : Integer.toHexString(manager.getProfileManager().getCurrentProfile().getColor(cell.getId(), null).getRGB()).substring(2);
                vertexShape = mxConstants.SHAPE_RECTANGLE;
                break;
        }

        String vertexOptions = "fontSize=16;fontColor=black;strokeColor=black;strokeWidth=%d;fillColor=#%s;shape=%s";
        int selectedStrokeWidth = 4;
        int unselectedStrokeWidth = 1;
        graph.getModel().setStyle(cell, String.format(
                vertexOptions,
                isSelected ? selectedStrokeWidth : unselectedStrokeWidth,
                vertexColor,
                vertexShape
        ));

    }

    private String getVertexTypeFromCellID(String cellID) {
        if (textSource.getAnnotationManager().getAnnotation(cellID) instanceof IdentityChainAnnotation) {
            return "IA";
        } else if (textSource.getAnnotationManager().getAnnotation(cellID) instanceof ConceptAnnotation) {
            return "CA";
        }

        return "NA";
    }
}
