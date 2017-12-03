package edu.ucdenver.ccp.knowtator.ui.text;


import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.listeners.ConceptAnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.graph.GraphDialog;
import edu.ucdenver.ccp.knowtator.ui.menus.AnnotationPopupMenu;
import org.apache.log4j.Logger;
import other.RectanglePainter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Map;
import java.util.TreeMap;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TextPane extends JTextPane implements ConceptAnnotationListener, SpanListener, ProfileListener {

	public static final Logger log = Logger.getLogger(KnowtatorManager.class);
	private final GraphDialog graphDialog;

	private KnowtatorManager manager;
	private BasicKnowtatorView view;
	private TextSource textSource;
	private Span selectedSpan;
	private ConceptAnnotation selectedAnnotation;
	private boolean filterByProfile;
	private boolean focusOnSelectedSpan;

	TextPane(KnowtatorManager manager, BasicKnowtatorView view, TextSource textSource) {
		super();
		this.manager = manager;
		this.view = view;
		this.textSource = textSource;

		this.setEditable(false);

		filterByProfile = false;
		focusOnSelectedSpan = false;

		graphDialog = new GraphDialog(manager, (JFrame)SwingUtilities.getWindowAncestor(view), view, textSource);

		view.addSpanListener(this);
		view.addConceptAnnotationListener(this);
		view.addProfileListener(this);
		view.addCompositionalAnnotationListener(graphDialog);
		view.addConceptAnnotationListener(graphDialog);
		view.addProfileListener(graphDialog);

		setupListeners();
		requestFocusInWindow();
		select(0, 0);
	}

	private void setupListeners() {
		addMouseListener(
				new MouseListener() {
					int press_offset;

					@Override
					public void mousePressed(MouseEvent e) {
						press_offset = viewToModel(e.getPoint());
					}

					@Override
					public void mouseReleased(MouseEvent e) { handleMouseRelease(e, press_offset, viewToModel(e.getPoint())); }

					@Override
					public void mouseEntered(MouseEvent e) {

					}

					@Override
					public void mouseExited(MouseEvent e) {

					}

					@Override
					public void mouseClicked(MouseEvent e) {

					}
				}
		);
	}

	private void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
		AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, view, this);

		Map<Span, ConceptAnnotation> spansContainingLocation = textSource.getAnnotationManager().getSpanMap(press_offset, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);



		if (SwingUtilities.isRightMouseButton(e)) {
			if (spansContainingLocation.size() == 1) {
				Map.Entry<Span, ConceptAnnotation> entry = spansContainingLocation.entrySet().iterator().next();
				setSelection(entry.getKey(), entry.getValue());
			}
			popupMenu.showPopUpMenu(release_offset);
		} else if (press_offset == release_offset) {
			if (spansContainingLocation.size() == 1) {
				Map.Entry<Span, ConceptAnnotation> entry = spansContainingLocation.entrySet().iterator().next();
				setSelection(entry.getKey(), entry.getValue());
			}else if (spansContainingLocation.size() > 1) {
				popupMenu.chooseAnnotation(spansContainingLocation);
			}

		} else {
			setSelectionAtWordLimits(press_offset, release_offset);
		}
	}

	private void setSelectedSpan(Span span) {
		selectedSpan = span;
		focusOnSelectedSpan = true;
		view.spanSelectionChangedEvent(selectedSpan);
	}

	private void setSelectedAnnotation(ConceptAnnotation annotation) {
		if (selectedAnnotation != annotation) {
			selectedAnnotation = annotation;

			if (selectedAnnotation != null) {
				view.owlEntitySelectionChanged(OWLAPIDataExtractor.getOWLClassByID(view, selectedAnnotation.getClassID()));
				if (annotation.getSpans().size() == 1) {
					setSelectedSpan(annotation.getSpans().first());
				}
			}
			view.annotationSelectionChangedEvent(selectedAnnotation);
		}
		if (selectedAnnotation != null) {
			view.owlEntitySelectionChanged(OWLAPIDataExtractor.getOWLClassByID(view, selectedAnnotation.getClassID()));
		}
	}

	public void setSelection(Span span, ConceptAnnotation annotation) {
		setSelectedSpan(span);

		setSelectedAnnotation(annotation);
	}

	private void setSelectionAtWordLimits(int press_offset, int release_offset) {

		try {
			int start = Utilities.getWordStart(this, min(press_offset, release_offset));
			int end = Utilities.getWordEnd(this, max(press_offset, release_offset));

			focusOnSelectedSpan = false;
			select(start, end);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}


	}

	public TextSource getTextSource() {
		return textSource;
	}

	public ConceptAnnotation getSelectedAnnotation() {
		return selectedAnnotation;
	}

	public void previousSpan() {
		TreeMap<Span, ConceptAnnotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);
		Map.Entry<Span, ConceptAnnotation> previous =
				annotationMap.containsKey(selectedSpan) ? annotationMap.lowerEntry(selectedSpan) : annotationMap.floorEntry(selectedSpan);

		if (previous == null) previous = annotationMap.lastEntry();

		setSelection(previous.getKey(), previous.getValue());
	}

	public void nextSpan() {
		TreeMap<Span, ConceptAnnotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);

		Map.Entry<Span, ConceptAnnotation> next =
				annotationMap.containsKey(selectedSpan) ? annotationMap.higherEntry(selectedSpan) : annotationMap.ceilingEntry(selectedSpan);

		if (next == null) next = annotationMap.firstEntry();

		setSelection(next.getKey(), next.getValue());
	}

	public void shrinkSelectionEnd() {
		if (focusOnSelectedSpan) {
			textSource.getAnnotationManager().shrinkSpanEnd(selectedSpan);
			setSelectedSpan(selectedSpan);
		} else {
			requestFocusInWindow();
			select(getSelectionStart(), getSelectionEnd() - 1);
		}

	}
	public void shrinkSelectionStart() {
		if (focusOnSelectedSpan) {
			textSource.getAnnotationManager().shrinkSpanStart(selectedSpan);
			setSelectedSpan(selectedSpan);
		} else {
			requestFocusInWindow();
			select(getSelectionStart() + 1, getSelectionEnd());
		}
	}
	public void growSelectionEnd() {
		if (focusOnSelectedSpan) {
			textSource.getAnnotationManager().growSpanEnd(selectedSpan, getText().length());
			setSelectedSpan(selectedSpan);
		} else {
			requestFocusInWindow();
			select(getSelectionStart(), getSelectionEnd() + 1);
		}
	}
	public void growSelectionStart() {
		if (focusOnSelectedSpan) {
			textSource.getAnnotationManager().growSpanStart(selectedSpan);
			setSelectedSpan(selectedSpan);
		} else {
			requestFocusInWindow();
			select(getSelectionStart() - 1, getSelectionEnd());
		}
	}

	private void setFilterByProfile(boolean filterByProfile) {
		this.filterByProfile = filterByProfile;
		setSelection(null, null);
	}

	void refreshHighlights() {

		//Remove all previous highlights in case a span has been deleted
		getHighlighter().removeAllHighlights();

		//Always highlight the selected annotation first so its color and border show up
		highlightSelectedAnnotation();

		// Highlight overlaps first, then spans
		Span lastSpan = Span.makeDefaultSpan();
		Color lastColor = null;

		Map<Span, ConceptAnnotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);
		for (Map.Entry<Span, ConceptAnnotation> entry : annotationMap.entrySet()) {
			Span span = entry.getKey();
			ConceptAnnotation annotation = entry.getValue();
			if (span.intersects(lastSpan)) {
				try {
					getHighlighter().addHighlight(span.getStart(), min(span.getEnd(), lastSpan.getEnd()), new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}
			if (span.getEnd() > lastSpan.getEnd()) {
				try {
					getHighlighter().addHighlight(lastSpan.getStart(), lastSpan.getEnd(), new DefaultHighlighter.DefaultHighlightPainter(lastColor));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
				lastSpan = span;
				lastColor = annotation.getColor();
			}
		}

		// Highlight remaining span
		try {
			getHighlighter().addHighlight(lastSpan.getStart(), lastSpan.getEnd(), new DefaultHighlighter.DefaultHighlightPainter(lastColor));
		} catch (BadLocationException e) {
			e.printStackTrace();
		}

		revalidate();
		repaint();
	}

	private void highlightSelectedAnnotation() {
		if (selectedAnnotation != null) {
			for (Span span : selectedAnnotation.getSpans()) {
				try {
					if (span.equals(selectedSpan))
						getHighlighter().addHighlight(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
					else
						getHighlighter().addHighlight(span.getStart(), span.getEnd(), new RectanglePainter(Color.GRAY));
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

		}
	}

	public Span getSelectedSpan() {
		return selectedSpan;
	}

	public GraphDialog getGraphDialog() {
		return graphDialog;
	}

	@Override
	public void annotationAdded(ConceptAnnotation newAnnotation) {
		setSelection(newAnnotation.getSpans().first(), newAnnotation);
		refreshHighlights();
	}

	@Override
	public void annotationRemoved(ConceptAnnotation removedAnnotation) {
		refreshHighlights();
	}

	@Override
	public void annotationSelectionChanged(ConceptAnnotation annotation) {
		setSelectedAnnotation(annotation);
		refreshHighlights();
	}



	@Override
	public void spanAdded(Span newSpan) {
		refreshHighlights();
	}

	@Override
	public void spanRemoved() {
		setSelection(null, null);
		refreshHighlights();
	}

	@Override
	public void spanSelectionChanged(Span span) {
		refreshHighlights();
	}

	@Override
	public void profileAdded(Profile profile) {
		refreshHighlights();
	}

	@Override
	public void profileRemoved() {
		refreshHighlights();
	}

	@Override
	public void profileSelectionChanged(Profile profile) {
		refreshHighlights();
	}

	@Override
	public void profileFilterSelectionChanged(boolean filterByProfile) {
		setFilterByProfile(filterByProfile);
	}

	@Override
	public void colorChanged() {
		refreshHighlights();
	}
}
