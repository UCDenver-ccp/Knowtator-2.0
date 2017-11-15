package edu.ucdenver.ccp.knowtator.ui.text;

import edu.ucdenver.ccp.knowtator.KnowtatorDocumentHandler;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@SuppressWarnings("PackageAccessibility")
public class KnowtatorTextViewer extends JTabbedPane implements AnnotationListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    public KnowtatorManager manager;

    public KnowtatorTextViewer(KnowtatorManager manager) {
        super();
        this.manager = manager;
    }

    public void addNewDocument(String fileName, Boolean fromResources) {
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

    public KnowtatorTextPane getTextPaneByIndex(int i) {
        JScrollPane scrollPane = (JScrollPane) getComponent(i);
        JViewport viewPort = (scrollPane).getViewport();
        return (KnowtatorTextPane) viewPort.getView();
    }

    public KnowtatorTextPane getSelectedTextPane() {
        return (KnowtatorTextPane) ((JScrollPane) getSelectedComponent()).getViewport().getView();
    }

    public void refreshHighlights() {
        KnowtatorTextPane selectedTextPane = getSelectedTextPane();

        selectedTextPane.getHighlighter().removeAllHighlights();
//TODO: Check for overlapping regions (highlight those in gray)
        Collection<Annotation> annotations = manager.getAnnotationManager().getTextAnnotations().get(selectedTextPane.getName());
        if (annotations != null) {
            Annotation selectedAnnotation = manager.getAnnotationManager().getSelectedAnnotation();

            // Apply highlights to annotations. Make sure to highlight the selected annotation first.
            if (selectedAnnotation != null) {
                selectedAnnotation.getSpans().forEach(
                        span -> selectedTextPane.highlightAnnotation(span.getStart(), span.getEnd(), selectedAnnotation.getClassName(), true)
                );
            }
            for (Annotation annotation : annotations) {
                if (annotation != selectedAnnotation) {
                    annotation.getSpans().forEach(
                            span -> selectedTextPane.highlightAnnotation(span.getStart(), span.getEnd(), annotation.getClassName(), false)
                    );
                }
            }

        }
        selectedTextPane.revalidate();
        selectedTextPane.repaint();
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
    public void annotationsChanged(Annotation annotation) {
        refreshHighlights();
    }
}
