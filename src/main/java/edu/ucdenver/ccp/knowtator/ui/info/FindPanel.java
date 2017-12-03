package edu.ucdenver.ccp.knowtator.ui.info;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

//TODO: Regex search
public class FindPanel extends JPanel {
    private static Logger log = LogManager.getLogger(FindPanel.class);
    private final JCheckBox isCaseSensitive;
    private final JCheckBox isRegex;
    private JTextField textField;
    private BasicKnowtatorView view;

    public FindPanel(BasicKnowtatorView view) {
        this.view = view;
        setLayout(new GridLayout(2, 3));


        JButton previousButton = new JButton("Previous");
        previousButton.addActionListener(getPreviousMatch());
        add(previousButton, BorderLayout.WEST);

        textField = new JTextField(20);
        textField.setPreferredSize(new Dimension(20, 1));
        add(textField, BorderLayout.CENTER);

        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(getNextMatch());
        add(nextButton, BorderLayout.EAST);

        isCaseSensitive = new JCheckBox("Case Sensitive");
        add(isCaseSensitive, BorderLayout.SOUTH);

        isRegex = new JCheckBox("Regex");
        add(isRegex, BorderLayout.SOUTH);

    }

    private ActionListener getNextMatch() {
        return e -> {
            String textToFind = textField.getText();
            TextPane currentTextPane = view.getTextViewer().getSelectedTextPane();

            String textToSearch = currentTextPane.getText();
            if (!isCaseSensitive.isSelected()) {
                textToSearch = textToSearch.toLowerCase();
            }

            log.warn(String.format("Text to find: %s", textToFind));
            int matchLoc = textToSearch.indexOf(textToFind, currentTextPane.getSelectionStart()+1);


            log.warn(String.format("Match loc: %d", matchLoc));
            if (matchLoc != -1) {
                currentTextPane.requestFocusInWindow();
                currentTextPane.select(matchLoc, matchLoc + textToFind.length());
            } else {
                currentTextPane.setSelectionStart(textToSearch.length());
            }

        };
    }

    private ActionListener getPreviousMatch() {
        return e -> {
            String textToFind = textField.getText();
            TextPane currentTextPane = view.getTextViewer().getSelectedTextPane();
            String textToSearch = currentTextPane.getText();
            if (!isCaseSensitive.isSelected()) {
                textToSearch = textToSearch.toLowerCase();
            }


            log.warn(String.format("Text to find: %s", textToFind));
            int matchLoc = textToSearch.lastIndexOf(textToFind, currentTextPane.getSelectionStart()-1);


            log.warn(String.format("Match loc: %d", matchLoc));
            if (matchLoc != -1) {
                currentTextPane.requestFocusInWindow();
                currentTextPane.select(matchLoc, matchLoc + textToFind.length());
            } else {
                currentTextPane.setSelectionStart(-1);
            }
        };
    }



}
