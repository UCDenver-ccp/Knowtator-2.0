package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.*;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TextViewer extends JPanel implements TextSourceListener, ProjectListener, AnnotationListener, SpanListener, ProfileListener {
    private static final Logger log = Logger.getLogger(KnowtatorController.class);
    private boolean first;
    private KnowtatorController controller;
    private KnowtatorView view;
    private Map<TextSource, TextPane> textPaneMap;
    private JScrollPane scrollPane;
    private DocumentToolBar documentToolBar;

    public TextViewer(KnowtatorController controller, KnowtatorView view) {
        this.controller = controller;
        this.view = view;
        textPaneMap = new HashMap<>();
        scrollPane = new JScrollPane();
        documentToolBar = new DocumentToolBar(this, controller);
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

    void showTextPane(TextSource textSource) {
        if (first) {
            log.warn("Adding first document");

            JPanel subPanel = new JPanel();
            subPanel.setLayout(new BorderLayout());
            subPanel.add(documentToolBar, BorderLayout.NORTH);
            subPanel.add(new AnnotationToolBar(this, view), BorderLayout.SOUTH);

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

        scrollPane.setViewportView(currentTextPane);
        currentTextPane.setIsVisible(true);

        controller.getSelectionManager().setActiveTextSource(textSource);
    }

    @Override
    public void textSourceAdded(TextSource textSource) {
        addNewDocument(textSource);
    }

    @Override
    public void activeTextSourceChanged(TextSource textSource) {
        getCurrentTextPane().refreshHighlights();
        documentToolBar.updateComboBox(textSource);
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
        showTextPane(controller.getSelectionManager().getActiveTextSource());
    }

    @Override
    public void annotationAdded(Annotation newAnnotation) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void annotationRemoved(Annotation removedAnnotation) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void annotationSelectionChanged(Annotation annotation) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void spanAdded(Span newSpan) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void spanRemoved() {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void spanSelectionChanged(Span span) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void profileAdded(Profile profile) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void profileRemoved() {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void profileSelectionChanged(Profile profile) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }

    @Override
    public void colorChanged() {
        if (getCurrentTextPane() != null) getCurrentTextPane().refreshHighlights();
    }
}
