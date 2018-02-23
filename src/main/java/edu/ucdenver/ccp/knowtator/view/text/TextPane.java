/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.text;


import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewer;
import edu.ucdenver.ccp.knowtator.view.menus.ProfileMenu;
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

public class TextPane extends JTextPane implements AnnotationListener, SpanListener, ProfileListener {

	public static final Logger log = Logger.getLogger(KnowtatorManager.class);
	private GraphViewer graphViewer;

	private KnowtatorManager manager;
	private KnowtatorView view;
	private TextSource textSource;
	private Span selectedSpan;
	private Annotation selectedAnnotation;
	private boolean filterByProfile;
	private boolean focusOnSelectedSpan;
    private boolean isVisible;

    TextPane(KnowtatorManager manager, KnowtatorView view, TextSource textSource) {
		super();
		this.manager = manager;
		this.view = view;
		this.textSource = textSource;

		this.setEditable(false);

		filterByProfile = false;
		focusOnSelectedSpan = false;

		if (textSource != null) {
            graphViewer = new GraphViewer((JFrame) SwingUtilities.getWindowAncestor(view), manager, view, textSource);
            manager.addConceptAnnotationListener(graphViewer);
            manager.addProfileListener(graphViewer);
        }
		manager.addSpanListener(this);
		manager.addConceptAnnotationListener(this);
		manager.addProfileListener(this);

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
            AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(manager, e, this);

            Map<Span, Annotation> spansContainingLocation = textSource.getAnnotationManager().getSpanMap(
                    press_offset,
                    filterByProfile ? manager.getProfileManager().getCurrentProfile() : null
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
		manager.spanSelectionChangedEvent(selectedSpan);
	}

	public void setSelection(Span span, Annotation annotation) {
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
				view.owlEntitySelectionChanged(manager.getOWLAPIDataExtractor().getOWLClassByID(selectedAnnotation.getClassID()));
				if (annotation.getSpans().size() == 1) {
					setSelectedSpan(annotation.getSpans().first());
				}
			}
			manager.annotationSelectionChangedEvent(selectedAnnotation);
		}
		if (selectedAnnotation != null) {
			view.owlEntitySelectionChanged(manager.getOWLAPIDataExtractor().getOWLClassByID(selectedAnnotation.getClassID()));
		}
	}

	public void previousSpan() {
		TreeMap<Span, Annotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);
		Map.Entry<Span, Annotation> previous =
				annotationMap.containsKey(selectedSpan) ? annotationMap.lowerEntry(selectedSpan) : annotationMap.floorEntry(selectedSpan);

		if (previous == null) previous = annotationMap.lastEntry();

		setSelection(previous.getKey(), previous.getValue());
	}

	public void nextSpan() {
		TreeMap<Span, Annotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);

		Map.Entry<Span, Annotation> next =
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
    	if (isVisible) {
			Profile profile = manager.getProfileManager().getCurrentProfile();

			//Remove all previous highlights in case a span has been deleted
			getHighlighter().removeAllHighlights();

			//Always highlight the selected annotation first so its color and border show up
			highlightSelectedAnnotation();

			// Highlight overlaps first, then spans
			Span lastSpan = Span.makeDefaultSpan(textSource);
			Color lastColor = null;

			Map<Span, Annotation> annotationMap = textSource.getAnnotationManager().getSpanMap(null, filterByProfile ? manager.getProfileManager().getCurrentProfile() : null);
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

					String classID = annotation.getClassID();
					lastColor = profile.getColor(classID);
					if (lastColor == null) {
						lastColor = ProfileMenu.pickAColor(classID, null, profile, manager.getProfileManager());
					}
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

	public GraphViewer getGraphViewer() {
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

	public void addAnnotation() {
		String className = null;
		String classID = null;
		String[] descendants = null;

		Map<String, String[]> clsInfo = manager.getOWLAPIDataExtractor().getSelectedOwlClassInfo();

		if (clsInfo == null) {
			log.warn("No OWLClass selected");

			JTextField nameField = new JTextField(10);
			JTextField idField = new JTextField(10);
			JPanel inputPanel = new JPanel();
			inputPanel.add(new JLabel("Name:"));
			inputPanel.add(nameField);
			inputPanel.add(Box.createHorizontalStrut(15));
			inputPanel.add(new JLabel("ID:"));
			inputPanel.add(idField);


			int result = JOptionPane.showConfirmDialog(null, inputPanel,
					"No OWL Class selected", JOptionPane.DEFAULT_OPTION);
			if (result == JOptionPane.OK_OPTION) {
				className = nameField.getText();
				classID = idField.getText();
			}
		} else {
			className = clsInfo.get("identifiers")[0];
			classID = clsInfo.get("identifiers")[1];
			descendants = clsInfo.get("descendants");
		}

		if (className != null) {
			Profile profile = manager.getProfileManager().getCurrentProfile();

			ProfileMenu.pickAColor(classID, descendants, profile, manager.getProfileManager());

			Span newSpan = new Span(textSource, getSelectionStart(), getSelectionEnd());
			textSource.getAnnotationManager().addAnnotation(className, classID, newSpan);
		}
	}

	public void removeAnnotation() {
		textSource.getAnnotationManager().removeAnnotation(selectedAnnotation.getID());
		setSelection(null, null);
	}

    public void setIsVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }
}
