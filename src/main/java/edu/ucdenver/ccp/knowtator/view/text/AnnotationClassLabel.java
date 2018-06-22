package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

import javax.swing.*;

public class AnnotationClassLabel extends JLabel
    implements ViewListener, OWLModelManagerListener {

  private KnowtatorView view;
  @SuppressWarnings("unused")
  private Logger log = Logger.getLogger(AnnotationClassLabel.class);

  AnnotationClassLabel(KnowtatorView view) {
    this.view = view;
  }

  private void displayAnnotation(Annotation annotation) {
    if (annotation != null) {
      try {
        setText(view.getController().getOWLAPIDataExtractor().getOWLEntityRendering(annotation.getOwlClass()));
      } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
        setText(String.format("ID: %s Label: %s", annotation.getOwlClassID(), annotation.getOwlClassLabel()));
      }
    } else {
      setText("");
    }
  }

  @Override
  public void handleChange(OWLModelManagerChangeEvent event) {
    if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
		try {
			Annotation annotation = view.getController().getSelectionManager().getActiveTextSource().getAnnotationManager().getSelectedAnnotation();
			displayAnnotation(annotation);
		} catch (ActiveTextSourceNotSetException ignored) {
		}
    }
  }

  public void dispose() {
    try {
      view.getController().getOWLAPIDataExtractor().getWorkSpace().getOWLModelManager().removeListener(this);
    } catch (OWLWorkSpaceNotSetException e) {
      e.printStackTrace();
    }
  }

  @Override
  public void viewChanged() {
	  try {
		  displayAnnotation(view.getController().getSelectionManager().getActiveTextSource().getAnnotationManager().getSelectedAnnotation());
	  } catch (ActiveTextSourceNotSetException ignored) {
	  }
  }
}
