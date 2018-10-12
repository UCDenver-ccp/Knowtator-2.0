package edu.ucdenver.ccp.knowtator.view.annotation;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorList;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import java.util.stream.Collector;

public class GraphSpaceList extends KnowtatorList<GraphSpace> {
    private final KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;


    public GraphSpaceList(KnowtatorView view) {
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
                reactToAnnotationChange(event.getNew());
            }
        };
    }

    private void setCollection(ConceptAnnotation conceptAnnotation) {
        if (conceptAnnotation == null) {
            dispose();
        } else {
            setCollection(conceptAnnotation.getTextSource().getGraphSpaceCollection()
                    .stream().filter(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation)).collect(Collector.of(
                            () -> new GraphSpaceCollection(conceptAnnotation.getController(), conceptAnnotation.getTextSource()),
                            ListenableCollection::add,
                            (graphSpace1, graphSpace2) -> graphSpace1)));
        }

    }

    @Override
    public void selected(SelectionChangeEvent<GraphSpace> event) {
    }

    @Override
    public void added(AddEvent<GraphSpace> event) {
        setCollection(event.getAdded().getTextSource().getConceptAnnotationCollection().getSelection());
    }

    @Override
    public void removed(RemoveEvent<GraphSpace> event) {
        setCollection(event.getRemoved().getTextSource().getConceptAnnotationCollection().getSelection());
    }

    @Override
    public void emptied() {
        setEnabled(false);
    }

    @Override
    public void firstAdded() {
        setEnabled(true);
    }

    @Override
    public void changed(ChangeEvent<GraphSpace> event) {
        reactToAnnotationChange(event.getNew().getTextSource().getConceptAnnotationCollection().getSelection());
    }

    @Override
    protected void reactToTextSourceChange(SelectionChangeEvent<TextSource> event) {
        if (event.getOld() != null) {
            event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);

        setCollection(event.getNew().getConceptAnnotationCollection().getSelection());
    }

    private void reactToAnnotationChange(ConceptAnnotation currentSelection) {
        setCollection(currentSelection);
    }
}
