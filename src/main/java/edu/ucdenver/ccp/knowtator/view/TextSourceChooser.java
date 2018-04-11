package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.TextSource;

import javax.swing.*;

public class TextSourceChooser extends JComboBox<TextSource>
		implements TextSourceCollectionListener, SelectionListener {

	TextSourceChooser(KnowtatorController controller) {
		super(
				controller
						.getTextSourceManager()
						.getTextSourceCollection()
						.getData()
						.toArray(new TextSource[0]));
		controller.getTextSourceManager().getTextSourceCollection().addListener(this);
	}

	@Override
	public void added(TextSource textSource) {
		addItem(textSource);
	}

	@Override
	public void removed(TextSource textSource) {
		removeItem(textSource);
	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
	}

	@Override
	public void selectedSpanChanged(SpanChangeEvent e) {
	}

	@Override
	public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {
	}

	@Override
	public void activeTextSourceChanged(TextSourceChangeEvent e) {
		setSelectedItem(e.getNew());
	}

	@Override
	public void activeProfileChange(ProfileChangeEvent e) {
	}
}
