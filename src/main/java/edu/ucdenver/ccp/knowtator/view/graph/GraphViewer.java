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
public class GraphViewer extends DnDTabbedPane implements ProfileListener, GraphListener {

    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(GraphViewer.class);
    private final KnowtatorManager manager;
    private final KnowtatorView view;
    private TextPane textPane;
    private JDialog dialog;

    private int graphCounter;

    public GraphViewer(JFrame frame, KnowtatorManager manager, KnowtatorView view, TextPane textPane) {
        this.manager = manager;
        this.view = view;
        this.textPane = textPane;
        makeDialog(frame);

        graphCounter = 0;

        //TODO: Remove this default graph. Will have to make sure nothing requires a current graphcomponent
        addNewGraphSpace(String.format("Graph %d", graphCounter++));
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

        GraphViewMenu graphViewMenu = new GraphViewMenu(this);
        menuBar.add(graphViewMenu);
        menuBar.add(new GraphOptionsMenu(this));
        dialog.setJMenuBar(menuBar);

        manager.addGraphListener(graphViewMenu);
        dialog.add(this);
    }


    @Override
    public void profileAdded(Profile profile) {

    }

    @Override
    public void profileRemoved() {

    }

    @Override
    public void profileSelectionChanged(Profile profile) {
        getAllGraphComponents().forEach(graphComponent -> ((GraphSpace) graphComponent.getGraph()).reDrawVertices());
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }

    @Override
    public void colorChanged() {
        getAllGraphComponents().forEach(graphComponent -> ((GraphSpace) graphComponent.getGraph()).reDrawVertices());
    }

    public TextPane getTextPane() {
        return textPane;
    }

    @Override
    public void newGraph(GraphSpace graphSpace) {
        addGraph(graphSpace);
    }

    @Override
    public void removeGraph(GraphSpace graphSpace) {

    }


    public void addNewGraphSpace(String title) {
        GraphSpace graphSpace = textPane.getTextSource().getAnnotationManager().addGraphSpace(title);
        addGraph(graphSpace);
    }

    private void closeGraphSpace(mxGraphComponent graphComponent) {
        if (getTabCount() == 1) {
            addNewGraphSpace(String.format("Graph %d", graphCounter++));
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

    public Component getGraphComponent(String title) {
        for (Component component : getComponents()) {
            if (Objects.equals(component.getName(), title)) {
                setSelectedComponent(component);
                return getSelectedComponent();
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

                        //*** This line was disabled because it fired an annotation selection event
                        //*** which causes a second cell selection event to occur. This interferes with
                        //*** node selection when a one annotation maps to multiple nodes.
//                        manager.annotationSelectionChangedEvent(annotation);
                        //***
                        manager.annotationSelectionChangedEvent(annotation);
//                        textPane.setSelection(null, annotation);

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

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                // Handles context menu on the Mac where the trigger is on mouse pressed
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    showGraphPopupMenu(graphComponent, e);
                }
//                graph.reDrawVertices();
            }
        });
    }

    public void addAnnotationVertex(Annotation annotation) {
        mxGraphComponent graphComponent = getSelectedGraphComponent();
        GraphSpace graph = (GraphSpace) graphComponent.getGraph();
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

    public void goToAnnotationVertex(Annotation annotation) {
        if (getSelectedGraphComponent() != null && annotation != null) {
            GraphSpace graphSpace = (GraphSpace) getSelectedGraphComponent().getGraph();
            List<mxCell> vertices = graphSpace.getVerticesForAnnotation(annotation);
            if (vertices.size() > 0) {
                goToVertex(vertices.get(0));
            }
        }
    }

    private void goToVertex(mxCell vertex) {
        GraphSpace graph = (GraphSpace) getSelectedGraphComponent().getGraph();

        if (!graph.getSelectionModel().isSelected(vertex)) graph.setSelectionCell(vertex);
        requestFocusInWindow();
        ((mxGraphComponent) getSelectedComponent()).scrollCellToVisible(vertex, true);
    }

    public void deleteSelectedGraph() {
        GraphSpace graph = (GraphSpace) getSelectedGraphComponent().getGraph();

        textPane.getTextSource().getAnnotationManager().removeGraphSpace(graph);

        closeGraphSpace((mxGraphComponent) getSelectedComponent());

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

    public void addAllAnnotations() {
        getGraphComponent("All annotations");

        textPane.getTextSource().getAnnotationManager().findOverlaps();
        Collection<Annotation> annotations = textPane.getTextSource().getAnnotationManager().getAnnotations();

        //TODO: Fix progress bar
        for (Annotation annotation : annotations) {
            if (annotation != null) {
                addAnnotationVertex(annotation);
            }
        }
    }

    public void addGraph(GraphSpace graph) {
        JScrollPane sp = new JScrollPane();
        sp.getVerticalScrollBar().setUnitIncrement(20);

        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.white);
        graphComponent.setDragEnabled(false);
        graphComponent.setSize(new Dimension(800, 800));
        graphComponent.getGraphControl().add(sp, 0);
        graphComponent.setName(graph.getId());

        setupListeners(graphComponent);

        addTab(graph.getId(), graphComponent);

        int index = indexOfTab(graph.getId());
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(graph.getId());
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

        btnClose.addActionListener(evt -> closeGraphSpace(graphComponent));

        setSelectedComponent(graphComponent);
        graph.reDrawVertices();
//        executeLayout(null);
    }

    void executeLayout(mxGraphComponent graphComponent) {
        if (graphComponent == null) graphComponent = getSelectedGraphComponent();
        GraphSpace graph = (GraphSpace) graphComponent.getGraph();
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
                mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

                morph.addListener(mxEvent.DONE, (arg0, arg1) -> graph.getModel().endUpdate());

                morph.startAnimation();
            }
        } finally {
            graph.getModel().endUpdate();
            graphComponent.zoomAndCenter();
        }
    }

    public JDialog getDialog() {
        return dialog;
    }
}
