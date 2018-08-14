package edu.ucdenver.ccp.knowtator.view.text.textpane;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.ColorListener;
import edu.ucdenver.ccp.knowtator.listeners.ViewListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;
import edu.ucdenver.ccp.knowtator.view.ControllerNotSetException;
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
import java.util.HashSet;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class KnowtatorTextPane extends JTextArea
        implements ViewListener, ColorListener {

    private KnowtatorView view;
    private TextSource textSource;
    private static Logger log = Logger.getLogger(KnowtatorTextPane.class);

    public KnowtatorTextPane(KnowtatorView view) {
        super();
        this.view = view;
        setEditable(false);
        setLineWrap(true);
        setWrapStyleWord(true);

        getCaret().setVisible(true);

        setupListeners();
        requestFocusInWindow();
        select(0, 0);
        getCaret().setSelectionVisible(true);
    }

    public void setController(KnowtatorController controller) {
        addCaretListener(controller.getSelectionManager());
        controller.addViewListener(this);
        controller.getProfileManager().addColorListener(this);
    }

    private void showTextPane(TextSource textSource) throws ControllerNotSetException {
        this.textSource = textSource;
        setText(textSource.getContent());
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
                        try {

                            handleMouseRelease(e, press_offset, viewToModel(e.getPoint()));

                        } catch (ControllerNotSetException ignored) {
                        }
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

    private void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) throws ControllerNotSetException {
        AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, view);

        Set<Span> spansContainingLocation = getSpans(press_offset);

        if (SwingUtilities.isRightMouseButton(e)) {
            if (spansContainingLocation.size() == 1) {
                Span span = spansContainingLocation.iterator().next();
                try {
                    view.getController()
                            .getSelectionManager()
                            .getActiveTextSource()
                            .getAnnotationManager()
                            .setSelectedSpan(span);
                } catch (ActiveTextSourceNotSetException e1) {
                    e1.printStackTrace();
                }
            }
            popupMenu.showPopUpMenu(release_offset);
        } else if (press_offset == release_offset) {
            if (spansContainingLocation.size() == 1) {
                Span span = spansContainingLocation.iterator().next();
                try {
                    view.getController()
                            .getSelectionManager()
                            .getActiveTextSource()
                            .getAnnotationManager()
                            .setSelectedSpan(span);
                } catch (ActiveTextSourceNotSetException e1) {
                    e1.printStackTrace();
                }
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

    private void refreshHighlights() throws ControllerNotSetException {
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
                                        .getSelectionManager()
                                        .getActiveProfile()
                                        .getColor(span.getAnnotation())));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        revalidate();
        repaint();

        SwingUtilities.invokeLater(
                () -> {
                    try {
                        if (view.getController()
                                .getSelectionManager()
                                .getActiveTextSource()
                                .getAnnotationManager()
                                .getSelectedSpan()
                                != null) {
                            try {
                                scrollRectToVisible(
                                        modelToView(
                                                view.getController()
                                                        .getSelectionManager()
                                                        .getActiveTextSource()
                                                        .getAnnotationManager()
                                                        .getSelectedSpan()
                                                        .getStart()));
                            } catch (BadLocationException | NullPointerException ignored) {

                            }
                        } else if (view.getController()
                                .getSelectionManager()
                                .getActiveTextSource()
                                .getAnnotationManager()
                                .getSelectedAnnotation()
                                != null) {
                            try {
                                scrollRectToVisible(
                                        modelToView(
                                                view.getController()
                                                        .getSelectionManager()
                                                        .getActiveTextSource()
                                                        .getAnnotationManager()
                                                        .getSelectedAnnotation()
                                                        .getSpanCollection()
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
                    } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

                    }
                });
    }

    private void highlightSpan(
            int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter)
            throws BadLocationException {
        getHighlighter().addHighlight(start, end, highlighter);
    }

    private Set<Span> getSpans(Integer loc) {
        try {
            return view.getController()
                    .getSelectionManager()
                    .getActiveTextSource()
                    .getAnnotationManager()
                    .getSpans(loc, 0, getText().length());
        } catch (ActiveTextSourceNotSetException | ControllerNotSetException e) {
            return new HashSet<>();
        }
    }

    private void highlightSelectedAnnotation() {
        try {
            if (view.getController()
                    .getSelectionManager()
                    .getActiveTextSource()
                    .getAnnotationManager()
                    .getSelectedAnnotation()
                    != null) {
                for (Span span :
                        view.getController()
                                .getSelectionManager()
                                .getActiveTextSource()
                                .getAnnotationManager()
                                .getSelectedAnnotation()
                                .getSpanCollection()
                                .getCollection()) {
                    try {
                        highlightSpan(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
                    } catch (BadLocationException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

        }
    }

    @Override
    public void viewChanged() {
        try {
            if (this.textSource != view.getController().getSelectionManager().getActiveTextSource()) {
                showTextPane(view.getController().getSelectionManager().getActiveTextSource());
            }
            refreshHighlights();
        } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

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
        try {
            refreshHighlights();
        } catch (ControllerNotSetException ignored) {

        }
    }
}
