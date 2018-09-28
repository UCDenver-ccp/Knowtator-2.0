package edu.ucdenver.ccp.knowtator.view.text.concept;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public class SpanList extends JList<Span> implements KnowtatorViewComponent, KnowtatorCollectionListener<Span> {

    private KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
    private KnowtatorView view;
    private KnowtatorCollectionListener<TextSource> textSourceCollectionListener;


    public SpanList(KnowtatorView view) {
        this.view = view;

        ListSelectionListener al = e -> {
            JList jList = (JList) e.getSource();
            if (jList.getSelectedValue() != null) {
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection().getSelection()
                        .getSpanCollection().setSelection((Span) jList.getSelectedValue());
            }
        };

        addListSelectionListener(al);

        KnowtatorCollectionListener<Span> spanList = this;
        textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
            @Override
            public void added(AddEvent<TextSource> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> removedObject) {

            }

            @Override
            public void changed(ChangeEvent<TextSource> changeEvent) {

            }

            @Override
            public void emptied(RemoveEvent<TextSource> object) {

            }

            @Override
            public void firstAdded(AddEvent<TextSource> object) {

            }

            @Override
            public void updated(TextSource updatedItem) {

            }

            @Override
            public void noSelection(TextSource previousSelection) {

            }

            @Override
            public void selected(SelectionChangeEvent<TextSource> event) {
                if (event.getOld() != null) {
                    event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
                }
                event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
            }
        };
        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void added(AddEvent<ConceptAnnotation> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> removedObject) {

            }

            @Override
            public void changed(ChangeEvent<ConceptAnnotation> changeEvent) {

            }

            @Override
            public void emptied(RemoveEvent<ConceptAnnotation> object) {

            }

            @Override
            public void firstAdded(AddEvent<ConceptAnnotation> object) {

            }

            @Override
            public void updated(ConceptAnnotation updatedItem) {

            }

            @Override
            public void noSelection(ConceptAnnotation previousSelection) {

            }

            @Override
            public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
                if (event.getNew() != null) {
                    setListData(event.getNew().getSpanCollection().toArray(new Span[0]));
                    event.getNew().getSpanCollection().addCollectionListener(spanList);
                } else {
                    setListData(new Span[0]);
                }

                if (event.getOld() != null) {
                    event.getOld().getSpanCollection().removeCollectionListener(spanList);
                }

            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);

    }

    @Override
    public void added(AddEvent<Span> addEvent) {
        setListData(addEvent.getAdded().getConceptAnnotation().getSpanCollection().toArray(new Span[0]));
    }

    @Override
    public void removed(RemoveEvent<Span> removeEvent) {
        setListData(removeEvent.getRemoved().getConceptAnnotation().getSpanCollection().toArray(new Span[0]));
    }

    @Override
    public void changed(ChangeEvent<Span> changeEvent) {

    }

    @Override
    public void emptied(RemoveEvent<Span> object) {

    }

    @Override
    public void firstAdded(AddEvent<Span> object) {

    }

    @Override
    public void updated(Span updatedItem) {
        setListData(updatedItem.getConceptAnnotation().getSpanCollection().toArray(new Span[0]));
    }

    @Override
    public void noSelection(Span previousSelection) {

    }

    @Override
    public void selected(SelectionChangeEvent<Span> event) {

    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    public void dispose() {

    }
}
