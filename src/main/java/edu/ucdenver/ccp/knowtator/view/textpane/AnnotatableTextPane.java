/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.textpane;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction.pickAction;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.ADD;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.ANNOTATION;
import static edu.ucdenver.ccp.knowtator.view.actions.collection.KnowtatorCollectionType.SPAN;
import static java.lang.Math.max;
import static java.lang.Math.min;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.ModelListener;
import edu.ucdenver.ccp.knowtator.model.collection.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.collection.CyclableCollection;
import edu.ucdenver.ccp.knowtator.model.collection.SelectableCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.Span;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorComponent;
import edu.ucdenver.ccp.knowtator.view.KnowtatorDefaultSettings;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.collection.ActionParameters;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputAdapter;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.JTextComponent;
import javax.swing.text.Position;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * A text pane that can be annotated.
 */
public abstract class AnnotatableTextPane extends SearchableTextPane
    implements KnowtatorComponent, ModelListener {
  @SuppressWarnings("unused")
  private final Logger log = LogManager.getLogger(AnnotatableTextPane.class.getName());

  private final DefaultHighlighter.DefaultHighlightPainter overlapHighlighter =
      new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY);

  /**
   * Instantiates a new Annotatable text pane.
   *
   * @param searchTextField the search text field
   */
  AnnotatableTextPane(KnowtatorView view, JTextField searchTextField) {
    super(view, searchTextField);
    setEditable(false);
    setFont(KnowtatorDefaultSettings.FONT);

    getCaret().setVisible(true);

    requestFocusInWindow();
    select(0, 0);
    getCaret().setSelectionVisible(true);
    // addCaretListener(mouseEvent -> refreshHighlights());

    setEnabled(true);

    MouseInputAdapter mouseListener =
        new MouseInputAdapter() {
          int pressOffset = 0;

          @Override
          public void mouseDragged(MouseEvent e) {
            super.mouseDragged(e);
            refreshHighlights();
            highlightRegion(pressOffset, pressOffset, new RectanglePainter(Color.BLACK));

            view.getModel()
                .map(BaseModel::getTextSources)
                .flatMap(TextSourceCollection::getOnlySelected)
                .map(TextSource::getConceptAnnotations)
                .flatMap(ConceptAnnotationCollection::getOnlySelected)
                .ifPresent(SelectableCollection::clearSelection);
          }

          @Override
          public void mouseMoved(MouseEvent e) {
            super.mouseMoved(e);
          }

          @Override
          public void mousePressed(MouseEvent e) {
            super.mousePressed(e);
            pressOffset = viewToModel(e.getPoint());

          }

          @Override
          public void mouseReleased(MouseEvent e) {
            super.mouseReleased(e);
            handleMouseRelease(e, pressOffset, viewToModel(e.getPoint()));
          }

          @Override
          public void mouseEntered(MouseEvent e) {
            super.mouseEntered(e);
          }

          @Override
          public void mouseExited(MouseEvent e) {
            super.mouseExited(e);
          }

          @Override
          public void mouseClicked(MouseEvent e) {
            super.mouseClicked(e);
          }
        };

    addMouseMotionListener(mouseListener);
    addMouseListener(mouseListener);
  }

  /**
   * Sets the text to the text sources content.
   */
  public void showTextSource() {
    view.getModel()
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .filter(textSource -> !textSource.getContent().equals(getText()))
        .ifPresent(textSource -> setText(textSource.getContent()));
    refreshHighlights();
  }

  /**
   * Does things when mouse is released.
   *
   * @param e             MouseEvent
   * @param pressOffset   Mouse position at press
   * @param releaseOffset Mouse position at release
   */
  private void handleMouseRelease(MouseEvent e, int pressOffset, int releaseOffset) {
    AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e);
    view.getModel()
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .map(TextSource::getConceptAnnotations)
        .ifPresent(
            conceptAnnotations -> {
              Set<Span> spansContainingLocation = conceptAnnotations.getSpans(pressOffset).getCollection();

              if (SwingUtilities.isRightMouseButton(e)) {
                if (spansContainingLocation.size() == 1) {
                  Span span = spansContainingLocation.iterator().next();
                  span.getConceptAnnotation().selectOnly(span);
                }
                popupMenu.showPopUpMenu(releaseOffset);
              } else if (pressOffset == releaseOffset) {
                if (spansContainingLocation.size() == 1) {
                  Span span = spansContainingLocation.iterator().next();
                  span.getConceptAnnotation().selectOnly(span);
                } else if (spansContainingLocation.size() > 1) {
                  popupMenu.chooseAnnotation(spansContainingLocation);
                }

              } else {
                if (view.isSnapToWords()) {
                  setSelectionAtWordLimits(pressOffset, releaseOffset);
                }
                refreshHighlights();
              }
            });
  }

  /**
   * Repaints the highlights.
   */
  @Override
  protected void refreshHighlights() {
    view.getModel()
        .filter(BaseModel::isNotLoading)
        .ifPresent(
            model -> {
              getHighlighter().removeAllHighlights();
              if (view.getModel()
                  .map(BaseModel::getTextSources)
                  .flatMap(TextSourceCollection::getOnlySelected)
                  .map(TextSource::getConceptAnnotations)
                  .map(ConceptAnnotationCollection::getSelection)
                  .filter(list -> list.stream().anyMatch(conceptAnnotation -> conceptAnnotation.getOnlySelected().isPresent()))
                  .isPresent()) {
                highlightSelectedAnnotation();
              } else {
                highlightRegion(
                    getSelectionStart(), getSelectionEnd(), new RectanglePainter(Color.BLACK));
              }

              // Highlight overlaps firstConceptAnnotation, then spans.
              // Overlaps must be highlighted first
              // because the highlights are displayed
              // in order of placement
              model
                  .getTextSources()
                  .getOnlySelected()
                  .map(TextSource::getConceptAnnotations)
                  .ifPresent(
                      conceptAnnotations -> {
                        Set<Span> spans = conceptAnnotations.getSpans(null).getCollection();
                        highlightOverlaps(spans);
                        highlightSpans(spans);
                      });

              revalidate();
              repaint();

              Optional<Span> span;
              if (!model
                  .getTextSources()
                  .getOnlySelected()
                  .map(TextSource::getConceptAnnotations)
                  .flatMap(ConceptAnnotationCollection::getOnlySelected)
                  .flatMap(SelectableCollection::getOnlySelected)
                  .isPresent()) {
                span =
                    model
                        .getTextSources()
                        .getOnlySelected()
                        .map(TextSource::getConceptAnnotations)
                        .flatMap(ConceptAnnotationCollection::getOnlySelected)
                        .flatMap(CyclableCollection::first);
              } else {
                span =
                    model
                        .getTextSources()
                        .getOnlySelected()
                        .map(TextSource::getConceptAnnotations)
                        .flatMap(ConceptAnnotationCollection::getOnlySelected)
                        .flatMap(SelectableCollection::getOnlySelected);
              }
              Optional<Span> finalSpan = span;
              SwingUtilities.invokeLater(
                  () -> {
                    if (finalSpan.isPresent()) {
                      finalSpan.ifPresent(
                          span1 -> {
                            try {
                              scrollRectToVisible(modelToView(span1.getStart()));
                            } catch (NullPointerException
                                     | ArrayIndexOutOfBoundsException
                                     | BadLocationException ignored) {
                              // It's fine if any of these are thrown
                            }
                          });
                    }
                  });
            });
  }

  /**
   * Highlights spans according to the color specified by their annotation. Underlines spans from
   * annotations whose OWL class is selected
   *
   * @param spans A set of spans to highlight
   */
  private void highlightSpans(Set<Span> spans) {
    spans.forEach(
        span ->
            highlightRegion(
                span.getStart(),
                span.getEnd(),
                new DefaultHighlighter.DefaultHighlightPainter(
                    span.getConceptAnnotation().getColor())));
  }

  /**
   * Highlights overlapping spans.
   *
   * @param spans A set of spans to highlight
   */
  private void highlightOverlaps(Set<Span> spans) {
    Iterator<Span> spanIterator = spans.iterator();
    if (spanIterator.hasNext()) {
      Span lastSpan = spanIterator.next();
      while (spanIterator.hasNext()) {
        Span span = spanIterator.next();
        if (span.intersects(lastSpan)) {
          highlightRegion(
              span.getStart(), min(lastSpan.getEnd(), span.getEnd()), overlapHighlighter);
        }
        if (span.getEnd() > lastSpan.getEnd()) {
          lastSpan = span;
        }
      }
    }
  }

  /**
   * Highlights a region using the given highlighter.
   *
   * @param start       Start of highlight
   * @param end         End of highlight
   * @param highlighter Highlighter to use to draw the highlight
   */
  private void highlightRegion(
      int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter) {
    try {
      getHighlighter().addHighlight(start, end, highlighter);
    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Highlights the spans for the selected annotation.
   */
  private void highlightSelectedAnnotation() {
    view.getModel()
        .map(BaseModel::getTextSources)
        .flatMap(TextSourceCollection::getOnlySelected)
        .map(TextSource::getConceptAnnotations)
        .map(ConceptAnnotationCollection::getSelection)
        .ifPresent(conceptAnnotations -> conceptAnnotations
            .forEach(conceptAnnotation -> conceptAnnotation
                .forEach(span -> highlightRegion(
                    span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK)))));
  }

  /**
   * Snaps selection to word limits.
   *
   * @param pressOffset   Mouse position at press
   * @param releaseOffset Mouse position at release
   */
  private void setSelectionAtWordLimits(int pressOffset, int releaseOffset) {

    try {
      int start = Utilities.getWordStart(this, min(pressOffset, releaseOffset));
      int end = Utilities.getWordEnd(this, max(pressOffset, releaseOffset));

      // I don't want to deselect the annotation here because
      // I may want to addProfile a spanOptional to it

      requestFocusInWindow();
      select(start, end);

    } catch (BadLocationException e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets the font size.
   *
   * @param size Value to set font getNumberOfGraphSpaces to
   */
  public void setFontSize(int size) {
    Font font = getFont();
    setFont(new Font(font.getName(), font.getStyle(), size));
    repaint();
  }

  /**
   * Alters the selection.
   *
   * @param startModification Value to modify the start selection by
   * @param endModification   Value to modify the end selection by
   */
  public void modifySelection(int startModification, int endModification) {
    select(getSelectionStart() + startModification, getSelectionEnd() + endModification);
  }

  @Override
  public void colorChangedEvent(Profile profile) {
    showTextSource();
  }

  @Override
  public void filterChangedEvent() {
    showTextSource();
  }

  @Override
  public void modelChangeEvent(ChangeEvent<ModelObject> event) {
    super.modelChangeEvent(event);
    view.getModel().ifPresent(knowtatorModel -> {
      if (knowtatorModel.getNumberOfTextSources() == 0) {
        setEnabled(false);
      } else {
        setEnabled(true);
        showTextSource();
      }
    });
  }

  @Override
  public void reset() {

    view.getModel().ifPresent(this::addCaretListener);
    view.getModel().ifPresent(model -> model.addModelListener(this));
  }

  @Override
  public void dispose() {
    view.getModel().ifPresent(model -> model.removeModelListener(this));
  }

  /**
   * The type Rectangle painter.
   */
  static class RectanglePainter extends DefaultHighlighter.DefaultHighlightPainter {

    /**
     * Instantiates a new Rectangle painter.
     *
     * @param color the color
     */
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

      if (r == null) {
        return null;
      }

      //  Do your custom painting

      Color color = getColor();
      g.setColor(color == null ? c.getSelectionColor() : color);

      ((Graphics2D) g).setStroke(new BasicStroke(4));

      // Code is the same as the default highlighter except we use drawRect(...)

      // g.fillRect(r.x, r.y, r.width, r.height);
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

  /**
   * The type Annotation popup menu.
   */
  class AnnotationPopupMenu extends JPopupMenu {
    private final MouseEvent mouseEvent;

    /**
     * Instantiates a new Annotation popup menu.
     *
     * @param mouseEvent the mouse event
     */
    AnnotationPopupMenu(MouseEvent mouseEvent) {
      this.mouseEvent = mouseEvent;
    }

    private JMenuItem addAnnotationCommand() {
      JMenuItem menuItem = new JMenuItem("Add concept");
      menuItem.addActionListener(
          e12 ->
              pickAction(
                  view,
                  null,
                  null,
                  new ActionParameters(ADD, ANNOTATION),
                  new ActionParameters(ADD, SPAN)));

      return menuItem;
    }

    private JMenuItem removeSpanFromAnnotationCommand(ConceptAnnotation conceptAnnotation) {
      JMenuItem removeSpanFromSelectedAnnotation =
          new JMenuItem(String.format("Delete span from %s", conceptAnnotation.getOwlClass()));
      removeSpanFromSelectedAnnotation.addActionListener(
          e5 -> pickAction(view, null, null, new ActionParameters(REMOVE, SPAN)));

      return removeSpanFromSelectedAnnotation;
    }

    private JMenuItem selectAnnotationCommand(Span span) {
      return view.getModel()
          .map(
              model -> {
                JMenuItem selectAnnotationMenuItem =
                    new JMenuItem(
                        String.format(
                            "Select %s",
                            model.getOwlEntityRendering(
                                span.getConceptAnnotation().getOwlClass())));
                selectAnnotationMenuItem.addActionListener(
                    e3 ->
                        model
                            .getTextSources()
                            .getOnlySelected()
                            .ifPresent(
                                textSource -> span.getConceptAnnotation().selectOnly(span)));
                return selectAnnotationMenuItem;
              })
          .orElse(null);
    }

    private JMenuItem removeAnnotationCommand(ConceptAnnotation conceptAnnotation) {
      JMenuItem removeAnnotationMenuItem =
          new JMenuItem(String.format("Delete %s", conceptAnnotation.getOwlClass()));
      removeAnnotationMenuItem.addActionListener(
          e4 -> pickAction(view, null, null, new ActionParameters(REMOVE, ANNOTATION)));

      return removeAnnotationMenuItem;
    }

    /**
     * Choose annotation.
     *
     * @param spansContainingLocation the spans containing location
     */
    void chooseAnnotation(Set<Span> spansContainingLocation) {
      // Menu items to select and remove annotations
      spansContainingLocation.forEach(span -> add(selectAnnotationCommand(span)));

      show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
    }

    /**
     * Show pop up menu.
     *
     * @param releaseOffset the release offset
     */
    void showPopUpMenu(int releaseOffset) {
      if (getSelectionStart() <= releaseOffset
          && releaseOffset <= getSelectionEnd()
          && getSelectionStart() != getSelectionEnd()) {
        select(getSelectionStart(), getSelectionEnd());
        add(addAnnotationCommand());

        show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
      } else {
        view.getModel()
            .map(BaseModel::getTextSources)
            .flatMap(TextSourceCollection::getOnlySelected)
            .map(TextSource::getConceptAnnotations)
            .flatMap(ConceptAnnotationCollection::getOnlySelected)
            .ifPresent(conceptAnnotation ->
                conceptAnnotation
                    .getOnlySelected()
                    .filter(
                        span ->
                            span.getStart() <= releaseOffset
                                && releaseOffset <= span.getEnd())
                    .ifPresent(span -> clickedInsideSpan(conceptAnnotation)));
      }
    }

    private void clickedInsideSpan(ConceptAnnotation conceptAnnotation) {
      add(removeAnnotationCommand(conceptAnnotation));
      if (conceptAnnotation.size() > 1) {
        add(removeSpanFromAnnotationCommand(conceptAnnotation));
      }
      show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY());
    }
  }
}
