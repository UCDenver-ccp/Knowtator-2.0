package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

public class GraphView extends JPanel implements SelectionListener {
    private JScrollPane scrollPane;
    private JButton removeCellButton;
    private JButton addAnnotationNodeButton;
    private JButton applyLayoutButton;
    private JButton previousGraphSpaceButton;
    private JButton nextGraphSpaceButton;
    private mxGraphComponent graphComponent;
    private JButton zoomOutButton;
    private JButton zoomInButton;
    private JPanel panel1;
    private GraphSpaceChooser graphSpaceChooser;
    private JPanel graphPane;
    private JDialog dialog;
    private KnowtatorController controller;

    GraphView(JDialog dialog, KnowtatorController controller) {
        this.dialog = dialog;
        this.controller = controller;
        controller.getSelectionManager().addSelectionListener(this);
        $$$setupUI$$$();
        makeButtons();
    }

    private void createUIComponents() {
        scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        graphSpaceChooser = new GraphSpaceChooser(controller);

    }

    private void makeButtons() {
        graphSpaceChooser.addActionListener(e -> {
            JComboBox comboBox = (JComboBox) e.getSource();
            if (comboBox.getSelectedItem() != null && comboBox.getSelectedItem() != controller.getSelectionManager().getActiveTextSource()) {
                showGraph((GraphSpace) comboBox.getSelectedItem());
            }
        });

        zoomOutButton.addActionListener(e -> graphComponent.zoomOut());
        zoomInButton.addActionListener(e -> graphComponent.zoomIn());
        previousGraphSpaceButton.addActionListener(e -> showPreviousGraphSpace());
        nextGraphSpaceButton.addActionListener(e -> showNextGraphSpace());
        removeCellButton.addActionListener(e -> removeSelectedCell());
        addAnnotationNodeButton.addActionListener(e -> {
            Annotation annotation = controller.getSelectionManager().getSelectedAnnotation();
            mxCell vertex = controller.getSelectionManager().getActiveGraphSpace().addNode(null, annotation);

            goToVertex(vertex);
        });
        applyLayoutButton.addActionListener(e -> applyLayout());
    }

    private void setupListeners(mxGraphComponent graphComponent) {
        GraphSpace graph = (GraphSpace) graphComponent.getGraph();
        //Handle drag and drop
        //Adds the current selected object property as the edge value
        graph.addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
            Object[] cells = (Object[]) evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell) && "".equals(((mxCell) cell).getValue())) {
                    mxCell edge = (mxCell) cell;
                    mxICell source = edge.getSource();
                    mxICell target = edge.getTarget();
                    Object property = controller.getOWLAPIDataExtractor().getSelectedProperty();

                    if (property != null) {
                        try {
                            throw new Exception();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
                                "Restriction options", JOptionPane.DEFAULT_OPTION);
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
                    break;
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

    @SuppressWarnings("unused")
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
        graphComponent.scrollCellToVisible(vertex, true);

    }

    private void showGraph(GraphSpace graphSpace) {
        if (graphComponent != null) {
            graphPane.remove(graphComponent);
        }
        graphComponent = new mxGraphComponent(graphSpace);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.white);
        graphComponent.setDragEnabled(false);
        graphComponent.setSize(new Dimension(800, 800));
        graphComponent.getGraphControl().add(scrollPane, 0);
        graphComponent.setName(graphSpace.getId());

        setupListeners(graphComponent);

        graphPane.add(graphComponent, BorderLayout.CENTER);
        applyLayout();
        graphSpace.reDrawGraph();
    }

    private void applyLayout() {
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
                mxMorphing morph = new mxMorphing(graphComponent, 20, 1.2, 20);

                morph.addListener(mxEvent.DONE, (arg0, arg1) -> graph.getModel().endUpdate());

                morph.startAnimation();
            }
        } finally {
            graph.getModel().endUpdate();
            graphComponent.zoomAndCenter();
        }
    }

    private void removeSelectedCell() {
        controller.getSelectionManager().getActiveGraphSpace().removeSelectedCell();
    }

    private void showPreviousGraphSpace() {
        GraphSpace previousGraphSpace = controller.getSelectionManager().getActiveTextSource().getAnnotationManager().getPreviousGraphSpace();
        showGraph(previousGraphSpace);
    }

    private void showNextGraphSpace() {
        GraphSpace nextGraphSpace = controller.getSelectionManager().getActiveTextSource().getAnnotationManager().getNextGraphSpace();
        showGraph(nextGraphSpace);
    }

    @Override
    public void selectedAnnotationChanged() {

    }

    @Override
    public void selectedSpanChanged() {

    }

    @Override
    public void activeGraphSpaceChanged() {
        showGraph(controller.getSelectionManager().getActiveGraphSpace());
    }

    @Override
    public void activeTextSourceChanged() {

    }

    @Override
    public void currentProfileChange() {

    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        createUIComponents();
        panel1 = new JPanel();
        panel1.setLayout(new BorderLayout(0, 0));
        panel1.setAlignmentX(0.0f);
        panel1.setAlignmentY(0.0f);
        panel1.setMinimumSize(new Dimension(400, 400));
        final JToolBar toolBar1 = new JToolBar();
        toolBar1.setAlignmentX(0.0f);
        toolBar1.setAlignmentY(0.0f);
        toolBar1.setFloatable(false);
        toolBar1.setMinimumSize(new Dimension(630, 100));
        panel1.add(toolBar1, BorderLayout.NORTH);
        zoomOutButton = new JButton();
        zoomOutButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-zoom-out-filled-50 (Custom).png")));
        zoomOutButton.setText("");
        zoomOutButton.setToolTipText(ResourceBundle.getBundle("ui").getString("zoom.out"));
        toolBar1.add(zoomOutButton);
        zoomInButton = new JButton();
        zoomInButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-zoom-in-filled-50 (Custom).png")));
        zoomInButton.setText("");
        zoomInButton.setToolTipText(ResourceBundle.getBundle("ui").getString("zoom.in"));
        toolBar1.add(zoomInButton);
        removeCellButton = new JButton();
        removeCellButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-minus-50 (Custom).png")));
        removeCellButton.setText("");
        removeCellButton.setToolTipText(ResourceBundle.getBundle("ui").getString("remove.item"));
        toolBar1.add(removeCellButton);
        addAnnotationNodeButton = new JButton();
        addAnnotationNodeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-50 (Custom).png")));
        addAnnotationNodeButton.setText("");
        addAnnotationNodeButton.setToolTipText(ResourceBundle.getBundle("ui").getString("add.annotation.node"));
        toolBar1.add(addAnnotationNodeButton);
        applyLayoutButton = new JButton();
        this.$$$loadButtonText$$$(applyLayoutButton, ResourceBundle.getBundle("ui").getString("apply.layout"));
        applyLayoutButton.setToolTipText(ResourceBundle.getBundle("ui").getString("apply.layout1"));
        toolBar1.add(applyLayoutButton);
        previousGraphSpaceButton = new JButton();
        previousGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-up-filled-50 (Custom).png")));
        previousGraphSpaceButton.setText("");
        previousGraphSpaceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("previous.graph.space"));
        toolBar1.add(previousGraphSpaceButton);
        nextGraphSpaceButton = new JButton();
        nextGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-down-filled-50 (Custom).png")));
        nextGraphSpaceButton.setText("");
        nextGraphSpaceButton.setToolTipText(ResourceBundle.getBundle("ui").getString("next.graph.space"));
        toolBar1.add(nextGraphSpaceButton);
        graphSpaceChooser.setMinimumSize(new Dimension(80, 30));
        toolBar1.add(graphSpaceChooser);
        graphPane = new JPanel();
        graphPane.setLayout(new BorderLayout(0, 0));
        panel1.add(graphPane, BorderLayout.CENTER);
    }

    /**
     * @noinspection ALL
     */
    private void $$$loadButtonText$$$(AbstractButton component, String text) {
        StringBuffer result = new StringBuffer();
        boolean haveMnemonic = false;
        char mnemonic = '\0';
        int mnemonicIndex = -1;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) == '&') {
                i++;
                if (i == text.length()) break;
                if (!haveMnemonic && text.charAt(i) != '&') {
                    haveMnemonic = true;
                    mnemonic = text.charAt(i);
                    mnemonicIndex = result.length();
                }
            }
            result.append(text.charAt(i));
        }
        component.setText(result.toString());
        if (haveMnemonic) {
            component.setMnemonic(mnemonic);
            component.setDisplayedMnemonicIndex(mnemonicIndex);
        }
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }
}
