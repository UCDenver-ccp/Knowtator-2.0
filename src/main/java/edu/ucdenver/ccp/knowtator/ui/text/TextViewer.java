package edu.ucdenver.ccp.knowtator.ui.text;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.SpanListener;
import edu.ucdenver.ccp.knowtator.listeners.TextSourceListener;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.Logger;
import other.RectanglePainter;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import java.awt.*;
import java.util.Map;
import java.util.Objects;

import static java.lang.Math.min;

public class TextViewer extends JTabbedPane implements AnnotationListener, TextSourceListener, SpanListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    private BasicKnowtatorView view;

    public TextViewer(BasicKnowtatorView view) {
        super();
        this.view = view;
        view.addSpanListener(this);
        view.addAnnotationListener(this);
        view.addTextSourceListener(this);
    }

    private void addNewDocument(TextSource textSource) {
        TextPane textPane = new TextPane(view, textSource);
        textPane.setName(textSource.getDocID());
        textPane.setText(textSource.getContent());

        JScrollPane sp = new JScrollPane(textPane);
        addTab(sp, textSource.getDocID());
    }

    private void addTab(Component c, String title) {


        addTab(title, c);

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

        TextViewer tabPane = this;

        btnClose.addActionListener(evt -> tabPane.remove(c));


    }



    @SuppressWarnings("unused")
    public TextPane getTextPaneByName(String name) {
        for (int i = 0; i < getTabCount(); i++) {
            if (Objects.equals(getTitleAt(i), name)) {
                return getTextPaneByIndex(i);
            }
        }
        return null;
    }

    private TextPane getTextPaneByIndex(int i) {
        JScrollPane scrollPane = (JScrollPane) getComponent(i);
        JViewport viewPort = (scrollPane).getViewport();
        return (TextPane) viewPort.getView();
    }

    public TextPane getSelectedTextPane() {
        if (getSelectedComponent() != null) {
            return (TextPane) ((JScrollPane) getSelectedComponent()).getViewport().getView();
        }
        return null;
    }

    private void refreshHighlights() {
        TextPane selectedTextPane = getSelectedTextPane();

        // If no textpane is displayed, don't bother refreshing the highlights
        if (selectedTextPane != null) {

            //Remove all previous highlights in case a span has been deleted
            selectedTextPane.getHighlighter().removeAllHighlights();

            //Always highlight the selected annotation first so its color and border show up
            highlightSelectedAnnotation(selectedTextPane, selectedTextPane.getSelectedAnnotation());

            // Highlight overlaps first, then spans
            Span lastSpan = Span.makeDefaultSpan();
            Color lastColor = null;

            Map<Span, Annotation> annotationMap = selectedTextPane.getAnnotationMap();
            for (Span span : annotationMap.keySet()) {
                if (span.intersects(lastSpan)) {
                    try {
                        selectedTextPane.getHighlighter().addHighlight(span.getStart(), min(span.getEnd(), lastSpan.getEnd()), new DefaultHighlighter.DefaultHighlightPainter(Color.GRAY));
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
                if (span.getEnd() > lastSpan.getEnd()) {
                    try {
                        selectedTextPane.getHighlighter().addHighlight(lastSpan.getStart(), lastSpan.getEnd(), new DefaultHighlighter.DefaultHighlightPainter(lastColor));
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                    lastSpan = span;
                    lastColor = annotationMap.get(span).getColor();
                }
            }
            // Highlight remaining span
            try {
                selectedTextPane.getHighlighter().addHighlight(lastSpan.getStart(), lastSpan.getEnd(), new DefaultHighlighter.DefaultHighlightPainter(lastColor));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }

            selectedTextPane.revalidate();
            selectedTextPane.repaint();
        }
    }

    private void highlightSelectedAnnotation(TextPane textPane, Annotation annotation) {
        if (annotation != null) {
            for (Span span : annotation.getSpans()) {
                try {
                    textPane.getHighlighter().addHighlight(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void annotationAdded(Annotation newAnnotation) {
        refreshHighlights();
    }

    @Override
    public void annotationRemoved() {
        refreshHighlights();
    }

    @Override
    public void annotationSelectionChanged(Annotation annotation) {
        refreshHighlights();
    }

    @Override
    public void textSourceChanged(TextSource textSource) {
        refreshHighlights();
    }

    @Override
    public void textSourceAdded(TextSource textSource) {
        addNewDocument(textSource);
    }

    @Override
    public void textSourceRemoved() {

    }

    @Override
    public void spanAdded(Span newSpan) {
        refreshHighlights();
    }

    @Override
    public void spanRemoved() {

    }

    @Override
    public void spanSelectionChanged(Span span) {
        refreshHighlights();
    }
}
