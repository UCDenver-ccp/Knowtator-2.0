package edu.ucdenver.ccp.knowtator.ui.info;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.ActionListener;

public class FindPanel extends JPanel {
    private static Logger log = LogManager.getLogger(FindPanel.class);
    private JTextField textField;
    private BasicKnowtatorView view;
    private int lastMatch;

    public FindPanel(BasicKnowtatorView view) {
        this.view = view;
        textField = new JTextField(20);
        JButton nextButton = new JButton("Next");
        JButton previousButton = new JButton("Previous");

        lastMatch = -1;

        nextButton.addActionListener(getNextMatch());
        previousButton.addActionListener(getPreviousMatch());



        add(previousButton);
        add(textField);
        add(nextButton);

    }

    private ActionListener getNextMatch() {
        return e -> {
            String textToFind = textField.getText();
            TextPane currentTextPane = view.getTextViewer().getSelectedTextPane();
            log.warn(String.format("Text to find: %s", textToFind));
            int matchLoc = currentTextPane.getText().indexOf(textToFind, lastMatch+1);


            log.warn(String.format("Match loc: %d", matchLoc));
            if (matchLoc != -1) {
                currentTextPane.requestFocusInWindow();
                currentTextPane.select(matchLoc, matchLoc + textToFind.length());
            }

            lastMatch = matchLoc;
        };
    }

    private ActionListener getPreviousMatch() {
        return e -> {
            String textToFind = textField.getText();
            TextPane currentTextPane = view.getTextViewer().getSelectedTextPane();
            log.warn(String.format("Text to find: %s", textToFind));
            int matchLoc = currentTextPane.getText().lastIndexOf(textToFind, lastMatch-1);


            log.warn(String.format("Match loc: %d", matchLoc));
            if (matchLoc != -1) {
                currentTextPane.requestFocusInWindow();
                currentTextPane.select(matchLoc, matchLoc + textToFind.length());
            } else {
                matchLoc = currentTextPane.getText().length();
            }
            lastMatch = matchLoc;
        };
    }



}
