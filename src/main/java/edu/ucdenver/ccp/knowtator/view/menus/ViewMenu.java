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

package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.text.TextPane;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.event.ActionEvent;

public class ViewMenu extends JMenu {

    private KnowtatorView view;
    private KnowtatorManager manager;

    public ViewMenu(KnowtatorView view, KnowtatorManager manager) {
        super("View");
        this.view = view;
        this.manager = manager;

        add(increaseTextSizeCommand());
        add(decreaseTextSizeCommand());
        addSeparator();
        add(showForCurrentProfileCommand());
        addSeparator();
        add(showGraphViewerCommand());
    }

    private JMenuItem decreaseTextSizeCommand() {
        JMenuItem decreaseTextSize = new JMenuItem("Increase text size", KnowtatorIcons.getIcon(KnowtatorIcons.DECREASE_TEXT_SIZE_ICON));
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

    private JMenuItem increaseTextSizeCommand() {
        JMenuItem increaseTextSize = new JMenuItem("Increase text size", KnowtatorIcons.getIcon(KnowtatorIcons.INCREASE_TEXT_SIZE_ICON));
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

    private JCheckBoxMenuItem showForCurrentProfileCommand() {
        JCheckBoxMenuItem showForCurrentProfile = new JCheckBoxMenuItem("Show only annotations for current profile");
        showForCurrentProfile.addActionListener(e -> {
            if (showForCurrentProfile.getState()) manager.profileFilterEvent(true);
            else manager.profileFilterEvent(false);
        });

        return  showForCurrentProfile;
    }

    private JMenuItem showGraphViewerCommand() {
        JMenuItem menuItem = new JMenuItem("Show graph viewer");
        menuItem.addActionListener(e -> view.getTextViewer().getSelectedTextPane().getGraphDialog().showViewer());

        return menuItem;
    }
}
