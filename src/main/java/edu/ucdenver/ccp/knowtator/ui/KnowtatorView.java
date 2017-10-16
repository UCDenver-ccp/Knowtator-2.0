package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.Commands.*;

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
        Create a tabbed pane containing the text viewer for each document
         */
        textViewer = new KnowtatorTextViewer(manager);
        textViewer.setMinimumSize(new Dimension(100, 50));
        manager.getTextAnnotationListeners().add(textViewer);

        /*
        Create a viewer to see the annotations as a graph
         */
        graphViewer = new KnowtatorGraphViewer(manager);
        manager.getTextAnnotationListeners().add(graphViewer);


        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setOneTouchExpandable(true);
        add(mainSplitPane);
        mainSplitPane.add(textViewer);
        mainSplitPane.add(graphViewer);
        mainSplitPane.setDividerLocation(300);

        /*
        Make the toolbar seen at the top of the view
         */
        createToolBar();
    }

    /**
     * TODO make initial document the last document that was edited
     */
    public void setupInitial() {

        manager.getAnnotatorManager().addNewAnnotator("Default", "Default");



        /*
        An initial documents to display
         */
        textViewer.addNewDocument("/file/test_article.txt", true);
        textViewer.addNewDocument("/file/test_article2.txt", true);

        manager.getXmlUtil().read("/file/test_profile.xml", true);
//        manager.getXmlUtil().read("/file/test_annotations.xml", true);

    }

    /**
     * Add buttons corresponding to each of the actions to the toolbar
     */
    public void createToolBar() {
        DocumentCommands documentCommands = new DocumentCommands(manager);
        AnnotatorCommands annotatorCommands = new AnnotatorCommands(manager);
        GraphCommands graphCommands = new GraphCommands(manager);
        IAACommands iaaCommands = new IAACommands(manager);
        TextCommands textCommands = new TextCommands(manager);


        /*
        Text document related actions
         */
        addAction(documentCommands.openDocumentCommand(), "A", "A");
        addAction(documentCommands.getCloseDocumentCommand(), "A", "B");

        addAction(textCommands.getIncreaseTextSizeCommand(), "A", "C");
        addAction(textCommands.getDecreaseTextSizeCommand(), "A", "D");

        /*
        Text annotation related actions
         */
        addAction(documentCommands.getLoadTextAnnotationsCommand(), "B", "A");
        addAction(documentCommands.getSaveTextAnnotationsCommand(), "B", "B");

        addAction(iaaCommands.getRunIAACommand(), "B", "E");

        addAction(textCommands.getIncrementSelectionLeftCommand(), "B", "F");
        addAction(textCommands.getDecrementSelectionLeftCommand(), "B", "G");
        addAction(textCommands.getDecrementSelectionRightCommand(), "B", "H");
        addAction(textCommands.getIncrementSelectionRightCommand(), "B", "I");

        /*
        Annotator and highlighter related commands
         */
        addAction(annotatorCommands.getNewAnnotatorCommand(), "C", "A");
        addAction(annotatorCommands.getSwitchProfileCommand(), "C", "B");
        addAction(annotatorCommands.getAssignHighlighterCommand(), "C", "C");

        /*
        Graph Viewer related commands
         */
        addAction(graphCommands.getAddTextAnnotationNodeCommand(), "D", "A");
    }

    public static void main(String[] args) {

    }
}
