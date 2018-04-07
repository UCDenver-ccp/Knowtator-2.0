package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

class AnnotationToolBar extends JToolBar {

    private TextViewer textViewer;
    private KnowtatorView view;

    AnnotationToolBar(TextViewer textViewer, KnowtatorView view) {
        this.textViewer = textViewer;
        this.view = view;

        setFloatable(false);

        add(showGraphViewerCommand());
        add(previousTextPaneCommand());
        add(nextTextPaneCommand());

        add(decreaseTextSizeCommand());
        add(increaseTextSizeCommand());

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

    private JButton decreaseTextSizeCommand() {
        JButton command = new JButton (KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
        command.setToolTipText("Decrease text size");
        command.addActionListener((ActionEvent e) -> {
            for (TextPane textPane : textViewer.getTextPaneMap().values()) {
                StyledDocument doc = textPane.getStyledDocument();
                MutableAttributeSet attrs = textPane.getInputAttributes();
                Font font = doc.getFont(attrs);
                StyleConstants.setFontSize(attrs, font.getSize() - 2);
                doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
                textPane.repaint();
            }
        });
        return command;
    }

    private JButton increaseTextSizeCommand() {
        JButton command = new JButton (KnowtatorIcons.getIcon(KnowtatorIcons.INCREASE_TEXT_SIZE_ICON));
        command.setToolTipText("Increase text size");
        command.addActionListener((ActionEvent e) -> {
            for (TextPane textPane : textViewer.getTextPaneMap().values()) {
                StyledDocument doc = textPane.getStyledDocument();
                MutableAttributeSet attrs = textPane.getInputAttributes();
                Font font = doc.getFont(attrs);
                StyleConstants.setFontSize(attrs, font.getSize() + 2);
                doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
                textPane.repaint();
            }
        });

        return command;
    }
    private JButton previousTextPaneCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.PREVIOUS_ICON));
        command.setToolTipText("Previous document");
        command.addActionListener(e -> textViewer.showPreviousTextPane());
        return command;
    }


    private JButton nextTextPaneCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.NEXT_ICON));
        command.setToolTipText("Next document");
        command.addActionListener(e -> textViewer.showNextTextPane());
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

