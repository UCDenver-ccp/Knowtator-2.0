package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.actions.AnnotationActions;
import edu.ucdenver.ccp.knowtator.model.FilterModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.*;
import edu.ucdenver.ccp.knowtator.model.profile.ColorListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

@SuppressWarnings("deprecation")
public class KnowtatorTextPane extends JTextPane implements ColorListener, KnowtatorComponent, FilterModelListener, KnowtatorCollectionListener<Profile> {

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
        setSelectedTextColor(Color.red);
        setFont(KnowtatorDefaultSettings.FONT);

        setupListeners();

        getCaret().setVisible(true);

        requestFocusInWindow();
        select(0, 0);
        getCaret().setSelectionVisible(true);
    }

    public BufferedImage getScreenShot() {

        BufferedImage image =
                new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        // call the Component's paint method, using
        // the Graphics object of the image.
        paint(image.getGraphics()); // alternately use .printAll(..)
        return image;
    }

    public void search(boolean searchForward) {
        try {
            TextSource textSource = view.getController().getTextSourceCollection().getSelection();

            int matchLoc;

            matchLoc = searchForward ? view.getSearchTextField().searchForward(textSource) : view.getSearchTextField().searchPrevious(textSource);

            requestFocusInWindow();
            select(matchLoc, view.getSearchTextField().getMatchEnd());


        } catch (NoSelectionException e) {
            e.printStackTrace();
        }


    }


    private void showTextPane() {
        try {
            String text = view.getController().getTextSourceCollection().getSelection().getContent();
            setText(text);
            refreshHighlights();
        } catch (NoSelectionException e) {
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        addCaretListener(view.getController().getSelectionModel());

        view.getController().getProfileCollection().addColorListener(this);
        view.getController().getProfileCollection().addCollectionListener(this);
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
        try {
            TextSource textSource = view.getController().getTextSourceCollection().getSelection();

            AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, textSource);

            Set<Span> spansContainingLocation = getSpans(press_offset);

            if (SwingUtilities.isRightMouseButton(e)) {
                if (spansContainingLocation.size() == 1) {
                    Span span = spansContainingLocation.iterator().next();
                    textSource.getConceptAnnotationCollection().setSelectedAnnotation(span);
                }
                popupMenu.showPopUpMenu(release_offset);
            } else if (press_offset == release_offset) {
                if (spansContainingLocation.size() == 1) {
                    Span span = spansContainingLocation.iterator().next();
                    textSource.getConceptAnnotationCollection().setSelectedAnnotation(span);
                } else if (spansContainingLocation.size() > 1) {
                    popupMenu.chooseAnnotation(spansContainingLocation);
                }

            } else {
                setSelectionAtWordLimits(press_offset, release_offset);
            }
        } catch (NoSelectionException e1) {
            e1.printStackTrace();
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
            try {
                highlightSelectedAnnotation();
            } catch (NoSelectionException ignored) {
            }

            // Highlight overlaps first, then spans

            try {
                Set<Span> spans = getSpans(null);
                highlightOverlaps(spans);
                highlightSpans(spans);
            } catch (NoSelectionException ignored) {
            }

            revalidate();
            repaint();

            Span span = null;
            try {
                TextSource textSource = view.getController().getTextSourceCollection().getSelection();
                ConceptAnnotation annotation = textSource.getConceptAnnotationCollection().getSelection();
                if (annotation != null && annotation.getSpanCollection().size() > 0) {
                    try {
                        span = annotation.getSpanCollection().getSelection();
                    } catch (NoSelectionException e) {
                        span = annotation.getSpanCollection().first();
                    }
                }
            } catch (NoSelectionException ignored) {

            }

            Span finalSpan = span;
            SwingUtilities.invokeLater(
                    () -> {
                        if (finalSpan != null) {
                            try {
                                scrollRectToVisible(modelToView(finalSpan.getStart()));
                            } catch (BadLocationException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );
        }
    }

    private void highlightSpans(Set<Span> spans) {
        SimpleAttributeSet underlinedSpan = new SimpleAttributeSet();
        StyleConstants.setUnderline(underlinedSpan, true);

        SimpleAttributeSet regularSpan = new SimpleAttributeSet();
        StyleConstants.setUnderline(regularSpan, false);

        getStyledDocument().setCharacterAttributes(0, getText().length(), regularSpan, false);

        Set<OWLClass> descendants = null;
        try {
            OWLClass owlClass = view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getOwlClass();
            descendants = view.getController().getOWLModel().getDescendants(owlClass);
            descendants.add(owlClass);
        } catch (NoSelectionException ignored) {

        }
        for (Span span : spans) {
            try {
                //Underline spans for the same class
                if (descendants != null && descendants.contains(span.getConceptAnnotation().getOwlClass())) {
                    getStyledDocument().setCharacterAttributes(span.getStart(), span.getSize(), underlinedSpan, false);
                }
                highlightSpan(
                        span.getStart(),
                        span.getEnd(),
                        new DefaultHighlighter.DefaultHighlightPainter(
                                span.getConceptAnnotation().getAnnotator()
                                        .getColor(span.getConceptAnnotation())));
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }
    }

    private void highlightOverlaps(Set<Span> spans) {
        Span lastSpan = null;

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
    }

    private void highlightSpan(
            int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter)
            throws BadLocationException {
        getHighlighter().addHighlight(start, end, highlighter);
    }

    private Set<Span> getSpans(Integer loc) throws NoSelectionException {
        return view.getController()
                .getTextSourceCollection().getSelection()
                .getConceptAnnotationCollection()
                .getSpans(loc);
    }

    private void highlightSelectedAnnotation() throws NoSelectionException {
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

    public void modifySelection(int startModification, int endModification) {
        select(getSelectionStart() + startModification, getSelectionEnd() + endModification);
    }

    @Override
    public void colorChanged() {
        refreshHighlights();
    }

    @Override
    public void reset() {
        view.getController().getTextSourceCollection().addCollectionListener(textSourceCollectionListener);
        view.getController().getProfileCollection().addColorListener(this);
        view.getController().getProfileCollection().addCollectionListener(this);
        view.getController().getFilterModel().addFilterModelListener(this);
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

    @Override
    public void selected(SelectionChangeEvent<Profile> event) {
        refreshHighlights();
    }

    @Override
    public void added(AddEvent<Profile> event) {
    }

    @Override
    public void removed(RemoveEvent<Profile> event) {
    }

    @Override
    public void changed(ChangeEvent<Profile> event) {

    }

    @Override
    public void emptied() {

    }

    @Override
    public void firstAdded() {

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
         * @return region drawing occurred in
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
            ((Graphics2D) g).setStroke(new BasicStroke());

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
        private final TextSource textSource;

        AnnotationPopupMenu(MouseEvent e, TextSource textSource) {
            this.e = e;
            this.textSource = textSource;
        }

        private JMenuItem reassignOWLClassCommand(ConceptAnnotation conceptAnnotation) {
            JMenuItem menuItem = new JMenuItem("Reassign OWL class");
            menuItem.addActionListener(e -> AnnotationActions.reassignOWLClass(view, conceptAnnotation));

            return menuItem;
        }

        private JMenuItem addAnnotationCommand() {
            JMenuItem menuItem = new JMenuItem("Add concept");
            menuItem.addActionListener(e12 -> AnnotationActions.addConceptAnnotation(view, textSource));

            return menuItem;
        }

        private JMenuItem removeSpanFromAnnotationCommand(ConceptAnnotation conceptAnnotation) {
            JMenuItem removeSpanFromSelectedAnnotation =
                    new JMenuItem(
                            String.format(
                                    "Delete span from %s",
                                    conceptAnnotation.getOwlClass()));
            removeSpanFromSelectedAnnotation.addActionListener(e5 -> AnnotationActions.removeConceptAnnotation(view, textSource, conceptAnnotation));

            return removeSpanFromSelectedAnnotation;
        }

        private JMenuItem selectAnnotationCommand(Span span) {
            JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + span.getConceptAnnotation().getOwlClassID());
            selectAnnotationMenuItem.addActionListener(e3 -> {
                try {
                    view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().setSelectedAnnotation(span);
                } catch (NoSelectionException e1) {
                    e1.printStackTrace();
                }
            });

            return selectAnnotationMenuItem;
        }

        private JMenuItem removeAnnotationCommand(ConceptAnnotation conceptAnnotation) {
            JMenuItem removeAnnotationMenuItem = new JMenuItem(
                    "Delete " +
                            conceptAnnotation.getOwlClass());

            removeAnnotationMenuItem.addActionListener(e4 -> AnnotationActions.removeConceptAnnotation(view, textSource, conceptAnnotation));

            return removeAnnotationMenuItem;
        }

        void chooseAnnotation(Set<Span> spansContainingLocation) {
            // Menu items to select and remove annotations
            spansContainingLocation.forEach(span -> add(selectAnnotationCommand(span)));

            show(e.getComponent(), e.getX(), e.getY());
        }

        void showPopUpMenu(int release_offset) {


            if (getSelectionStart() <= release_offset && release_offset <= getSelectionEnd() && getSelectionStart() != getSelectionEnd()) {
                select(getSelectionStart(), getSelectionEnd());
                add(addAnnotationCommand());
            } else {
                try {
                    ConceptAnnotation selectedConceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();
                    Span selectedSpan = selectedConceptAnnotation.getSpanCollection().getSelection();
                    if (selectedSpan.getStart() <= release_offset && release_offset <= selectedSpan.getEnd()) {
                        add(removeAnnotationCommand(selectedConceptAnnotation));
                        if (selectedConceptAnnotation.getSpanCollection().size() > 1) {
                            add(removeSpanFromAnnotationCommand(selectedConceptAnnotation));
                        }
                        add(reassignOWLClassCommand(selectedConceptAnnotation));
                    } else {
                        return;
                    }
                } catch (NoSelectionException e1) {
                    e1.printStackTrace();
                }
            }

            show(e.getComponent(), e.getX(), e.getY());
        }


    }
}
