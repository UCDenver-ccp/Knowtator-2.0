package edu.ucdenver.ccp.knowtator.events;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;

public class TextSourceChangeEvent extends ChangeEvent<TextSource> {

	public TextSourceChangeEvent(TextSource oldTextSource, TextSource newTextSource) {
		super(oldTextSource, newTextSource);
	}
}
