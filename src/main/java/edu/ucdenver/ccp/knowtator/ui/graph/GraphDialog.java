package edu.ucdenver.ccp.knowtator.ui.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.CompositionalAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.AssertionListener;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.menus.GraphMenu;

import javax.swing.*;
import java.awt.*;

public class GraphDialog extends JDialog implements AnnotationListener, AssertionListener {

    private final GraphViewer graphViewer;

    public GraphDialog(KnowtatorManager manager, JFrame frame, BasicKnowtatorView view) {
        super(frame, "Graph Viewer");

        setLayout(new BorderLayout());

        setVisible(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(new Dimension(800, 800));
        setLocationRelativeTo(view);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GraphMenu(view));
        setJMenuBar(menuBar);

        graphViewer = new GraphViewer(manager, view);
        add(graphViewer);
    }

    public GraphViewer getGraphViewer() {
        return graphViewer;
    }

    @Override
    public void annotationAdded(ConceptAnnotation newAnnotation) {
//        if (graphViewer.getSelectedGraph() != null) graphViewer.addVertex(newAnnotation);
    }

    @Override
    public void annotationRemoved(ConceptAnnotation removedAnnotation) {
        if (graphViewer.getSelectedGraph() != null) graphViewer.removeVertex(removedAnnotation);
    }

    @Override
    public void annotationSelectionChanged(ConceptAnnotation annotation) {
        if (graphViewer.getSelectedGraph() != null) graphViewer.goToVertex(annotation);
    }

    @Override
    public void assertionAdded(CompositionalAnnotation compositionalAnnotation) {
        if (graphViewer.getSelectedGraph() != null) graphViewer.addEdge(compositionalAnnotation);
    }
}
