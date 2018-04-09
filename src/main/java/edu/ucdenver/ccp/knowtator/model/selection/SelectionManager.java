package edu.ucdenver.ccp.knowtator.model.selection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.ArrayList;
import java.util.List;

public class SelectionManager implements CaretListener, ChangeListener {
    private KnowtatorController controller;
    private List<SelectionListener> listeners;

    private Annotation selectedAnnotation;
    private Span selectedSpan;
    private GraphSpace activeGraphSpace;
    private TextSource activeTextSource;
    private Profile activeProfile;
    private boolean focusOnSelectedSpan;
    private boolean filterByProfile;
    private OWLClass selectedOWLClass;
    private int start;
    private int end;

    public SelectionManager(KnowtatorController knowtatorController) {
        controller = knowtatorController;
        focusOnSelectedSpan = false;
        filterByProfile = false;
        listeners = new ArrayList<>();

        start = 0;
        end = 0;
    }

    public void addSelectionListener(SelectionListener selectionListener) {
        listeners.add(selectionListener);
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

    public int getEnd() {
        return end;
    }
    public KnowtatorController getController() {
        return controller;
    }
    public boolean isFocusOnSelectedSpan() {
        return focusOnSelectedSpan;
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
    }

    public boolean isFilterByProfile() {
        return filterByProfile;
    }

    public TextSource getActiveTextSource() {
        return activeTextSource;
    }

    public void setActiveTextSource(TextSource textSource) {
        if (activeTextSource != null) {
            activeTextSource.getAnnotationManager().getGraphSpaces().removeAllListeners();
        }
        this.activeTextSource = textSource;
        setSelectedAnnotation(null, null);
        listeners.forEach(SelectionListener::activeTextSourceChanged);
    }

    public Span getSelectedSpan() {
        return selectedSpan;
    }

    public void setSelectedSpan(Span span) {
        this.selectedSpan = span;
        if (span != null) {
            focusOnSelectedSpan = true;
            setSelectedAnnotation(span.getAnnotation(), span);
        }
        listeners.forEach(SelectionListener::selectedSpanChanged);
    }

    public GraphSpace getActiveGraphSpace() {
        return activeGraphSpace;
    }

    public void setActiveGraphSpace(GraphSpace activeGraphSpace) {
        this.activeGraphSpace = activeGraphSpace;
        listeners.forEach(SelectionListener::activeGraphSpaceChanged);
    }

    public void setActiveProfile(Profile profile) {
        this.activeProfile = profile;
        listeners.forEach(SelectionListener::currentProfileChange);
    }

    public void setFocusOnSelectedSpan(boolean focusOnSelectedSpan) {
        this.focusOnSelectedSpan = focusOnSelectedSpan;
    }

    public void setSelectedAnnotation(Annotation annotation, Span span) {
        if (selectedAnnotation != annotation) {
            selectedAnnotation = annotation;
            if (selectedAnnotation != null) {
                setSelectedSpan(span);
            } else if (activeGraphSpace != null) {
                activeGraphSpace.setSelectionCell(null);
            }
            listeners.forEach(SelectionListener::selectedAnnotationChanged);
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
}
