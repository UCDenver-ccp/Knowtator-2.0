package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class TextSourceChooser extends Chooser<TextSource> implements TextSourceCollectionListener, ViewListener {

    private TextSourceCollection collection;

    public TextSourceChooser(KnowtatorView view) {
        super(
                view,
                new TextSource[0]);
        collection = view.getController().getTextSourceManager();
        view.getController().addViewListener(this);
        collection.addCollectionListener(this);

    }


    @Override
    public void viewChanged() {
        setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new TextSource[0])));
        TextSource textSource = getView().getController()
                .getTextSourceManager().getSelection();

        setSelectedItem(textSource);
    }

    @Override
    public void projectLoaded() {

        setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new TextSource[0])));
    }
}
