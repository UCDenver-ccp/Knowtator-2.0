package edu.ucdenver.cpbs.mechanic.ui;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.mxGraphOutline;
import com.mxgraph.util.mxResources;
import com.mxgraph.view.mxGraph;

import javax.swing.*;
import java.awt.*;

public class MechAnICGraphViewer extends JPanel {

    private final mxGraph graph;
    private JTabbedPane libraryPane;
    private mxGraphOutline graphOutline;
    private JLabel statusBar;
    private mxGraphComponent graphComponent;

    public MechAnICGraphViewer() {
        graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try
        {
            Object v1 = graph.insertVertex(parent, null, "Hello", 20, 20, 80,
                    30);
            Object v2 = graph.insertVertex(parent, null, "World!", 240, 150,
                    80, 30);
            graph.insertEdge(parent, null, "Edge", v1, v2);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        graphComponent = new mxGraphComponent(graph);
        add(graphComponent);
//
//        // Creates the graph outline component
//        graphOutline = new mxGraphOutline(graphComponent);
//
//        // Creates the library pane that contains the tabs with the palettes
//        libraryPane = new JTabbedPane();
//
//        // Creates the inner split pane that contains the library with the
//        // palettes and the graph outline on the left side of the window
//        JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
//        JSplitPane inner = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
//                libraryPane, graphOutline);
//        inner.setDividerLocation(320);
//        inner.setResizeWeight(1);
//        inner.setDividerSize(6);
//        inner.setBorder(null);
//
//        // Creates the outer split pane that contains the inner split pane and
//        // the graph component on the right side of the window
//        JSplitPane outer = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inner,
//                graphComponent);
//        outer.setOneTouchExpandable(true);
//        outer.setDividerLocation(200);
//        outer.setDividerSize(6);
//        outer.setBorder(null);
//
//        // Creates the status bar
//        statusBar = createStatusBar();
//
//        // Puts everything together
//        setLayout(new BorderLayout());
//        add(outer, BorderLayout.CENTER);
//        add(statusBar, BorderLayout.SOUTH);

    }

    private JLabel createStatusBar()
    {
        JLabel statusBar = new JLabel(mxResources.get("ready"));
        statusBar.setBorder(BorderFactory.createEmptyBorder(2, 4, 2, 4));

        return statusBar;
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame();
        MechAnICGraphViewer viewer = new MechAnICGraphViewer();
        frame.add(viewer);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 320);
        frame.setVisible(true);
    }
}
