package edu.ucdenver.ccp.knowtator.ui.text;

import edu.ucdenver.ccp.knowtator.KnowtatorDocumentHandler;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.text.Span;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import other.RectanglePainter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.lang.Math.min;

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
            if (selectedAnnotation != null) {
                highlightSelectedAnnotation(selectedTextPane, selectedAnnotation);
            }

            // Create a map that is ordered by start of span
            Map<List<Integer>, Color> spanMap = new TreeMap<>((o1, o2) -> {
                int firstCompare = o1.get(0).compareTo(o2.get(0));
                if (firstCompare == 0) {
                    return o1.get(1).compareTo(o2.get(1));
                }
                return firstCompare;
            });

            // Accumulate all spans
            for (Annotation annotation : annotations) {
                for (Span span : annotation.getSpans()) {
                    spanMap.put(Arrays.asList(span.getStart(), span.getEnd()), annotation.getAnnotator().getColor(annotation.getClassName()));
                }
            }

            // Highlight overlaps first, then spans
            List<Integer> lastSpan = Arrays.asList(0, 0);
            Color lastColor = Color.WHITE;
            for(List<Integer> span : spanMap.keySet())  {
                if (span.get(0) >= lastSpan.get(0) && span.get(0) <= lastSpan.get(1)) {
                    try {
                        selectedTextPane.getHighlighter().addHighlight(span.get(0), min(span.get(1), lastSpan.get(1)), new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY));
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    selectedTextPane.getHighlighter().addHighlight(lastSpan.get(0), lastSpan.get(1), new DefaultHighlighter.DefaultHighlightPainter(lastColor));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
                lastSpan = span;
                lastColor = spanMap.get(span);
            }
            // Highlight remaining span
            try {
                selectedTextPane.getHighlighter().addHighlight(lastSpan.get(0), lastSpan.get(1), new DefaultHighlighter.DefaultHighlightPainter(lastColor));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

        }
        selectedTextPane.revalidate();
        selectedTextPane.repaint();
    }

    private void highlightSelectedAnnotation(KnowtatorTextPane textPane, Annotation annotation) {
        for (Span span : annotation.getSpans()) {
            try {
                textPane.getHighlighter().addHighlight(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
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
