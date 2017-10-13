package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;

import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class TextCommands {

    private KnowtatorManager manager;

    public TextCommands(KnowtatorManager manager) {

        this.manager = manager;
    }

    public KnowtatorCommand getDecreaseTextSizeCommand() {
        return new KnowtatorCommand(manager, "Decrease Text Size", KnowtatorIcons.DECREASE_TEXT_SIZE_ICON, "Decrease the document text size") {


            @Override
            public void actionPerformed(ActionEvent e) {
                KnowtatorTextPane textPane = manager.getKnowtatorView().getTextViewer().getSelectedTextPane();
                StyledDocument doc = textPane.getStyledDocument();
                MutableAttributeSet attrs = textPane.getInputAttributes();
                Font font = doc.getFont(attrs);
                StyleConstants.setFontSize(attrs, font.getSize() - 2);
                doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
                textPane.repaint();
            }
        };
    }

    public KnowtatorCommand getIncreaseTextSizeCommand() {
        return new KnowtatorCommand(manager, "Increase Text Size", KnowtatorIcons.INCREASE_TEXT_SIZE_ICON, "Increase the document text size") {


            @Override
            public void actionPerformed(ActionEvent e) {
                KnowtatorTextPane textPane = manager.getKnowtatorView().getTextViewer().getSelectedTextPane();
                StyledDocument doc = textPane.getStyledDocument();
                MutableAttributeSet attrs = textPane.getInputAttributes();
                Font font = doc.getFont(attrs);
                StyleConstants.setFontSize(attrs, font.getSize() + 2);
                doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
                textPane.repaint();
            }
        };
    }

    public KnowtatorCommand getDecrementSelectionLeftCommand() {
        return new KnowtatorCommand(manager, "Decrement left selection", KnowtatorIcons.INCREMENT_LEFT, "Decrement selection left") {


            @Override
            public void actionPerformed(ActionEvent e) {
                KnowtatorTextPane textPane = manager.getKnowtatorView().getTextViewer().getSelectedTextPane();
                textPane.setSelectionStart(textPane.getSelectionStart() + 1);

            }
        };
    }

    public KnowtatorCommand getDecrementSelectionRightCommand() {
        return new KnowtatorCommand(manager, "Decrement right selection", KnowtatorIcons.INCREMENT_RIGHT, "Decrement selection right") {


            @Override
            public void actionPerformed(ActionEvent e) {
                KnowtatorTextPane textPane = manager.getKnowtatorView().getTextViewer().getSelectedTextPane();
                textPane.setSelectionEnd(textPane.getSelectionEnd() - 1);

            }
        };
    }

    public KnowtatorCommand getIncrementSelectionLeftCommand() {
        return new KnowtatorCommand(manager, "Increment left selection", KnowtatorIcons.INCREMENT_RIGHT, "Increment selection left") {

            @Override
            public void actionPerformed(ActionEvent e) {
                KnowtatorTextPane textPane = manager.getKnowtatorView().getTextViewer().getSelectedTextPane();
                textPane.setSelectionStart(textPane.getSelectionStart() - 1);
            }
        };
    }

    public KnowtatorCommand getIncrementSelectionRightCommand() {
        return new KnowtatorCommand(manager, "Increment right selection", KnowtatorIcons.INCREMENT_LEFT, "Increment selection right") {

            @Override
            public void actionPerformed(ActionEvent e) {
                KnowtatorTextPane textPane = manager.getKnowtatorView().getTextViewer().getSelectedTextPane();
                textPane.setSelectionEnd(textPane.getSelectionEnd() + 1);

            }
        };
    }
}

