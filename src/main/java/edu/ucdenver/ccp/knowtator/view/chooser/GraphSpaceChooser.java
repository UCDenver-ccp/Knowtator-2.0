package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.events.GraphSpaceChangeEvent;
import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;

import javax.swing.*;

public class GraphSpaceChooser extends Chooser<GraphSpace> implements GraphSpaceCollectionListener {

	public GraphSpaceChooser(KnowtatorController controller) {
		super(controller);
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
		if (e.getOld() != null) {
			e.getOld().getAnnotationManager().getGraphSpaceCollection().removeListener(this);
		}
		e.getNew().getAnnotationManager().getGraphSpaceCollection().addListener(this);
	}


}
