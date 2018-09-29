package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.util.mxMorphing;
import com.mxgraph.util.mxCellRenderer;
import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

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
        graph =
                view.getController()
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

    public static void goToAnnotationVertex(KnowtatorView view, GraphSpace graphSpace, ConceptAnnotation conceptAnnotation) {
        if (conceptAnnotation != null && graphSpace != null) {
            view.getController()
                    .getTextSourceCollection().getSelection()
                    .getGraphSpaceCollection().setSelection(graphSpace);
            List<Object> vertices = graphSpace.getVerticesForAnnotation(conceptAnnotation);
            if (vertices.size() > 0) {
                graphSpace.setSelectionCells(vertices);
                goToVertex(view, (AnnotationNode) vertices.get(0));
            }
        }
    }

    static void exportToPNG(JDialog parent, KnowtatorView view) {
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
}
