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

class DocumentToolBar extends JToolBar {

    private TextViewer textViewer;
    private KnowtatorController controller;
    private JComboBox<TextSource> comboBox;

    DocumentToolBar(TextViewer textViewer, KnowtatorController controller) {
        this.textViewer = textViewer;
        this.controller = controller;

        setFloatable(false);

        add(decreaseTextSizeCommand());
        add(increaseTextSizeCommand());

        add(previousTextPaneCommand());
        add(documentMenuCommand());
        add(nextTextPaneCommand());

    }

    private JComboBox<TextSource> documentMenuCommand() {
        comboBox = new JComboBox<>();

        comboBox.addActionListener(e -> {
            JComboBox comboBox = (JComboBox) e.getSource();
            if (comboBox.getSelectedItem() != null && comboBox.getSelectedItem() != controller.getSelectionManager().getActiveTextSource()) {
                textViewer.showTextPane((TextSource) comboBox.getSelectedItem());
            }
        });

//        updateComboBox(controller.);

        return comboBox;
    }

    void updateComboBox(TextSource textSource) {
        comboBox.removeAllItems();
        textViewer.getTextPaneMap().keySet().forEach(textSource1 -> comboBox.addItem(textSource1));
        comboBox.setSelectedItem(textSource);
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

    private JButton decreaseTextSizeCommand() {
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
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
        JButton command = new JButton(KnowtatorIcons.getIcon(KnowtatorIcons.INCREASE_TEXT_SIZE_ICON));
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
}
