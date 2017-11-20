package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.ui.menus.*;
import edu.ucdenver.ccp.knowtator.ui.graph.GraphViewer;
import edu.ucdenver.ccp.knowtator.ui.info.InfoPane;
import edu.ucdenver.ccp.knowtator.ui.text.TextViewer;

import javax.swing.*;
import java.awt.*;

public class KnowtatorView extends BasicKnowtatorView {

    @Override
    public void initialiseClassView() {
        log.warn("************************Initializing Knowtator");
        super.initialiseClassView();
        createUI();
        setupInitial();

        log.warn("*************************Done initializing Knowtator");
    }

    private void createUI() {
        setLayout(new BorderLayout());

        createMenuBar();

        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        JSplitPane annotationSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        textViewer = new TextViewer(this);
        infoPane = new InfoPane(this);
        graphViewer = new GraphViewer(this);

        textViewer.setMinimumSize(new Dimension(100, 50));
        infoPane.setMinimumSize(new Dimension(20, 50));

        annotationSplitPane.setOneTouchExpandable(true);
        annotationSplitPane.setDividerLocation(800);

        annotationSplitPane.add(textViewer);
        annotationSplitPane.add(infoPane);
        mainSplitPane.add(annotationSplitPane);
        mainSplitPane.add(graphViewer);

        mainSplitPane.setOneTouchExpandable(true);
        add(mainSplitPane, BorderLayout.CENTER);
        mainSplitPane.setDividerLocation(300);


    }


    private void setupInitial() {
        manager.getXmlUtil().read("file/test_project.xml", true);
    }

    /**
     * Add buttons corresponding to each of the actions to the menus
     */
    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();


        FileMenu fileMenu = new FileMenu(manager);
        AnnotatorMenu annotatorMenu = new AnnotatorMenu(manager, this);
        IAAMenu iaaMenu = new IAAMenu(manager);
        GraphMenu graphMenu = new GraphMenu(this);

        KnowtatorToolBar toolBar = new KnowtatorToolBar(this);

        menuBar.add(fileMenu);
        menuBar.add(annotatorMenu);
        menuBar.add(iaaMenu);
        menuBar.add(graphMenu);
        menuBar.add(toolBar);

        add(menuBar, BorderLayout.NORTH);

    }

    public static void main(String[] args) {

    }
}
