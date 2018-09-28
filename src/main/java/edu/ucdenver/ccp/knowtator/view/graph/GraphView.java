package edu.ucdenver.ccp.knowtator.view.graph;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;
import edu.ucdenver.ccp.knowtator.view.chooser.GraphSpaceChooser;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;

public class GraphView extends JPanel implements GraphSpaceCollectionListener, KnowtatorViewComponent {
    private JButton removeCellButton;
    private JButton addAnnotationNodeButton;
    private JButton applyLayoutButton;
    private JButton previousGraphSpaceButton;
    private JButton nextGraphSpaceButton;
    private mxGraphComponent graphComponent;
    private JPanel panel1;
    private GraphSpaceChooser graphSpaceChooser;
    private JButton addGraphSpaceButton;
    private JButton removeGraphSpaceButton;
    private JSlider zoomSlider;
    private JButton graphMenuButton;
    private JButton renameButton;
    private JDialog dialog;

    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(GraphView.class);

    private KnowtatorView view;
    private TextSourceCollectionListener textSourceCollectionListener;


    GraphView(JDialog dialog, KnowtatorView view) {
        this.dialog = dialog;
        this.view = view;
        $$$setupUI$$$();
        makeButtons();

        GraphView graphView = this;

        textSourceCollectionListener = new TextSourceCollectionListener() {
            @Override
            public void added(AddEvent<TextSource> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> removedObject) {

            }

            @Override
            public void changed(ChangeEvent<TextSource> changeEvent) {

            }

            @Override
            public void emptied(RemoveEvent<TextSource> object) {

            }

            @Override
            public void firstAdded(AddEvent<TextSource> object) {

            }

            @Override
            public void updated(TextSource updatedItem) {

            }

            @Override
            public void noSelection(TextSource previousSelection) {

            }

            @Override
            public void selected(SelectionChangeEvent<TextSource> event) {
                if (event.getOld() != null) {
                    event.getOld().getGraphSpaceCollection().removeCollectionListener(graphView);
                }
                event.getNew().getGraphSpaceCollection().addCollectionListener(graphView);
            }
        };
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    private static String getGraphNameInput(KnowtatorView view, JTextField field1) {
        if (field1 == null) {
            field1 = new JTextField();

            JTextField finalField = field1;
            field1
                    .getDocument()
                    .addDocumentListener(
                            new DocumentListener() {
                                @Override
                                public void insertUpdate(DocumentEvent e) {
                                    warn();
                                }

                                @Override
                                public void removeUpdate(DocumentEvent e) {
                                    warn();
                                }

                                @Override
                                public void changedUpdate(DocumentEvent e) {
                                    warn();
                                }

                                private void warn() {
                                    if (view.getController()
                                            .getTextSourceCollection().getSelection()
                                            .getGraphSpaceCollection()
                                            .containsID(finalField.getText())) {
                                        try {
                                            finalField
                                                    .getHighlighter()
                                                    .addHighlight(
                                                            0,
                                                            finalField.getText().length(),
                                                            new DefaultHighlighter.DefaultHighlightPainter(Color.RED));
                                        } catch (BadLocationException e1) {
                                            e1.printStackTrace();
                                        }
                                    } else {
                                        finalField.getHighlighter().removeAllHighlights();
                                    }
                                }
                            });
        }
        Object[] message = {
                "Graph Title", field1,
        };
        field1.addAncestorListener(new RequestFocusListener());
        field1.setText("Graph Space " + Integer.toString(view.getController()
                .getTextSourceCollection().getSelection()
                .getGraphSpaceCollection().size()));
        int option =
                JOptionPane.showConfirmDialog(
                        view,
                        message,
                        "Enter a name for this graph",
                        JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (view.getController()
                    .getTextSourceCollection().getSelection()
                    .getGraphSpaceCollection()
                    .containsID(field1.getText())) {
                JOptionPane.showMessageDialog(field1, "Graph name already in use");
                return getGraphNameInput(view, field1);
            } else {
                return field1.getText();
            }
        }

        return null;
    }

    private void createUIComponents() {
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        graphSpaceChooser = new GraphSpaceChooser(view);
        mxGraph testGraph = new mxGraph();
        graphComponent = new mxGraphComponent(testGraph);
    }

    private void makeButtons() {

        graphMenuButton.addActionListener(e -> {
            GraphMenuDialog graphMenuDialog = new GraphMenuDialog(view);
            graphMenuDialog.pack();
            graphMenuDialog.setVisible(true);
        });

        graphSpaceChooser.addActionListener(
                e -> {
                    JComboBox comboBox = (JComboBox) e.getSource();
                    if (comboBox.getSelectedItem() != null
                            && comboBox.getSelectedItem()
                            != view.getController()
                            .getTextSourceCollection().getSelection()
                            .getGraphSpaceCollection().getSelection()) {
                        view.getController()
                                .getTextSourceCollection().getSelection()
                                .getGraphSpaceCollection().setSelection((GraphSpace) comboBox.getSelectedItem());
                    }

                });

        zoomSlider.addChangeListener(e -> graphComponent.zoomTo(zoomSlider.getValue() / 50.0, false));

        renameButton.addActionListener(e -> {
            String graphName = getGraphNameInput(view, null);
            if (graphName != null) {
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getGraphSpaceCollection().getSelection().setId(graphName);
            }
        });

        addGraphSpaceButton.addActionListener(e -> {
            String graphName = getGraphNameInput(view, null);

            if (graphName != null) {
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getGraphSpaceCollection().addGraphSpace(graphName);
            }
        });

        removeGraphSpaceButton.addActionListener(e -> {
            if (JOptionPane.showConfirmDialog(
                    view, "Are you sure you want to delete this graph?")
                    == JOptionPane.YES_OPTION) {
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getGraphSpaceCollection()
                        .removeGraphSpace(view.getController().getTextSourceCollection().getSelection()
                                .getGraphSpaceCollection().getSelection());
            }
        });

        previousGraphSpaceButton.addActionListener(
                e -> GraphActions.selectPreviousGraphSpace(view));
        nextGraphSpaceButton.addActionListener(
                e -> GraphActions.selectNextGraphSpace(view));
        removeCellButton.addActionListener(e -> GraphActions.removeSelectedCell(view));
        addAnnotationNodeButton.addActionListener(
                e -> GraphActions.addAnnotationNode(view));
        applyLayoutButton.addActionListener(e -> GraphActions.applyLayout(view));
    }

    private void showGraph(GraphSpace graphSpace) {
        graphComponent.setGraph(graphSpace);
        graphSpace.setParentWindow(dialog);

        graphComponent.setName(graphSpace.getId());

        graphSpace.setupListeners();

        graphSpace.reDrawGraph();
        graphComponent.refresh();
    }

    @Override
    public void updated(GraphSpace updatedItem) {

    }

    @Override
    public void noSelection(GraphSpace previousSelection) {

    }

    @Override
    public void selected(SelectionChangeEvent<GraphSpace> event) {
        if (event.getNew() != null && event.getNew() != graphComponent.getGraph()) {
            showGraph(event.getNew());
        }
    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
        graphSpaceChooser.reset();
    }

    @Override
    public void dispose() {

    }

    @Override
    public void added(AddEvent<GraphSpace> addedObject) {

    }

    @Override
    public void removed(RemoveEvent<GraphSpace> removedObject) {

    }

    @Override
    public void changed(ChangeEvent<GraphSpace> changeEvent) {

    }

    @Override
    public void emptied(RemoveEvent<GraphSpace> object) {

    }

    @Override
    public void firstAdded(AddEvent<GraphSpace> object) {

    }

    mxGraphComponent getGraphComponent() {
        return graphComponent;
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
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayoutManager(1, 5, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel2, BorderLayout.NORTH);
        addAnnotationNodeButton = new JButton();
        addAnnotationNodeButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addAnnotationNodeButton.setText("");
        panel2.add(addAnnotationNodeButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final Spacer spacer1 = new Spacer();
        panel2.add(spacer1, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        removeCellButton = new JButton();
        removeCellButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeCellButton.setText("");
        panel2.add(removeCellButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        applyLayoutButton = new JButton();
        applyLayoutButton.setText("Apply Layout");
        panel2.add(applyLayoutButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        graphMenuButton = new JButton();
        graphMenuButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-menu-24.png")));
        graphMenuButton.setText("");
        panel2.add(graphMenuButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new BorderLayout(0, 0));
        panel1.add(panel3, BorderLayout.CENTER);
        graphComponent.setCenterPage(false);
        graphComponent.setGridVisible(true);
        panel3.add(graphComponent, BorderLayout.CENTER);
        final JPanel panel4 = new JPanel();
        panel4.setLayout(new GridLayoutManager(2, 6, new Insets(0, 0, 0, 0), -1, -1));
        panel1.add(panel4, BorderLayout.SOUTH);
        previousGraphSpaceButton = new JButton();
        previousGraphSpaceButton.setText("Previous");
        panel4.add(previousGraphSpaceButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        graphSpaceChooser.setMaximumSize(new Dimension(200, 32767));
        graphSpaceChooser.setMinimumSize(new Dimension(80, 30));
        panel4.add(graphSpaceChooser, new GridConstraints(0, 1, 1, 3, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        addGraphSpaceButton = new JButton();
        addGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-plus-24.png")));
        addGraphSpaceButton.setText("");
        panel4.add(addGraphSpaceButton, new GridConstraints(1, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        removeGraphSpaceButton = new JButton();
        removeGraphSpaceButton.setIcon(new ImageIcon(getClass().getResource("/icon/icons8-delete-24.png")));
        removeGraphSpaceButton.setText("");
        panel4.add(removeGraphSpaceButton, new GridConstraints(1, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        zoomSlider = new JSlider();
        zoomSlider.setMaximum(100);
        panel4.add(zoomSlider, new GridConstraints(1, 3, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_FIXED, null, new Dimension(194, 16), null, 0, false));
        final Spacer spacer2 = new Spacer();
        panel4.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        renameButton = new JButton();
        renameButton.setText("Rename");
        panel4.add(renameButton, new GridConstraints(1, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
        nextGraphSpaceButton = new JButton();
        nextGraphSpaceButton.setText("Next");
        panel4.add(nextGraphSpaceButton, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_FIXED, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return panel1;
    }

    /**
     * Taken from https://tips4java.wordpress.com/2010/03/14/dialog-focus/
     */
    private static class RequestFocusListener implements AncestorListener {
        private boolean removeListener;

        /*
         *  Convenience constructor. The listener is only used once and then it is
         *  removed from the component.
         */
        RequestFocusListener() {
            this(true);
        }

        /*
         *  Constructor that controls whether this listen can be used once or
         *  multiple times.
         *
         *  @param removeCollectionListener when true this listener is only invoked once
         *                        otherwise it can be invoked multiple times.
         */
        RequestFocusListener(boolean removeListener) {
            this.removeListener = removeListener;
        }

        @Override
        public void ancestorAdded(AncestorEvent e) {
            JComponent component = e.getComponent();
            component.requestFocusInWindow();

            if (removeListener) component.removeAncestorListener(this);
        }

        @Override
        public void ancestorMoved(AncestorEvent e) {
        }

        @Override
        public void ancestorRemoved(AncestorEvent e) {
        }
    }


}
