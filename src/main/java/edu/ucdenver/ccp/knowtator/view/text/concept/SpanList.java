package edu.ucdenver.ccp.knowtator.view.text.concept;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;

public class SpanList extends JList<Span> implements KnowtatorViewComponent {

    private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;
    private SpanCollectionListener spanCollectionListener;
    private KnowtatorView view;
    private TextSourceCollectionListener textSourceCollectionListener;


    SpanList(KnowtatorView view) {
        this.view = view;

        textSourceCollectionListener = new TextSourceCollectionListener() {
            @Override
            public void updated(TextSource updatedItem) {

            }

            @Override
            public void noSelection(TextSource previousSelection) {

            }

            @Override
            public void selected(TextSource previousSelection, TextSource currentSelection) {
                if (previousSelection != null) {
                    previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
                }
                currentSelection.getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
            }

            @Override
            public void added(TextSource addedObject) {

            }

            @Override
            public void removed(TextSource removedObject) {

            }

            @Override
            public void emptied(TextSource object) {

            }

            @Override
            public void firstAdded(TextSource object) {

            }
        };
        conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
            @Override
            public void updated(ConceptAnnotation updatedItem) {

            }

            @Override
            public void noSelection(ConceptAnnotation previousSelection) {

            }

            @Override
            public void selected(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {
                setListData(currentSelection.getSpanCollection().toArray(new Span[0]));

                if (previousSelection != null) {
                    previousSelection.getSpanCollection().removeCollectionListener(spanCollectionListener);
                }
                currentSelection.getSpanCollection().addCollectionListener(spanCollectionListener);
            }

            @Override
            public void added(ConceptAnnotation addedObject) {

            }

            @Override
            public void removed(ConceptAnnotation removedObject) {

            }

            @Override
            public void emptied(ConceptAnnotation object) {

            }

            @Override
            public void firstAdded(ConceptAnnotation object) {

            }
        };

        spanCollectionListener = new SpanCollectionListener() {
            @Override
            public void updated(Span updatedItem) {
                setListData(updatedItem.getConceptAnnotation().getSpanCollection().toArray(new Span[0]));
            }

            @Override
            public void noSelection(Span previousSelection) {

            }

            @Override
            public void selected(Span previousSelection, Span currentSelection) {

            }

            @Override
            public void added(Span addedObject) {

            }

            @Override
            public void removed(Span removedObject) {

            }

            @Override
            public void emptied(Span object) {

            }

            @Override
            public void firstAdded(Span object) {

            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);

    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    public void dispose() {

    }
}
