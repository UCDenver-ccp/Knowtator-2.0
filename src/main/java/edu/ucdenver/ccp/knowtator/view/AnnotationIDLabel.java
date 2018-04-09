package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;

import javax.swing.*;

class AnnotationIDLabel extends JLabel implements SelectionListener {

    private KnowtatorController controller;

    AnnotationIDLabel(KnowtatorController controller) {

        this.controller = controller;
    }

    @Override
    public void selectedAnnotationChanged() {
        Annotation annotation = controller.getSelectionManager().getSelectedAnnotation();
        if (annotation != null) {
            setText(annotation.getID());
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
