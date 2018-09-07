package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.graph.ActiveGraphSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class GraphSpaceChooser extends Chooser<GraphSpace>
    implements ViewListener {

  public GraphSpaceChooser(KnowtatorView view) {
    super(view);
  }



	@Override
	public void viewChanged() {
      try {
        setModel(
                new DefaultComboBoxModel<>(
                        getView().getController().getSelectionManager().getActiveTextSource()
                                .getGraphSpaceManager().getGraphSpaceCollection()
                                .getCollection()
                                .toArray(new GraphSpace[0])));
        setSelectedItem(getView().getController().getSelectionManager().getActiveTextSource().getGraphSpaceManager().getActiveGraphSpace());
      } catch (ActiveTextSourceNotSetException | ActiveGraphSpaceNotSetException ignored) {

      }
    }
}
