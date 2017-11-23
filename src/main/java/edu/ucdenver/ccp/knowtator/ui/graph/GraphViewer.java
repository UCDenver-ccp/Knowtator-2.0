package edu.ucdenver.ccp.knowtator.ui.graph;

import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxICell;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxEvent;
import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.Assertion;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.AssertionListener;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import other.GraphPopupMenu;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class GraphViewer extends JDialog implements AnnotationListener, AssertionListener {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(GraphViewer.class);

    private final BasicKnowtatorView view;
    private Object parent;
    private mxGraph graph;
    private mxGraphComponent graphComponent;


    public GraphViewer(JFrame frame, BasicKnowtatorView view) {
        super(frame, "Graph Viewer");
        this.view = view;
        setName("Graph Viewer");
        graph = new mxGraph();
        parent = graph.getDefaultParent();

        createGraphComponent();
        setupListeners();

        setVisible(false);
        setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        setSize(new Dimension(300, 300));
        setLocationRelativeTo(view);
    }

    private void setupListeners() {

        //Handle drag and drop
        //Adds the current selected object property as the edge value
        graph.addListener(mxEvent.ADD_CELLS, (sender, evt) -> {
            Object[] cells = (Object[])evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
                    if (((mxCell) cell).getValue().equals("")) {
                        OWLObjectProperty property = OWLAPIDataExtractor.getSelectedProperty(view);
                        if (property != null) {


                            // Set the value
                            String relationship = property.toString();
                            ((mxCell) cell).setValue(relationship);
                            ((mxCell) cell).setStyle("startArrow=dash;startSize=12;endArrow=block;labelBackgroundColor=#FFFFFF;");

                            mxICell source = ((mxCell) cell).getSource();
                            mxICell target = ((mxCell) cell).getTarget();

                            graph.insertEdge(parent, null, relationship, source, target);

                            Annotation sourceAnnotation = (Annotation) source.getValue();
                            Annotation targetAnnotation = (Annotation) target.getValue();
                            view.getTextViewer().getSelectedTextPane().getTextSource().getAnnotationManager()
                                    .addAssertion(sourceAnnotation, targetAnnotation, relationship);

                        } else {
                            graph.getModel().remove(cell);
                        }
                    }
                }
            }
        });

        graph.addListener(mxEvent.REMOVE_CELLS, (sender, evt) -> {
            Object[] cells = (Object[])evt.getProperty("cells");
            for (Object cell : cells) {
                if (graph.getModel().isEdge(cell)) {
                    Annotation sourceAnnotation = (Annotation)((mxCell) cell).getSource().getValue();
                    Annotation targetAnnotation = (Annotation)((mxCell) cell).getTarget().getValue();
                    view.getTextViewer().getSelectedTextPane().getTextSource().getAnnotationManager()
                            .removeAssertion(sourceAnnotation, targetAnnotation);
                    graph.getModel().remove(cell);
                }
            }
        });

        graphComponent.getGraphControl().addMouseListener(new MouseAdapter() {

            public void mousePressed(MouseEvent e) {
                // Handles context menu on the Mac where the trigger is on mousepressed
                mouseReleased(e);
            }

            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    showGraphPopupMenu(e);
                }
            }
        });
    }

    private void createGraphComponent() {

        graphComponent = new mxGraphComponent(graph);
        graphComponent.setDragEnabled(false);
        graphComponent.setPreferredSize(new Dimension(1200, 200));
        JScrollPane sp = new JScrollPane();
        graphComponent.getGraphControl().add(sp, 0);
        add(graphComponent);
    }

    public mxGraph getGraph() {
        return graph;
    }
    private mxGraphComponent getGraphComponent() {
        return graphComponent;
    }


    public Object addAnnotationNode(Annotation annotation) {
        graph.getModel().beginUpdate();
        Object node;
        try {
            node = graph.insertVertex(parent, annotation.getClassID(), annotation, 20, 20,
                    80, 30,
                    String.format(
                            "fillColor=#%s",
                            Integer.toHexString(
                                    annotation.getColor().getRGB()
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
        return node;
    }

    private void addAssertionEdge(Assertion assertion) {
        graph.getModel().beginUpdate();
        try {
            Object source =  addAnnotationNode(assertion.getSource());
            Object target =  addAnnotationNode(assertion.getTarget());
            graph.insertEdge(parent, null, assertion.getRelationship(), source, target, "startArrow=dash;startSize=12;endArrow=block;labelBackgroundColor=#FFFFFF;");
            mxHierarchicalLayout layout = new mxHierarchicalLayout(
                    graph);
            layout.setOrientation(SwingConstants.WEST);
            layout.execute(parent);
        } finally {
            graph.getModel().endUpdate();
        }
    }

    public Action bind(String name, final Action action) {
        AbstractAction newAction = new AbstractAction(name) {
            public void actionPerformed(ActionEvent e) {
                action.actionPerformed(new ActionEvent(getGraphComponent(), e.getID(), e.getActionCommand()));
            }
        };

        newAction.putValue(Action.SHORT_DESCRIPTION, action.getValue(Action.SHORT_DESCRIPTION));

        return newAction;
    }

    private void showGraphPopupMenu(MouseEvent e) {
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

//    public void readFromXml(String path) {
//        try {
//            Document doc = mxXmlUtils.parseXml(mxUtils.readFile(path));
//            mxCodec codec = new mxCodec(doc);
//            codec.decode(doc.getDocumentElement(), graph.getModel());
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
//    }

//    public void saveToXml(String path) {
//        try {
//            mxCodec codec = new mxCodec();
//            String xml = URLEncoder.encode(mxXmlUtils.getXml(codec.encode(graph.getModel())), "UTF-8");
//            BufferedWriter bw = new BufferedWriter(new FileWriter(path));
//            bw.write(xml);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    public void goToNode(Annotation annotation) {
        Object cellToGoTo = ((mxGraphModel)graph.getModel()).getCell(annotation.getClassID());
        if (cellToGoTo == null) {
            JOptionPane.showMessageDialog(null, "No node found in graph for current annotation");
            return;
        }
        requestFocusInWindow();
        graph.setSelectionCell(cellToGoTo);
    }

    @Override
    public void assertionAdded(Assertion assertion) {
        addAssertionEdge(assertion);
    }

}
