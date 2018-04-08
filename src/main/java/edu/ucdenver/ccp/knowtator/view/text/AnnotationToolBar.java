package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import java.awt.event.ActionEvent;

class AnnotationToolBar extends JToolBar {

    private TextViewer textViewer;
    private KnowtatorView view;

    AnnotationToolBar(TextViewer textViewer, KnowtatorView view) {
        this.textViewer = textViewer;
        this.view = view;

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
    }

    private JButton shrinkSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Shrink selection start");
        button.addActionListener((ActionEvent e) -> textViewer.getCurrentTextPane().shrinkSelectionStart());
        return button;
    }

    private JButton shrinkSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Shrink selection end");
        button.addActionListener((ActionEvent e) -> textViewer.getCurrentTextPane().shrinkSelectionEnd());
        return button;
    }

    private JButton growSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Grow selection start");
        button.addActionListener((ActionEvent e) -> textViewer.getCurrentTextPane().growSelectionStart());
        return button;
    }

    private JButton growSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Grow selection end");
        button.addActionListener((ActionEvent e) -> textViewer.getCurrentTextPane().growSelectionEnd());
        return button;
    }

    private JButton nextSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.NEXT_ICON));
        command.setToolTipText("Next span");
        command.addActionListener((ActionEvent e) -> textViewer.getCurrentTextPane().nextSpan());

        return command;
    }

    private JButton previousSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.PREVIOUS_ICON));
        command.setToolTipText("Previous span");
        command.addActionListener((ActionEvent e) -> textViewer.getCurrentTextPane().previousSpan());

        return command;
    }


    private JButton showGraphViewerCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.GRAPH_VIEWER));
        command.setToolTipText("Show graph viewer");
        command.addActionListener(e -> view.getGraphViewer().getDialog().setVisible(true));

        return command;
    }

    private JButton addAnnotationCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.ADD));
        command.setToolTipText("Add annotation");
        command.addActionListener(e -> textViewer.getCurrentTextPane().addAnnotation());
        return command;
    }


    private JButton removeAnnotationCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE));
        command.setToolTipText("Remove annotation");
        command.addActionListener(e -> textViewer.getCurrentTextPane().removeAnnotation());
        return command;
    }
}

