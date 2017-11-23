package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;

public class GraphMenu extends JMenu {

    private BasicKnowtatorView view;

    public GraphMenu(BasicKnowtatorView view) {
        super("Graph");
        this.view = view;

        add(showGraphViewerCommand());
        addSeparator();
        add(displayAnnotationAsNodeCommand());
        add(goToAnnotationInGraphCommand());
        addSeparator();
//        add(openGraphCommand());
//        add(saveGraphCommand());

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

//    private JMenuItem openGraphCommand() {
//        JMenuItem openGraphItem = new JMenuItem("Load graph from file");
//        openGraphItem.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser();
//            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
//            if(fileChooser.showOpenDialog(null ) == JFileChooser.APPROVE_OPTION) {
//                view.getGraphViewer().readFromXml(fileChooser.getSelectedFile().getAbsolutePath());
//
//
//            }
//        });
//
//        return openGraphItem;
//    }

//    private JMenuItem saveGraphCommand() {
//        JMenuItem saveGraphItem = new JMenuItem("Save graph to file");
//        saveGraphItem.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser();
//            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
//            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
//                view.getGraphViewer().saveToXml(fileChooser.getSelectedFile().getAbsolutePath());
//            }
//        });
//
//        return saveGraphItem;
//    }

    private JMenuItem goToAnnotationInGraphCommand() {
        JMenuItem goToAnnotationInGraph = new JMenuItem("Go to annotation in graph");
        goToAnnotationInGraph.addActionListener(e -> view.getGraphViewer().goToNode(view.getTextViewer().getSelectedTextPane().getSelectedAnnotation()));

        return goToAnnotationInGraph;
    }
}
