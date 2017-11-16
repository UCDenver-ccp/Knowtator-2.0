package edu.ucdenver.ccp.knowtator.ui.info;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.text.AnnotationProperties;
import edu.ucdenver.ccp.knowtator.annotation.text.Span;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InfoPane extends JPanel implements AnnotationListener, ActionListener {

    private static Logger log = Logger.getLogger(KnowtatorManager.class);

    public KnowtatorManager manager;


    public InfoPane(KnowtatorManager manager) {
        this.manager = manager;

        //Lay out the text controls and the labels.
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER; //last
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;

        setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Annotation Info"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));
    }

    @Override
    public void annotationsChanged(Annotation annotation) {
        displayInfo(annotation);
    }

    public void displayInfo(Annotation annotation) {

        this.removeAll();

        List<List<JComponent>> annotationComponents = new ArrayList<>();

        if(annotation != null) {
            String name;
            String content;
            // **********************************Meta Data*********************************
            // Annotator field
            name = AnnotationProperties.ANNOTATOR;
            content = annotation.getAnnotator().getName();
            annotationComponents.add(addLabel(name, content));

            // Text Source field
            name = AnnotationProperties.TEXT_SOURCE;
            content = annotation.getTextSource();
            annotationComponents.add(addLabel(name, content));

            // TODO: Date field

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

    public List<JComponent> addLabel(String name, String content) {
        JLabel label = new JLabel(content);
        JLabel labelLabel = new JLabel(name + ": ");
        labelLabel.setLabelFor(label);

        return Arrays.asList(labelLabel, label);
    }

//    public List<JComponent> addField(String name) {
//        JTextField field = new JTextField(10);
//        field.setActionCommand(name);
//        field.addActionListener(this);
//        JLabel fieldLabel = new JLabel(name + ": ");
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

    public static void simpleTest() {
        KnowtatorManager manager = new KnowtatorManager();
        manager.simpleTest();

        JFrame frame = new JFrame("TextSamplerDemo");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        //Add content to the window.
        frame.add(new InfoPane(manager));
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {

        simpleTest();

    }
}
