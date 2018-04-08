package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewer;
import edu.ucdenver.ccp.knowtator.view.info.FindPanel;
import edu.ucdenver.ccp.knowtator.view.info.InfoPanel;
import edu.ucdenver.ccp.knowtator.view.menus.ProjectMenu;
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

    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(KnowtatorController.class);

    private KnowtatorController controller;
    private TextViewer textViewer;
    private GraphViewer graphViewer;
    private InfoPanel infoPanel;
    private FindPanel findPanel;
    private ProjectMenu projectMenu;
    private JFrame frame;
    private File fileToLoad;

    @Override
    public void initialiseClassView() {
        setMinimumSize(new Dimension(50, 20));

        setFrame((JFrame) SwingUtilities.getWindowAncestor(this));

        controller = new KnowtatorController(this);
        controller.setUpOWL(getOWLWorkspace(), getOWLModelManager());

        textViewer = new TextViewer(controller);
        graphViewer = new GraphViewer(getFrame(), controller);
        infoPanel = new InfoPanel(controller);
        findPanel = new FindPanel(controller);

        projectMenu = new ProjectMenu(controller);

        controller.addSpanListener(infoPanel);
        controller.addAnnotationListener(infoPanel);

        controller.addTextSourceListener(textViewer);
        controller.addProjectListener(textViewer);
        controller.addSpanListener(textViewer);
        controller.addConceptAnnotationListener(textViewer);
        controller.addProfileListener(textViewer);

        controller.addProfileListener(graphViewer);
        controller.addGraphListener(graphViewer);
        controller.addTextSourceListener(graphViewer);

        getOWLModelManager().addOntologyChangeListener(controller.getTextSourceManager());

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        createUI();
        setupInitial();

        if (fileToLoad != null) {
            controller.getProjectManager().loadProject(fileToLoad);
        }
    }

    public void owlEntitySelectionChanged(OWLEntity owlEntity) {
        if (getView() != null) {
            if (getView().isSyncronizing()) {
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(owlEntity);
            }
        }
    }

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {

        return selectedClass;
    }

    @Override
    public void disposeView() {
        // For some reason, clicking yes here discards changes, even saved ones...
        if (JOptionPane.showConfirmDialog(controller.getView(), "Save changes to Knowtator project?", "Save Project", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
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
        this.fileToLoad = (file);
        textViewer.getTextPaneMap().clear();
        textViewer.removeAll();
        graphViewer.getGraphSpaceMap().clear();
        graphViewer.getDialog().removeAll();
        removeAll();
        repaint();
        initialiseClassView();
    }

    private JFrame getFrame() {
        return frame;
    }

    private void setFrame(JFrame frame) {
        this.frame = frame;
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

        add(menuBar, BorderLayout.NORTH);

    }

    public GraphViewer getGraphViewer() {
        return graphViewer;
    }
}
