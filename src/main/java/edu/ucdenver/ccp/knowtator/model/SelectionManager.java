package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.*;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class SelectionManager implements CaretListener, ChangeListener, ProjectListener {
  private KnowtatorController controller;
  private Annotation selectedAnnotation;
  private Span selectedSpan;
  private GraphSpace activeGraphSpace;
  private TextSource activeTextSource;
  private Profile activeProfile;
  private boolean filterByProfile;
  private OWLClass selectedOWLClass;
  private int start;
  private int end;
  private List<TextSourceSelectionListener> textSourceListeners;
  private List<AnnotationSelectionListener> annotationListeners;
  private List<SpanSelectionListener> spanListeners;
  private List<ProfileSelectionListener> profileListeners;
  private List<OWLClassSelectionListener> owlClassListeners;
  private List<OWLObjectPropertySelectionListener> owlObjectPropertyListeners;
  private List<GraphSpaceSelectionListener> graphSpaceListeners;
  private OWLObjectProperty selectedOWLObjectProperty;

  public SelectionManager(KnowtatorController knowtatorController) {
    controller = knowtatorController;
    controller.getProjectManager().addListener(this);
    filterByProfile = false;
    textSourceListeners = new ArrayList<>();
    annotationListeners = new ArrayList<>();
    spanListeners = new ArrayList<>();
    profileListeners = new ArrayList<>();
    owlClassListeners = new ArrayList<>();
    owlObjectPropertyListeners = new ArrayList<>();
    graphSpaceListeners = new ArrayList<>();

    start = 0;
    end = 0;
  }

  public int getStart() {
    return start;
  }

  public void setStart(int start) {
    this.start = start;
  }

  public Profile getActiveProfile() {
    return activeProfile;
  }

  public void setSelectedProfile(Profile newProfile) {
    ProfileChangeEvent e = new ProfileChangeEvent(this.activeProfile, newProfile);
    this.activeProfile = newProfile;
    profileListeners.forEach(selectionListener -> selectionListener.activeProfileChange(e));
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }

  public Annotation getSelectedAnnotation() {
    return selectedAnnotation;
  }

  public OWLClass getSelectedOWLClass() {
    return selectedOWLClass;
  }

  public void setSelectedOWLClass(OWLClass owlClass) {
    selectedOWLClass = owlClass;
    owlClassListeners.forEach(listener -> listener.owlClassChanged(owlClass));
  }

  boolean isFilterByProfile() {
    return filterByProfile;
  }

  public TextSource getActiveTextSource() {
    return activeTextSource;
  }

  public void setSelectedTextSource(TextSource newTextSource) {
    if (controller.getProjectManager().isProjectLoaded()) {
      TextSourceChangeEvent e = new TextSourceChangeEvent(this.activeTextSource, newTextSource);
      this.activeTextSource = newTextSource;
      setSelectedAnnotation(null, null);
      if (!newTextSource.getAnnotationManager().getGraphSpaceCollection().getCollection().isEmpty()) {
        setSelectedGraphSpace(
                newTextSource.getAnnotationManager().getGraphSpaceCollection().getCollection().first());
      }
      textSourceListeners.forEach(selectionListener -> selectionListener.activeTextSourceChanged(e));
    }
  }

  public Span getSelectedSpan() {
    return selectedSpan;
  }

  public void setSelectedSpan(Span newSpan) {
    SpanChangeEvent e = new SpanChangeEvent(selectedSpan, newSpan);

    this.selectedSpan = newSpan;
    if (newSpan != null) {
      setSelectedAnnotation(newSpan.getAnnotation(), newSpan);
    }

    spanListeners.forEach(listener -> listener.selectedSpanChanged(e));
  }

  public GraphSpace getActiveGraphSpace() {
    return activeGraphSpace;
  }

  public void setSelectedGraphSpace(GraphSpace newGraphSpace) {
    if (controller.getProjectManager().isProjectLoaded()) {
      GraphSpaceChangeEvent e = new GraphSpaceChangeEvent(this.activeGraphSpace, newGraphSpace);
      this.activeGraphSpace = newGraphSpace;
      graphSpaceListeners.forEach(selectionListener -> selectionListener.activeGraphSpaceChanged(e));
    }
  }

  public void setSelectedAnnotation(Annotation newAnnotation, Span newSpan) {
    if (selectedAnnotation != newAnnotation) {
      AnnotationChangeEvent e = new AnnotationChangeEvent(this.selectedAnnotation, newAnnotation);
      selectedAnnotation = newAnnotation;
      if (selectedAnnotation != null) {
        setSelectedSpan(newSpan);
      } else if (activeGraphSpace != null) {
        activeGraphSpace.setSelectionCell(null);
      }
      annotationListeners.forEach(selectionListener -> selectionListener.selectedAnnotationChanged(e));
    }
  }

  @Override
  public void caretUpdate(CaretEvent e) {
    setStart(Math.min(e.getDot(), e.getMark()));
    setEnd(Math.max(e.getDot(), e.getMark()));
  }

  @Override
  public void stateChanged(ChangeEvent e) {
    filterByProfile = ((JCheckBox) e.getSource()).isSelected();
  }

  public void getNextGraphSpace() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedGraphSpace(
              activeTextSource
                      .getAnnotationManager()
                      .getGraphSpaceCollection()
                      .getNext(activeGraphSpace));
    }
  }

  public void getPreviousGraphSpace() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedGraphSpace(
              activeTextSource
                      .getAnnotationManager()
                      .getGraphSpaceCollection()
                      .getPrevious(activeGraphSpace));
    }
  }

  public void getNextSpan() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedSpan(
              activeTextSource.getAnnotationManager().getAllSpanCollection().getNext(selectedSpan));
    }
  }

  public void getPreviousSpan() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedSpan(
              activeTextSource.getAnnotationManager().getAllSpanCollection().getPrevious(selectedSpan));
    }
  }

  public void getNextTextSource() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedTextSource(
              controller.getTextSourceManager().getTextSourceCollection().getNext(activeTextSource));
    }
  }

  public void getPreviousTextSource() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelectedTextSource(
              controller
                      .getTextSourceManager()
                      .getTextSourceCollection()
                      .getPrevious(activeTextSource));
    }
  }

  public void setSelectedOWLObjectProperty(OWLObjectProperty owlObjectProperty) {
    this.selectedOWLObjectProperty = owlObjectProperty;
    owlObjectPropertyListeners.forEach(selectionListener -> selectionListener.owlObjectPropertyChanged(owlObjectProperty));
  }

  @Override
  public void projectClosed() {
  }

  @Override
  public void projectLoaded() {
    setSelectedTextSource(controller.getTextSourceManager().getTextSourceCollection().getCollection().first());
  }

  public void addTextSourceListener(TextSourceSelectionListener listener) {
    textSourceListeners.add(listener);
  }

  public void addProfileListener(ProfileSelectionListener listener) {
    profileListeners.add(listener);
  }

  public void addAnnotationListener(AnnotationSelectionListener listener) {
    annotationListeners.add(listener);
  }

  public void addSpanListener(SpanSelectionListener listener) {
    spanListeners.add(listener);
  }

  public void addGraphSpaceListener(GraphSpaceSelectionListener listener) {
    graphSpaceListeners.add(listener);
  }

  public void addOWLClassListener(OWLClassSelectionListener listener) {
    owlClassListeners.add(listener);
  }

  public void addOWLObjectPropertyListener(OWLObjectPropertySelectionListener listener) {
    owlObjectPropertyListeners.add(listener);
  }

  public OWLObjectProperty getSelectedOWLObjectProperty() {
    return selectedOWLObjectProperty;
  }
}
