package edu.ucdenver.ccp.knowtator.ui.info;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.text.AnnotationProperties;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoPane extends JPanel implements AnnotationListener, ActionListener {

    public KnowtatorManager manager;
    public JLabel actionLabel;


    public InfoPane(KnowtatorManager manager) {
        this.manager = manager;

        //Lay out the text controls and the labels.
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridwidth = GridBagConstraints.REMAINDER; //last
        c.anchor = GridBagConstraints.WEST;
        c.weightx = 1.0;
        actionLabel = new JLabel("Type text in a field and press Enter.");
        actionLabel.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        add(actionLabel, c);
        setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createTitledBorder("Text Fields"),
                        BorderFactory.createEmptyBorder(5,5,5,5)));

        displayInfo(manager.getAnnotationManager().getSelectedAnnotation());
    }

    @Override
    public void annotationsChanged() {

    }

    @Override
    public void annotationsChanged(Annotation annotation) {
        displayInfo(annotation);
    }

    public void displayInfo(Annotation annotation) {
        removeAll();

        Map<String, List<JComponent>> annotationComponents = new HashMap<>();

        String fieldName;
        // **********************************Meta Data*********************************
        // Annotator field
        fieldName = AnnotationProperties.ANNOTATOR;
        annotationComponents.put(fieldName, addField(fieldName));

        // Text Source field
        fieldName = AnnotationProperties.TEXT_SOURCE;
        annotationComponents.put(fieldName, addField(fieldName));

        // TODO: Date field

        // **********************************OWL Class*********************************
        // Class Name field
        fieldName = AnnotationProperties.CLASS_NAME;
        annotationComponents.put(fieldName, addField(fieldName));

        // Class ID field
        fieldName = AnnotationProperties.CLASS_ID;
        annotationComponents.put(fieldName, addField(fieldName));

        // **********************************SPANS*********************************
        // Span Start field
        fieldName = AnnotationProperties.SPAN_START;
        annotationComponents.put(fieldName, addField(fieldName));

        // Span End field
        fieldName = AnnotationProperties.SPAN_END;
        annotationComponents.put(fieldName, addField(fieldName));

        addLabelTextRows(annotationComponents);
    }

    public List<JComponent> addField(String name) {
        JTextField field = new JTextField(10);
        field.setActionCommand(name);
        field.addActionListener(this);
        JLabel fieldLabel = new JLabel(name + ": ");
        fieldLabel.setLabelFor(field);

        return Arrays.asList(fieldLabel, field);
    }

    private void addLabelTextRows(Map<String, List<JComponent>> componentsMap) {
        GridBagConstraints c = new GridBagConstraints();
        c.anchor = GridBagConstraints.EAST;

        for (List<JComponent> components : componentsMap.values()) {
            c.gridwidth = GridBagConstraints.RELATIVE; //next-to-last
            c.fill = GridBagConstraints.NONE;      //reset to default
            c.weightx = 0.0;                       //reset to default
            this.add(components.get(0), c);

            c.gridwidth = GridBagConstraints.REMAINDER;     //end row
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            this.add(components.get(1), c);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String prefix = "You typed \"";
        JTextField source = (JTextField)e.getSource();
        actionLabel.setText(prefix + source.getText() + "\"");
    }

    public static void simpleTest() {
        KnowtatorManager manager = new KnowtatorManager();
        manager.simpleTest();

        //Schedule a job for the event dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(() -> {
            //Turn off metal's use of bold fonts
            UIManager.put("swing.boldMetal", Boolean.FALSE);
            //Create and set up the window.
            JFrame frame = new JFrame("TextSamplerDemo");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            //Add content to the window.
            frame.add(new InfoPane(manager));
            //Display the window.
            frame.pack();
            frame.setVisible(true);
        });
    }

    public static void main(String[] args) {

        simpleTest();

    }
}
