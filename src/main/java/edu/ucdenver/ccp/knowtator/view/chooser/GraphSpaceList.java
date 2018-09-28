package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import java.util.stream.Collector;

public class GraphSpaceList extends KnowtatorList<GraphSpace> {
    private KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;


    public GraphSpaceList(KnowtatorView view) {
        super(view);

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
    public void emptied(RemoveEvent<GraphSpace> event) {
        setEnabled(false);
    }

    @Override
    public void firstAdded(AddEvent<GraphSpace> event) {
        setEnabled(true);
    }

    @Override
    public void changed(ChangeEvent<GraphSpace> event) {
    }

    @Override
    public void updated(GraphSpace updatedItem) {
        reactToAnnotationChange(updatedItem.getTextSource().getConceptAnnotationCollection().getSelection());
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
