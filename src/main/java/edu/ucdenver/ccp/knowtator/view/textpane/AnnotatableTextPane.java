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
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorDefaultSettings;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class AnnotatableTextPane extends SearchableTextPane {

	AnnotatableTextPane(KnowtatorController controller, JTextField searchTextField) {
		super(controller, searchTextField);
		setEditable(false);
		setEnabled(false);
		setSelectedTextColor(Color.red);
		setFont(KnowtatorDefaultSettings.FONT);

		getCaret().setVisible(true);

		requestFocusInWindow();
		select(0, 0);
		getCaret().setSelectionVisible(true);

		MouseListener mouseListener = new MouseListener() {
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
				refreshHighlights();
			}

			@Override
			public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
				refreshHighlights();
			}

			@Override
			public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {
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

	private void showTextSource() {
		String text = textSource.getContent();
		setText(text);
		refreshHighlights();
	}

	protected abstract void handleMouseRelease(MouseEvent e, int press_offset, int viewToModel);

	abstract void refreshHighlights();

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

	public void setFontSize(int size) {
		Font font = getFont();
		setFont(new Font(font.getName(), font.getStyle(), size));
		repaint();
	}

	public void modifySelection(int startModification, int endModification) {
		select(getSelectionStart() + startModification, getSelectionEnd() + endModification);
	}

}
