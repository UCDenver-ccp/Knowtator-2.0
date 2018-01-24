package edu.ucdenver.ccp.knowtator.ui.info;

import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.IdentityChainAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.listeners.ConceptAnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class InfoPanel extends JPanel implements SpanListener, ConceptAnnotationListener {
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(InfoPanel.class);
    private DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd");
    private BasicKnowtatorView view;


    public InfoPanel(BasicKnowtatorView view) {
        this.view = view;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(20, 50));
        setBackground(Color.white);
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


        if (annotation != null) {
            GridBagConstraints gbc = new GridBagConstraints();
            JLabel titleLabel = new JLabel(("Annotation Information"));
            titleLabel.setFont(new Font(titleLabel.getFont().getName(), Font.BOLD, titleLabel.getFont().getSize() + 5));

            JLabel idLabel = new JLabel(String.format("Annotation ID: %s", annotation.getID()));
            JLabel classLabel = new JLabel(String.format("Class ID: %s", annotation.getClassID()));
            JLabel classNameLabel = new JLabel(String.format("Class Name: %s", annotation.getClassName()));
            JLabel profileLabel = new JLabel(String.format("Profile ID: %s", annotation.getAnnotator().getProfileID()));
            JLabel textSourceLabel = new JLabel(String.format("Document ID: %s", annotation.getTextSource().getDocID()));
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
            add(classNameLabel, gbc);
            add(profileLabel, gbc);
            add(textSourceLabel, gbc);
            add(dateLabel, gbc);
            add(spanTitleLabel, gbc);
            spanLabels.forEach(spanLabel -> add(spanLabel, gbc));

            if (annotation instanceof IdentityChainAnnotation) {
                JLabel coreferenceTitleLabel = new JLabel(("Coreferrences"));
                coreferenceTitleLabel.setFont(new Font(coreferenceTitleLabel.getFont().getName(), Font.BOLD, coreferenceTitleLabel.getFont().getSize() + 2));
                List<JLabel> coreferenceLabels = new ArrayList<>();
                for (Span span : annotation.getSpans()) {
                    JLabel spanLabel = new JLabel(String.format("%d, %d", span.getStart(), span.getEnd()));
                    coreferenceLabels.add(spanLabel);
                }
                add(coreferenceTitleLabel, gbc);
                coreferenceLabels.forEach(coreferenceLabel -> add(coreferenceLabel, gbc));
            }

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
