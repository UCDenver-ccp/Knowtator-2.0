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

package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceListener;
import edu.ucdenver.ccp.knowtator.model.annotation.TextSource;
import org.apache.log4j.Logger;
import other.DnDTabbedPane;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TextViewer extends DnDTabbedPane implements TextSourceListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    private boolean realTabAdded;
    private KnowtatorManager manager;
    private KnowtatorView view;

    public TextViewer(KnowtatorManager manager, KnowtatorView view) {
        super();
        this.manager = manager;
        this.view = view;

        addDefaultTab();

    }

    private void addDefaultTab() {
        add("Untitled", new JScrollPane(new JTextPane()));
        realTabAdded = false;
    }

    private void addNewDocument(TextSource textSource) {
        TextPane textPane = new TextPane(manager, view, textSource);
        textPane.setName(textSource.getDocID());
        textPane.setText(textSource.getContent());

        addClosableTab(textPane, textSource.getDocID());
    }

    private void addClosableTab(TextPane textPane, String title) {
        JScrollPane sp = new JScrollPane(textPane);

        if (!realTabAdded) this.remove(0);
        addTab(title, sp);

        int index = indexOfTab(title);
        JPanel pnlTab = new JPanel(new GridBagLayout());
        pnlTab.setOpaque(false);
        JLabel lblTitle = new JLabel(title);
        JButton btnClose = new JButton("x");

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1;

        pnlTab.add(lblTitle, gbc);

        gbc.gridx++;
        gbc.weightx = 0;
        pnlTab.add(btnClose, gbc);

        setTabComponentAt(index, pnlTab);

        TextViewer textViewer = this;
        btnClose.addActionListener(evt -> {
            int result = JOptionPane.showConfirmDialog(null, String.format("Would you like to save changes to %s", title), "Closing document", JOptionPane.YES_NO_CANCEL_OPTION);
            switch (result) {
                case JOptionPane.YES_OPTION:
                    manager.getProjectManager().saveProject();
                case JOptionPane.NO_OPTION:
                    if (getTabCount() == 1) {
                        addDefaultTab();
                    }
                    manager.getTextSourceManager().remove(textPane.getTextSource());
                    textPane.getGraphDialog().setVisible(false);
                    textViewer.remove(sp);
                    break;
            }
        });

        setSelectedComponent(sp);

        realTabAdded = true;
    }

    public TextPane getSelectedTextPane() {
        if (getSelectedComponent() != null) {
            Component c = ((JScrollPane) getSelectedComponent()).getViewport().getView();
            if (c.getClass() == TextPane.class) {
                return (TextPane) ((JScrollPane) getSelectedComponent()).getViewport().getView();
            }
        }
        return null;
    }

    public List<TextPane> getAllTextPanes() {
        List<TextPane> textPaneList = new ArrayList<>();
        for (int i=0; i<getTabCount(); i++) {
            Component c = ((JScrollPane) getComponentAt(i)).getViewport().getView();
            if (c.getClass() == TextPane.class) {
                textPaneList.add((TextPane) c);
            }
        }
        return textPaneList;
    }

    @Override
    public void textSourceAdded(TextSource textSource) {
        addNewDocument(textSource);
        getSelectedTextPane().refreshHighlights();
    }

//    public void refreshAll() {
//        getAllTextPanes().forEach(TextPane::refreshHighlights);
//    }
}
