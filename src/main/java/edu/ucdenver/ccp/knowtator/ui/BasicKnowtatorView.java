package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceListener;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.graph.GraphViewer;
import edu.ucdenver.ccp.knowtator.ui.info.InfoPane;
import edu.ucdenver.ccp.knowtator.ui.text.TextViewer;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.dnd.*;
import java.util.ArrayList;
import java.util.List;

public class BasicKnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {

    static final Logger log = Logger.getLogger(KnowtatorManager.class);
    KnowtatorManager manager;
    TextViewer textViewer;
    GraphViewer graphViewer;
    InfoPane infoPane;

    private List<TextSourceListener> textSourceListeners;
    private List<ProfileListener> profileListeners;
    private List<AnnotationListener> annotationListeners;
    private List<SpanListener> spanListeners;

    @Override
    public void initialiseClassView() {
        initListeners();
        manager = new KnowtatorManager();
        manager.getProfileManager().setView(this);
        manager.getTextSourceManager().setView(this);

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);
    }

    private void initListeners() {
        textSourceListeners = new ArrayList<>();
        profileListeners = new ArrayList<>();
        annotationListeners = new ArrayList<>();
        spanListeners = new ArrayList<>();
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

    public void addTextSourceListener(TextSourceListener listener) {
        textSourceListeners.add(listener);
    }

    public void addAnnotationListener(AnnotationListener listener) {
        annotationListeners.add(listener);
    }

    public void addSpanListener(SpanListener listener) {
        spanListeners.add(listener);
    }

    public void addProfileListener(ProfileListener listener) {
        profileListeners.add(listener);
    }

    public void annotationAddedEvent(Annotation newAnnotation) {
        annotationListeners.forEach(listener -> listener.annotationAdded(newAnnotation));
    }

    public void spanSelectionChangedEvent(Span span) {
        spanListeners.forEach(spanListener -> spanListener.spanSelectionChanged(span));
    }

    public void annotationRemovedEvent() {
        annotationListeners.forEach(AnnotationListener::annotationRemoved);
    }

    public void annotationSelectionChangedEvent(Annotation selectedAnnotation) {
        annotationListeners.forEach(listener -> listener.annotationSelectionChanged(selectedAnnotation));
    }

    public void profileAddedEvent(Profile profile) {
        profileListeners.forEach(profileListener -> profileListener.profileAdded(profile));
    }

    public void profileSelectionChangedEvent(Profile profile) {
        profileListeners.forEach(profileListener -> profileListener.profileSelectionChanged(profile));
    }

    public void profileRemovedEvent() {
        profileListeners.forEach(ProfileListener::profileRemoved);
    }
}
