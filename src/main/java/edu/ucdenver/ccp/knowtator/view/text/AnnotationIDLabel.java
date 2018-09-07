package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

class AnnotationIDLabel extends JLabel implements ViewListener {

  private KnowtatorView view;

  AnnotationIDLabel(KnowtatorView view) {

    this.view = view;
    view.getController().addViewListener(this);
  }

  public void dispose() {}

  @Override
  public void viewChanged() {
    try {
      Annotation annotation =
          view.getController()
              .getSelectionManager()
              .getActiveTextSource()
              .getAnnotationManager()
              .getSelectedAnnotation();

      if (annotation != null) {
        setText(annotation.getId());
      } else {
        setText("");
      }
    } catch (ActiveTextSourceNotSetException ignored) {

    }
  }
}
