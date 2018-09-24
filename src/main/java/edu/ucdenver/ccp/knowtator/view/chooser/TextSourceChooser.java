package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class TextSourceChooser extends Chooser<TextSource> implements TextSourceCollectionListener {

    public TextSourceChooser(KnowtatorView view) {
        super(view);
        setCollection(view.getController().getTextSourceCollection());
        setModel(new DefaultComboBoxModel<>(collection.toArray(new TextSource[0])));
    }

    @Override
    void reactToTextSourceChange(TextSource previousSelection, TextSource currentSelection) {

    }

    @Override
    void reactToAnnotationChange(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {

    }
}
