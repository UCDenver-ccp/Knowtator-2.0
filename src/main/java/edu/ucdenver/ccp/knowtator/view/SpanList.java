package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.AddEvent;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.RemoveEvent;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionChangeEvent;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;

public class SpanList extends KnowtatorList<Span> {

    private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;


    SpanList(KnowtatorView view) {
        super(view);

        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void added(AddEvent<ConceptAnnotation> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> removedObject) {

            }

            @Override
            public void changed() {

            }

            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }

            @Override
            public void updated(ConceptAnnotation updatedItem) {

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
    public void added(AddEvent<Span> addEvent) {
        setCollection(addEvent.getAdded().getConceptAnnotation().getSpanCollection());
    }

    @Override
    public void removed(RemoveEvent<Span> removeEvent) {
        setCollection(removeEvent.getRemoved().getConceptAnnotation().getSpanCollection());
    }

    @Override
    public void changed() {

    }

    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

    }

    @Override
    public void updated(Span updatedItem) {
        setCollection(updatedItem.getConceptAnnotation().getSpanCollection());
    }


    @Override
    public void selected(SelectionChangeEvent<Span> event) {

    }
}
