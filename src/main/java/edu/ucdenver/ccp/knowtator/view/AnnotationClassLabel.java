package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationSelectionListener;
import edu.ucdenver.ccp.knowtator.model.Annotation;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;

import javax.swing.*;

public class AnnotationClassLabel extends JLabel
    implements AnnotationSelectionListener, OWLModelManagerListener {

  private KnowtatorView view;
  @SuppressWarnings("unused")
  private Logger log = Logger.getLogger(AnnotationClassLabel.class);

  AnnotationClassLabel(KnowtatorView view) {
    this.view = view;
    view.getController().getSelectionManager().addAnnotationListener(this);
  }

  @Override
  public void selectedAnnotationChanged(AnnotationChangeEvent e) {
    displayAnnotation(e.getNew());
  }

  private void displayAnnotation(Annotation annotation) {
    if (annotation != null) {
      try {
        setText(view.getController().getOWLAPIDataExtractor().getOWLEntityRendering(annotation.getOwlClass()));
      } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
        setText(annotation.getOwlClassID());
      }
    }
  }

  @Override
  public void handleChange(OWLModelManagerChangeEvent event) {
    if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
      Annotation annotation = view.getController().getSelectionManager().getSelectedAnnotation();
      displayAnnotation(annotation);
    }
  }
}
