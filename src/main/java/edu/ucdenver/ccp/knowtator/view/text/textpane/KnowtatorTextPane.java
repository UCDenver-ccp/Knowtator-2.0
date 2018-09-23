package edu.ucdenver.ccp.knowtator.view.text.textpane;

import edu.ucdenver.ccp.knowtator.listeners.ColorListener;
import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;
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
public class KnowtatorTextPane extends JTextArea
        implements ViewListener, ColorListener {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(KnowtatorTextPane.class);

    private KnowtatorView view;

    public KnowtatorTextPane(KnowtatorView view) {
        super();
        this.view = view;
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);

        addCaretListener(view.getController().getSelectionManager());
        view.getController().addViewListener(this);
        view.getController().getProfileManager().addColorListener(this);

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
                    .getTextSourceManager().getSelection()
                    .getAnnotationManager().getSelection()
                    .getSpanManager().getSelection();
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
                view.getController().getTextSourceManager().getSelection()
                        .getAnnotationManager().getSelection()
                        .getSpanManager()
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
        setText(view.getController().getTextSourceManager().getSelection().getContent());
        refreshHighlights();
    }

    private void setupListeners() {
        addMouseListener(
                new MouseListener() {
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
                        .getTextSourceManager()
                        .getSelection()
                        .getAnnotationManager().getSelection()
                        .getSpanManager()
                        .setSelection(span);
            }
            popupMenu.showPopUpMenu(release_offset);
        } else if (press_offset == release_offset) {
            if (spansContainingLocation.size() == 1) {
                Span span = spansContainingLocation.iterator().next();
                view.getController()
                        .getTextSourceManager().getSelection()
                        .getAnnotationManager().getSelection()
                        .getSpanManager()
                        .setSelection(span);
            } else if (spansContainingLocation.size() > 1) {
                popupMenu.chooseAnnotation(spansContainingLocation);
            }

        } else {
            //        view.getController().getSelectionManager().setSelectedAnnotation(null, null);
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

        // Always highlight the selected annotation first so its color and border show up
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
                                        .getProfileManager().getSelection()
                                        .getColor(span.getAnnotation())));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        revalidate();
        repaint();

        SwingUtilities.invokeLater(
                () -> {
                    if (view.getController()
                            .getTextSourceManager().getSelection()
                            .getAnnotationManager().getSelection()
                            .getSpanManager().getSelection()
                            != null) {
                        try {
                            scrollRectToVisible(
                                    modelToView(
                                            view.getController()
                                                    .getTextSourceManager().getSelection()
                                                    .getAnnotationManager().getSelection()
                                                    .getSpanManager().getSelection()
                                                    .getStart()));
                        } catch (BadLocationException | NullPointerException ignored) {

                        }
                    } else if (view.getController()
                            .getTextSourceManager().getSelection()
                            .getAnnotationManager().getSelection()
                            != null) {
                        try {
                            scrollRectToVisible(
                                    modelToView(
                                            view.getController()
                                                    .getTextSourceManager().getSelection()
                                                    .getAnnotationManager().getSelection()
                                                    .getSpanManager()
                                                    .getCollection()
                                                    .first()
                                                    .getStart()));
                        } catch (BadLocationException | NullPointerException ignored) {

                        }
                    } else {
                        try {
                            scrollRectToVisible(modelToView(0));
                        } catch (BadLocationException | NullPointerException ignored) {

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
                .getTextSourceManager().getSelection()
                .getAnnotationManager()
                .getSpans(loc, 0, getText().length());
    }

    private void highlightSelectedAnnotation() {
        if (view.getController()
                .getTextSourceManager().getSelection()
                .getAnnotationManager().getSelection()
                != null) {
            for (Span span :
                    view.getController()
                            .getTextSourceManager().getSelection()
                            .getAnnotationManager().getSelection()
                            .getSpanManager().getCollection()) {
                try {
                    highlightSpan(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
                } catch (BadLocationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void viewChanged() {
        if (!getText().equals(view.getController().getTextSourceManager().getSelection()
                .getContent())) {
            showTextPane();
        }
        refreshHighlights();
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
}
