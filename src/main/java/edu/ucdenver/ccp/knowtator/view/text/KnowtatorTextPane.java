package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.model.profile.ColorListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.SpanCollectionListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.menu.AnnotationPopupMenu;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

@SuppressWarnings("deprecation")
public class KnowtatorTextPane extends JTextArea implements ColorListener {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(KnowtatorTextPane.class);

    private KnowtatorView view;
    private TextSourceCollectionListener textSourceCollectionListener;
    private ConceptAnnotationCollectionListener conceptAnnotationCollectionListener;
    private SpanCollectionListener spanCollectionListener;

    public KnowtatorTextPane(KnowtatorView view) {
        super();
        this.view = view;
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);

        setupListeners();

        getCaret().setVisible(true);

        setupListeners();
        requestFocusInWindow();
        select(0, 0);
        getCaret().setSelectionVisible(true);
    }

    private int find(String text, String textToFind, int fromIndex, boolean searchForward) {
        if (searchForward) {
            return text.indexOf(textToFind, fromIndex + 1);
        } else {
            return text.lastIndexOf(textToFind, fromIndex - 1);
        }
    }

    public void search(String textToFind, boolean isCaseSensitive, boolean inAnnotations, boolean searchForward) {
        String text = isCaseSensitive ? getText().toLowerCase() : getText();
        textToFind = isCaseSensitive ? textToFind : textToFind.toLowerCase();

        if (inAnnotations) {
            Span selectedSpan = view.getController()
                    .getTextSourceCollection().getSelection()
                    .getConceptAnnotationCollection().getSelection()
                    .getSpanCollection().getSelection();
            if (searchForward) {
                select(selectedSpan.getEnd(), selectedSpan.getEnd());
            } else {
                select(selectedSpan.getStart(), selectedSpan.getStart());
            }
        }

        int matchLoc = find(text, textToFind, getSelectionStart(), searchForward);
        Set<Span> spans = null;
        int newMatchLoc = matchLoc;
        if (inAnnotations) {
            do {
                spans = getSpans(newMatchLoc);
                if (!spans.isEmpty()) {
                    inAnnotations = false;
                } else {
                    newMatchLoc = find(text, textToFind, newMatchLoc, searchForward);
                }
                if (!searchForward && newMatchLoc == -1) {
                    newMatchLoc = text.length();
                }
            } while (inAnnotations && newMatchLoc != matchLoc);
        }
        matchLoc = newMatchLoc;
        if (matchLoc != -1) {
            if (spans != null) {
                view.getController().getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection().getSelection()
                        .getSpanCollection()
                        .setSelection(spans.iterator().next());
            } else {
                requestFocusInWindow();
                select(matchLoc, matchLoc + textToFind.length());
            }
        } else {
            select(searchForward ? -1 : text.length(), searchForward ? -1 : text.length());
        }
    }


    private void showTextPane() {
        String text = view.getController().getTextSourceCollection().getSelection().getContent();
        setText(text);
        refreshHighlights();
    }

    private void setupListeners() {
        addCaretListener(view.getController().getTextSourceCollection());
        view.getController().getProfileCollection().addColorListener(this);

        textSourceCollectionListener = new TextSourceCollectionListener() {
            @Override
            public void updated(TextSource updatedItem) {

            }

            @Override
            public void noSelection(TextSource previousSelection) {
                refreshHighlights();
            }

            @Override
            public void selected(TextSource previousSelection, TextSource currentSelection) {
                if (previousSelection != null) {
                    previousSelection.getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
                }
                currentSelection.getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
                showTextPane();
            }

            @Override
            public void added(TextSource object) {

            }

            @Override
            public void removed(TextSource removedObject) {
            }

            @Override
            public void emptied(TextSource object) {

            }

            @Override
            public void firstAdded(TextSource object) {
            }
        };
        conceptAnnotationCollectionListener = new ConceptAnnotationCollectionListener() {
            @Override
            public void updated(ConceptAnnotation updatedItem) {
                refreshHighlights();
            }

            @Override
            public void noSelection(ConceptAnnotation previousSelection) {
                previousSelection.getSpanCollection().removeCollectionListener(spanCollectionListener);
                refreshHighlights();
            }

            @Override
            public void selected(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {
                if (previousSelection != null) {
                    previousSelection.getSpanCollection().removeCollectionListener(spanCollectionListener);
                }
                currentSelection.getSpanCollection().addCollectionListener(spanCollectionListener);
                refreshHighlights();
            }

            @Override
            public void added(ConceptAnnotation addedObject) {

            }

            @Override
            public void removed(ConceptAnnotation removedObject) {

            }

            @Override
            public void emptied(ConceptAnnotation object) {

            }

            @Override
            public void firstAdded(ConceptAnnotation object) {

            }
        };

        spanCollectionListener = new SpanCollectionListener() {
            @Override
            public void updated(Span updatedItem) {
                refreshHighlights();
            }

            @Override
            public void noSelection(Span previousSelection) {
                refreshHighlights();
            }

            @Override
            public void selected(Span previousSelection, Span currentSelection) {
                refreshHighlights();
            }

            @Override
            public void added(Span addedObject) {
                refreshHighlights();
            }

            @Override
            public void removed(Span removedObject) {
                refreshHighlights();
            }

            @Override
            public void emptied(Span object) {
                refreshHighlights();
            }

            @Override
            public void firstAdded(Span object) {
                refreshHighlights();
            }
        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);

        addMouseListener(new MouseListener() {
            int press_offset;

            @Override
            public void mousePressed(MouseEvent e) {
                press_offset = viewToModel(e.getPoint());
            }

            @Override
            public void mouseReleased(MouseEvent e) {

                handleMouseRelease(e, press_offset, viewToModel(e.getPoint()));

            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }
        });
    }

    private void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
        //TODO: test selecting of annotations
        AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, view);

        Set<Span> spansContainingLocation = getSpans(press_offset);

        if (SwingUtilities.isRightMouseButton(e)) {
            if (spansContainingLocation.size() == 1) {
                Span span = spansContainingLocation.iterator().next();
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection().setSelectedAnnotation(span);
            }
            popupMenu.showPopUpMenu(release_offset);
        } else if (press_offset == release_offset) {
            if (spansContainingLocation.size() == 1) {
                Span span = spansContainingLocation.iterator().next();
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection().setSelectedAnnotation(span);
            } else if (spansContainingLocation.size() > 1) {
                popupMenu.chooseAnnotation(spansContainingLocation);
            }

        } else {
            setSelectionAtWordLimits(press_offset, release_offset);
        }
    }

    private void setSelectionAtWordLimits(int press_offset, int release_offset) {

        try {
            int start = Utilities.getWordStart(this, min(press_offset, release_offset));
            int end = Utilities.getWordEnd(this, max(press_offset, release_offset));
            requestFocusInWindow();
            select(start, end);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private void refreshHighlights() {
        // Remove all previous highlights in case a span has been deleted
        getHighlighter().removeAllHighlights();

        // Always highlight the selected concept first so its color and border show up
        highlightSelectedAnnotation();

        // Highlight overlaps first, then spans
        Span lastSpan = null;

        Set<Span> spans = getSpans(null);
        for (Span span : spans) {
            if (lastSpan != null) {
                if (span.intersects(lastSpan)) {
                    try {
                        highlightSpan(
                                span.getStart(),
                                min(lastSpan.getEnd(), span.getEnd()),
                                new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (lastSpan == null || span.getEnd() > lastSpan.getEnd()) {
                lastSpan = span;
            }
        }

        for (Span span : spans) {
            try {
                highlightSpan(
                        span.getStart(),
                        span.getEnd(),
                        new DefaultHighlighter.DefaultHighlightPainter(
                                view.getController()
                                        .getProfileCollection().getSelection()
                                        .getColor(span.getConceptAnnotation())));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        revalidate();
        repaint();

        SwingUtilities.invokeLater(
                () -> {
                    if (view.getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection().getSelection() != null && view.getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection().getSelection()
                            .getSpanCollection().getSelection()
                            != null) {
                        try {
                            scrollRectToVisible(
                                    modelToView(
                                            view.getController()
                                                    .getTextSourceCollection().getSelection()
                                                    .getConceptAnnotationCollection().getSelection()
                                                    .getSpanCollection().getSelection()
                                                    .getStart()));
                        } catch (BadLocationException | NullPointerException e) {
                            e.printStackTrace();

                        }
                    } else if (view.getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection().getSelection()
                            != null) {
                        try {
                            scrollRectToVisible(
                                    modelToView(
                                            view.getController()
                                                    .getTextSourceCollection().getSelection()
                                                    .getConceptAnnotationCollection().getSelection()
                                                    .getSpanCollection().first()
                                                    .getStart()));
                        } catch (BadLocationException | NullPointerException e) {
                            e.printStackTrace();

                        }
                    } else {
                        try {
                            scrollRectToVisible(modelToView(0));
                        } catch (BadLocationException | NullPointerException e) {
                            e.printStackTrace();

                        }
                    }
                });
    }

    private void highlightSpan(
            int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter)
            throws BadLocationException {
        getHighlighter().addHighlight(start, end, highlighter);
    }

    private Set<Span> getSpans(Integer loc) {
        return view.getController()
                .getTextSourceCollection().getSelection()
                .getConceptAnnotationCollection()
                .getSpans(loc, 0, getText().length());
    }

    private void highlightSelectedAnnotation() {
        ConceptAnnotation selectedConceptAnnotation = view.getController()
                .getTextSourceCollection().getSelection()
                .getConceptAnnotationCollection().getSelection();
        if (selectedConceptAnnotation != null) {
            for (Span span : selectedConceptAnnotation.getSpanCollection()) {
                try {
                    highlightSpan(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void decreaseFontSize() {
        Font font = getFont();
        setFont(new Font(font.getName(), font.getStyle(), font.getSize() - 2));
        repaint();
    }

    public void increaseFindSize() {
        Font font = getFont();
        setFont(new Font(font.getName(), font.getStyle(), font.getSize() + 2));
        repaint();
    }

    public void growStart() {
        select(getSelectionStart() - 1, getSelectionEnd());
    }

    public void shrinkStart() {
        select(getSelectionStart() + 1, getSelectionEnd());
    }

    public void shrinkEnd() {
        select(getSelectionStart(), getSelectionEnd() - 1);
    }

    public void growEnd() {
        select(getSelectionStart(), getSelectionEnd() + 1);
    }

    @Override
    public void colorChanged() {
        refreshHighlights();
    }

    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }
}
