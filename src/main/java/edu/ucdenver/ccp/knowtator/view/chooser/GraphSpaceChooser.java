package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class GraphSpaceChooser extends Chooser<GraphSpace> {

    private TextSourceCollectionListener textSourceCollectionListener;

    public GraphSpaceChooser(KnowtatorView view) {
        super(view);

        textSourceCollectionListener = new TextSourceCollectionListener() {
            @Override
            public void updated(TextSource updatedItem) {

            }

            @Override
            public void noSelection(TextSource previousSelection) {

            }

            @Override
            public void selected(TextSource previousSelection, TextSource currentSelection) {
                reactToTextSourceChange(currentSelection);
            }

            @Override
            public void added(TextSource addedObject) {

            }

            @Override
            public void removed(TextSource removedObject) {

            }

            @Override
            public void emptied(TextSource object) {

            }

            @Override
            public void firstAdded(TextSource object) {

            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    private void reactToTextSourceChange(TextSource currentSelection) {
        setModel(new DefaultComboBoxModel<>(currentSelection.getGraphSpaceCollection().toArray(new GraphSpace[0])));
        setCollection(currentSelection.getGraphSpaceCollection());
    }


    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }
}
