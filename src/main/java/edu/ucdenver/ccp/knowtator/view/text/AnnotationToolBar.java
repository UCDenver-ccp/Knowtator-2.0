package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Set;

class AnnotationToolBar extends JToolBar {

    private KnowtatorController controller;

    AnnotationToolBar(KnowtatorController controller) {
        this.controller = controller;

        setFloatable(false);

        add(showGraphViewerCommand());

        add(addAnnotationCommand());
        add(removeAnnotationCommand());

        add(previousSpanCommand());
        add(growSelectionStartCommand());
        add(shrinkSelectionStartCommand());
        add(shrinkSelectionEndCommand());
        add(growSelectionEndCommand());
        add(nextSpanCommand());
        add(profileFilterCommand());
        add(assignColorToClassCommand());
    }

    private JButton assignColorToClassCommand() {

        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.COLOR_PICKER_ICON));
        button.setToolTipText("Assign color to class");
        button.addActionListener(e -> {
            OWLClass owlClass = controller.getOWLAPIDataExtractor().getSelectedClass();
            Set<OWLClass> descendants = controller.getOWLAPIDataExtractor().getSelectedOwlClassDescendants();
            if (owlClass != null) {

                Profile profile = controller.getSelectionManager().getActiveProfile();

                pickAColor(owlClass, descendants, profile);

                controller.colorChangedEvent();
            }

        });
        return button;
    }

    private void pickAColor(OWLClass owlClass, Set<OWLClass> descendants, Profile profile) {
        Color c = JColorChooser.showDialog(controller.getView(), "Pick a color for " + owlClass, Color.CYAN);
        if (c != null) {
            profile.addColor(owlClass, c);

            if (JOptionPane.showConfirmDialog(controller.getView(), "Assign color to descendants of " + owlClass + "?") == JOptionPane.OK_OPTION) {
                if (descendants != null) {
                    for (OWLClass descendant : descendants) {
                        profile.addColor(descendant, c);
                    }
                }
            }
        }
    }

    private JCheckBox profileFilterCommand() {
        JCheckBox command = new JCheckBox("Profile filter");
        command.addChangeListener(e -> controller.getSelectionManager().setFilterByProfile(command.isSelected()));

        return command;
    }

    private JButton shrinkSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Shrink selection start");
        button.addActionListener((ActionEvent e) -> controller.getView().getTextViewer().getCurrentTextPane().shrinkSelectionStart());
        return button;
    }

    private JButton shrinkSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Shrink selection end");
        button.addActionListener((ActionEvent e) -> controller.getView().getTextViewer().getCurrentTextPane().shrinkSelectionEnd());
        return button;
    }

    private JButton growSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Grow selection start");
        button.addActionListener((ActionEvent e) -> controller.getView().getTextViewer().getCurrentTextPane().growSelectionStart());
        return button;
    }

    private JButton growSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Grow selection end");
        button.addActionListener((ActionEvent e) -> controller.getView().getTextViewer().getCurrentTextPane().growSelectionEnd());
        return button;
    }

    private JButton nextSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.NEXT_ICON));
        command.setToolTipText("Next span");
        command.addActionListener((ActionEvent e) -> controller.getView().getTextViewer().getCurrentTextPane().nextSpan());

        return command;
    }

    private JButton previousSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.PREVIOUS_ICON));
        command.setToolTipText("Previous span");
        command.addActionListener((ActionEvent e) -> controller.getView().getTextViewer().getCurrentTextPane().previousSpan());

        return command;
    }


    private JButton showGraphViewerCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.GRAPH_VIEWER));
        command.setToolTipText("Show graph viewer");
        command.addActionListener(e -> controller.getView().getGraphViewer().getDialog().setVisible(true));

        return command;
    }

    private JButton addAnnotationCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.ADD));
        command.setToolTipText("Add annotation");
        command.addActionListener(e -> controller.getView().getTextViewer().getCurrentTextPane().addAnnotation());
        return command;
    }


    private JButton removeAnnotationCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE));
        command.setToolTipText("Remove annotation");
        command.addActionListener(e -> controller.getView().getTextViewer().getCurrentTextPane().removeAnnotation());
        return command;
    }
}

