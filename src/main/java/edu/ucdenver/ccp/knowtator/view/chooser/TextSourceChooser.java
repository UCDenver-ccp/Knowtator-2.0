package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class TextSourceChooser extends Chooser<TextSource> implements TextSourceCollectionListener {

	private TextSourceCollection collection;

	public TextSourceChooser(KnowtatorView view) {
		super(
				view,
				view.getController()
						.getTextSourceManager()
						.getTextSourceCollection()
						.getCollection()
						.toArray(new TextSource[0]));
		collection = view.getController().getTextSourceManager().getTextSourceCollection();
		collection.addListener(this);
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

}
