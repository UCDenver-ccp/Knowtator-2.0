package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.*;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceCollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;

import javax.swing.*;

public class GraphSpaceChooser extends JComboBox<GraphSpace>
		implements GraphSpaceCollectionListener, SelectionListener {

	GraphSpaceChooser(KnowtatorController controller) {
		controller.getSelectionManager().addSelectionListener(this);
	}

	@Override
	public void selectedAnnotationChanged(AnnotationChangeEvent e) {
	}

	@Override
	public void selectedSpanChanged(SpanChangeEvent e) {
	}

	@Override
	public void activeGraphSpaceChanged(GraphSpaceChangeEvent e) {
		setSelectedItem(e.getNew());
	}

	@Override
	public void activeTextSourceChanged(TextSourceChangeEvent e) {
		setModel(
				new DefaultComboBoxModel<>(
						e.getNew()
								.getAnnotationManager()
								.getGraphSpaceCollection()
								.getData()
								.toArray(new GraphSpace[0])));
		e.getOld().getAnnotationManager().getGraphSpaceCollection().removeListener(this);
		e.getNew().getAnnotationManager().getGraphSpaceCollection().addListener(this);
	}

	@Override
	public void activeProfileChange(ProfileChangeEvent e) {
	}

	@Override
	public void added(GraphSpace newGraphSpace) {
		addItem(newGraphSpace);
	}

	@Override
	public void removed(GraphSpace removedGrahSpacce) {
		removeItem(removedGrahSpacce);
	}
}
