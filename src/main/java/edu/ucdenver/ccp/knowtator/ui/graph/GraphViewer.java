package edu.ucdenver.ccp.knowtator.ui.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.CompositionalAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
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

public class GraphViewer extends DnDTabbedPane {

    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(GraphViewer.class);
    private final KnowtatorManager manager;
    private final BasicKnowtatorView view;
    private int graphCounter;

    public GraphViewer(KnowtatorManager manager, BasicKnowtatorView view) {

        this.manager = manager;
        this.view = view;

        graphCounter = 0;
        addNewGraph(String.format("Graph %d", graphCounter++));
    }


    public mxGraph addNewGraph(String title) {

        mxGraph graph = new mxGraph();
        graph.setCellsResizable(false);
        graph.setEdgeLabelsMovable(false);
        graph.setAllowDanglingEdges(false);
        graph.setCellsEditable(false);
        graph.setConnectableEdges(false);
        graph.setCellsBendable(false);

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.white);
        setupListeners(graphComponent);
        graphComponent.setDragEnabled(false);
        graphComponent.setSize(new Dimension(800, 800));
        JScrollPane sp = new JScrollPane();
        graphComponent.getGraphControl().add(sp, 0);
        graphComponent.zoomAndCenter();

        graphComponent.setName(title);

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

        return graph;
    }

    private void closeGraph(mxGraphComponent graphComponent) {
        if (getTabCount() == 1) {
            addNewGraph(String.format("Graph %d", graphCounter++));
        }
        remove(graphComponent);
    }

    mxGraph getSelectedGraph() {
        if (getSelectedComponent() != null) {
            if (getSelectedComponent().getClass() == mxGraphComponent.class) {
                return ((mxGraphComponent) getSelectedComponent()).getGraph();
            }
        }
        return null;
    }

    private mxGraph getGraph(String title) {
        for (Component component : getComponents()) {
            if (Objects.equals(component.getName(), title)) {
                setSelectedComponent(component);
                return ((mxGraphComponent) getSelectedComponent()).getGraph();
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

                            Annotation sourceAnnotation = (Annotation) ((mxCell) cell).getSource().getValue();
                            Annotation targetAnnotation = (Annotation) ((mxCell) cell).getTarget().getValue();

                            view.getTextViewer().getSelectedTextPane().getTextSource().getAnnotationManager()
                                    .addCompositionalAnnotation(
                                            getSelectedComponent().getName(),
                                            sourceAnnotation,
                                            targetAnnotation,
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

        graph.addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> {
            Object[] cells = (Object[])evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
//                    ConceptAnnotation sourceAnnotation = (ConceptAnnotation)((mxCell) cell).getSource().getValue();
//                    ConceptAnnotation targetAnnotation = (ConceptAnnotation)((mxCell) cell).getTarget().getValue();
                    view.getTextViewer().getSelectedTextPane().getTextSource().getAnnotationManager()
                            .removeAnnotation(((mxCell) cell).getId());
                    graph.getModel().remove(cell);
                }
            }
        });

        graph.getSelectionModel().addListener(mxEvent.CHANGE, (sender, evt) -> {
            Collection deselectedCells = (Collection) evt.getProperty("added");
            Collection selectedCells = (Collection) evt.getProperty("removed");

            if (selectedCells != null) {
                for (Object cell : selectedCells) {
                    if (graph.getModel().isVertex(cell)) {
                        ConceptAnnotation annotation = (ConceptAnnotation) ((mxCell) cell).getValue();
                        graph.getModel().setStyle(cell,
                                String.format(
                                        "fontSize=16;strokeWidth=4;strokeColor=black;fillColor=#%s",
                                        Integer.toHexString(
                                                annotation.getColor().getRGB()
                                        ).substring(2)
                                )
                        );
                        view.annotationSelectionChangedEvent(annotation);
                    }
                    if (graph.getModel().isEdge(cell)) {
                        CompositionalAnnotation annotation = (CompositionalAnnotation) ((mxCell) cell).getValue();
                        view.owlEntitySelectionChanged(OWLAPIDataExtractor.getOWLObjectPropertyByID(view, annotation.getRelationship()));
                    }
                }
            }

            if (deselectedCells != null) {
                for (Object cell : deselectedCells) {
                    if (graph.getModel().isVertex(cell)) {
                        graph.getModel().setStyle(cell,
                                String.format(
                                        "fontSize=16;strokeWidth=0;strokeColor=black;fillColor=#%s",
                                        Integer.toHexString(
                                                ((ConceptAnnotation) ((mxCell) cell).getValue()).getColor().getRGB()
                                        ).substring(2)
                                )
                        );
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

    public Object addVertex(Annotation annotation) {
        mxGraph graph = getSelectedGraph();
        if (((mxGraphModel) graph.getModel()).getCell(annotation.getID()) == null) {
            graph.getModel().beginUpdate();
            try {
                Object vertex = graph.insertVertex(graph.getDefaultParent(), annotation.getID(), annotation, 20, 20,
                        80, 30,
                        String.format(
                                "fontSize=16;fillColor=#%s",
                                Integer.toHexString(
                                        annotation.getColor().getRGB()
                                ).substring(2)
                        )
                );
                graph.updateCellSize(vertex);

                // apply layout to graph
                mxHierarchicalLayout layout = new mxHierarchicalLayout(graph);
                layout.setOrientation(SwingConstants.WEST);
                layout.execute(graph.getDefaultParent());
            } finally {
                graph.getModel().endUpdate();
            }
        }
        return goToVertex(annotation);
    }

    void addEdge(CompositionalAnnotation compositionalAnnotation) {

        mxGraph graph = getGraph(compositionalAnnotation.getGraphTitle());
        graph.getModel().beginUpdate();
        try {
            Object source = ((mxGraphModel) graph.getModel()).getCell(compositionalAnnotation.getSource().getID());
            Object target =  ((mxGraphModel) graph.getModel()).getCell(compositionalAnnotation.getTarget().getID());

            if (source == null) {
                source = addVertex(compositionalAnnotation.getSource());
            }
            if (target == null) {
                target = addVertex(compositionalAnnotation.getTarget());
            }


            graph.insertEdge(graph.getDefaultParent(),
                    compositionalAnnotation.getID(),
                    compositionalAnnotation,
                    source,
                    target,
                    "straight=1;" +
                            "startArrow=dash;" +
                            "startSize=12;" +
                            "endArrow=block;"
            );

            mxHierarchicalLayout layout = new mxHierarchicalLayout(
                    graph);
            layout.setOrientation(SwingConstants.WEST);
            layout.execute(graph.getDefaultParent());
        } finally {
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
        mxGraph graph = getSelectedGraph();
        Object cellToGoTo = ((mxGraphModel) graph.getModel()).getCell(annotation.getID());

        if (!graph.getSelectionModel().isSelected(cellToGoTo)) graph.setSelectionCell(cellToGoTo);
        if (cellToGoTo != null) {
            requestFocusInWindow();
            ((mxGraphComponent) getSelectedComponent()).scrollCellToVisible(cellToGoTo, true);
        }

        return cellToGoTo;
    }

    void removeVertex(ConceptAnnotation removedAnnotation) {
        mxGraph graph = getSelectedGraph();
        graph.getModel().remove(((mxGraphModel) graph.getModel()).getCell(removedAnnotation.getID()));
    }

    public void deleteSelectedGraph() {
        mxGraph graph = getSelectedGraph();

        for (Object cell : graph.getAllEdges(graph.getChildCells(graph.getDefaultParent()))) {
            view.getTextViewer().getSelectedTextPane().getTextSource().getAnnotationManager().removeAnnotation(((mxCell) cell).getId());
        }

        closeGraph((mxGraphComponent) getSelectedComponent());

    }

    private java.util.List<mxGraphComponent> getAllGraphComponents() {
        List<mxGraphComponent> graphComponentList = new ArrayList<>();
        for (Component component : getComponents()) {
            if (component.getClass() == mxGraphComponent.class) {
                graphComponentList.add((mxGraphComponent) component);
            }
        }
        return graphComponentList;
    }

    void reDrawVertices() {
        for (mxGraphComponent graphComponent : getAllGraphComponents()) {
            mxGraph graph = graphComponent.getGraph();

            graph.getModel().beginUpdate();
            try {
                for (Object cell : graph.getChildVertices(graph.getDefaultParent())) {

                    Annotation annotation = (Annotation) ((mxCell) cell).getValue();
                    graph.getModel().setStyle(cell, String.format(
                            "fillColor=#%s",
                            Integer.toHexString(
                                    annotation.getColor().getRGB()
                            ).substring(2)
                    ));
                    graph.getView().validateCell(cell);

                }
                // apply layout to graph
                mxHierarchicalLayout layout = new mxHierarchicalLayout(
                        graph);
                layout.setOrientation(SwingConstants.WEST);
                layout.execute(graph.getDefaultParent());
            } finally {
                graph.getModel().endUpdate();
            }
        }
    }
}
