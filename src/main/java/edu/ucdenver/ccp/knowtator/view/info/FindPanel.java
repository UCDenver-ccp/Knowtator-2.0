/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.info;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.text.TextPane;
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
    private KnowtatorView view;

    public FindPanel(KnowtatorView view) {
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

            int matchLoc = textToSearch.indexOf(textToFind, currentTextPane.getSelectionStart()+1);

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


            int matchLoc = textToSearch.lastIndexOf(textToFind, currentTextPane.getSelectionStart()-1);

            if (matchLoc != -1) {
                currentTextPane.requestFocusInWindow();
                currentTextPane.select(matchLoc, matchLoc + textToFind.length());
            } else {
                currentTextPane.setSelectionStart(-1);
            }
        };
    }



}
