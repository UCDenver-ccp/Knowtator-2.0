package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceListener;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextViewer extends JPanel implements TextSourceListener, ProjectListener {
    private static final Logger log = Logger.getLogger(KnowtatorController.class);
    private boolean first;
    private KnowtatorController controller;
    private KnowtatorView view;
    private Map<TextSource, TextPane> textPaneMap;
    private JScrollPane scrollPane;
    private JLabel documentLabel;

    public TextViewer(KnowtatorController controller, KnowtatorView view) {
        this.controller = controller;
        this.view = view;
        textPaneMap = new HashMap<>();
        scrollPane = new JScrollPane();
        documentLabel = new JLabel();
        setLayout(new BorderLayout());
        first = true;
    }

    private void addNewDocument(TextSource textSource) {
        TextPane newTextPane = new TextPane(view, controller, textSource);
        if (textSource != null) {
            newTextPane.setName(textSource.getDocID());
            try {
                newTextPane.read(new FileReader(textSource.getTextFile()), textSource.getTextFile());
            } catch (IOException e) {
                e.printStackTrace();
            }

            log.warn("TextViewer: adding: " + textSource.getDocID());
        }

        textPaneMap.put(textSource, newTextPane);

        showTextPane(textSource);
    }

    private void showTextPane(TextSource textSource) {
        if (first) {
            log.warn("Adding first document");

            JPanel subPanel = new JPanel();
            subPanel.setLayout(new BorderLayout());
            subPanel.add(new AnnotationToolBar(this, view), BorderLayout.NORTH);
            subPanel.add(documentLabel, BorderLayout.SOUTH);

            add(subPanel, BorderLayout.NORTH);
            add(scrollPane, BorderLayout.CENTER);

            first = false;
        } else {
            TextPane previousTextPane = textPaneMap.get(controller.getSelectionManager().getActiveTextSource());
            if (previousTextPane != null) {
                scrollPane.remove(previousTextPane);
                previousTextPane.setIsVisible(false);
            }
        }

        TextPane currentTextPane = textPaneMap.get(textSource);
        controller.getSelectionManager().setActiveTextSource(textSource);

        documentLabel.setText(currentTextPane.getTextSource().getDocID());
        scrollPane.setViewportView(currentTextPane);
        currentTextPane.setIsVisible(true);

        currentTextPane.refreshHighlights();

        view.getGraphViewer().removeAllGraphs();
        for (GraphSpace graphSpace : textSource.getAnnotationManager().getGraphSpaces()) {
            view.getGraphViewer().addGraph(graphSpace);
            graphSpace.connectEdgesToProperties();
        }
    }




    @Override
    public void textSourceAdded(TextSource textSource) {
        addNewDocument(textSource);

    }

    public TextPane getCurrentTextPane() {
        return textPaneMap.get(controller.getSelectionManager().getActiveTextSource());
    }

    void showPreviousTextPane() {
        showTextPane(controller.getTextSourceManager().getPreviousTextSource());
    }

    void showNextTextPane() {
        showTextPane(controller.getTextSourceManager().getNextTextSource());
    }

    Map<TextSource, TextPane> getTextPaneMap() {
        return textPaneMap;
    }

    @Override
    public void projectLoaded() {
        controller.getTextSourceManager().getTextSources().forEach(this::addNewDocument);
        showTextPane(controller.getTextSourceManager().getTextSources().get(0));
    }
}
