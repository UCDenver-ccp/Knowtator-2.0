package edu.ucdenver.ccp.knowtator.view.textpane;

import edu.ucdenver.ccp.knowtator.events.AnnotationChangeEvent;
import edu.ucdenver.ccp.knowtator.events.ProfileChangeEvent;
import edu.ucdenver.ccp.knowtator.events.SpanChangeEvent;
import edu.ucdenver.ccp.knowtator.events.TextSourceChangeEvent;
import edu.ucdenver.ccp.knowtator.listeners.*;
import edu.ucdenver.ccp.knowtator.model.Span;
import edu.ucdenver.ccp.knowtator.model.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.menu.AnnotationPopupMenu;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Set;

import static java.lang.Math.max;
import static java.lang.Math.min;

public abstract class KnowtatorTextPane extends JTextPane
    implements ProfileSelectionListener,
        TextSourceSelectionListener,
        AnnotationSelectionListener,
        SpanSelectionListener,
        ProjectListener,
        ColorListener {

  KnowtatorView view;

  KnowtatorTextPane(KnowtatorView view) {
    super();
    this.view = view;
	  view.getController().getSelectionManager().addTextSourceListener(this);
	  view.getController().getSelectionManager().addAnnotationListener(this);
    view.getController().getSelectionManager().addProfileListener(this);
    view.getController().getSelectionManager().addSpanListener(this);
    view.getController().getProfileManager().addColorListener(this);

    getCaret().setVisible(true);
    addCaretListener(view.getController().getSelectionManager());

    setupListeners();
    requestFocusInWindow();
    select(0, 0);
  }

  abstract void showTextPane(TextSource textSource);

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
          public void mouseEntered(MouseEvent e) {}

          @Override
          public void mouseExited(MouseEvent e) {}

          @Override
          public void mouseClicked(MouseEvent e) {}
        });
  }

  private void handleMouseRelease(MouseEvent e, int press_offset, int release_offset) {
    if (view.getController().getSelectionManager().getActiveTextSource() != null) {
      AnnotationPopupMenu popupMenu = new AnnotationPopupMenu(e, view);

      Set<Span> spansContainingLocation = getSpans(press_offset);

      if (SwingUtilities.isRightMouseButton(e)) {
        if (spansContainingLocation.size() == 1) {
          Span span = spansContainingLocation.iterator().next();
          view.getController().getSelectionManager().setSelectedSpan(span);
        }
        popupMenu.showPopUpMenu(release_offset);
      } else if (press_offset == release_offset) {
        if (spansContainingLocation.size() == 1) {
          Span span = spansContainingLocation.iterator().next();
          view.getController().getSelectionManager().setSelectedSpan(span);
        } else if (spansContainingLocation.size() > 1) {
          popupMenu.chooseAnnotation(spansContainingLocation);
        }

      } else {
        view.getController().getSelectionManager().setSelectedAnnotation(null, null);
        setSelectionAtWordLimits(press_offset, release_offset);
      }
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

  void refreshHighlights() {
    if (view.getController().getSelectionManager().getActiveTextSource() != null) {

      if (view.getController().getSelectionManager().getSelectedSpan() != null) {
        try {
          scrollRectToVisible(
              modelToView(view.getController().getSelectionManager().getSelectedSpan().getStart()));
        } catch (BadLocationException | NullPointerException ignored) {

        }
      }

      // Remove all previous highlights in case a span has been deleted
      getHighlighter().removeAllHighlights();

      // Always highlight the selected annotation first so its color and border show up
      highlightSelectedAnnotation();

      // Highlight overlaps first, then spans
      Span lastSpan = null;
      Color lastColor = null;

      Set<Span> spans = getSpans(null);
      for (Span span : spans) {
        if (lastSpan != null) {
          if (span.intersects(lastSpan)) {
            try {
              highlightSpan(
                  span.getStart(),
                  min(span.getEnd(), lastSpan.getEnd()),
                  new DefaultHighlighter.DefaultHighlightPainter(Color.LIGHT_GRAY));
            } catch (BadLocationException e) {
              e.printStackTrace();
            }
          }
          if (span.getEnd() > lastSpan.getEnd()) {
            try {
              highlightSpan(
                  lastSpan.getStart(),
                  lastSpan.getEnd(),
                  new DefaultHighlighter.DefaultHighlightPainter(lastColor));
            } catch (BadLocationException e) {
              e.printStackTrace();
            }
          }
        }
        lastSpan = span;

        lastColor = span.getAnnotation().getAnnotator().getColor(span.getAnnotation());
      }
      if (lastSpan != null) {

        // Highlight remaining span
        try {
          highlightSpan(
              lastSpan.getStart(),
              lastSpan.getEnd(),
              new DefaultHighlighter.DefaultHighlightPainter(lastColor));
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      }

      revalidate();
      repaint();
    }
  }

  public abstract void highlightSpan(
      int start, int end, DefaultHighlighter.DefaultHighlightPainter highlighter)
      throws BadLocationException;

  protected abstract Set<Span> getSpans(Integer loc);

  private void highlightSelectedAnnotation() {
    if (view.getController().getSelectionManager().getSelectedAnnotation() != null) {
      for (Span span :
          view.getController()
              .getSelectionManager()
              .getSelectedAnnotation()
              .getSpanCollection()
              .getCollection()) {
        try {
          if (span.equalStartAndEnd(view.getController().getSelectionManager().getSelectedSpan())) {
            highlightSpan(span.getStart(), span.getEnd(), new RectanglePainter(Color.BLACK));
          } else {
            highlightSpan(span.getStart(), span.getEnd(), new RectanglePainter(Color.GRAY));
          }
        } catch (BadLocationException e) {
          e.printStackTrace();
        }
      }
    }
  }

  @Override
  public void selectedAnnotationChanged(AnnotationChangeEvent e) {
    refreshHighlights();
  }

  @Override
  public void selectedSpanChanged(SpanChangeEvent e) {
    refreshHighlights();
  }

  @Override
  public void activeTextSourceChanged(TextSourceChangeEvent e) {
    showTextPane(e.getNew());
    refreshHighlights();
  }

  @Override
  public void activeProfileChange(ProfileChangeEvent e) {
    refreshHighlights();
  }

  @Override
  public void projectClosed() {}

  @Override
  public void projectLoaded() {
    showTextPane(view.getController().getSelectionManager().getActiveTextSource());
  }

  public void decreaseFontSize() {
    StyledDocument doc = getStyledDocument();
    MutableAttributeSet attrs = getInputAttributes();
    Font font = doc.getFont(attrs);
    StyleConstants.setFontSize(attrs, font.getSize() - 2);
    doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
    repaint();
  }

  public void increaseFindSize() {
    StyledDocument doc = getStyledDocument();
    MutableAttributeSet attrs = getInputAttributes();
    Font font = doc.getFont(attrs);
    StyleConstants.setFontSize(attrs, font.getSize() + 2);
    doc.setCharacterAttributes(0, doc.getLength() + 1, attrs, false);
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
