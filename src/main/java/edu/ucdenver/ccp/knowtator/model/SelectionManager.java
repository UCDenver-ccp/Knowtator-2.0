package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
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
	private boolean filterByProfile;
	private OWLClass selectedOWLClass;
	private int start;
	private int end;

	public SelectionManager(KnowtatorController knowtatorController) {
		controller = knowtatorController;
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
		TextSourceChangeEvent e = new TextSourceChangeEvent(this.activeTextSource, newTextSource);
		this.activeTextSource = newTextSource;
		setSelected(null, null);
		listeners.forEach(selectionListener -> selectionListener.activeTextSourceChanged(e));
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
		GraphSpaceChangeEvent e = new GraphSpaceChangeEvent(this.activeGraphSpace, newGraphSpace);
		this.activeGraphSpace = newGraphSpace;
		listeners.forEach(selectionListener -> selectionListener.activeGraphSpaceChanged(e));
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
		setSelected(
				activeTextSource
						.getAnnotationManager()
						.getGraphSpaceCollection()
						.getNext(activeGraphSpace));
	}

	public void getPreviousGraphSpace() {
		setSelected(
				activeTextSource
						.getAnnotationManager()
						.getGraphSpaceCollection()
						.getPrevious(activeGraphSpace));
	}

	public void getNextSpan() {
		setSelected(
				activeTextSource.getAnnotationManager().getAllSpanCollection().getNext(selectedSpan));
	}

	public void getPreviousSpan() {
		setSelected(
				activeTextSource.getAnnotationManager().getAllSpanCollection().getPrevious(selectedSpan));
	}

	public void getNextTextSource() {
		setSelected(
				controller.getTextSourceManager().getTextSourceCollection().getNext(activeTextSource));
	}

	public void getPreviousTextSource() {
		setSelected(
				controller.getTextSourceManager().getTextSourceCollection().getPrevious(activeTextSource));
	}
}
