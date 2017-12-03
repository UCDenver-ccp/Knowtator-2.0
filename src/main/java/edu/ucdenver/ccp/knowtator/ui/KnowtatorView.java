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

    private void setupInitial() {
//        loadOntologyFromLocation(new File("C:/Users/pielk/KnowtatorProjects/CPBS_7785/Nucleus2Cytosol_Transport_Project/CPBS7785_ontology_3.owl").toURI().toString());
//        ProjectActions.loadProject(manager, "C:/Users/pielk/KnowtatorProjects/CPBS_7785/Nucleus2Cytosol_Transport_Project/NBK26932.txt.xml", false);
//        ProjectActions.loadProject(manager, "file/test_project.xml", true);
    }

    private void createUI() {
        setLayout(new BorderLayout());

        createMenuBar();

        JScrollPane infoPanelSP = new JScrollPane(infoPanel);

        JSplitPane annotationSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        annotationSplitPane.setOneTouchExpandable(true);
        annotationSplitPane.setDividerLocation(650);

        JSplitPane infoSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        infoSplitPane.setDividerLocation(50);

        annotationSplitPane.add(textViewer, JSplitPane.LEFT);
        annotationSplitPane.add(infoSplitPane, JSplitPane.RIGHT);
        infoSplitPane.add(findPanel, JSplitPane.TOP);
        infoSplitPane.add(infoPanelSP, JSplitPane.BOTTOM);
        add(annotationSplitPane);


    }


    private void createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        menuBar.add(projectMenu);
        menuBar.add(viewMenu);
        menuBar.add(profileMenu);
        menuBar.add(iaaMenu);
        menuBar.add(toolBar);

        add(menuBar, BorderLayout.NORTH);

    }

    public static void main(String[] args) {

    }
}
