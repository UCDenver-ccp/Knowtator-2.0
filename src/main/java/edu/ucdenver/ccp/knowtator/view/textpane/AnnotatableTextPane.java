/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.textpane;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.NoSelectedOWLClassException;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorDefaultSettings;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * A text pane that can be annotated
 */
public abstract class AnnotatableTextPane extends SearchableTextPane implements KnowtatorComponent {

	private final MouseListener mouseListener;
	private ConceptAnnotation conceptAnnotation;
	TextSource textSource;
	private Span span;
	private final KnowtatorController controller;
	private final DefaultHighlighter.DefaultHighlightPainter overlapHighlighter = new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);
	private final DefaultHighlighter.DefaultHighlightPainter selectionHighlighter = new RectanglePainter(Color.BLACK);

	AnnotatableTextPane(KnowtatorController controller, JTextField searchTextField) {
		super(controller, searchTextField);
		this.controller = controller;
		setEditable(false);
		setSelectedTextColor(Color.red);
		setFont(KnowtatorDefaultSettings.FONT);

		getCaret().setVisible(true);

		requestFocusInWindow();
		select(0, 0);
		getCaret().setSelectionVisible(true);

		mouseListener = new MouseListener() {
			int press_offset;

			@Override
			public void mousePressed(MouseEvent e) {
				press_offset = viewToModel(e.getPoint());
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				handleMouseRelease(e, press_offset, viewToModel(e.getPoint()));
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}

			@Override
			public void mouseClicked(MouseEvent e) {
			}
		};

		setEnabled(true);
		addMouseListener(mouseListener);
	}

	@Override
	public void setupListeners() {
		new TextBoundModelListener(controller) {
			@Override
			public void respondToConceptAnnotationModification() {
				refreshHighlights();
			}

			@Override
			public void respondToSpanModification() {
				refreshHighlights();
			}

			@Override
			public void respondToGraphSpaceModification() {

			}

			@Override
			public void respondToGraphSpaceCollectionFirstAdded() {

			}

			@Override
			public void respondToGraphSpaceCollectionEmptied() {

			}

			@Override
			public void respondToGraphSpaceRemoved() {

			}

			@Override
			public void respondToGraphSpaceAdded() {

			}

			@Override
			public void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {

			}

			@Override
			public void respondToConceptAnnotationCollectionEmptied() {

			}

			@Override
			public void respondToConceptAnnotationRemoved() {

			}

			@Override
			public void respondToConceptAnnotationAdded() {

			}

			@Override
			public void respondToConceptAnnotationCollectionFirstAdded() {

			}

			@Override
			public void respondToSpanCollectionFirstAdded() {
				refreshHighlights();
			}

			@Override
			public void respondToSpanCollectionEmptied() {
				refreshHighlights();
			}

			@Override
			public void respondToSpanRemoved() {
				refreshHighlights();
			}

			@Override
			public void respondToSpanAdded() {
				refreshHighlights();
			}

			@Override
			public void respondToSpanSelection(SelectionEvent<Span> event) {
				span = event.getNew();
				refreshHighlights();
			}

			@Override
			public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
				conceptAnnotation = event.getNew();
				refreshHighlights();
			}

			@Override
			public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {
				textSource = event.getNew();
				showTextSource();
			}

			@Override
			public void respondToTextSourceAdded() {

			}

			@Override
			public void respondToTextSourceRemoved() {

			}

			@Override
			public void respondToTextSourceCollectionEmptied() {
				setEnabled(false);
				removeMouseListener(mouseListener);
			}

			@Override
			public void respondToTextSourceCollectionFirstAdded() {
				setEnabled(true);
				addMouseListener(mouseListener);
			}
		};
	}

	/**
	 * Sets the text to the text sources content
	 */
	private void showTextSource() {
		String text = textSource.getContent();
		setText(text);
		refreshHighlights();
	}

	/**
	 * @param e              MouseEvent
	 * @param press_offset   Mouse position at press
	 * @param release_offset Mouse position at release
	 */
	protected abstract void handleMouseRelease(MouseEvent e, int press_offset, int release_offset);

	/**
	 * Repaints the highlights
	 */
	public void refreshHighlights() {
		if (controller.isNotLoading()) {
			// Remove all previous highlights in case a span has been deleted
			getHighlighter().removeAllHighlights();

			// Always highlight the selected concept first so its color and border show up
			highlightSelectedAnnotation();

			// Highlight overlaps first, then spans. Overlaps must be highlighted first because the highlights are displayed
			// in order of placement
			Set<Span> spans = textSource.getConceptAnnotationCollection().getSpans(null).getCollection();
			highlightOverlaps(spans);
			highlightSpans(spans);

			revalidate();
			repaint();

			SwingUtilities.invokeLater(
					() -> {
						if (conceptAnnotation != null) {
							Span span1 = span == null ? conceptAnnotation.getSpanCollection().first() : span;
							if (span1 != null) {
								try {
									scrollRectToVisible(modelToView(span1.getStart()));
								} catch (BadLocationException | NullPointerException ignored) {
								}
							}
						}
					}
			);
		}
	}

	/**
	 * Highlights spans according to the color specified by their annotation. Underlines spans from annotations whose OWL class is selected
	 *
	 * @param spans A set of spans to highlight
	 */
	private void highlightSpans(Set<Span> spans) {
		SimpleAttributeSet underlinedSpan = new SimpleAttributeSet();
		StyleConstants.setUnderline(underlinedSpan, true);

		SimpleAttributeSet regularSpan = new SimpleAttributeSet();
		StyleConstants.setUnderline(regularSpan, false);

		getStyledDocument().setCharacterAttributes(0, getText().length(), regularSpan, false);

		Set<OWLClass> descendants = null;
		try {
			OWLClass owlClass = controller.getOWLModel().getSelectedOWLClass();
			descendants = controller.getOWLModel().getDescendants(owlClass);
			descendants.add(owlClass);
		} catch (NoSelectedOWLClassException ignored) {

		}
		for (Span span : spans) {
			//Underline spans for the same class
			if (descendants != null && descendants.contains(span.getConceptAnnotation().getOwlClass())) {
				getStyledDocument().setCharacterAttributes(span.getStart(), span.getSize(), underlinedSpan, false);
			}
			DefaultHighlighter.DefaultHighlightPainter spanHighlighter = new DefaultHighlighter.DefaultHighlightPainter(span.getConceptAnnotation().getColor());

			highlightRegion(span.getStart(), span.getEnd(), spanHighlighter);
		}
	}

	/**
	 * Highlights overlapping spans
	 *
	 * @param spans A set of spans to highlight
	 */
	private void highlightOverlaps(Set<Span> spans) {
		Span lastSpan = null;

		for (Span span : spans) {
			if (lastSpan != null) {
				if (span.intersects(lastSpan)) {
					highlightRegion(span.getStart(), min(lastSpan.getEnd(), span.getEnd()), overlapHighlighter);
				}
			}

			if (lastSpan == null || span.getEnd() > lastSpan.getEnd()) {
				lastSpan = span;
			}
		}
	}

	/**
	 * Highlights a region using the given highlighter
	 *
	 * @param start       Start of highlight
	 * @param end         End of highlight
	 * @param highlighter Highlighter to use to draw the highlight
	 */
	private void highlightRegion(int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter) {
		try {
			getHighlighter().addHighlight(start, end, highlighter);
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Highlights the spans for the selected annotation
	 */
	private void highlightSelectedAnnotation() {
		if (conceptAnnotation != null) {
			for (Span span : conceptAnnotation.getSpanCollection()) {
				highlightRegion(span.getStart(), span.getEnd(), selectionHighlighter);
			}
		}
	}

	/**
	 * Snaps selection to word limits
	 *
	 * @param press_offset   Mouse position at press
	 * @param release_offset Mouse position at release
	 */
	void setSelectionAtWordLimits(int press_offset, int release_offset) {

		try {
			int start = Utilities.getWordStart(this, min(press_offset, release_offset));
			int end = Utilities.getWordEnd(this, max(press_offset, release_offset));

			//I don't want to deselect the annotation here because I may want to add a span to it

			requestFocusInWindow();
			select(start, end);

		} catch (BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param size Value to set font size to
	 */
	public void setFontSize(int size) {
		Font font = getFont();
		setFont(new Font(font.getName(), font.getStyle(), size));
		repaint();
	}

	/**
	 * Alters the selection
	 *
	 * @param startModification Value to modify the start selection by
	 * @param endModification   Value to modify the end selection by
	 */
	public void modifySelection(int startModification, int endModification) {
		select(getSelectionStart() + startModification, getSelectionEnd() + endModification);
	}

	class RectanglePainter extends DefaultHighlighter.DefaultHighlightPainter {

		@SuppressWarnings("SameParameterValue")
		RectanglePainter(Color color) {
			super(color);
		}

		/**
		 * Paints a portion of a highlight.
		 *
		 * @param g      the graphics context
		 * @param offs0  the starting model offset >= 0
		 * @param offs1  the ending model offset >= offs1
		 * @param bounds the bounding box of the view, which is not necessarily the region to paint.
		 * @param c      the editor
		 * @param view   View painting for
		 * @return region drawing occurred in
		 */
		public Shape paintLayer(
				Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
			Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

			if (r == null) return null;

			//  Do your custom painting

			Color color = getColor();
			g.setColor(color == null ? c.getSelectionColor() : color);

			((Graphics2D) g).setStroke(new BasicStroke(4));

			//  Code is the same as the default highlighter except we use drawRect(...)

			//		g.fillRect(r.x, r.y, r.width, r.height);
			g.drawRect(r.x, r.y, r.width - 1, r.height - 1);
			((Graphics2D) g).setStroke(new BasicStroke());

			// Return the drawing area

			return r;
		}

		private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view) {
			// Contained in view, can just use bounds.

			if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
				Rectangle alloc;

				if (bounds instanceof Rectangle) {
					alloc = (Rectangle) bounds;
				} else {
					alloc = bounds.getBounds();
				}

				return alloc;
			} else {
				// Should only render part of View.
				try {
					// --- determine locations ---
					Shape shape =
							view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);

					return (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();
				} catch (BadLocationException e) {
					// can't render
				}
			}

			// Can't render

			return null;
		}
	}

}
