package edu.ucdenver.ccp.knowtator.view.graph;

import com.mxgraph.model.mxCell;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;
import org.apache.log4j.Logger;

import javax.swing.*;

class GraphEditorToolBar extends JToolBar {

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(GraphEditorToolBar.class);

    private GraphViewer graphViewer;
    private KnowtatorController controller;

    GraphEditorToolBar(GraphViewer graphViewer, KnowtatorController controller) {

        this.graphViewer = graphViewer;
        this.controller = controller;

        setFloatable(false);
        add(addGraphNodeCommand());
        add(removeCellCommand());
        //
    }

    private JButton removeCellCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE));
        button.setToolTipText("Remove vertex or edge");

        button.addActionListener(e -> graphViewer.removeSelectedCell());

        return button;
    }

    private JButton addGraphNodeCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.ADD));
        button.setToolTipText("Add annotation as node");

        button.addActionListener(e -> {
            Annotation annotation = controller.getSelectionManager().getSelectedAnnotation();
            mxCell vertex = controller.getSelectionManager().getActiveGraphSpace().addNode(null, annotation);

            graphViewer.goToVertex(vertex);
        });

        return button;
    }


}
