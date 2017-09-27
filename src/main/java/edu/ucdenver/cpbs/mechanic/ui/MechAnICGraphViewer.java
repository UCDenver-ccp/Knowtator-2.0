package edu.ucdenver.cpbs.mechanic.ui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.cpbs.mechanic.MechAnICView;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("PackageAccessibility")
public class MechAnICGraphViewer extends JPanel {

    private Object parent;
    private mxGraph graph;

    private mxGraphComponent graphComponent;

    public MechAnICGraphViewer(MechAnICView view) {
        graph = new mxGraph();
        parent = graph.getDefaultParent();


        graphComponent = new mxGraphComponent(graph);
        graphComponent.setDragEnabled(false);
        graphComponent.setPreferredSize(new Dimension(1200, 200));
        add(graphComponent);

        graph.addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
            Object[] cells = (Object[])evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
                    OWLObjectProperty property = view.getOWLWorkspace().getOWLSelectionModel().getLastSelectedObjectProperty();
                    if (property != null) {
                        ((mxCell) cell).setValue(property);
                    } else {
                        graph.getModel().remove(cell);
                    }
                }
            }
        });

        addAnnotationNode("hello");
        addAnnotationNode("bye");

    }

    public void addAnnotationNode(Object value) {
        graph.getModel().beginUpdate();
        try {
            graph.insertVertex(parent, null, value, 20, 20,
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
