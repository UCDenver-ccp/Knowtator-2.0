package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceListener;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewer;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TextViewer extends JPanel implements TextSourceListener, ProjectListener {
    private static final Logger log = Logger.getLogger(KnowtatorController.class);
    private boolean first;
    private KnowtatorController controller;
    private KnowtatorView view;
    private int currentTextPaneIndex;
    private List<TextPane> textPaneList;
    private JScrollPane scrollPane;
    private JLabel documentLabel;

    public TextViewer(KnowtatorController controller, KnowtatorView view) {
        this.controller = controller;
        this.view = view;
        textPaneList = new ArrayList<>();
        scrollPane = new JScrollPane();
        documentLabel = new JLabel();
        setLayout(new BorderLayout());
        first = true;
    }

    private void addNewDocument(TextSource textSource) {
        TextPane newTextPane = new TextPane(controller, view, textSource);
        if (textSource != null) {
            newTextPane.setName(textSource.getDocID());
            try {
                newTextPane.read(new FileReader(textSource.getTextFile()), textSource.getTextFile());
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

    void showPreviousTextPane() {
        showTextPane(Math.max(0, currentTextPaneIndex - 1));
    }

    void showNextTextPane() {
        showTextPane(Math.min(textPaneList.size()-1, currentTextPaneIndex + 1));
    }

    List<TextPane> getTextPaneList() {
        return textPaneList;
    }

    @Override
    public void projectLoaded() {
        List<TextSource> textsourceEntries = new ArrayList<>(controller.getTextSourceManager().getTextSources());
        textsourceEntries.sort(Comparator.comparing(TextSource::getDocID));
        textsourceEntries.forEach(this::addNewDocument);
        showTextPane(0);
    }
}
