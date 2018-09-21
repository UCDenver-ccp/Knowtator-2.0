package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.selection.SelectionModel;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;

import java.util.TreeSet;

public class TextSourceCollection
		extends SelectionModel<TextSource, TextSourceCollectionListener> {

	public TextSourceCollection(KnowtatorController controller) {
		super(controller, new TreeSet<>(TextSource::compare));
	}
}
