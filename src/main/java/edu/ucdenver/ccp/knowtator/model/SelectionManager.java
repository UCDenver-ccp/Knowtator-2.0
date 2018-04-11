package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
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
  private List<SelectionListener> listeners;

  private Annotation selectedAnnotation;
  private Span selectedSpan;
  private GraphSpace activeGraphSpace;
  private TextSource activeTextSource;
  private Profile activeProfile;
  private boolean filterByProfile;
  private OWLClass selectedOWLClass;
  private int start;
  private int end;

  public SelectionManager(KnowtatorController knowtatorController) {
    controller = knowtatorController;
    controller.getProjectManager().addListener(this);
    filterByProfile = false;
    listeners = new ArrayList<>();

    start = 0;
    end = 0;
  }

  public void addListener(SelectionListener listener) {
    listeners.add(listener);
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

  public void setSelected(Profile newProfile) {
    ProfileChangeEvent e = new ProfileChangeEvent(this.activeProfile, newProfile);
    this.activeProfile = newProfile;
    listeners.forEach(selectionListener -> selectionListener.activeProfileChange(e));
  }

  public int getEnd() {
    return end;
  }

  public void setEnd(int end) {
    this.end = end;
  }

  public KnowtatorController getController() {
    return controller;
  }

  public Annotation getSelectedAnnotation() {
    return selectedAnnotation;
  }

  public OWLClass getSelectedOWLClass() {
    return selectedOWLClass;
  }

  public void setSelectedOWLClass(OWLClass owlClass) {
    selectedOWLClass = owlClass;
  }

  boolean isFilterByProfile() {
    return filterByProfile;
  }

  public TextSource getActiveTextSource() {
    return activeTextSource;
  }

  public void setSelected(TextSource newTextSource) {
    if (controller.getProjectManager().isProjectLoaded()) {
      TextSourceChangeEvent e = new TextSourceChangeEvent(this.activeTextSource, newTextSource);
      this.activeTextSource = newTextSource;
      setSelected(null, null);
      setSelected(newTextSource.getAnnotationManager().getGraphSpaceCollection().getData().first());
      listeners.forEach(selectionListener -> selectionListener.activeTextSourceChanged(e));
    }
  }

  public Span getSelectedSpan() {
    return selectedSpan;
  }

  public void setSelected(Span newSpan) {
    SpanChangeEvent e = new SpanChangeEvent(selectedSpan, newSpan);

    this.selectedSpan = newSpan;
    if (newSpan != null) {
      setSelected(newSpan.getAnnotation(), newSpan);
    }

    listeners.forEach(listener -> listener.selectedSpanChanged(e));
  }

  public GraphSpace getActiveGraphSpace() {
    return activeGraphSpace;
  }

  public void setSelected(GraphSpace newGraphSpace) {
    if (controller.getProjectManager().isProjectLoaded()) {
      GraphSpaceChangeEvent e = new GraphSpaceChangeEvent(this.activeGraphSpace, newGraphSpace);
      this.activeGraphSpace = newGraphSpace;
      listeners.forEach(selectionListener -> selectionListener.activeGraphSpaceChanged(e));
    }
  }

  public void setSelected(Annotation newAnnotation, Span newSpan) {
    if (selectedAnnotation != newAnnotation) {
      AnnotationChangeEvent e = new AnnotationChangeEvent(this.selectedAnnotation, newAnnotation);
      selectedAnnotation = newAnnotation;
      if (selectedAnnotation != null) {
        setSelected(newSpan);
      } else if (activeGraphSpace != null) {
        activeGraphSpace.setSelectionCell(null);
      }
      listeners.forEach(selectionListener -> selectionListener.selectedAnnotationChanged(e));
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
      setSelected(
              activeTextSource
                      .getAnnotationManager()
                      .getGraphSpaceCollection()
                      .getNext(activeGraphSpace));
    }
  }

  public void getPreviousGraphSpace() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelected(
              activeTextSource
                      .getAnnotationManager()
                      .getGraphSpaceCollection()
                      .getPrevious(activeGraphSpace));
    }
  }

  public void getNextSpan() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelected(
              activeTextSource.getAnnotationManager().getAllSpanCollection().getNext(selectedSpan));
    }
  }

  public void getPreviousSpan() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelected(
              activeTextSource.getAnnotationManager().getAllSpanCollection().getPrevious(selectedSpan));
    }
  }

  public void getNextTextSource() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelected(
              controller.getTextSourceManager().getTextSourceCollection().getNext(activeTextSource));
    }
  }

  public void getPreviousTextSource() {
    if (controller.getProjectManager().isProjectLoaded()) {
      setSelected(
              controller
                      .getTextSourceManager()
                      .getTextSourceCollection()
                      .getPrevious(activeTextSource));
    }
  }

  public void setSelectedOWLProperty(OWLObjectProperty value) {
    listeners.forEach(selectionListener -> selectionListener.owlPropertyChangedEvent(value));
  }

  @Override
  public void projectClosed() {
  }

  @Override
  public void projectLoaded() {
    setSelected(controller.getTextSourceManager().getTextSourceCollection().getData().first());
  }
}
