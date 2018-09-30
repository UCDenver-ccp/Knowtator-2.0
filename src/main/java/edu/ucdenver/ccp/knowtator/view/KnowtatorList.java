package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.KnowtatorObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;

import javax.swing.*;
import javax.swing.event.ListSelectionListener;

public abstract class KnowtatorList<K extends KnowtatorObjectInterface> extends JList<K> implements KnowtatorComponent, KnowtatorCollectionListener<K> {

    private final KnowtatorView view;
    private KnowtatorCollection<K> collection;
    private final KnowtatorCollectionListener<TextSource> textSourceCollectionListener;

    KnowtatorList(KnowtatorView view) {
        this.view = view;
        setModel(new DefaultListModel<>());

        ListSelectionListener al = e -> {
            JList jList = (JList) e.getSource();
            if (jList.getSelectedValue() != null) {
                collection.setSelection(this.getSelectedValue());
            }
        };

        addListSelectionListener(al);

        textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
            @Override
            public void added(AddEvent<TextSource> addedObject) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> removedObject) {

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
            public void updated(TextSource updatedItem) {

            }


            @Override
            public void selected(SelectionChangeEvent<TextSource> event) {
                reactToTextSourceChange(event);
            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    protected abstract void reactToTextSourceChange(SelectionChangeEvent<TextSource> event);

    void setCollection(KnowtatorCollection<K> collection) {
        //clear collection
        dispose();

        this.collection = collection;
        this.collection.addCollectionListener(this);
        this.collection.forEach(k -> ((DefaultListModel<K>) getModel()).addElement(k));
    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    @Override
    public void dispose() {
        if (collection != null) collection.forEach(k -> ((DefaultListModel) getModel()).removeElement(k));
    }
}
