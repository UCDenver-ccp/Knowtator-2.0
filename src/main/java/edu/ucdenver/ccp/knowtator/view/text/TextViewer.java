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
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceListener;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewer;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;

public class TextViewer extends JPanel implements TextSourceListener, ProjectListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    private boolean first;
    private KnowtatorManager manager;
    private KnowtatorView view;
    private int currentTextPaneIndex;
    private List<TextPane> textPaneList;
    private JScrollPane scrollPane;
    private JLabel documentLabel;

    public TextViewer(KnowtatorManager manager, KnowtatorView view) {
        this.manager = manager;
        this.view = view;
        textPaneList = new ArrayList<>();
        scrollPane = new JScrollPane();
        documentLabel = new JLabel();
        setLayout(new BorderLayout());
        first = true;
    }

    private void addNewDocument(TextSource textSource) {
        TextPane newTextPane = new TextPane(manager, view, textSource);
        if (textSource != null) {
            newTextPane.setName(textSource.getDocID());
            try {
                newTextPane.read(new FileReader(textSource.getFile()), textSource.getFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            log.warn("TextViewer: adding: " + textSource.getDocID());
        }

        textPaneList.add(newTextPane);
        showTextPane(textPaneList.size() - 1);
    }

    private void showTextPane(int indexToShow) {
        boolean graphViewerWasVisible = false;


        if (first) {
            log.warn("Adding first document");

            JPanel subPanel = new JPanel();
            subPanel.setLayout(new BorderLayout());
            subPanel.add(new AnnotationToolBar(this), BorderLayout.NORTH);
            subPanel.add(documentLabel, BorderLayout.SOUTH);

            add(subPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            first = false;
        } else {
            TextPane previousTextPane = textPaneList.get(currentTextPaneIndex);
            GraphViewer graphViewer = previousTextPane.getGraphViewer();

            if (graphViewer != null) {
                graphViewerWasVisible = graphViewer.getDialog().isVisible();
                graphViewer.getDialog().setVisible(false);
            }

            scrollPane.remove(previousTextPane);
            previousTextPane.setIsVisible(false);
        }

        TextPane currentTextPane = textPaneList.get(indexToShow);
        documentLabel.setText(currentTextPane.getTextSource().getDocID());
        scrollPane.setViewportView(currentTextPane);
        currentTextPane.setIsVisible(true);
        currentTextPaneIndex = indexToShow;
        if (graphViewerWasVisible) currentTextPane.getGraphViewer().getDialog().setVisible(true);

//        currentTextPane.setSelection(null, null);
        currentTextPane.refreshHighlights();
    }




    @Override
    public void textSourceAdded(TextSource textSource) {
        addNewDocument(textSource);

    }

    public TextPane getCurrentTextPane() {
        return textPaneList.get(currentTextPaneIndex);
    }

    public void showPreviousTextPane() {
        showTextPane(Math.max(0, currentTextPaneIndex - 1));
    }

    public void showNextTextPane() {
        showTextPane(Math.min(textPaneList.size()-1, currentTextPaneIndex + 1));
    }

    public List<TextPane> getTextPaneList() {
        return textPaneList;
    }

    @Override
    public void projectLoaded() {
        List<Map.Entry<String,TextSource>> textsourceEntries = new ArrayList<>(manager.getTextSourceManager().getTextSources().entrySet());
        textsourceEntries.sort(Comparator.comparing(Map.Entry::getKey));
        textsourceEntries.forEach(entry -> addNewDocument(entry.getValue()));
    }
}
