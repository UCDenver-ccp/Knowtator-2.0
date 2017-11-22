package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;
import java.io.File;

public class GraphMenu extends JMenu {

    private KnowtatorManager manager;
    private BasicKnowtatorView view;

    public GraphMenu(KnowtatorManager manager, BasicKnowtatorView view) {
        super("Graph");
        this.manager = manager;
        this.view = view;

        add(showGraphViewerCommand());
        add(displayAnnotationAsNodeCommand());
        addSeparator();
        add(openGraphCommand());
        add(saveGraphCommand());

    }

    private JMenuItem showGraphViewerCommand() {
        JMenuItem menuItem = new JMenuItem("Show graph viewer");
        menuItem.addActionListener(e -> view.getGraphViewer().setVisible(true));

        return menuItem;
    }

    private JMenuItem displayAnnotationAsNodeCommand() {
        JMenuItem menuItem = new JMenuItem("Add annotation node to graph");
        menuItem.addActionListener(e -> {

            Annotation selectedAnnotation = view.getTextViewer().getSelectedTextPane().getSelectedAnnotation();
            if (selectedAnnotation != null) {
                view.getGraphViewer().addAnnotationNode(selectedAnnotation);
            }
        });

        return menuItem;
    }

    private JMenuItem openGraphCommand() {
        JMenuItem openGraphItem = new JMenuItem("Load graph from file");
        openGraphItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
            if(fileChooser.showOpenDialog(null ) == JFileChooser.APPROVE_OPTION) {
                view.getGraphViewer().readFromXml(fileChooser.getSelectedFile().getAbsolutePath());


            }
        });

        return openGraphItem;
    }

    private JMenuItem saveGraphCommand() {
        JMenuItem saveGraphItem = new JMenuItem("Save graph to file");
        saveGraphItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                view.getGraphViewer().saveToXml(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        return saveGraphItem;
    }

    //TODO: Go to annotation node
    //TODO: Show graph viewer/ hide graph viewer
    //TODO: Read/write graphs
}
