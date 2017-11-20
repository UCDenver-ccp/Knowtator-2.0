package edu.ucdenver.ccp.knowtator.ui.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorView;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import other.GraphPopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class GraphViewer extends JPanel implements AnnotationListener {

    private Object parent;

    public mxGraph getGraph() {
        return graph;
    }

    private mxGraphComponent getGraphComponent() {
        return graphComponent;
    }

    private mxGraph graph;

    private mxGraphComponent graphComponent;

    public GraphViewer(KnowtatorView view) {
        view.addAnnotationListener(this);
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
                    OWLObjectProperty relationship = OWLAPIDataExtractor.getSelectedProperty(view);
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

    public void addAnnotationNode(Annotation annotation) {
        Profile profile = annotation.getAnnotator();
        String className = annotation.getClassName();
        graph.getModel().beginUpdate();
        try {
            graph.insertVertex(parent, null, annotation, 20, 20,
                    80, 30,
                    String.format(
                            "fillColor=#%s",
                            Integer.toHexString(
                                    profile.getColor(className).getRGB()
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

    public Action bind(String name, final Action action)
    {
        return bind(name, action, null);
    }

    public Action bind(String name, final Action action, String iconUrl)
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

    @Override
    public void annotationAdded(Annotation newAnnotation) {

    }

    @Override
    public void annotationRemoved() {

    }

    @Override
    public void annotationSelectionChanged(Annotation annotation) {

    }
}