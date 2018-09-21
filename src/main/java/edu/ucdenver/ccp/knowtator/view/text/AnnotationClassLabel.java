package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
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
        view.getController().addViewListener(this);
    }

    private void displayAnnotation(Annotation annotation) {
        if (annotation != null) {
            try {
                setText(view.getController().getOWLManager().getOWLEntityRendering(annotation.getOwlClass()));
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
            Annotation annotation = view.getController()
                    .getTextSourceManager().getSelection()
                    .getAnnotationManager().getSelection();
            displayAnnotation(annotation);
        }
    }

    public void dispose() {
        try {
            view.getController().getOWLManager().getWorkSpace().getOWLModelManager().removeListener(this);
        } catch (OWLWorkSpaceNotSetException ignored) {
        }
    }

    @Override
    public void viewChanged() {
        displayAnnotation(view.getController()
                .getTextSourceManager().getSelection()
                .getAnnotationManager().getSelection());
    }
}
