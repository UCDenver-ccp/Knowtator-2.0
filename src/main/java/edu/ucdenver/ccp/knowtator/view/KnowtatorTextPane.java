/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.actions.*;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.FilterModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollectionListener;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.profile.ColorListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.min;

@SuppressWarnings("deprecation")
public class KnowtatorTextPane extends AnnotatableTextPane implements ColorListener, KnowtatorComponent, FilterModelListener, KnowtatorCollectionListener<Profile> {

    @SuppressWarnings("unused")
    private static Logger log = Logger.getLogger(KnowtatorTextPane.class);

    private final KnowtatorView view;

    KnowtatorTextPane(KnowtatorView view, JTextField searchTextField, JCheckBox onlyInAnnotationsCheckBox, JCheckBox regexCheckBox, JCheckBox caseSensitiveCheckBox) {
        super(view.getController(), searchTextField, onlyInAnnotationsCheckBox, regexCheckBox, caseSensitiveCheckBox);
        this.view = view;

        view.getController().getProfileCollection().addColorListener(this);
        view.getController().getProfileCollection().addCollectionListener(this);
        view.getController().getFilterModel().addFilterModelListener(this);
        addCaretListener(view.getController().getSelectionModel());
    }

    public BufferedImage getScreenShot() {

        BufferedImage image =
                new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        // call the Component's paint method, using
        // the Graphics object of the image.
        paint(image.getGraphics()); // alternately use .printAll(..)
        return image;
    }


    protected void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
        try {
            AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e);

            Set<Span> spansContainingLocation =  view.getController()
                    .getTextSourceCollection().getSelection()
                    .getConceptAnnotationCollection()
                    .getSpans(press_offset).getCollection();

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

    void refreshHighlights() {
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
                Set<Span> spans = view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection()
                        .getSpans(null).getCollection();
                highlightOverlaps(spans);
                highlightSpans(spans);
            } catch (NoSelectionException ignored) {
            }

            revalidate();
            repaint();

            Span span = null;
            try {
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
            OWLClass owlClass = textSource.getConceptAnnotationCollection().getSelection().getOwlClass();
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
                        new DefaultHighlighter.DefaultHighlightPainter(span.getConceptAnnotation().getColor()));
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



    @Override
    public void reset() {
        view.getController().getProfileCollection().addColorListener(this);
        view.getController().getProfileCollection().addCollectionListener(this);
        view.getController().getFilterModel().addFilterModelListener(this);
    }

    @Override
    public void dispose() {

    }

    @Override
    public void colorChanged() {
        refreshHighlights();
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
    public void selected(SelectionEvent<Profile> event) {
        refreshHighlights();
    }

    @Override
    public void added() {
    }

    @Override
    public void removed() {
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

        AnnotationPopupMenu(MouseEvent e) {
            this.e = e;
        }

        private JMenuItem reassignOWLClassCommand() {
            JMenuItem menuItem = new JMenuItem("Reassign OWL class");
            menuItem.addActionListener(e -> {
                try {
                    AbstractKnowtatorAction action = new OWLActions.ReassignOWLClassAction(view.getController());
                    view.getController().registerAction(action);
                } catch (NoSelectionException | ActionUnperformableException e1) {
                    e1.printStackTrace();
                }
            });

            return menuItem;
        }

        private JMenuItem addAnnotationCommand() {
            JMenuItem menuItem = new JMenuItem("Add concept");
            menuItem.addActionListener(e12 -> {
                Map<String, String> actionParameters = new HashMap<>();
                actionParameters.put(KnowtatorXMLTags.ANNOTATION, AbstractKnowtatorCollectionAction.ADD);
                actionParameters.put(KnowtatorXMLTags.SPAN, AbstractKnowtatorCollectionAction.ADD);
                KnowtatorCollectionActions.pickAction(actionParameters, view, null, null);
            });

            return menuItem;
        }

        private JMenuItem removeSpanFromAnnotationCommand(ConceptAnnotation conceptAnnotation) {
            JMenuItem removeSpanFromSelectedAnnotation = new JMenuItem(String.format("Delete span from %s", conceptAnnotation.getOwlClass()));
            removeSpanFromSelectedAnnotation.addActionListener(e5 -> {
                Map<String, String> actionParameters = new HashMap<>();
                actionParameters.put(KnowtatorXMLTags.SPAN, AbstractKnowtatorCollectionAction.REMOVE);

                KnowtatorCollectionActions.pickAction(actionParameters, view, null, null);
            });

            return removeSpanFromSelectedAnnotation;
        }

        private JMenuItem selectAnnotationCommand(Span span) {
            JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + span.getConceptAnnotation().getOwlClassID());
            selectAnnotationMenuItem.addActionListener(e3 -> textSource.getConceptAnnotationCollection().setSelectedAnnotation(span));

            return selectAnnotationMenuItem;
        }

        private JMenuItem removeAnnotationCommand(ConceptAnnotation conceptAnnotation) {
            JMenuItem removeAnnotationMenuItem = new JMenuItem("Delete " + conceptAnnotation.getOwlClass());
            removeAnnotationMenuItem.addActionListener(e4 -> {
                Map<String, String> actionParameters = new HashMap<>();
                actionParameters.put(KnowtatorXMLTags.ANNOTATION, AbstractKnowtatorCollectionAction.REMOVE);
                KnowtatorCollectionActions.pickAction(actionParameters, view, null, null);
            });

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
                        add(reassignOWLClassCommand());
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
