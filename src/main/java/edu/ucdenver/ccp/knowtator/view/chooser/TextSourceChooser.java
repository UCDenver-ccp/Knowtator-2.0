package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;

import javax.swing.*;

public class TextSourceChooser extends Chooser<TextSource> implements TextSourceCollectionListener {

	private TextSourceCollection collection;

	public TextSourceChooser(KnowtatorController controller) {
		super(
				controller,
				controller
						.getTextSourceManager()
						.getTextSourceCollection()
						.getData()
						.toArray(new TextSource[0]));
		collection = controller.getTextSourceManager().getTextSourceCollection();
		collection.addListener(this);
	}


	@Override
	public void activeTextSourceChanged(TextSourceChangeEvent e) {
		setSelectedItem(e.getNew());
	}

	@Override
	public void projectLoaded() {
		collection.addListener(this);
		setModel(new DefaultComboBoxModel<>(collection.getData().toArray(new TextSource[0])));
		collection.addListener(this);
	}

}
