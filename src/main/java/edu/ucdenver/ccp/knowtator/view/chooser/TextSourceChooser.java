package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceSelectionListener;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class TextSourceChooser extends Chooser<TextSource> implements TextSourceCollectionListener, TextSourceSelectionListener {

	private TextSourceCollection collection;

	public TextSourceChooser(KnowtatorView view) {
		super(
				view,
				new TextSource[0]);

	}


	@Override
	public void activeTextSourceChanged(TextSourceChangeEvent e) {
		setSelectedItem(e.getNew());
	}

	@Override
	public void projectLoaded() {
		collection.addListener(this);
		setModel(new DefaultComboBoxModel<>(collection.getCollection().toArray(new TextSource[0])));
		collection.addListener(this);
	}

	public void setController(KnowtatorController controller) {
		controller.getSelectionManager().addTextSourceListener(this);
		collection = controller.getTextSourceManager().getTextSourceCollection();
		collection.addListener(this);
	}
}
