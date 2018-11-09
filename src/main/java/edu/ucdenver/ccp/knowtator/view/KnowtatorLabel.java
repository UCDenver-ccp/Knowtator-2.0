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

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import javax.swing.*;

public abstract class KnowtatorLabel extends JLabel implements KnowtatorComponent {

    private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
    private final KnowtatorCollectionListener<TextSource> textSourceCollectionListener;
    protected KnowtatorView view;

    protected KnowtatorLabel(KnowtatorView view) {
        this.view = view;
        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void added(AddEvent<ConceptAnnotation> event) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> event) {

            }

            @Override
            public void changed(ChangeEvent<ConceptAnnotation> event) {
                reactToConceptAnnotationChange();
            }

            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }


            @Override
            public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
                reactToConceptAnnotationSelectionChange(event);

            }
        };
        textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
            @Override
            public void added(AddEvent<TextSource> event) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> event) {

            }

            @Override
            public void changed(ChangeEvent<TextSource> event) {
            }

            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }

            @Override
            public void selected(SelectionChangeEvent<TextSource> event) {
                reactToTextSourceChange(event);
            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    private void reactToTextSourceChange(SelectionChangeEvent<TextSource> event) {
        if (event.getOld() != null) {
            event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
    }

    protected abstract void reactToConceptAnnotationSelectionChange(SelectionChangeEvent<ConceptAnnotation> event);
    protected abstract void reactToConceptAnnotationChange();


    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    public void dispose() {

    }
}
