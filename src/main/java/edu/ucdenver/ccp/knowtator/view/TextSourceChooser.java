package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;

public class TextSourceChooser extends KnowtatorChooser<TextSource> {

    TextSourceChooser(KnowtatorView view) {
        super(view);
        setCollection(view.getController().getTextSourceCollection());
    }

    @Override
    public void reset() {
        setCollection(view.getController().getTextSourceCollection());
    }
}
