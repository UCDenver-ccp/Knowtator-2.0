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
