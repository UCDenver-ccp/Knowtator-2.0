package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.FilterModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.profile.ColorListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.annotation.AnnotationActions;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Math.max;
import static java.lang.Math.min;

@SuppressWarnings("deprecation")
public class KnowtatorTextPane extends JTextArea implements ColorListener, KnowtatorComponent, FilterModelListener {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(KnowtatorTextPane.class);

    private final KnowtatorView view;
    private KnowtatorCollectionListener<TextSource> textSourceCollectionListener;
    private KnowtatorCollectionListener<ConceptAnnotation> conceptAnnotationCollectionListener;
    private KnowtatorCollectionListener<Span> spanCollectionListener;

    KnowtatorTextPane(KnowtatorView view) {
        super();
        this.view = view;
        setEditable(false);
        setEnabled(false);
        setLineWrap(true);
        setWrapStyleWord(true);
        setSelectedTextColor(Color.red);

        setupListeners();

        getCaret().setVisible(true);

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

    public BufferedImage getScreenShot() {

        BufferedImage image =
                new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        // call the Component's paint method, using
        // the Graphics object of the image.
        paint(image.getGraphics()); // alternately use .printAll(..)
        return image;
    }

    public void search(String textToFind, boolean isCaseSensitive, boolean inAnnotations, boolean isRegex, boolean searchForward) {
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
        int matchLoc;
        Matcher matcher = null;

        if (isRegex) {
            Pattern patternToFind = Pattern.compile(textToFind);
            matcher = patternToFind.matcher(text);
            matchLoc = matcher.start();
        } else {
            matchLoc = find(text, textToFind, getSelectionStart(), searchForward);
        }
        Set<Span> spans = null;
        int newMatchLoc = matchLoc;
        if (inAnnotations) {
            do {
                spans = getSpans(newMatchLoc);
                if (!spans.isEmpty()) {
                    inAnnotations = false;
                } else {
                    newMatchLoc = isRegex ? matcher.start() : find(text, textToFind, newMatchLoc, searchForward);
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
        addCaretListener(view.getController().getSelectionModel());
        addCaretListener(e -> view.getSearchTextField().setText(getSelectedText()));
        view.getController().getProfileCollection().addColorListener(this);
        view.getController().getFilterModel().addFilterModelListener(this);

        MouseListener mouseListener = new MouseListener() {
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
        };

        textSourceCollectionListener = new KnowtatorCollectionListener<TextSource>() {
            @Override
            public void added(AddEvent<TextSource> event) {

            }

            @Override
            public void removed(RemoveEvent<TextSource> event) {

            }

            @Override
            public void changed(ChangeEvent<TextSource> event) {

            }

            @Override
            public void emptied() {
                setEnabled(false);
                removeMouseListener(mouseListener);
            }

            @Override
            public void firstAdded() {
                setEnabled(true);
                addMouseListener(mouseListener);
            }

            @Override
            public void selected(SelectionChangeEvent<TextSource> event) {
                if (event.getOld() != null) {
                    event.getOld().getConceptAnnotationCollection().removeCollectionListener(conceptAnnotationCollectionListener);
                    event.getOld().getConceptAnnotationCollection().getAllSpanCollection().removeCollectionListener(spanCollectionListener);
                }
                event.getNew().getConceptAnnotationCollection().addCollectionListener(conceptAnnotationCollectionListener);
                event.getNew().getConceptAnnotationCollection().getAllSpanCollection().addCollectionListener(spanCollectionListener);

                showTextPane();
            }
        };
        conceptAnnotationCollectionListener = new KnowtatorCollectionListener<ConceptAnnotation>() {
            @Override
            public void added(AddEvent<ConceptAnnotation> event) {

            }

            @Override
            public void removed(RemoveEvent<ConceptAnnotation> event) {

            }

            @Override
            public void changed(ChangeEvent<ConceptAnnotation> event) {
                refreshHighlights();
            }

            @Override
            public void emptied() {

            }

            @Override
            public void firstAdded() {

            }

            @Override
            public void selected(SelectionChangeEvent<ConceptAnnotation> event) {
                if (event.getOld() != null) {
                    event.getOld().getSpanCollection().removeCollectionListener(spanCollectionListener);
                }
                if (event.getNew() != null) {
                    event.getNew().getSpanCollection().addCollectionListener(spanCollectionListener);
                }
                refreshHighlights();
            }
        };

        spanCollectionListener = new KnowtatorCollectionListener<Span>() {
            @Override
            public void added(AddEvent<Span> event) {
                refreshHighlights();
            }

            @Override
            public void removed(RemoveEvent<Span> event) {
                refreshHighlights();
            }

            @Override
            public void changed(ChangeEvent<Span> event) {
                refreshHighlights();
            }

            @Override
            public void emptied() {
                refreshHighlights();
            }

            @Override
            public void firstAdded() {
                refreshHighlights();
            }

            @Override
            public void selected(SelectionChangeEvent<Span> event) {
                refreshHighlights();
            }

        };

        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);


    }

    private void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
        AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e);

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

            //I don't want to deselect the annotation here because I may want to add a span to it

            requestFocusInWindow();
            select(start, end);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    public void refreshHighlights() {
        if (view.getController().isNotLoading()) {
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
                        TextSource textSource = view.getController().getTextSourceCollection().getSelection();
                        ConceptAnnotation annotation = textSource.getConceptAnnotationCollection().getSelection();

                        if (annotation != null) {
                            Span span = annotation.getSpanCollection().getSelection();
                            if (span == null) {
                                span = annotation.getSpanCollection().first();
                            }
                            try {
                                scrollRectToVisible(modelToView(span.getStart()));
                            } catch (BadLocationException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
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

    public void setFontSize(int size) {
        Font font = getFont();
        setFont(new Font(font.getName(), font.getStyle(), size));
        repaint();
    }

    void modifySelection(int startModification, int endModification) {
        select(getSelectionStart() + startModification, getSelectionEnd() + endModification);
    }

    @Override
    public void colorChanged() {
        refreshHighlights();
    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void profileFilterChanged(boolean filterValue) {
        refreshHighlights();
    }

    @Override
    public void owlClassFilterChanged(boolean filterVale) {
        refreshHighlights();
    }

    class RectanglePainter extends DefaultHighlighter.DefaultHighlightPainter {

        @SuppressWarnings("SameParameterValue")
        RectanglePainter(Color color) {
            super(color);
        }

        /**
         * Paints a portion of a highlight.
         *
         * @param g      the graphics context
         * @param offs0  the starting model offset >= 0
         * @param offs1  the ending model offset >= offs1
         * @param bounds the bounding box of the view, which is not necessarily the region to paint.
         * @param c      the editor
         * @param view   View painting for
         * @return region drawing occured in
         */
        public Shape paintLayer(
                Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
            Rectangle r = getDrawingArea(offs0, offs1, bounds, view);

            if (r == null) return null;

            //  Do your custom painting

            Color color = getColor();
            g.setColor(color == null ? c.getSelectionColor() : color);

            ((Graphics2D) g).setStroke(new BasicStroke(4));

            //  Code is the same as the default highlighter except we use drawRect(...)

            //		g.fillRect(r.x, r.y, r.width, r.height);
            g.drawRect(r.x, r.y, r.width - 1, r.height - 1);

            // Return the drawing area

            return r;
        }

        private Rectangle getDrawingArea(int offs0, int offs1, Shape bounds, View view) {
            // Contained in view, can just use bounds.

            if (offs0 == view.getStartOffset() && offs1 == view.getEndOffset()) {
                Rectangle alloc;

                if (bounds instanceof Rectangle) {
                    alloc = (Rectangle) bounds;
                } else {
                    alloc = bounds.getBounds();
                }

                return alloc;
            } else {
                // Should only render part of View.
                try {
                    // --- determine locations ---
                    Shape shape =
                            view.modelToView(offs0, Position.Bias.Forward, offs1, Position.Bias.Backward, bounds);

                    return (shape instanceof Rectangle) ? (Rectangle) shape : shape.getBounds();
                } catch (BadLocationException e) {
                    // can't render
                }
            }

            // Can't render

            return null;
        }
    }

    class AnnotationPopupMenu extends JPopupMenu {
        private final MouseEvent e;

        AnnotationPopupMenu(MouseEvent e) {
            this.e = e;
        }

        private JMenuItem reassignOWLClassCommand() {
            JMenuItem menuItem = new JMenuItem("Reassign OWL class");
            menuItem.addActionListener(e1 -> view.getController().getTextSourceCollection()
                    .getSelection().getConceptAnnotationCollection().reassignSelectedOWLClassToSelectedAnnotation());

            return menuItem;
        }

        private JMenuItem addAnnotationCommand() {
            JMenuItem menuItem = new JMenuItem("Add concept");
            menuItem.addActionListener(e12 -> AnnotationActions.addAnnotation(view));

            return menuItem;
        }

        private JMenuItem removeSpanFromAnnotationCommand() {
            JMenuItem removeSpanFromSelectedAnnotation =
                    new JMenuItem(
                            String.format(
                                    "Delete span from %s",
                                    view.getController()
                                            .getTextSourceCollection()
                                            .getSelection()
                                            .getConceptAnnotationCollection()
                                            .getSelection()
                                            .getOwlClass()));
            removeSpanFromSelectedAnnotation.addActionListener(e5 -> AnnotationActions.removeAnnotation(view));

            return removeSpanFromSelectedAnnotation;
        }

        private JMenuItem selectAnnotationCommand(Span span) {
            JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + span.getConceptAnnotation().getOwlClassID());
            selectAnnotationMenuItem.addActionListener(e3 -> view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().setSelectedAnnotation(span));

            return selectAnnotationMenuItem;
        }

        private JMenuItem removeAnnotationCommand() {
            JMenuItem removeAnnotationMenuItem = new JMenuItem(
                    "Delete " +
                            view.getController()
                                    .getTextSourceCollection()
                                    .getSelection()
                                    .getConceptAnnotationCollection()
                                    .getSelection()
                                    .getOwlClass());

            removeAnnotationMenuItem.addActionListener(e4 -> AnnotationActions.removeAnnotation(view));

            return removeAnnotationMenuItem;
        }

        void chooseAnnotation(Set<Span> spansContainingLocation) {
            // Menu items to select and remove annotations
            spansContainingLocation.forEach(span -> add(selectAnnotationCommand(span)));

            show(e.getComponent(), e.getX(), e.getY());
        }

        void showPopUpMenu(int release_offset) {
            TextSource textSource = view.getController().getTextSourceCollection().getSelection();
            ConceptAnnotation selectedConceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();

            if (getSelectionStart() <= release_offset && release_offset <= getSelectionEnd() && getSelectionStart() != getSelectionEnd()) {
                select(getSelectionStart(), getSelectionEnd());
                add(addAnnotationCommand());
            } else if (selectedConceptAnnotation != null) {
                Span selectedSpan = selectedConceptAnnotation.getSpanCollection().getSelection();
                if (selectedSpan != null && selectedSpan.getStart() <= release_offset && release_offset <= selectedSpan.getEnd()) {
                    add(removeAnnotationCommand());
                    if (selectedConceptAnnotation.getSpanCollection().size() > 1) {
                        add(removeSpanFromAnnotationCommand());
                    }
                    add(reassignOWLClassCommand());
                } else {
                    return;
                }
            } else {
                return;
            }

            show(e.getComponent(), e.getX(), e.getY());
        }
    }
}
