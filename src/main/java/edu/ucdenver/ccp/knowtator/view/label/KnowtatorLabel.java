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

package edu.ucdenver.ccp.knowtator.view.label;

import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public abstract class KnowtatorLabel extends JLabel implements KnowtatorComponent {

    KnowtatorView view;

    KnowtatorLabel(KnowtatorView view) {
        this.view = view;

    }

    @Override
    public void setupListeners() {
	    new TextBoundModelListener(KnowtatorView.MODEL) {
            @Override
            public void respondToConceptAnnotationModification() {
                react();
            }

            @Override
            public void respondToSpanModification() {

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
                react();
            }

            @Override
            public void respondToConceptAnnotationRemoved() {
                react();
            }

            @Override
            public void respondToConceptAnnotationAdded() {
                react();
            }

            @Override
            public void respondToConceptAnnotationCollectionFirstAdded() {
                react();
            }

            @Override
            public void respondToSpanCollectionFirstAdded() {

            }

            @Override
            public void respondToSpanCollectionEmptied() {

            }

            @Override
            public void respondToSpanRemoved() {

            }

            @Override
            public void respondToSpanAdded() {

            }

            @Override
            public void respondToSpanSelection(SelectionEvent<Span> event) {

            }

            @Override
            public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
                react();
            }

            @Override
            public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {

            }

            @Override
            public void respondToTextSourceAdded() {

            }

            @Override
            public void respondToTextSourceRemoved() {

            }

            @Override
            public void respondToTextSourceCollectionEmptied() {

            }

            @Override
            public void respondToTextSourceCollectionFirstAdded() {

            }
        };

    }

    protected abstract void react();

    @Override
    public void reset() {
        setupListeners();
    }

    @Override
    public void dispose() {

    }
}
