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

package edu.ucdenver.ccp.knowtator.view.annotation;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorList;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

public class SpanList extends KnowtatorList<Span> {

    private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;


    public SpanList(KnowtatorView view) {
        super(view);

        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void added(AddEvent<ConceptAnnotation> event) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> event) {

            }

            @Override
            public void changed(ChangeEvent<ConceptAnnotation> event) {

            }


            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }


            @Override
            public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
                if (event.getNew() != null) {
                    setCollection(event.getNew().getSpanCollection());
                }
            }
        };
    }

    @Override
    protected void reactToTextSourceChange(SelectionChangeEvent<TextSource> event) {
        if (event.getOld() != null) {
            event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
    }

    @Override
    public void added(AddEvent<Span> event) {
        setCollection(event.getAdded().getConceptAnnotation().getSpanCollection());
    }

    @Override
    public void removed(RemoveEvent<Span> event) {
        setCollection(event.getRemoved().getConceptAnnotation().getSpanCollection());
    }

    @Override
    public void changed(ChangeEvent<Span> event) {
        setCollection(event.getNew().getConceptAnnotation().getSpanCollection());
    }


    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

    }




    @Override
    public void selected(SelectionChangeEvent<Span> event) {

    }
}
