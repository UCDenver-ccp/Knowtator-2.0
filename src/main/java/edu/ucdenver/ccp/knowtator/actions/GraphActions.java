/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.actions;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.graph.GraphMenuDialog;
import edu.ucdenver.ccp.knowtator.view.graph.GraphView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class GraphActions {

    public static class removeCellsAction extends AbstractKnowtatorAction {
        private final Object[] cellsToRemove;
        private final GraphSpace graphSpace;
        private final KnowtatorEdit edit;

        public removeCellsAction(KnowtatorController controller) throws NoSelectionException {
            super("Remove cells");
            graphSpace = controller.getTextSourceCollection().getSelection().getGraphSpaceCollection().getSelection();
            cellsToRemove = graphSpace.getSelectionCells();
            edit = new KnowtatorEdit(getPresentationName()) {

            };
        }

        @Override
        public void execute() {
            graphSpace.getModel().addListener(mxEvent.UNDO, edit);
            graphSpace.removeCells(cellsToRemove, true);
            graphSpace.getModel().removeListener(edit, mxEvent.UNDO);
        }

        @Override
        public UndoableEdit getEdit() {
            return edit;
        }
    }


    public static void addAnnotationNode(KnowtatorView view) {
        try {


            TextSource textSource = view.getController().getTextSourceCollection().getSelection();
            ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();

            AnnotationNode vertex =
                    textSource
                            .getGraphSpaceCollection().getSelection()
                            .makeOrGetAnnotationNode(conceptAnnotation, null);


            goToVertex(view, vertex);
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }

    }

    private static void goToVertex(KnowtatorView view, AnnotationNode vertex) {
        view.getGraphViewDialog().requestFocusInWindow();
        view.getGraphViewDialog().getGraphView().getGraphComponent().scrollCellToVisible(vertex, true);
    }

    public static void applyLayout(KnowtatorView view) {
        try {


            GraphSpace graph = view.getController()
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
        } catch (NoSelectionException e) {
            e.printStackTrace();
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

    public static void exportToPNG(KnowtatorView view) {
        try {
            TextSource textSource = view.getController().getTextSourceCollection().getSelection();
            GraphSpace graphSpace = textSource.getGraphSpaceCollection().getSelection();

            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(view.getController().getSaveLocation());
            FileFilter fileFilter = new FileNameExtensionFilter("PNG", "png");
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setSelectedFile(new File(textSource.getId() + "_" + graphSpace.getId() + ".png"));
            if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                BufferedImage image =
                        mxCellRenderer.createBufferedImage(graphSpace, null, 1, Color.WHITE, true, null);
                try {
                    ImageIO.write(
                            image, "PNG", new File(fileChooser.getSelectedFile().getAbsolutePath()));
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }

    public static void showGraphMenuDialog(KnowtatorView view) {
        GraphMenuDialog graphMenuDialog = new GraphMenuDialog(view);
        graphMenuDialog.pack();
        graphMenuDialog.setVisible(true);
    }

    public static void zoomGraph(mxGraphComponent graphComponent, int zoomValue) {
        graphComponent.zoomTo(zoomValue / 50.0, false);
    }

    public static void renameGraphSpace(KnowtatorView view) {
        try {


            TextSource textSource = view.getController().getTextSourceCollection().getSelection();
            String graphName = getGraphNameInput(view, textSource, null);
            if (graphName != null) {
                textSource.getGraphSpaceCollection().getSelection().setId(graphName);
            }
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }

    public static void addGraphSpace(KnowtatorView view) {
        try {
            TextSource textSource = view.getController().getTextSourceCollection().getSelection();
            String graphName = getGraphNameInput(view, textSource, null);

            if (graphName != null) {
                textSource.getGraphSpaceCollection().addGraphSpace(graphName);
            }
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }

    public static void removeGraphSpace(KnowtatorView view) {
        try {


            if (JOptionPane.showConfirmDialog(view, "Are you sure you want to delete this graph?") == JOptionPane.YES_OPTION) {
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getGraphSpaceCollection()
                        .removeSelected();
            }
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }

    private static String getGraphNameInput(KnowtatorView view, TextSource textSource, JTextField field1) {
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
                                    if (textSource
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
        field1.setText("Graph Space " + Integer.toString(textSource.getGraphSpaceCollection().size()));
        int option =
                JOptionPane.showConfirmDialog(
                        view,
                        message,
                        "Enter a name for this graph",
                        JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            if (textSource
                    .getGraphSpaceCollection()
                    .containsID(field1.getText())) {
                JOptionPane.showMessageDialog(field1, "Graph name already in use");
                return getGraphNameInput(view, textSource, field1);
            } else {
                return field1.getText();
            }
        }

        return null;
    }
}
