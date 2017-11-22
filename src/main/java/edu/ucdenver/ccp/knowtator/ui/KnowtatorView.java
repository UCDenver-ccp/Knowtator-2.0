package edu.ucdenver.ccp.knowtator.ui;

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

        JScrollPane infoPaneSP = new JScrollPane(infoPane);

        JSplitPane annotationSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        annotationSplitPane.setOneTouchExpandable(true);
        annotationSplitPane.setDividerLocation(800);

        annotationSplitPane.add(textViewer);
        annotationSplitPane.add(infoPaneSP);
        add(annotationSplitPane);


    }


    private void setupInitial() {
//        manager.getXmlUtil().read("file/test_project.xml", true);
    }

    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(fileMenu);
        menuBar.add(profileMenu);
        menuBar.add(iaaMenu);
        menuBar.add(graphMenu);
        menuBar.add(toolBar);

        add(menuBar, BorderLayout.NORTH);

    }

    public static void main(String[] args) {

    }
}
