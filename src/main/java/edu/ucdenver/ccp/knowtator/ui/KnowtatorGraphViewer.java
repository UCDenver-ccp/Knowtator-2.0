package edu.ucdenver.ccp.knowtator.ui;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.iaa.AssertionRelationship;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

@SuppressWarnings("PackageAccessibility")
public class KnowtatorGraphViewer extends JPanel {

    private Object parent;

    mxGraph getGraph() {
        return graph;
    }

    private mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

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
                    AssertionRelationship relationship = view.getTextAnnotationManager().addAssertion();
                    if (relationship != null) {
                        ((mxCell) cell).setValue(relationship);
                    } else {
                        graph.getModel().remove(cell);
                    }
                }
            }
        });

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter()
        {

            /**
             *
             */
            public void mousePressed(MouseEvent e)
            {
                // Handles context menu on the Mac where the trigger is on mousepressed
                mouseReleased(e);
            }

            /**
             *
             */
            public void mouseReleased(MouseEvent e)
            {
                if (e.isPopupTrigger())
                {
                    showGraphPopupMenu(e);
                }
            }

        });

        graph.addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> {
            Object[] cells = (Object[])evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
                    graph.getModel().remove(cell);
                }
            }
        });

    }

    public void addAnnotationNode(TextAnnotation value) {
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

    Action bind(String name, final Action action)
    {
        return bind(name, action, null);
    }

    Action bind(String name, final Action action, String iconUrl)
    {
        AbstractAction newAction = new AbstractAction(name, (iconUrl != null) ? new ImageIcon(
                getClass().getResource(iconUrl)) : null)
        {
            public void actionPerformed(ActionEvent e)
            {
                action.actionPerformed(new ActionEvent(getGraphComponent(), e
                        .getID(), e.getActionCommand()));
            }
        };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
    }

    private void showGraphPopupMenu(MouseEvent e)
    {
        Point pt = SwingUtilities.convertPoint(e.getComponent(), e.getPoint(),
                graphComponent);
        GraphPopupMenu menu = new GraphPopupMenu(this);
        menu.show(graphComponent, pt.x, pt.y);

        e.consume();
    }

}
