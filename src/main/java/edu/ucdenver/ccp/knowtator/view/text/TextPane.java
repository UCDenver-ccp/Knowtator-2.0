package edu.ucdenver.ccp.knowtator.view.text;


import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class TextPane extends JTextPane {

	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(KnowtatorController.class);

	private KnowtatorController controller;
	private TextSource textSource;
	private boolean isVisible;

	TextPane(KnowtatorController controller, TextSource textSource) {
		super();
		this.controller = controller;
		this.textSource = textSource;

		this.setEditable(false);

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
					public void mouseEntered(MouseEvent e) { }

					@Override
					public void mouseExited(MouseEvent e) { }

					@Override
					public void mouseClicked(MouseEvent e) { }
				}
		);
	}

	private void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
		if (isVisible) {
			AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, this, controller);

			Set<Span> spansContainingLocation = textSource.getAnnotationManager().getSpanSet(press_offset);

            if (SwingUtilities.isRightMouseButton(e)) {
                if (spansContainingLocation.size() == 1) {
					Span span = spansContainingLocation.iterator().next();
					controller.getSelectionManager().setSelectedSpan(span);
                }
                popupMenu.showPopUpMenu(release_offset);
            } else if (press_offset == release_offset) {
                if (spansContainingLocation.size() == 1) {
					Span span = spansContainingLocation.iterator().next();
					controller.getSelectionManager().setSelectedSpan(span);
                } else if (spansContainingLocation.size() > 1) {
                    popupMenu.chooseAnnotation(spansContainingLocation);
                }

            } else {
				controller.getSelectionManager().setFocusOnSelectedSpan(false);
                setSelectionAtWordLimits(press_offset, release_offset);
            }
        }
	}

	private void setSelectionAtWordLimits(int press_offset, int release_offset) {

		try {
			int start = Utilities.getWordStart(this, min(press_offset, release_offset));
			int end = Utilities.getWordEnd(this, max(press_offset, release_offset));

			select(start, end);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	void previousSpan() {
		Span previousSpan = textSource.getAnnotationManager().getPreviousSpan();

		controller.getSelectionManager().setSelectedSpan(previousSpan);
	}

	void nextSpan() {
		Span nextSpan = textSource.getAnnotationManager().getNextSpan();

		controller.getSelectionManager().setSelectedSpan(nextSpan);
	}

	void shrinkSelectionEnd() {
		if (controller.getSelectionManager().isFocusOnSelectedSpan()) {
			textSource.getAnnotationManager().shrinkSpanEnd(controller.getSelectionManager().getSelectedSpan());
			refreshHighlights();
		} else {
			requestFocusInWindow();
			select(getSelectionStart(), getSelectionEnd() - 1);
		}

	}
	void shrinkSelectionStart() {
		if (controller.getSelectionManager().isFocusOnSelectedSpan()) {
			textSource.getAnnotationManager().shrinkSpanStart(controller.getSelectionManager().getSelectedSpan());
			refreshHighlights();
		} else {
			requestFocusInWindow();
			select(getSelectionStart() + 1, getSelectionEnd());
		}
	}
	void growSelectionEnd() {
		if (controller.getSelectionManager().isFocusOnSelectedSpan()) {
			textSource.getAnnotationManager().growSpanEnd(controller.getSelectionManager().getSelectedSpan(), getText().length());
			refreshHighlights();
		} else {
			requestFocusInWindow();
			select(getSelectionStart(), getSelectionEnd() + 1);
		}
	}
	void growSelectionStart() {
		if (controller.getSelectionManager().isFocusOnSelectedSpan()) {
			textSource.getAnnotationManager().growSpanStart(controller.getSelectionManager().getSelectedSpan());
			refreshHighlights();
		} else {
			requestFocusInWindow();
			select(getSelectionStart() - 1, getSelectionEnd());
		}
	}

	void refreshHighlights() {
    	if (isVisible) {
			if (controller.getSelectionManager().getSelectedSpan() != null) {
				try {
					scrollRectToVisible(modelToView(controller.getSelectionManager().getSelectedSpan().getStart()));
				} catch (BadLocationException | NullPointerException ignored) {

				}
			}

			Profile profile = controller.getSelectionManager().getActiveProfile();

			//Remove all previous highlights in case a span has been deleted
			getHighlighter().removeAllHighlights();

			//Always highlight the selected annotation first so its color and border show up
			highlightSelectedAnnotation();

			// Highlight overlaps first, then spans
			Span lastSpan = Span.makeDefaultSpan();
			Color lastColor = null;

			Set<Span> spans = textSource.getAnnotationManager().getSpanSet(null);
			for (Span span : spans) {
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

					OWLClass owlClass = span.getAnnotation().getOwlClass();
					lastColor = profile.getColor(owlClass == null ? span.getAnnotation().getOwlClassID() : owlClass);
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
	}

	private void highlightSelectedAnnotation() {
		if (controller.getSelectionManager().getSelectedAnnotation() != null) {
			for (Span span : controller.getSelectionManager().getSelectedAnnotation().getSpans()) {
				try {
					if (span.equals(controller.getSelectionManager().getSelectedSpan())) {
						getHighlighter().addHighlight(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
					} else {
						getHighlighter().addHighlight(span.getStart(), span.getEnd(), new RectanglePainter(Color.GRAY));
					}
				} catch (BadLocationException e) {
					e.printStackTrace();
				}
			}

		}
	}

	void addAnnotation() {
		OWLClass owlClass = controller.getOWLAPIDataExtractor().getSelectedClass();
		String owlClassID = controller.getOWLAPIDataExtractor().getOWLClassID(owlClass);

		if (owlClass != null) {


			Span newSpan = new Span(
					getSelectionStart(),
					getSelectionEnd(),
					getText().substring(getSelectionStart(), getSelectionEnd())
			);
			textSource.getAnnotationManager().addAnnotation(owlClass, owlClassID, newSpan);
		}

	}

	void removeAnnotation() {
		if (JOptionPane.showConfirmDialog(controller.getView(), "Are you sure you want to remove the selected annotation?", "Remove Annotation", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
			textSource.getAnnotationManager().removeAnnotation(controller.getSelectionManager().getSelectedAnnotation());
		}
	}

    void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

	void addSpanToAnnotation() {
		textSource.getAnnotationManager()
				.addSpanToAnnotation(
						controller.getSelectionManager().getSelectedAnnotation(),
						new Span(
								getSelectionStart(),
								getSelectionEnd(),
								getText().substring(getSelectionStart(), getSelectionEnd()))
				);
	}
}
