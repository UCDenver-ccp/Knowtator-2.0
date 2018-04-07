package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.view.info.FindPanel;
import edu.ucdenver.ccp.knowtator.view.info.InfoPanel;
import edu.ucdenver.ccp.knowtator.view.menus.IAAMenu;
import edu.ucdenver.ccp.knowtator.view.menus.ProfileMenu;
import edu.ucdenver.ccp.knowtator.view.menus.ProjectMenu;
import edu.ucdenver.ccp.knowtator.view.menus.ViewMenu;
import edu.ucdenver.ccp.knowtator.view.text.TextViewer;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.dnd.*;
import java.io.File;

public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {

    private static final Logger log = Logger.getLogger(KnowtatorController.class);

    private KnowtatorController controller;
    private TextViewer textViewer;
    private InfoPanel infoPanel;
    private FindPanel findPanel;
    private ProjectMenu projectMenu;
    private ViewMenu viewMenu;
    private ProfileMenu profileMenu;
    private IAAMenu iaaMenu;

    public void owlEntitySelectionChanged(OWLEntity owlEntity) {
        if (getView() != null) {
            if (getView().isSyncronizing()) {
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(owlEntity);
            }
        }
    }

    // I want to keep this method in case I want to autoLoad ontologies eventually


    @Override
    protected OWLClass updateView(OWLClass selectedClass) {

        return selectedClass;
    }

    @Override
    public void disposeView() {

        // For some reason, clicking yes here discards changes, even saved ones...
        if (JOptionPane.showConfirmDialog(null, "Save changes to Knowtator project?") == JOptionPane.OK_OPTION) {
            controller.getProjectManager().saveProject();
        }
    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {

    }

    @Override
    public void dragOver(DropTargetDragEvent e) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent e) {

    }

    @Override
    public void dragExit(DropTargetEvent e) {

    }

    @Override
    public void drop(DropTargetDropEvent e) {

    }

    public TextViewer getTextViewer() {
        return textViewer;
    }


    public void close(File file) {
        initialiseClassView();
        log.warn("3.a: " + file);
        this.controller.getProjectManager().loadProject(file);
    }

    @Override
    public void initialiseClassView() {
//        try {
//            UIManager.setLookAndFeel(new Plastic3DLookAndFeel());
//        } catch (UnsupportedLookAndFeelException e) {
//            e.printStackTrace();
//        }
//        UIManager.getLookAndFeelDefaults().put("ClassLoader", Plastic3DLookAndFeel.class.getClassLoader());
        controller = new KnowtatorController(this);
        controller.setUpOWL(getOWLWorkspace(), getOWLModelManager());

        textViewer = new TextViewer(controller, this);
        infoPanel = new InfoPanel(controller);
        findPanel = new FindPanel(this);

        projectMenu = new ProjectMenu(controller);
        viewMenu = new ViewMenu(controller);
        profileMenu = new ProfileMenu(controller);
        iaaMenu = new IAAMenu(controller);

        controller.addSpanListener(infoPanel);
        controller.addAnnotationListener(infoPanel);
        controller.addTextSourceListener(textViewer);
        controller.addProjectListener(textViewer);
        controller.addProfileListener(profileMenu);

        getOWLModelManager().addOntologyChangeListener(controller.getTextSourceManager());

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        createUI();
        setupInitial();
    }

    private void setupInitial() {
//        File project = new File("E:/Documents/repos/Knowtator-2.0/src/test/resources/test_project/test_project.knowtator");
//        controller.loadProject(project);
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

        add(menuBar, BorderLayout.NORTH);

    }
}
