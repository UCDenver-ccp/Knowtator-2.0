package edu.ucdenver.ccp.knowtator.view.text;


import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;

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

public class TextPane extends JTextPane implements AnnotationListener, SpanListener, ProfileListener {

	private static final Logger log = Logger.getLogger(KnowtatorController.class);
	private final KnowtatorView view;

	private KnowtatorController controller;
	private TextSource textSource;


	private boolean isVisible;

	TextPane(KnowtatorView view, KnowtatorController controller, TextSource textSource) {
		super();
		this.view = view;
		this.controller = controller;
		this.textSource = textSource;

		this.setEditable(false);

		controller.addSpanListener(this);
		controller.addConceptAnnotationListener(this);
		controller.addProfileListener(this);

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
			AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, this, controller, view);

			Set<Span> spansContainingLocation = textSource.getAnnotationManager().getSpanSet(press_offset);

            if (SwingUtilities.isRightMouseButton(e)) {
                if (spansContainingLocation.size() == 1) {
					Span span = spansContainingLocation.iterator().next();
					setSelection(span, span.getAnnotation());
                }
                popupMenu.showPopUpMenu(release_offset);
            } else if (press_offset == release_offset) {
                if (spansContainingLocation.size() == 1) {
					Span span = spansContainingLocation.iterator().next();
					setSelection(span, span.getAnnotation());
                } else if (spansContainingLocation.size() > 1) {
                    popupMenu.chooseAnnotation(spansContainingLocation);
                }

            } else {
				controller.getSelectionManager().setFocusOnSelectedSpan(false);
                setSelectionAtWordLimits(press_offset, release_offset);
            }
        }
	}

	void setSelection(Span span, Annotation annotation) {
		if (isVisible) {
			controller.getSelectionManager().setSelectedSpan(span);
			controller.getSelectionManager().setSelectedAnnotation(annotation);
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

	public TextSource getTextSource() {
		return textSource;
	}


	void previousSpan() {
		Span previousSpan = textSource.getAnnotationManager().getPreviousSpan();

		setSelection(previousSpan, previousSpan.getAnnotation());
	}

	void nextSpan() {
		Span nextSpan = textSource.getAnnotationManager().getNextSpan();

		setSelection(nextSpan, nextSpan.getAnnotation());
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

					lastColor = profile.getColor(span.getAnnotation().getOwlClass());
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

	@Override
	public void annotationAdded(Annotation newAnnotation) {
		setSelection(newAnnotation.getSpans().first(), newAnnotation);
		refreshHighlights();
	}

	@Override
	public void annotationRemoved(Annotation removedAnnotation) {
		refreshHighlights();
	}

	@Override
	public void annotationSelectionChanged(Annotation annotation) {
		if (isVisible) {
			refreshHighlights();
		}
	}



	@Override
	public void spanAdded(Span newSpan) {
		refreshHighlights();
	}

	@Override
	public void spanRemoved() {
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
		setSelection(null, null);
	}

	@Override
	public void colorChanged() {
		refreshHighlights();
	}

	void addAnnotation() {
		String classID = controller.getOWLAPIDataExtractor().getSelectedOwlClassID();

		if (classID == null) {
			log.warn("No OWLClass selected");

			JTextField idField = new JTextField(10);
			JPanel inputPanel = new JPanel();
			inputPanel.add(new JLabel("ID:"));
			inputPanel.add(idField);


			int result = JOptionPane.showConfirmDialog(null, inputPanel,
					"No OWL Class selected", JOptionPane.DEFAULT_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				classID = idField.getText();
			}
		}

        Span newSpan = new Span(
                getSelectionStart(),
                getSelectionEnd(),
                getText().substring(getSelectionStart(), getSelectionEnd())
        );
        textSource.getAnnotationManager().addAnnotation(classID, newSpan);

	}

	void removeAnnotation() {
		textSource.getAnnotationManager().removeAnnotation(controller.getSelectionManager().getSelectedAnnotation());
		setSelection(null, null);
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
