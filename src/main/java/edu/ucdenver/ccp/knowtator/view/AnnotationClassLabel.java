package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;

import javax.swing.*;

public class AnnotationClassLabel extends JLabel implements SelectionListener {
    private KnowtatorController controller;

    AnnotationClassLabel(KnowtatorController controller) {
        this.controller = controller;
    }

    @Override
    public void selectedAnnotationChanged() {
        Annotation annotation = controller.getSelectionManager().getSelectedAnnotation();
        if (annotation != null) {
            setText(annotation.getOwlClassID());
        }
    }

    @Override
    public void selectedSpanChanged() {

    }

    @Override
    public void activeGraphSpaceChanged() {

    }

    @Override
    public void activeTextSourceChanged() {

    }

    @Override
    public void currentProfileChange() {

    }
}
