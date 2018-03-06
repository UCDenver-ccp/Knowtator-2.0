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

import com.mxgraph.swing.mxGraphComponent;
import edu.ucdenver.ccp.knowtator.listeners.GraphListener;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;

import javax.swing.*;

//TODO: Toggle expand to show ontology terms as separate nodes
//TODO; Toggle expand to show coreference
public class GraphViewMenu extends JMenu implements GraphListener {


    private GraphViewer graphViewer;

    GraphViewMenu(GraphViewer graphViewer) {
        super("View");
        this.graphViewer = graphViewer;

        updateMenus();
    }

    private JMenu goToGraphMenu() {
        JMenu menu = new JMenu("Go to graph");
        graphViewer.getTextPane().getTextSource().getAnnotationManager().getGraphSpaces()
                .forEach(graphSpace -> {
                    JMenuItem menuItem = new JMenuItem(graphSpace.getId());
                    menuItem.addActionListener(e -> {
                        if (graphViewer.getGraphComponent(graphSpace.getId()) == null) {
                            graphViewer.addGraph(graphSpace);
                        }
                    });
                    menu.add(menuItem);
                });
        return menu;
    }

    private JMenuItem zoomInCommand() {
        JMenuItem menuItem = new JMenuItem("Zoom In");
        menuItem.addActionListener(e -> ((mxGraphComponent) graphViewer.getSelectedComponent()).zoomIn());
        return menuItem;
    }

    private JMenuItem zoomOutCommand() {
        JMenuItem menuItem = new JMenuItem("Zoom Out");
        menuItem.addActionListener(e -> ((mxGraphComponent) graphViewer.getSelectedComponent()).zoomOut());
        return menuItem;
    }

    private void updateMenus() {
        removeAll();
        add(zoomInCommand());
        add(zoomOutCommand());
        addSeparator();
        add(goToGraphMenu());
    }

    @Override
    public void newGraph(GraphSpace graphSpace) {
        updateMenus();
    }

    @Override
    public void removeGraph(GraphSpace graphSpace) {
        updateMenus();
    }
}
