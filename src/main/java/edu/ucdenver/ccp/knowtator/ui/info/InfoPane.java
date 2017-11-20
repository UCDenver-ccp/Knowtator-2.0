package edu.ucdenver.ccp.knowtator.ui.info;

import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.AnnotationProperties;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoPane extends JPanel implements AnnotationListener, ActionListener {

    private DateFormat dateFormat = new SimpleDateFormat("yyy/MM/dd HH:mm:ss");


    public InfoPane(BasicKnowtatorView view) {
        view.addAnnotationListener(this);

        //Lay out the text controls and the labels.
        setLayout(new GridBagLayout());

        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("annotation Info"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
    }

    @Override
    public void annotationAdded(Annotation newAnnotation) {
        displayInfo(newAnnotation);
    }

    @Override
    public void annotationRemoved() {
        displayInfo(null);
    }

    @Override
    public void annotationSelectionChanged(Annotation annotation) {
        displayInfo(annotation);
    }

    private void displayInfo(Annotation annotation) {

        removeAll();

        List<List<JComponent>> annotationComponents = new ArrayList<>();

        if(annotation != null) {
            String name;
            String content;
            // **********************************Meta Data*********************************
            // Profile field
            name = AnnotationProperties.ANNOTATOR;
            content = annotation.getAnnotator().getProfileID();
            annotationComponents.add(addLabel(name, content));

            // Text Source field
            name = AnnotationProperties.TEXT_SOURCE;
            content = annotation.getTextSource().getDocID();
            annotationComponents.add(addLabel(name, content));


            // TODO: Date field
            name = AnnotationProperties.DATE;
            content = dateFormat.format(annotation.getDate());
            annotationComponents.add(addLabel(name, content));

            // **********************************SPANS*********************************
            for (Span span : annotation.getSpans()) {
                // Span Start field
                name = AnnotationProperties.SPAN_START;
                content = Integer.toString(span.getStart());
                annotationComponents.add(addLabel(name, content));

                // Span End field
                name = AnnotationProperties.SPAN_END;
                content = Integer.toString(span.getEnd());
                annotationComponents.add(addLabel(name, content));
            }
            addLabelTextRows(annotationComponents);
        }
        this.revalidate();
        this.repaint();
    }

    private List<JComponent> addLabel(String name, String content) {
        JLabel label = new JLabel(content);
        JLabel labelLabel = new JLabel(name + ": ");
        labelLabel.setLabelFor(label);

        return Arrays.asList(labelLabel, label);
    }

//    public List<JComponent> addField(String docID) {
//        JTextField field = new JTextField(10);
//        field.setActionCommand(docID);
//        field.addActionListener(this);
//        JLabel fieldLabel = new JLabel(docID + ": ");
//        fieldLabel.setLabelFor(field);
//
//        return Arrays.asList(fieldLabel, field);
//    }

    private void addLabelTextRows(List<List<JComponent>> components) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;

        for (List<JComponent> row : components) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            this.add(row.get(0), c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            this.add(row.get(1), c);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

}
