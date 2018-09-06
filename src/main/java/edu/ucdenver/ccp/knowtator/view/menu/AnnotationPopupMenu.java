package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;
import edu.ucdenver.ccp.knowtator.view.ControllerNotSetException;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Set;

public class AnnotationPopupMenu extends JPopupMenu {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(AnnotationPopupMenu.class);

  private MouseEvent e;
  private KnowtatorView view;

  public AnnotationPopupMenu(MouseEvent e, KnowtatorView view) {
    this.e = e;
    this.view = view;
  }

  private JMenuItem reassignOWLClassCommand() {
    JMenuItem menuItem = new JMenuItem("Reassign OWL class");
    menuItem.addActionListener(e1 -> {
      try {
        view.getController().getSelectionManager().getActiveTextSource().getAnnotationManager().reassignSelectedOWLClassToSelectedAnnotation();
      } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {
      }
    });

    return menuItem;
  }

  private JMenuItem addAnnotationCommand() {
    JMenuItem menuItem = new JMenuItem("Add annotation");
    menuItem.addActionListener(
        e12 ->
        {
          try {
            view.getController()
                .getSelectionManager()
                .getActiveTextSource()
                .getAnnotationManager()
                .addSelectedAnnotation();
          } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

          }
        });

    return menuItem;
  }

  private JMenuItem addSpanToAnnotationCommand() {
    JMenuItem addSpanToAnnotation = new JMenuItem("Add span");
    addSpanToAnnotation.addActionListener(
        e4 ->
        {
          try {
            view.getController()
                .getSelectionManager()
                .getActiveTextSource()
                .getAnnotationManager()
                .addSpanToSelectedAnnotation();
          } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

          }
        });

    return addSpanToAnnotation;
  }

  private JMenuItem removeSpanFromAnnotationCommand() {
    try{


    JMenuItem removeSpanFromSelectedAnnotation =
        new JMenuItem(
            String.format(
                "Delete span from %s",
                view.getController()
                    .getSelectionManager()
                    .getActiveTextSource()
                    .getAnnotationManager()
                    .getSelectedAnnotation()
                    .getOwlClass()));
    removeSpanFromSelectedAnnotation.addActionListener(
        e5 ->
        {
          try {
            view.getController()
                    .getSelectionManager()
                    .getActiveTextSource()
                    .getAnnotationManager()
                    .removeSpanFromSelectedAnnotation();
          } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

          }
        });

    return removeSpanFromSelectedAnnotation;
    } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {
      return null;
    }
  }

  private JMenuItem selectAnnotationCommand(Annotation annotation, Span span) {
    JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + annotation.getOwlClassID());
    selectAnnotationMenuItem.addActionListener(
        e3 ->
        {
          try {
            view.getController()
                .getSelectionManager()
                .getActiveTextSource()
                .getAnnotationManager()
                .setSelectedSpan(span);
          }  catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

          }
        });

    return selectAnnotationMenuItem;
  }

  private JMenuItem removeAnnotationCommand() {
    JMenuItem removeAnnotationMenuItem =
            null;
    try {
      removeAnnotationMenuItem = new JMenuItem(
          "Delete "
              + view.getController()
                  .getSelectionManager()
                  .getActiveTextSource()
                  .getAnnotationManager()
                  .getSelectedAnnotation()
                  .getOwlClass());

    removeAnnotationMenuItem.addActionListener(
        e4 -> {
          if (JOptionPane.showConfirmDialog(
                  view,
                  "Are you sure you want to remove the selected annotation?",
                  "Remove Annotation",
                  JOptionPane.YES_NO_OPTION)
              == JOptionPane.YES_OPTION) {
            try {
              view.getController()
                  .getSelectionManager()
                  .getActiveTextSource()
                  .getAnnotationManager()
                  .addSelectedAnnotation();
            } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

            }
          }
        });
    } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {
    }

    return removeAnnotationMenuItem;
  }

  public void chooseAnnotation(Set<Span> spansContainingLocation) {
    // Menu items to select and remove annotations
    spansContainingLocation.forEach(
        span -> add(selectAnnotationCommand(span.getAnnotation(), span)));

    show(e.getComponent(), e.getX(), e.getY());
  }

  public void showPopUpMenu(int release_offset) {

    try{


    Annotation selectedAnnotation =
        view.getController()
            .getSelectionManager()
            .getActiveTextSource()
            .getAnnotationManager()
            .getSelectedAnnotation();
    Span selectedSpan =
        view.getController()
            .getSelectionManager()
            .getActiveTextSource()
            .getAnnotationManager()
            .getSelectedSpan();

    if (view.getKnowtatorTextPane().getSelectionStart() <= release_offset
        && release_offset <= view.getKnowtatorTextPane().getSelectionEnd()
        && view.getKnowtatorTextPane().getSelectionStart()
            != view.getKnowtatorTextPane().getSelectionEnd()) {
      view.getKnowtatorTextPane()
          .select(
              view.getKnowtatorTextPane().getSelectionStart(),
              view.getKnowtatorTextPane().getSelectionEnd());
      add(addAnnotationCommand());
      if (view.getController()
              .getSelectionManager()
              .getActiveTextSource()
              .getAnnotationManager()
              .getSelectedAnnotation()
          != null) {
        add(addSpanToAnnotationCommand());
      }
    } else if (selectedAnnotation != null
        && selectedSpan.getStart() <= release_offset
        && release_offset <= selectedSpan.getEnd()) {
      add(removeAnnotationCommand());
      if (view.getController()
                  .getSelectionManager()
                  .getActiveTextSource()
                  .getAnnotationManager()
                  .getSelectedSpan()
              != null
          && view.getController()
                  .getSelectionManager()
                  .getActiveTextSource()
                  .getAnnotationManager()
                  .getSelectedAnnotation()
              != null
          && view.getController()
                  .getSelectionManager()
                  .getActiveTextSource()
                  .getAnnotationManager()
                  .getSelectedAnnotation()
                  .getSpanCollection()
                  .getCollection()
                  .size()
              > 1) {
        add(removeSpanFromAnnotationCommand());
      }
      add(reassignOWLClassCommand());

    } else {
      return;
    }

    show(e.getComponent(), e.getX(), e.getY());
    } catch (ActiveTextSourceNotSetException | ControllerNotSetException ignored) {

    }
  }
}
