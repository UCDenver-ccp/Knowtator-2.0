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

import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;

import javax.swing.*;

public abstract class KnowtatorLabel extends JLabel implements KnowtatorComponent {

    protected KnowtatorView view;

    protected KnowtatorLabel(KnowtatorView view) {
        this.view = view;

        new TextBoundModelListener(view.getController()) {
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
            public void respondToGraphSpaceCollectionFirstAddedEvent() {

            }

            @Override
            public void respondToGraphSpaceCollectionEmptiedEvent() {

            }

            @Override
            public void respondToGraphSpaceRemovedEvent(RemoveEvent<GraphSpace> event) {

            }

            @Override
            public void respondToGraphSpaceAddedEvent(AddEvent<GraphSpace> event) {

            }

            @Override
            public void respondToGraphSpaceSelectionEvent(SelectionEvent<GraphSpace> event) {

            }

            @Override
            public void respondToConceptAnnotationCollectionEmptiedEvent() {
                react();
            }

            @Override
            public void respondToConceptAnnotationRemovedEvent(RemoveEvent<ConceptAnnotation> event) {
                react();
            }

            @Override
            public void respondToConceptAnnotationAddedEvent(AddEvent<ConceptAnnotation> event) {
                react();
            }

            @Override
            public void respondToConceptAnnotationCollectionFirstAddedEvent() {
                react();
            }

            @Override
            public void respondToSpanCollectionFirstAddedEvent() {

            }

            @Override
            public void respondToSpanCollectionEmptiedEvent() {

            }

            @Override
            public void respondToSpanRemovedEvent(RemoveEvent<Span> event) {

            }

            @Override
            public void respondToSpanAddedEvent(AddEvent<Span> event) {

            }

            @Override
            public void respondToSpanSelectionEvent(SelectionEvent<Span> event) {

            }

            @Override
            public void respondToConceptAnnotationSelectionEvent(SelectionEvent<ConceptAnnotation> event) {
                react();
            }

            @Override
            public void respondToTextSourceSelectionEvent(SelectionEvent<TextSource> event) {

            }

            @Override
            public void respondToTextSourceAddedEvent(AddEvent<TextSource> event) {

            }

            @Override
            public void respondToTextSourceRemovedEvent(RemoveEvent<TextSource> event) {

            }

            @Override
            public void respondToTextSourceCollectionEmptiedEvent() {

            }

            @Override
            public void respondToTextSourceCollectionFirstAddedEvent() {

            }
        };
    }

    protected abstract void react();

    @Override
    public void reset() {

    }

    @Override
    public void dispose() {

    }
}
