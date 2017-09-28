package edu.ucdenver.ccp.knowtator.ui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.iaa.AssertionRelationship;
import edu.ucdenver.ccp.knowtator.iaa.Annotation;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("PackageAccessibility")
public class KnowtatorGraphViewer extends JPanel {

    private Object parent;
    private mxGraph graph;

    private mxGraphComponent graphComponent;
    private KnowtatorView view;

    public KnowtatorGraphViewer(KnowtatorView view) {
        this.view = view;
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
                    AssertionRelationship relationship = ((KnowtatorTextPane) ((JScrollPane) view.getTextViewer().getSelectedComponent()).getViewport().getView()).getTextAnnotationManager().addAssertion();
                    if (relationship != null) {
                        ((mxCell) cell).setValue(relationship);
                    } else {
                        graph.getModel().remove(cell);
                    }
                }
            }
        });

    }

    public void addAnnotationNode(Annotation value) {
        graph.getModel().beginUpdate();
        try {
            graph.insertVertex(parent, null, value, 20, 20,
                    80, 30,
                    String.format(
                            "fillColor=#%s",
                            Integer.toHexString(
                                    view.getProfileManager().getCurrentAnnotator().getHighlighter(
                                            value.getOwlClass()
                                    ).getColor().getRGB()
                            ).substring(2)
                    )
            );

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
