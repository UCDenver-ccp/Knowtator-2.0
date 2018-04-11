package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.listeners.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.TextSource;

import java.util.TreeSet;

public class TextSourceCollection
		extends CyclableCollection<TextSource, TextSourceCollectionListener> {

	public TextSourceCollection() {
		super(new TreeSet<>(TextSource::compare));
	}
}
