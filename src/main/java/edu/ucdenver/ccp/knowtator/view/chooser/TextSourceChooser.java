package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

public class TextSourceChooser extends Chooser<TextSource> {

    public TextSourceChooser(KnowtatorView view) {
        super(view);
        setCollection(view.getController().getTextSourceCollection());
    }

    @Override
    public void reset() {
        setCollection(view.getController().getTextSourceCollection());
    }
}
