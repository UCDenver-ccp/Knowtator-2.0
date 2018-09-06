package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.view.ControllerNotSetException;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class TextSourceChooser extends Chooser<TextSource> implements TextSourceCollectionListener, ViewListener {

	private TextSourceCollection collection;

	public TextSourceChooser(KnowtatorView view) {
		super(
				view,
				new TextSource[0]);

	}


	@Override
	public void viewChanged() {
		setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new TextSource[0])));
		try {
			TextSource textSource = getView().getController().getSelectionManager().getActiveTextSource();

			setSelectedItem(textSource);
		} catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

		}
	}

	@Override
	public void projectLoaded() {

		setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new TextSource[0])));
	}

	public void setController(KnowtatorController controller) {
		collection = controller.getTextSourceManager().getTextSourceCollection();
		controller.addViewListener(this);
		collection.addListener(this);
	}

}
