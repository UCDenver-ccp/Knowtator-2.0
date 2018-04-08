package edu.ucdenver.ccp.knowtator.view.info;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends JPanel implements SpanListener, AnnotationListener {
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(InfoPanel.class);
    private DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
    private KnowtatorController controller;


    public InfoPanel(KnowtatorController controller) {
        this.controller = controller;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(10, 50));
        setBackground(Color.white);
    }

    @Override
    public void annotationAdded(Annotation newAnnotation) {
        displayInfo(newAnnotation);
    }

    @Override
    public void annotationRemoved(Annotation removedAnnotation) {

        displayInfo(null);
    }

    @Override
    public void annotationSelectionChanged(Annotation annotation) {
        displayInfo(annotation);
    }

    private void displayInfo(Annotation annotation) {

        removeAll();


        if (annotation != null) {
            GridBagConstraints gbc = new GridBagConstraints();
            JLabel titleLabel = new JLabel(("Annotation Information"));
            titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, titleLabel.getFont().getSize() + 5));

            JLabel idLabel = new JLabel(String.format("Annotation ID: %s", annotation.getID()));
            JLabel classLabel = new JLabel(String.format("Class ID: %s", annotation.getOwlClassID()));
            JLabel profileLabel = new JLabel(String.format("Annotator: %s", annotation.getAnnotator().getId()));
            JLabel textSourceLabel = new JLabel(String.format("Document: %s", annotation.getTextSource().getId()));
            JLabel dateLabel = new JLabel(String.format("Date: %s", dateFormat.format(annotation.getDate())));

            // **********************************SPANS*********************************
            JLabel spanTitleLabel = new JLabel(("Spans"));
            spanTitleLabel.setFont(new Font(spanTitleLabel.getFont().getName(), Font.BOLD, spanTitleLabel.getFont().getSize() + 2));
            List<JLabel> spanLabels = new ArrayList<>();
            for (Span span : annotation.getSpans()) {

                JLabel spanLabel = new JLabel(String.format("%d, %d", span.getStart(), span.getEnd()));
                spanLabels.add(spanLabel);
            }

            add(titleLabel, gbc);
            add(idLabel, gbc);
            add(classLabel, gbc);
            add(profileLabel, gbc);
            add(textSourceLabel, gbc);
            add(dateLabel, gbc);
            add(spanTitleLabel, gbc);
            spanLabels.forEach(spanLabel -> add(spanLabel, gbc));

        }
        this.revalidate();
        this.repaint();
    }


    @Override
    public void spanAdded(Span newSpan) {
        displayInfo(controller.getSelectionManager().getSelectedAnnotation());
    }

    @Override
    public void spanRemoved() {
        displayInfo(controller.getSelectionManager().getSelectedAnnotation());
    }

    @Override
    public void spanSelectionChanged(Span span) {
        displayInfo(controller.getSelectionManager().getSelectedAnnotation());
    }
}
