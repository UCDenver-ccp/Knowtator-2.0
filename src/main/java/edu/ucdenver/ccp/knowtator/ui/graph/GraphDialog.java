package edu.ucdenver.ccp.knowtator.ui.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.CompositionalAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.listeners.CompositionalAnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.ConceptAnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.menus.GraphMenu;
import edu.ucdenver.ccp.knowtator.ui.menus.GraphOptionsMenu;
import edu.ucdenver.ccp.knowtator.ui.menus.GraphViewMenu;

import javax.swing.*;
import java.awt.*;

public class GraphDialog extends JDialog implements ConceptAnnotationListener, CompositionalAnnotationListener, ProfileListener {

    private final GraphViewer graphViewer;

    public GraphDialog(KnowtatorManager manager, JFrame frame, BasicKnowtatorView view, TextSource textSource) {
        super(frame, String.format("Graph Viewer - %s", textSource.getDocID()));

        setLayout(new BorderLayout());

        setVisible(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(new Dimension(800, 800));
        setLocationRelativeTo(view);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GraphMenu(manager, this));
        menuBar.add(new GraphViewMenu(this));
        menuBar.add(new GraphOptionsMenu(this));
        setJMenuBar(menuBar);

        graphViewer = new GraphViewer(manager, view, textSource);
        add(graphViewer);
    }

    @Override
    public void annotationAdded(ConceptAnnotation newAnnotation) {
//        if (graphViewer.getSelectedGraphComponent() != null) graphViewer.addVertex(newAnnotation);
    }

    @Override
    public void annotationRemoved(ConceptAnnotation removedAnnotation) {
        if (graphViewer.getSelectedGraphComponent() != null) graphViewer.removeVertex(removedAnnotation);
    }

    @Override
    public void annotationSelectionChanged(ConceptAnnotation annotation) {
        if (graphViewer.getSelectedGraphComponent() != null && annotation != null) graphViewer.goToVertex(annotation);
    }

    @Override
    public void compositionalAnnotationAdded(CompositionalAnnotation compositionalAnnotation) {
        if (graphViewer.getSelectedGraphComponent() != null) graphViewer.addEdge(compositionalAnnotation, false);
    }

    public GraphViewer getGraphViewer() {
        return graphViewer;
    }

    @Override
    public void profileAdded(Profile profile) {

    }

    @Override
    public void profileRemoved() {

    }

    @Override
    public void profileSelectionChanged(Profile profile) {
        graphViewer.getAllGraphComponents().forEach(graphViewer::reDrawVertices);
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }

    @Override
    public void colorChanged() {
        graphViewer.getAllGraphComponents().forEach(graphViewer::reDrawVertices);
    }
}
