package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.KnowtatorDocumentHandler;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.listeners.TextAnnotationListener;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("PackageAccessibility")
public class KnowtatorTextViewer extends JTabbedPane implements TextAnnotationListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    public KnowtatorManager manager;

    public KnowtatorTextViewer(KnowtatorManager manager) {
        super();
        this.manager = manager;
    }

    public void addNewDocument(String fileName, Boolean fromResources) {
        log.warn(String.format("Name: %s", FilenameUtils.getBaseName(fileName)));
        KnowtatorTextPane textPane = new KnowtatorTextPane(manager);
        textPane.setName(FilenameUtils.getBaseName(fileName));
        manager.documentChangedEvent(textPane);

        JScrollPane sp = new JScrollPane(textPane);
        if (getTabCount() == 1 && getTitleAt(0).equals("Untitled")) {
            setComponentAt(0, sp);
        } else {
            add(sp);
        }
        setTitleAt(getTabCount() - 1, FilenameUtils.getBaseName(fileName));

        try {
            textPane.read(KnowtatorDocumentHandler.getReader(fileName, fromResources), fileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addDocuments(List<String> articleFileNames, Boolean fromResources) {
        for(String articleFileName: articleFileNames) {
            addNewDocument(articleFileName, fromResources);
        }
    }

    @SuppressWarnings("unused")
    public KnowtatorTextPane getTextPaneByName(String name) {
        for (int i = 0; i < getTabCount(); i++) {
            if (Objects.equals(getTitleAt(i), name)) {
                return getTextPaneByIndex(i);
            }
        }
        return null;
    }

    @SuppressWarnings("unused")
    public ArrayList<KnowtatorTextPane> getAllTextPanes() {
        ArrayList<KnowtatorTextPane> textPanes = new ArrayList<>();

        for (Component component: getComponents()) {
            textPanes.add((KnowtatorTextPane) ((JScrollPane) component).getViewport().getView());
        }

        return textPanes;
    }

    @SuppressWarnings("unused")
    public KnowtatorTextPane getTextPaneByIndex(int i) {
        JScrollPane scrollPane = (JScrollPane) getComponent(i);
        JViewport viewPort = (scrollPane).getViewport();
        return (KnowtatorTextPane) viewPort.getView();
    }

    public KnowtatorTextPane getSelectedTextPane() {
        return (KnowtatorTextPane) ((JScrollPane) getSelectedComponent()).getViewport().getView();
    }

    public void refreshHighlights() {
        KnowtatorTextPane textPane = getSelectedTextPane();

        textPane.getHighlighter().removeAllHighlights();
        manager.getTextAnnotationManager().getTextAnnotations().get(textPane.getName()).forEach(
                textAnnotation -> {
                    Boolean selectAnnotation = textAnnotation == manager.getTextAnnotationManager().selectedTextAnnotation;
                    textAnnotation.getTextSpans().forEach(
                            textSpan -> textPane.highlightAnnotation(textSpan.getStart(), textSpan.getEnd(), textAnnotation.getOwlClass(), selectAnnotation)
                    );
                }
        );
    }

    public void closeSelectedDocument() {

        if (getTabCount() > 1) {
            remove(getSelectedIndex());
        } else {
            setComponentAt(0, new KnowtatorTextPane(manager));
            setTitleAt(0, "Untitled");
        }
    }



    @Override
    public void textAnnotationsChanged() {
        refreshHighlights();
    }

    @Override
    public void textAnnotationsChanged(TextAnnotation newAnnotation) {

    }
}
