package edu.ucdenver.cpbs.mechanic.ui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("PackageAccessibility")
public class MechAnICGraphViewer extends JPanel {

    private Object parent;
    private mxGraph graph;

    private mxGraphComponent graphComponent;

    public MechAnICGraphViewer() {
        graph = new mxGraph();
        parent = graph.getDefaultParent();


        graphComponent = new mxGraphComponent(graph);
        graphComponent.setDragEnabled(false);
        graphComponent.setPreferredSize(new Dimension(1200, 200));
        add(graphComponent);

        addNode("hello");
        addNode("bye");

    }

    public void addNode(Object value) {
        graph.getModel().beginUpdate();
        try {
            Object v1 = graph.insertVertex(parent, null, value, 20, 20,
                    80, 30);

            // apply layout to graph
            mxHierarchicalLayout layout = new mxHierarchicalLayout(
                    graph);
            layout.setOrientation(SwingConstants.WEST);
            layout.execute(parent);
        } finally {
            graph.getModel().endUpdate();
        }
    }
}
