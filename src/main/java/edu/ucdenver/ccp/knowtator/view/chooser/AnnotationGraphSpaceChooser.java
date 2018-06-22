package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.ControllerNotSetException;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotationGraphSpaceChooser extends Chooser<GraphSpace> implements ViewListener {

  private KnowtatorView view;

  public AnnotationGraphSpaceChooser(KnowtatorView view) {
    super(view);
    this.view = view;
  }

  @Override
  public void added(GraphSpace graphSpace) {
    try {
      if (graphSpace.containsAnnotation(
          view.getController()
              .getSelectionManager()
              .getActiveTextSource()
              .getAnnotationManager()
              .getSelectedAnnotation())) {
        addItem(graphSpace);
      }
    } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

    }
  }

  @Override
  public void viewChanged() {
    try {
      setModel(
          new DefaultComboBoxModel<>(
              view.getController()
                  .getSelectionManager()
                  .getActiveTextSource()
                  .getGraphSpaceManager()
                  .getGraphSpaceCollection()
                  .stream()
                  .filter(
                      graphSpace ->
                      {
                        try {
                          return graphSpace.containsAnnotation(
                              view.getController()
                                  .getSelectionManager()
                                  .getActiveTextSource()
                                  .getAnnotationManager()
                                  .getSelectedAnnotation());
                        } catch (ActiveTextSourceNotSetException | ControllerNotSetException e) {
                          return false;
                        }
                      })
                  .toArray(GraphSpace[]::new)));
    } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

    }
  }
}
