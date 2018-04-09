package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.SelectionListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;

import javax.swing.*;
import java.util.Set;

public class SpanList extends JList<Span> implements SelectionListener {
    private KnowtatorController controller;

    SpanList(KnowtatorController controller) {
        this.controller = controller;
    }

    @Override
    public void selectedAnnotationChanged() {
        Annotation annotation = controller.getSelectionManager().getSelectedAnnotation();
        if (annotation != null) {
            Set<Span> spans = annotation.getSpans();
            setListData(spans.toArray(new Span[0]));
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
