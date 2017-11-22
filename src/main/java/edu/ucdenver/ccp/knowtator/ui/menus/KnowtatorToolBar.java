package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
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

        add(previousSpanCommand());
        add(growSelectionStartCommand());
        add(shrinkSelectionStartCommand());
        add(shrinkSelectionEndCommand());
        add(growSelectionEndCommand());
        add(nextSpanCommand());
    }

    private JButton decreaseTextSizeCommand() {
         JButton decreaseTextSize = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));

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
        JButton menuItem = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        menuItem.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().shrinkSelectionStart());
        return menuItem;
    }

    private JButton shrinkSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().shrinkSelectionEnd());
        return button;
    }

    private JButton growSelectionStartCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.LEFT_ICON));
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().growSelectionStart());
        return button;
    }

    private JButton growSelectionEndCommand() {
        JButton button = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.RIGHT_ICON));
        button.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().growSelectionEnd());
        return button;
    }

    private JButton nextSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.NEXT_ICON));
        command.addActionListener((ActionEvent e) -> view.getTextViewer().getSelectedTextPane().nextSpan());

        return command;
    }

    private JButton previousSpanCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.PREVIOUS_ICON));
        command.addActionListener((ActionEvent e) ->view.getTextViewer().getSelectedTextPane().previousSpan());

        return command;
    }
}

