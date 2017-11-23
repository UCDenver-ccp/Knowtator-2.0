package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.Assertion;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.listeners.*;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.graph.GraphViewer;
import edu.ucdenver.ccp.knowtator.ui.info.InfoPane;
import edu.ucdenver.ccp.knowtator.ui.menus.*;
import edu.ucdenver.ccp.knowtator.ui.text.TextViewer;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.dnd.*;
import java.util.HashSet;
import java.util.Set;

public class BasicKnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {

    static final Logger log = Logger.getLogger(KnowtatorManager.class);
    TextViewer textViewer;
    private GraphViewer graphViewer;
    InfoPane infoPane;

    FileMenu fileMenu;
    ProfileMenu profileMenu;
    AnnotationMenu annotationMenu;
    IAAMenu iaaMenu;
    GraphMenu graphMenu;

    KnowtatorToolBar toolBar = new KnowtatorToolBar(this);

    private Set<TextSourceListener> textSourceListeners;
    private Set<ProfileListener> profileListeners;
    private Set<AnnotationListener> annotationListeners;
    private Set<SpanListener> spanListeners;
    private Set<AssertionListener> assertionListeners;
    private KnowtatorManager manager;

    @Override
    public void initialiseClassView() {

        manager = new KnowtatorManager();
        manager.getProfileManager().setView(this);
        manager.getTextSourceManager().setView(this);

        textViewer = new TextViewer(manager, this);
        infoPane = new InfoPane(this);
        graphViewer = new GraphViewer((JFrame)SwingUtilities.getWindowAncestor(this), this);

        fileMenu = new FileMenu(manager);
        profileMenu = new ProfileMenu(manager, this);
        annotationMenu = new AnnotationMenu(this);
        iaaMenu = new IAAMenu(manager);
        graphMenu = new GraphMenu(this);

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        initListeners();
    }

    private void initListeners() {
        textSourceListeners = new HashSet<>();
        profileListeners = new HashSet<>();
        annotationListeners = new HashSet<>();
        spanListeners = new HashSet<>();
        assertionListeners = new HashSet<>();

        spanListeners.add(textViewer);
        spanListeners.add(infoPane);
        annotationListeners.add(textViewer);
        annotationListeners.add(graphViewer);
        annotationListeners.add(infoPane);
        textSourceListeners.add(textViewer);
        profileListeners.add(profileMenu);
        profileListeners.add(textViewer);
        assertionListeners.add(graphViewer);
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
        if (manager.getConfigProperties().getLastWritePath() != null) {
            manager.getXmlUtil().write(manager.getConfigProperties().getLastWritePath());
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

    public GraphViewer getGraphViewer() {
        return graphViewer;
    }

    public void textSourceAddedEvent(TextSource textSource) {
        textSourceListeners.forEach(textSourceListener -> textSourceListener.textSourceAdded(textSource));
    }

    public void annotationAddedEvent(Annotation newAnnotation) {
        annotationListeners.forEach(listener -> listener.annotationAdded(newAnnotation));
    }
    public void profileAddedEvent(Profile profile) {
        profileListeners.forEach(profileListener -> profileListener.profileAdded(profile));
    }

    public void spanSelectionChangedEvent(Span span) {
        spanListeners.forEach(spanListener -> spanListener.spanSelectionChanged(span));
    }
    public void profileSelectionChangedEvent(Profile profile) {
        profileListeners.forEach(profileListener -> profileListener.profileSelectionChanged(profile));
    }
    public void annotationSelectionChangedEvent(Annotation selectedAnnotation) {
        annotationListeners.forEach(listener -> listener.annotationSelectionChanged(selectedAnnotation));
    }

    public void annotationRemovedEvent() {
        annotationListeners.forEach(AnnotationListener::annotationRemoved);
    }
    public void profileRemovedEvent() {
        profileListeners.forEach(ProfileListener::profileRemoved);
    }


    public void profileFilterEvent(boolean filterByProfile) {
        profileListeners.forEach(profileListener -> profileListener.profileFilterSelectionChanged(filterByProfile));
    }

    public void spanAddedEvent(Span newSpan) {
        spanListeners.forEach(spanListener -> spanListener.spanAdded(newSpan));
    }

    public void assertionAddedEvent(Assertion assertion) {
        assertionListeners.forEach(assertionListener -> assertionListener.assertionAdded(assertion));
    }
}
