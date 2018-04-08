package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorIcons;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

class TextSourceToolBar extends JToolBar {

    private KnowtatorController controller;
    private JComboBox<TextSource> textSourceChooser;

    TextSourceToolBar(KnowtatorController controller) {
        this.controller = controller;

        setFloatable(false);

        add(decreaseTextSizeCommand());
        add(increaseTextSizeCommand());

        add(previousTextPaneCommand());
        add(nextTextPaneCommand());
        add(textSourceChooserCommand());

    }

    private JComboBox<TextSource> textSourceChooserCommand() {
        textSourceChooser = new JComboBox<>();

        textSourceChooser.addActionListener(e -> {
            JComboBox comboBox = (JComboBox) e.getSource();
            if (comboBox.getSelectedItem() != null && comboBox.getSelectedItem() != controller.getSelectionManager().getActiveTextSource()) {
                controller.getView().getTextViewer().showTextPane((TextSource) comboBox.getSelectedItem());
            }
        });

//        update(controller.);

        return textSourceChooser;
    }

    void update(TextSource textSource) {
        textSourceChooser.removeAllItems();
        controller.getView().getTextViewer().getTextPaneMap().keySet().forEach(textSource1 -> textSourceChooser.addItem(textSource1));
        textSourceChooser.setSelectedItem(textSource);
    }

    private JButton previousTextPaneCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.UP_ICON));
        command.setToolTipText("Previous document");
        command.addActionListener(e -> controller.getView().getTextViewer().showPreviousTextPane());
        return command;
    }


    private JButton nextTextPaneCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.DOWN_ICON));
        command.setToolTipText("Next document");
        command.addActionListener(e -> controller.getView().getTextViewer().showNextTextPane());
        return command;
    }

    private JButton decreaseTextSizeCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
        command.setToolTipText("Decrease text size");
        command.addActionListener((ActionEvent e) -> {
            for (TextPane textPane : controller.getView().getTextViewer().getTextPaneMap().values()) {
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
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.INCREASE_TEXT_SIZE_ICON));
        command.setToolTipText("Increase text size");
        command.addActionListener((ActionEvent e) -> {
            for (TextPane textPane : controller.getView().getTextViewer().getTextPaneMap().values()) {
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


}
