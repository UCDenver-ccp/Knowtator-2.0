package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

class GraphActions {
    static void selectPreviousGraphSpace(KnowtatorView view) {
        view.getController()
                .getTextSourceCollection().getSelection()
                .getGraphSpaceCollection()
                .selectPrevious();
    }

    static void selectNextGraphSpace(KnowtatorView view) {
        view.getController()
                .getTextSourceCollection().getSelection()
                .getGraphSpaceCollection()
                .selectNext();
    }

    static void removeSelectedCell(KnowtatorView view) {
        view.getController()
                .getTextSourceCollection().getSelection()
                .getGraphSpaceCollection().getSelection()
                .removeSelectedCell();
    }

    static void addAnnotationNode(KnowtatorView view) {
        ConceptAnnotation conceptAnnotation =
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection().getSelection();

        AnnotationNode vertex =
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getGraphSpaceCollection().getSelection()
                        .makeOrGetAnnotationNode(conceptAnnotation, null);


        goToVertex(view, vertex);

    }

    private static void goToVertex(KnowtatorView view, AnnotationNode vertex) {
        view.getGraphViewDialog().requestFocusInWindow();
        view.getGraphViewDialog().getGraphView().getGraphComponent().scrollCellToVisible(vertex, true);
    }

    static void applyLayout(KnowtatorView view) {
        GraphSpace graph;
        graph = view.getController()
                .getTextSourceCollection().getSelection()
                .getGraphSpaceCollection().getSelection();

        //		graph.reDrawGraph();
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
                mxMorphing morph = new mxMorphing(view.getGraphViewDialog().getGraphView().getGraphComponent(), 20, 1.2, 20);

                morph.addListener(mxEvent.DONE, (arg0, arg1) -> graph.getModel().endUpdate());

                morph.startAnimation();
            }
        } finally {
            graph.getModel().endUpdate();
            view.getGraphViewDialog().getGraphView().getGraphComponent().zoomAndCenter();
        }
    }

// --Commented out by Inspection START (9/29/2018 9:44 PM):
//    public static void goToAnnotationVertex(KnowtatorView view, GraphSpace graphSpace, ConceptAnnotation conceptAnnotation) {
//        if (conceptAnnotation != null && graphSpace != null) {
//            view.getController()
//                    .getTextSourceCollection().getSelection()
//                    .getGraphSpaceCollection().setSelection(graphSpace);
//            List<Object> vertices = graphSpace.getVerticesForAnnotation(conceptAnnotation);
//            if (vertices.size() > 0) {
//                graphSpace.setSelectionCells(vertices);
//                goToVertex(view, (AnnotationNode) vertices.get(0));
//            }
//        }
//    }
// --Commented out by Inspection STOP (9/29/2018 9:44 PM)

    static void exportToPNG(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getSaveLocation());
        FileFilter fileFilter = new FileNameExtensionFilter("PNG", "png");
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setSelectedFile(new File(view.getController()
                .getTextSourceCollection().getSelection()
                .getId() + "_" + view.getController()
                .getTextSourceCollection().getSelection()
                .getGraphSpaceCollection().getSelection()
                .getId() + ".png"));
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            BufferedImage image =
                    mxCellRenderer.createBufferedImage(
                            view.getController()
                                    .getTextSourceCollection().getSelection()
                                    .getGraphSpaceCollection().getSelection(),
                            null,
                            1,
                            Color.WHITE,
                            true,
                            null);
            try {
                ImageIO.write(
                        image, "PNG", new File(fileChooser.getSelectedFile().getAbsolutePath()));
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    static void showGraphMenuDialog(KnowtatorView view) {
        GraphMenuDialog graphMenuDialog = new GraphMenuDialog(view);
        graphMenuDialog.pack();
        graphMenuDialog.setVisible(true);
    }

    static void zoomGraph(mxGraphComponent graphComponent, int zoomValue) {
        graphComponent.zoomTo(zoomValue / 50.0, false);
    }

    static void renameGraphSpace(KnowtatorView view) {
        String graphName = getGraphNameInput(view, null);
        if (graphName != null) {
            view.getController()
                    .getTextSourceCollection().getSelection()
                    .getGraphSpaceCollection().getSelection().setId(graphName);
        }
    }

    static void addGraphSpace(KnowtatorView view) {
        String graphName = getGraphNameInput(view, null);

        if (graphName != null) {
            view.getController()
                    .getTextSourceCollection().getSelection()
                    .getGraphSpaceCollection().addGraphSpace(graphName);
        }
    }

    static void removeGraphSpace(KnowtatorView view) {
        if (JOptionPane.showConfirmDialog(
                view, "Are you sure you want to delete this graph?")
                == JOptionPane.YES_OPTION) {
            view.getController()
                    .getTextSourceCollection().getSelection()
                    .getGraphSpaceCollection()
                    .removeSelected();
        }
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
        field1.addAncestorListener(new GraphView.RequestFocusListener());
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
}
