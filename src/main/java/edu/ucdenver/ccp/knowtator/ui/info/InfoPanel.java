package edu.ucdenver.ccp.knowtator.ui.info;

import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.listeners.ConceptAnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends JPanel implements SpanListener, ConceptAnnotationListener {

    private DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
    private BasicKnowtatorView view;


    public InfoPanel(BasicKnowtatorView view) {
        this.view = view;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(20, 50));
    }

    @Override
    public void annotationAdded(ConceptAnnotation newAnnotation) {
        displayInfo(newAnnotation);
    }

    @Override
    public void annotationRemoved(ConceptAnnotation removedAnnotation) {
        displayInfo(null);
    }

    @Override
    public void annotationSelectionChanged(ConceptAnnotation annotation) {
        displayInfo(annotation);
    }

    private void displayInfo(ConceptAnnotation annotation) {

        removeAll();


        if(annotation != null) {
            GridBagConstraints gbc = new GridBagConstraints();
            JLabel titleLabel = new JLabel(("ConceptAnnotation Information"));
            add(titleLabel, gbc);

            JLabel idLabel = new JLabel(String.format("ConceptAnnotation ID: %s", annotation.getID()));
            JLabel classLabel = new JLabel(String.format("Class ID: %s  Name: %s", annotation.getClassID(), annotation.getClassName()));
            JLabel profileLabel = new JLabel(String.format("Profile ID: %s", annotation.getAnnotator().getProfileID()));
            JLabel textSourceLabel = new JLabel(String.format("Document ID: %s", annotation.getTextSource().getDocID()));
            JLabel dateLabel = new JLabel(String.format("Date: %s", dateFormat.format(annotation.getDate())));

            // **********************************SPANS*********************************
            List<JLabel> spanLabels = new ArrayList<>();
            for (Span span : annotation.getSpans()) {

                JLabel spanLabel = new JLabel(String.format("Span: %d, %d", span.getStart(), span.getEnd()));
                spanLabels.add(spanLabel);
            }

            add(idLabel, gbc);
            add(classLabel);
            add(profileLabel, gbc);
            add(textSourceLabel, gbc);
            add(dateLabel, gbc);
            spanLabels.forEach(spanLabel -> add(spanLabel, gbc));
        }
        this.revalidate();
        this.repaint();
    }


    @Override
    public void spanAdded(Span newSpan) {
        displayInfo(view.getTextViewer().getSelectedTextPane().getSelectedAnnotation());
    }

    @Override
    public void spanRemoved() {
        displayInfo(view.getTextViewer().getSelectedTextPane().getSelectedAnnotation());
    }

    @Override
    public void spanSelectionChanged(Span span) {
        displayInfo(view.getTextViewer().getSelectedTextPane().getSelectedAnnotation());
    }
}
