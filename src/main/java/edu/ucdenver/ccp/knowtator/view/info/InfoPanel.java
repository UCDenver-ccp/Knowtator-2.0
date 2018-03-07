/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.info;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
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
    private KnowtatorView view;


    public InfoPanel(KnowtatorView view) {
        this.view = view;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setMinimumSize(new Dimension(20, 50));
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
            JLabel classLabel = new JLabel(String.format("Class ID: %s", annotation.getClassID()));
            JLabel classNameLabel = new JLabel(String.format("Class Name: %s", annotation.getClassName()));
            JLabel profileLabel = new JLabel(String.format("Annotator: %s", annotation.getAnnotator().getId()));
            JLabel textSourceLabel = new JLabel(String.format("Document: %s", annotation.getTextSource().getDocID()));
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

        }
        this.revalidate();
        this.repaint();
    }


    @Override
    public void spanAdded(Span newSpan) {
        displayInfo(view.getTextViewer().getCurrentTextPane().getSelectedAnnotation());
    }

    @Override
    public void spanRemoved() {
        displayInfo(view.getTextViewer().getCurrentTextPane().getSelectedAnnotation());
    }

    @Override
    public void spanSelectionChanged(Span span) {
        displayInfo(view.getTextViewer().getCurrentTextPane().getSelectedAnnotation());
    }
}
