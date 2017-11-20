package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class KnowtatorToolBar extends JToolBar {

    private final BasicKnowtatorView view;

    public KnowtatorToolBar(BasicKnowtatorView view) {
        this.view = view;

        add(decreaseTextSizeCommand());
        add(increaseTextSizeCommand());

        add(previousSpanCommand());
        add(incrementSelectionLeftCommand());
        add(decrementSelectionLeftCommand());
        add(decrementSelectionRightCommand());
        add(incrementSelectionRightCommand());
        add(nextSpanCommand());
    }

    private JButton decreaseTextSizeCommand() {
         JButton decreaseTextSize = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));

         decreaseTextSize.addActionListener(e -> {

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
        increaseTextSize.addActionListener(e -> {
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

    private JButton decrementSelectionLeftCommand() {
        JButton menuItem = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.INCREMENT_LEFT_ICON));
        menuItem.addActionListener(e -> {
                JTextPane textPane = view.getTextViewer().getSelectedTextPane();
                textPane.setSelectionStart(textPane.getSelectionStart() + 1);

        });
        return menuItem;
    }

    private JButton decrementSelectionRightCommand() {
        JButton menuItem = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.INCREMENT_RIGHT_ICON));
        menuItem.addActionListener(e -> {
            TextPane textPane = view.getTextViewer().getSelectedTextPane();
            textPane.setSelectionEnd(textPane.getSelectionEnd() - 1);});

        return menuItem;
    }

    private JButton incrementSelectionLeftCommand() {
        JButton menuItem = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.INCREMENT_RIGHT_ICON));
        menuItem.addActionListener(e -> {
            JTextPane textPane = view.getTextViewer().getSelectedTextPane();
            textPane.setSelectionStart(textPane.getSelectionStart() - 1);
        });
        return menuItem;
    }

    private JButton incrementSelectionRightCommand() {
        JButton menuItem = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.INCREMENT_LEFT_ICON));
        menuItem.addActionListener(e -> {
            JTextPane textPane = view.getTextViewer().getSelectedTextPane();
            textPane.setSelectionEnd(textPane.getSelectionEnd() + 1);});

        return menuItem;
    }

    private JButton nextSpanCommand() {
        JButton menuItem = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.SELECT_NEXT_ICON));
        menuItem.addActionListener(e -> view.getTextViewer().getSelectedTextPane().nextSpan());

        return menuItem;
    }

    private JButton previousSpanCommand() {
        JButton menuItem = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.SELECT_NEXT_ICON));
        menuItem.addActionListener(e ->view.getTextViewer().getSelectedTextPane().previousSpan());

        return menuItem;
    }
}

