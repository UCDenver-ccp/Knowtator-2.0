/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.view.info.FindPanel;
import edu.ucdenver.ccp.knowtator.view.info.InfoPanel;
import edu.ucdenver.ccp.knowtator.view.menus.*;
import edu.ucdenver.ccp.knowtator.view.text.TextPane;
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

    private static final Logger log = Logger.getLogger(KnowtatorManager.class);
    @SuppressWarnings("WeakerAccess")
    KnowtatorManager manager;
    private TextViewer textViewer;
    private InfoPanel infoPanel;
    private FindPanel findPanel;
    private ProjectMenu projectMenu;
    private ViewMenu viewMenu;
    private ProfileMenu profileMenu;
    private IAAMenu iaaMenu;
    private KnowtatorToolBar toolBar;

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
//        if (JOptionPane.showConfirmDialog(null, "Save changes to Knowtator project?") == JOptionPane.OK_OPTION) {
//            manager.getProjectManager().saveProject();
//        }
        for (TextPane textPane : textViewer.getAllTextPanes()) {
            textPane.getGraphDialog().setVisible(false);
            textPane.getGraphDialog().dispose();
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
        manager.getProjectManager().loadProject(file);
    }

    @Override
    public void initialiseClassView() {
        manager = new KnowtatorManager();
        manager.setUpOWL(getOWLWorkspace(), getOWLModelManager());

        textViewer = new TextViewer(manager, this);
        infoPanel = new InfoPanel(this);
        findPanel = new FindPanel(this);
        toolBar = new KnowtatorToolBar(this);

        projectMenu = new ProjectMenu(manager, this);
        viewMenu = new ViewMenu(this, manager);
        profileMenu = new ProfileMenu(manager);
        iaaMenu = new IAAMenu(manager);

        manager.addSpanListener(infoPanel);
        manager.addAnnotationListener(infoPanel);
        manager.addTextSourceListener(textViewer);
        manager.addProfileListener(profileMenu);

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        createUI();
        setupInitial();
    }

    private void setupInitial() {
//        File project = new File("E:/Documents/repos/Knowtator-2.0/src/test/resources/test_project/test_project.xml");
//        manager.loadProject(project);
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
}
