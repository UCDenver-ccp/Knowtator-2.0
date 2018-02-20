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

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.GraphListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.menus.GraphMenu;
import edu.ucdenver.ccp.knowtator.view.menus.GraphOptionsMenu;
import edu.ucdenver.ccp.knowtator.view.menus.GraphViewMenu;

import javax.swing.*;
import java.awt.*;

public class GraphDialog extends JDialog implements AnnotationListener, ProfileListener, GraphListener {

    private final GraphViewer graphViewer;
    private TextSource textSource;

    public GraphDialog(KnowtatorManager manager, JFrame frame, KnowtatorView view, TextSource textSource) {
        super(frame, String.format("Graph Viewer - %s", textSource.getDocID()));
        this.textSource = textSource;
        setLayout(new BorderLayout());

        setVisible(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(new Dimension(800, 800));
        setLocationRelativeTo(view);

        JMenuBar menuBar = new JMenuBar();
        menuBar.add(new GraphMenu(manager, this));

        GraphViewMenu graphViewMenu = new GraphViewMenu(this);
        menuBar.add(graphViewMenu);
        menuBar.add(new GraphOptionsMenu(this));
        setJMenuBar(menuBar);

        manager.addGraphListener(graphViewMenu);
        graphViewer = new GraphViewer(manager, view, textSource);
        add(graphViewer);
    }

    @Override
    public void annotationAdded(Annotation newAnnotation) {

    }

    @Override
    public void annotationRemoved(Annotation removedAnnotation) {

    }


    @Override
    public void annotationSelectionChanged(Annotation annotation) {
        if (graphViewer.getSelectedGraphComponent() != null && annotation != null) graphViewer.goToVertex(annotation);
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
        graphViewer.getAllGraphComponents().forEach(graphViewer::executeLayout);
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }

    @Override
    public void colorChanged() {
        graphViewer.getAllGraphComponents().forEach(graphViewer::executeLayout);
    }

    public void showViewer() {
        setVisible(true);
    }

    public TextSource getTextSource() {
        return textSource;
    }

    @Override
    public void newGraph(GraphSpace graphSpace) {
        graphViewer.addGraph(graphSpace);
    }

    @Override
    public void removeGraph(GraphSpace graphSpace) {

    }
}
