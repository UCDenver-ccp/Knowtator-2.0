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
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewer;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

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

public class TextPane extends JTextPane implements AnnotationListener, SpanListener, ProfileListener {

	private static final Logger log = Logger.getLogger(KnowtatorController.class);
	private GraphViewer graphViewer;

	private KnowtatorController controller;
	private KnowtatorView view;
	private TextSource textSource;
	private Span selectedSpan;
	private Annotation selectedAnnotation;
	private boolean filterByProfile;
	private boolean focusOnSelectedSpan;
    private boolean isVisible;

	TextPane(KnowtatorController controller, KnowtatorView view, TextSource textSource) {
		super();
		this.controller = controller;
		this.view = view;
		this.textSource = textSource;

		this.setEditable(false);

		filterByProfile = false;
		focusOnSelectedSpan = false;

		if (textSource != null) {
			graphViewer = new GraphViewer((JFrame) SwingUtilities.getWindowAncestor(view), controller, view, this);
            textSource.getAnnotationManager().getGraphSpaces().forEach(graphSpace -> {
            	graphViewer.addGraph(graphSpace);
            	graphSpace.connectEdgesToProperties();
			});
			controller.addProfileListener(graphViewer);
			controller.addGraphListener(graphViewer);
        }
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
            AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, this);

            Map<Span, Annotation> spansContainingLocation = textSource.getAnnotationManager().getSpanMap(
                    press_offset,
					filterByProfile ? controller.getProfileManager().getCurrentProfile() : null
            );

            if (SwingUtilities.isRightMouseButton(e)) {
                if (spansContainingLocation.size() == 1) {
                    Map.Entry<Span, Annotation> entry = spansContainingLocation.entrySet().iterator().next();
                    setSelection(entry.getKey(), entry.getValue());
                }
                popupMenu.showPopUpMenu(release_offset);
            } else if (press_offset == release_offset) {
                if (spansContainingLocation.size() == 1) {
                    Map.Entry<Span, Annotation> entry = spansContainingLocation.entrySet().iterator().next();
                    setSelection(entry.getKey(), entry.getValue());
                } else if (spansContainingLocation.size() > 1) {
                    popupMenu.chooseAnnotation(spansContainingLocation);
                }

            } else {
                setSelectionAtWordLimits(press_offset, release_offset);
            }
        }
	}

	private void setSelectedSpan(Span span) {
		selectedSpan = span;
		focusOnSelectedSpan = true;
		controller.spanSelectionChangedEvent(selectedSpan);
	}

	void setSelection(Span span, Annotation annotation) {
		if (isVisible) {
			setSelectedSpan(span);

			setSelectedAnnotation(annotation);
		}
	}

	public Annotation getSelectedAnnotation() {
		return selectedAnnotation;
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

	private void setSelectedAnnotation(Annotation annotation) {
		if (selectedAnnotation != annotation) {
			selectedAnnotation = annotation;

			if (selectedAnnotation != null) {
				if (selectedAnnotation.isOwlClassSet()) {
					view.owlEntitySelectionChanged((OWLClass) selectedAnnotation.getOwlClass());
				}
				if (annotation.getSpans().size() == 1) {
					setSelectedSpan(annotation.getSpans().first());
				}
			}
			graphViewer.goToAnnotationVertex(null, selectedAnnotation);
			controller.annotationSelectionChangedEvent(selectedAnnotation);
		}
		if (selectedAnnotation != null) {
			if (selectedAnnotation.isOwlClassSet()) {
				view.owlEntitySelectionChanged((OWLClass) selectedAnnotation.getOwlClass());
			}
		}
	}

	void previousSpan() {
		TreeMap<Span, Annotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? controller.getProfileManager().getCurrentProfile() : null);
		Map.Entry<Span, Annotation> previous;
		try {
            previous = annotationMap.containsKey(selectedSpan) ? annotationMap.lowerEntry(selectedSpan) : annotationMap.floorEntry(selectedSpan);
        } catch (NullPointerException npe) {
		    previous = null;
        }
		if (previous == null) previous = annotationMap.lastEntry();

		setSelection(previous.getKey(), previous.getValue());
	}

	void nextSpan() {
		TreeMap<Span, Annotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? controller.getProfileManager().getCurrentProfile() : null);

		Map.Entry<Span, Annotation> next;
		try {
            next = annotationMap.containsKey(selectedSpan) ? annotationMap.higherEntry(selectedSpan) : annotationMap.ceilingEntry(selectedSpan);
        } catch (NullPointerException npe) {
		    next = null;
        }
		if (next == null) next = annotationMap.firstEntry();

		setSelection(next.getKey(), next.getValue());
	}

	void shrinkSelectionEnd() {
		if (focusOnSelectedSpan) {
			textSource.getAnnotationManager().shrinkSpanEnd(selectedSpan);
			setSelectedSpan(selectedSpan);
		} else {
			requestFocusInWindow();
			select(getSelectionStart(), getSelectionEnd() - 1);
		}

	}
	void shrinkSelectionStart() {
		if (focusOnSelectedSpan) {
			textSource.getAnnotationManager().shrinkSpanStart(selectedSpan);
			setSelectedSpan(selectedSpan);
		} else {
			requestFocusInWindow();
			select(getSelectionStart() + 1, getSelectionEnd());
		}
	}
	void growSelectionEnd() {
		if (focusOnSelectedSpan) {
			textSource.getAnnotationManager().growSpanEnd(selectedSpan, getText().length());
			setSelectedSpan(selectedSpan);
		} else {
			requestFocusInWindow();
			select(getSelectionStart(), getSelectionEnd() + 1);
		}
	}
	void growSelectionStart() {
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
    	if (isVisible) {
    		if (selectedSpan != null) {
				try {
					scrollRectToVisible(modelToView(selectedSpan.getStart()));
				} catch (BadLocationException | NullPointerException ignored) {

				}
			}

			Profile profile = controller.getProfileManager().getCurrentProfile();

			//Remove all previous highlights in case a span has been deleted
			getHighlighter().removeAllHighlights();

			//Always highlight the selected annotation first so its color and border show up
			highlightSelectedAnnotation();

			// Highlight overlaps first, then spans
			Span lastSpan = Span.makeDefaultSpan();
			Color lastColor = null;

			Map<Span, Annotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? controller.getProfileManager().getCurrentProfile() : null);
			for (Map.Entry<Span, Annotation> entry : annotationMap.entrySet()) {
				Span span = entry.getKey();
				Annotation annotation = entry.getValue();
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

					lastColor = profile.getColor(annotation.getOwlClass());
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
		if (selectedAnnotation != null) {
			for (Span span : selectedAnnotation.getSpans()) {
				try {
					if (span.equals(selectedSpan)) {
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

	Span getSelectedSpan() {
		return selectedSpan;
	}

	GraphViewer getGraphViewer() {
		return graphViewer;
	}

	@Override
	public void annotationAdded(Annotation newAnnotation) {
		setSelection(newAnnotation.getSpans().first(), newAnnotation);
		refreshHighlights();
	}

	@Override
	public void annotationRemoved(Annotation removedAnnotation) {
		if (selectedAnnotation != null && selectedAnnotation.equals(removedAnnotation)) setSelectedAnnotation(null);
		refreshHighlights();
	}

	@Override
	public void annotationSelectionChanged(Annotation annotation) {
		if (isVisible) {
			setSelectedAnnotation(annotation);
			refreshHighlights();
		}
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

	void addAnnotation() {
//		String className = controller.getOWLAPIDataExtractor().getSelectedOwlClassName();
		String classID = controller.getOWLAPIDataExtractor().getSelectedOwlClassID();

		if (classID == null) {
			log.warn("No OWLClass selected");

//			JTextField nameField = new JTextField(10);
			JTextField idField = new JTextField(10);
			JPanel inputPanel = new JPanel();
//			inputPanel.add(new JLabel("Name:"));
//			inputPanel.add(nameField);
//			inputPanel.add(Box.createHorizontalStrut(15));
			inputPanel.add(new JLabel("ID:"));
			inputPanel.add(idField);


			int result = JOptionPane.showConfirmDialog(null, inputPanel,
					"No OWL Class selected", JOptionPane.DEFAULT_OPTION);
			if (result == JOptionPane.OK_OPTION) {
//				className = nameField.getText();
				classID = idField.getText();
			}
		}

//		log.warn(String.format("Class name: %s Class ID: %s", className, classID));

        Span newSpan = new Span(
                getSelectionStart(),
                getSelectionEnd(),
                getText().substring(getSelectionStart(), getSelectionEnd())
        );
        textSource.getAnnotationManager().addAnnotation(classID, newSpan);

	}

	void removeAnnotation() {
		textSource.getAnnotationManager().removeAnnotation(selectedAnnotation.getID());
		setSelection(null, null);
	}

    void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

	void addSpanToAnnotation() {
		textSource.getAnnotationManager()
				.addSpanToAnnotation(
						selectedAnnotation,
						new Span(
								getSelectionStart(),
								getSelectionEnd(),
								getText().substring(getSelectionStart(), getSelectionEnd()))
				);
	}
}
