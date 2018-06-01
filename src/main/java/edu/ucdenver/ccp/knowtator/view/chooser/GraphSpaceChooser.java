package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.events.GraphSpaceChangeEvent;
import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceCollectionListener;
import edu.ucdenver.ccp.knowtator.listeners.GraphSpaceSelectionListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceSelectionListener;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class GraphSpaceChooser extends Chooser<GraphSpace>
    implements GraphSpaceCollectionListener,
        GraphSpaceSelectionListener,
        TextSourceSelectionListener {

  public GraphSpaceChooser(KnowtatorView view) {
    super(view);
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
                .getCollection()
                .toArray(new GraphSpace[0])));
    if (e.getOld() != null) {
      e.getOld().getAnnotationManager().getGraphSpaceCollection().removeListener(this);
    }
    e.getNew().getAnnotationManager().getGraphSpaceCollection().addListener(this);
  }
}
