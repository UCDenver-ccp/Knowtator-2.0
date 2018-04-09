package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourcesListener;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;

import javax.swing.*;

public class TextSourceChooser extends JComboBox<TextSource> implements TextSourcesListener, SelectionListener {

    private KnowtatorController controller;

    TextSourceChooser(KnowtatorController controller) {
        super(controller.getTextSourceManager().getTextSources().getTextSources().toArray(new TextSource[0]));
        this.controller = controller;
        controller.getTextSourceManager().getTextSources().addListener(this);
    }

    @Override
    public void textSourcesChanged(TextSource textSource, Boolean wasAdded) {
        if (wasAdded) {
            addItem(textSource);
        } else {
            removeItem(textSource);
        }

    }

    @Override
    public void selectedAnnotationChanged() {

    }

    @Override
    public void selectedSpanChanged() {

    }

    @Override
    public void activeGraphSpaceChanged() {

    }

    @Override
    public void activeTextSourceChanged() {
        setSelectedItem(controller.getSelectionManager().getActiveTextSource());
    }

    @Override
    public void currentProfileChange() {

    }
}
