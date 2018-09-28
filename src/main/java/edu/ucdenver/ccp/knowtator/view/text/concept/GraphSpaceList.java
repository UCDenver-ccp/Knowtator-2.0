package edu.ucdenver.ccp.knowtator.view.text.concept;

import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpaceCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public class GraphSpaceList extends JList<GraphSpace> implements KnowtatorViewComponent, GraphSpaceCollectionListener {
    private KnowtatorCollection<GraphSpace> collection;
    private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;
    private TextSourceCollectionListener textSourceCollectionListener;
    private KnowtatorView view;


    public GraphSpaceList(KnowtatorView view) {
        this.view = view;
        ListSelectionListener al = e -> {
            JList jList = (JList) e.getSource();
            if (jList.getSelectedValue() != null) {
                this.collection.setSelection((GraphSpace) jList.getSelectedValue());
            }
        };
        addListSelectionListener(al);

        conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
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
                reactToAnnotationChange(event.getNew());
            }
        };

        textSourceCollectionListener = new TextSourceCollectionListener() {
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
                reactToTextSourceChange(event.getOld(), event.getNew());
            }

        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    private void setCollection(KnowtatorCollection<GraphSpace> collection, ConceptAnnotation conceptAnnotation) {
        if (this.collection != null) {
            this.collection.removeCollectionListener(this);
            setListData(new GraphSpace[0]);
        }

        this.collection = collection;
        this.collection.addCollectionListener(this);

        setList(conceptAnnotation);

    }

    private void setList(ConceptAnnotation conceptAnnotation) {
        GraphSpace[] graphSpaces = conceptAnnotation.getTextSource().getGraphSpaceCollection()
                .stream().filter(graphSpace -> graphSpace.containsAnnotation(conceptAnnotation))
                .toArray(GraphSpace[]::new);
        setListData(graphSpaces);
    }

    @Override
    public void selected(SelectionChangeEvent<GraphSpace> event) {
    }

    @Override
    public void added(AddEvent<GraphSpace> event) {
        setList(event.getAdded().getTextSource().getConceptAnnotationCollection().getSelection());
    }

    @Override
    public void removed(RemoveEvent<GraphSpace> event) {
        setList(event.getRemoved().getTextSource().getConceptAnnotationCollection().getSelection());
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
    public void noSelection(GraphSpace previousSelection) {
    }

    @Override
    public void changed(ChangeEvent<GraphSpace> event) {
    }

    @Override
    public void updated(GraphSpace updatedItem) {
        reactToAnnotationChange(updatedItem.getTextSource().getConceptAnnotationCollection().getSelection());
    }

    private void reactToTextSourceChange(TextSource previousSelection, TextSource currentSelection) {
        if (previousSelection != null) {
            previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
        }
        currentSelection.getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);

        setCollection(currentSelection.getGraphSpaceCollection(), currentSelection.getConceptAnnotationCollection().getSelection());
    }

    private void reactToAnnotationChange(ConceptAnnotation currentSelection) {
        if (currentSelection == null) {
            setModel(new DefaultComboBoxModel<>(new GraphSpace[0]));
        } else {
            setList(currentSelection);
        }
    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    @Override
    public void dispose() {

    }
}
