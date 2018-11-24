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

package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AnnotationNotes extends JTextArea implements KnowtatorComponent {
	private ConceptAnnotation conceptAnnotation;
	private final KnowtatorController controller;

	AnnotationNotes(KnowtatorView view) {
		this.controller = view.getController();
		addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
				if (getText().length() > 100) {
					e.consume();
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {

			}
		});

		getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void insertUpdate(DocumentEvent e) {

			}

			@Override
			public void removeUpdate(DocumentEvent e) {

			}

			@Override
			public void changedUpdate(DocumentEvent e) {
				conceptAnnotation.setMotivation(getText());
			}
		});


	}

	private void setText() {
		super.setText(conceptAnnotation.getMotivation());
	}

	@Override
	public void reset() {
		setupListeners();
	}

	@Override
	public void setupListeners() {
		new TextBoundModelListener(controller) {
			@Override
			protected void respondToConceptAnnotationModification() {

			}

			@Override
			protected void respondToSpanModification() {

			}

			@Override
			protected void respondToGraphSpaceModification() {

			}

			@Override
			protected void respondToGraphSpaceCollectionFirstAdded() {

			}

			@Override
			protected void respondToGraphSpaceCollectionEmptied() {

			}

			@Override
			protected void respondToGraphSpaceRemoved() {

			}

			@Override
			protected void respondToGraphSpaceAdded() {

			}

			@Override
			protected void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {

			}

			@Override
			protected void respondToConceptAnnotationCollectionEmptied() {

			}

			@Override
			protected void respondToConceptAnnotationRemoved() {

			}

			@Override
			protected void respondToConceptAnnotationAdded() {

			}

			@Override
			protected void respondToConceptAnnotationCollectionFirstAdded() {

			}

			@Override
			protected void respondToSpanCollectionFirstAdded() {

			}

			@Override
			protected void respondToSpanCollectionEmptied() {

			}

			@Override
			protected void respondToSpanRemoved() {

			}

			@Override
			protected void respondToSpanAdded() {

			}

			@Override
			protected void respondToSpanSelection(SelectionEvent<Span> event) {

			}

			@Override
			protected void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
				if (event.getNew() == null) {
					setEnabled(false);
				} else {
					setEnabled(true);
					conceptAnnotation = event.getNew();
					setText();
				}
			}

			@Override
			protected void respondToTextSourceSelection(SelectionEvent<TextSource> event) {

			}

			@Override
			protected void respondToTextSourceAdded() {

			}

			@Override
			protected void respondToTextSourceRemoved() {

			}

			@Override
			protected void respondToTextSourceCollectionEmptied() {

			}

			@Override
			protected void respondToTextSourceCollectionFirstAdded() {

			}
		};
	}

	@Override
	public void dispose() {

	}
}