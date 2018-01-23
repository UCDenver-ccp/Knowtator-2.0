package edu.ucdenver.ccp.knowtator.ui;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.actions.ProjectActions;
import edu.ucdenver.ccp.knowtator.annotation.*;
import edu.ucdenver.ccp.knowtator.listeners.*;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.info.FindPanel;
import edu.ucdenver.ccp.knowtator.ui.info.InfoPanel;
import edu.ucdenver.ccp.knowtator.ui.menus.*;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;
import edu.ucdenver.ccp.knowtator.ui.text.TextViewer;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.awt.dnd.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class BasicKnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {

    static final Logger log = Logger.getLogger(KnowtatorManager.class);
    TextViewer textViewer;
    InfoPanel infoPanel;
    FindPanel findPanel;


    ProjectMenu projectMenu;
    ViewMenu viewMenu;
    ProfileMenu profileMenu;
    IAAMenu iaaMenu;

    KnowtatorToolBar toolBar;

    private Set<TextSourceListener> textSourceListeners;
    private Set<ProfileListener> profileListeners;
    private Set<ConceptAnnotationListener> conceptAnnotationListeners;
    private Set<SpanListener> spanListeners;
    private Set<CompositionalAnnotationListener> compositionalAnnotationListeners;
    @SuppressWarnings("WeakerAccess")
    KnowtatorManager manager;

    @Override
    public void initialiseClassView() {

        manager = new KnowtatorManager();
        manager.getProfileManager().setView(this);
        manager.getTextSourceManager().setView(this);

        textViewer = new TextViewer(manager, this);
        infoPanel = new InfoPanel(this);
        findPanel = new FindPanel(this);
        toolBar = new KnowtatorToolBar(this);

        projectMenu = new ProjectMenu(manager);
        viewMenu = new ViewMenu(this);
        profileMenu = new ProfileMenu(manager, this);
        iaaMenu = new IAAMenu(manager);

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        initListeners();
    }

    private void initListeners() {
        textSourceListeners = new HashSet<>();
        profileListeners = new HashSet<>();
        conceptAnnotationListeners = new HashSet<>();
        spanListeners = new HashSet<>();
        compositionalAnnotationListeners = new HashSet<>();


        spanListeners.add(infoPanel);
        conceptAnnotationListeners.add(infoPanel);
        textSourceListeners.add(textViewer);
        profileListeners.add(profileMenu);
    }

    public void owlEntitySelectionChanged(OWLEntity owlEntity) {
        if (getView() != null) {
            if (getView().isSyncronizing()) {
                getOWLWorkspace().getOWLSelectionModel().setSelectedEntity(owlEntity);
            }
        }
    }

    // I want to keep this method in case I want to autoLoad ontologies eventually
    @SuppressWarnings("unused")
    void loadOntologyFromLocation(String ontologyLocation) {
        List<String> ontologies = getOWLModelManager().getActiveOntologies().stream().map(ontology -> {
            OWLOntologyID ontID = ontology.getOntologyID();
            //noinspection Guava
            Optional<IRI> ontIRI = ontID.getOntologyIRI();
            if(ontIRI.isPresent()) {
                return ontIRI.get().toURI().toString();
            } else {
                return null;
            }
        }).collect(Collectors.toList());

//        String ontologyLocation = OntologyTranslator.translate(classID);
        if (!ontologies.contains(ontologyLocation)) {
            try {
                OWLOntology newOntology = getOWLModelManager().getOWLOntologyManager().loadOntology((IRI.create(ontologyLocation)));
                getOWLModelManager().setActiveOntology(newOntology);
            } catch (OWLOntologyCreationException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {

        return selectedClass;
    }

    @Override
    public void disposeView() {
        if (JOptionPane.showConfirmDialog(null, "Save changes to Knowtator project?") == JOptionPane.OK_OPTION) {
            ProjectActions.saveProject(manager);
        }
        for(TextPane textPane : textViewer.getAllTextPanes()) {
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

    public void textSourceAddedEvent(TextSource textSource) {
        textSourceListeners.forEach(textSourceListener -> textSourceListener.textSourceAdded(textSource));
    }

    public void conceptAnnotationAddedEvent(ConceptAnnotation newAnnotation) {
        conceptAnnotationListeners.forEach(listener -> listener.annotationAdded(newAnnotation));
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
        if (selectedAnnotation instanceof ConceptAnnotation) conceptAnnotationListeners.forEach(listener -> listener.annotationSelectionChanged((ConceptAnnotation) selectedAnnotation));
    }

    public void annotationRemovedEvent(ConceptAnnotation removedAnnotation) {
        conceptAnnotationListeners.forEach(conceptAnnotationListener -> conceptAnnotationListener.annotationRemoved(removedAnnotation));
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

    public void compositionalAnnotationAddedEvent(CompositionalAnnotation compositionalAnnotation) {
        compositionalAnnotationListeners.forEach(compositionalAnnotationListener -> compositionalAnnotationListener.compositionalAnnotationAdded(compositionalAnnotation));
    }

    public void spanRemovedEvent() {
        spanListeners.forEach(SpanListener::spanRemoved);
    }

    public void addCompositionalAnnotationListener(CompositionalAnnotationListener listener) {
        compositionalAnnotationListeners.add(listener);
    }

    public void addConceptAnnotationListener(ConceptAnnotationListener listener) {
        conceptAnnotationListeners.add(listener);
    }

    public void colorChangedEvent() {
        profileListeners.forEach(ProfileListener::colorChanged);
    }

    public void addSpanListener(SpanListener listener) {
        spanListeners.add(listener);
    }

    public void addProfileListener(ProfileListener listener) {
        profileListeners.add(listener);
    }
}
