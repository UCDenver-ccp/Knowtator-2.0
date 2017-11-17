package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.commands.*;
import edu.ucdenver.ccp.knowtator.ui.graph.KnowtatorGraphViewer;
import edu.ucdenver.ccp.knowtator.ui.info.InfoPane;
import edu.ucdenver.ccp.knowtator.ui.text.KnowtatorTextViewer;

import javax.swing.*;
import java.awt.*;

public class KnowtatorView extends BasicKnowtatorView {

    @Override
    public void initialiseClassView() {
        super.initialiseClassView();
        createUI();
        setupInitial();
    }

    public void createUI() {
        setLayout(new BorderLayout());

        /*
        Make the toolbar seen at the top of the view
         */
        createToolBar();

        /*
        Create a tabbed pane containing the text viewer for each document
         */
        textViewer = new KnowtatorTextViewer(manager);
        textViewer.setMinimumSize(new Dimension(100, 50));
        manager.getAnnotationListeners().add(textViewer);

        /*
        Create an info pane displaying info about the currently selected annotation
         */
        infoPane = new InfoPane(manager);
        infoPane.setMinimumSize(new Dimension(20, 50));
        manager.getAnnotationListeners().add(infoPane);

        JSplitPane annotationSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        annotationSplitPane.setOneTouchExpandable(true);
        annotationSplitPane.add(textViewer);
        annotationSplitPane.add(infoPane);
        annotationSplitPane.setDividerLocation(800);


        /*
        Create a viewer to see the annotations as a graph
         */
        graphViewer = new KnowtatorGraphViewer(manager);
        manager.getAnnotationListeners().add(graphViewer);


        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setOneTouchExpandable(true);

        add(mainSplitPane, BorderLayout.CENTER);
        mainSplitPane.add(annotationSplitPane);
        mainSplitPane.add(graphViewer);
        mainSplitPane.setDividerLocation(300);


    }


    public void setupInitial() {
        manager.getXmlUtil().read("file/test_project.xml", true);
    }

    /**
     * Add buttons corresponding to each of the actions to the toolbar
     */
    public void createToolBar() {
        ProjectCommands projectCommands = new ProjectCommands(manager);
        AnnotatorMenu annotatorCommands = new AnnotatorMenu(manager);
        GraphCommands graphCommands = new GraphCommands(manager);
        IAACommands iaaCommands = new IAACommands(manager);
        TextCommands textCommands = new TextCommands(manager);

        JMenuBar menuBar = new JMenuBar();

        menuBar.add(projectCommands.getFileMenu());

        AnnotatorMenu annotatorMenu = new AnnotatorMenu(manager);
        manager.addProfileListener(annotatorMenu);
        menuBar.add(annotatorMenu);

        add(menuBar, BorderLayout.NORTH);



//        addAction(projectCommands.getFileMenuCommand(), "A", "A");



//        addAction(textCommands.getIncreaseTextSizeCommand(), "B", "C");
//        addAction(textCommands.getDecreaseTextSizeCommand(), "B", "D");

        /*
        Text Annotation related actions
         */
//        addAction(projectCommands.getLoadAnnotationsCommand(), "C", "A");

//        addAction(iaaCommands.getRunIAACommand(), "C", "C");

//        addAction(textCommands.getIncrementSelectionLeftCommand(), "C", "F");
//        addAction(textCommands.getDecrementSelectionLeftCommand(), "C", "G");
//        addAction(textCommands.getDecrementSelectionRightCommand(), "C", "H");
//        addAction(textCommands.getIncrementSelectionRightCommand(), "C", "I");

        /*
        Profile and highlighter related commands
         */
//        addAction(annotatorCommands.getNewAnnotatorCommand(), "D", "A");
//        addAction(annotatorCommands.getSwitchProfileCommand(), "D", "B");
//        addAction(annotatorCommands.getRemoveAnnotatorCommand(), "D", "C");
//        addAction(annotatorCommands.getAssignHighlighterCommand(), "D", "D");

        /*
        Graph Viewer related commands
         */
//        addAction(graphCommands.getAddTextAnnotationNodeCommand(), "E", "A");
    }

    public static void main(String[] args) {

    }
}
