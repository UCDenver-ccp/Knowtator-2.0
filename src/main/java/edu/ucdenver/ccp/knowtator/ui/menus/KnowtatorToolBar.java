package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class KnowtatorToolBar extends JToolBar {

    private final BasicKnowtatorView view;

    public KnowtatorToolBar(BasicKnowtatorView view) {
        this.view = view;

        add(decreaseTextSizeCommand());
        add(increaseTextSizeCommand());

        add(addAnnotationCommand());
        add(previousSpanCommand());
        add(growSelectionStartCommand());
        add(shrinkSelectionStartCommand());
        add(shrinkSelectionEndCommand());
        add(growSelectionEndCommand());
        add(nextSpanCommand());
        add(removeAnnotationCommand());
    }

    private JButton addAnnotationCommand() {
        JButton addAnnotationButton = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.ADD_ICON));
        addAnnotationButton.setToolTipText("Add annotation");
        addAnnotationButton.addActionListener(e -> view.getTextViewer().getSelectedTextPane().addSelectedAnnotation());

        return addAnnotationButton;
    }

    private JButton removeAnnotationCommand() {
        JButton removeAnnotationButton = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE_ICON));
        removeAnnotationButton.setToolTipText("Remove annotation");
        removeAnnotationButton.addActionListener(e -> view.getTextViewer().getSelectedTextPane().removeSelectedAnnotation());

        return removeAnnotationButton;
    }

    private JButton decreaseTextSizeCommand() {
         JButton decreaseTextSize = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
        decreaseTextSize.setToolTipText("Decrease text size");
         decreaseTextSize.addActionListener((ActionEvent e) -> {

            TextPane textPane = view.getTextViewer().getSelectedTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            MutableAttributeSet attrs = textPane.getInputAttributes();
            Font font = doc.getFont(attrs);
            StyleConstants.setFontSize(attrs, font.getSize() - 2);
            doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
            textPane.repaint();

        });
        return decreaseTextSize;
    }

    private JButton increaseTextSizeCommand() {
        JButton increaseTextSize = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.INCREASE_TEXT_SIZE_ICON));
        increaseTextSize.setToolTipText("Increase text size");
        increaseTextSize.addActionListener((ActionEvent e) -> {
            TextPane textPane = view.getTextViewer().getSelectedTextPane();
            StyledDocument doc = textPane.getStyledDocument();
            MutableAttributeSet attrs = textPane.getInputAttributes();
            Font font = doc.getFont(attrs);
            StyleConstants.setFontSize(attrs, font.getSize() + 2);
            doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
            textPane.repaint();
        });

        return increaseTextSize;
    }

    private JButton shrinkSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Shrink selection start");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().shrinkSelectionStart());
        return button;
    }

    private JButton shrinkSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Shrink selection end");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().shrinkSelectionEnd());
        return button;
    }

    private JButton growSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.setToolTipText("Grow selection start");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().growSelectionStart());
        return button;
    }

    private JButton growSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.setToolTipText("Grow selection end");
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().growSelectionEnd());
        return button;
    }

    private JButton nextSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.NEXT_ICON));
        command.setToolTipText("Next span");
        command.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().nextSpan());

        return command;
    }

    private JButton previousSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.PREVIOUS_ICON));
        command.setToolTipText("Previous span");
        command.addActionListener((ActionEvent e) ->view.getTextViewer().getSelectedTextPane().previousSpan());

        return command;
    }

}

