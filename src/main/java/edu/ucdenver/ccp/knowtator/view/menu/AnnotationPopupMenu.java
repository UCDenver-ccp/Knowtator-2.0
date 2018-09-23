package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;
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
        menuItem.addActionListener(e1 -> view.getController().getTextSourceManager()
                .getSelection().getAnnotationManager().reassignSelectedOWLClassToSelectedAnnotation());

        return menuItem;
    }

    private JMenuItem addAnnotationCommand() {
        JMenuItem menuItem = new JMenuItem("Add annotation");
        menuItem.addActionListener(
                e12 ->
                        view.getController()
                                .getTextSourceManager()
                                .getSelection()
                                .getAnnotationManager()
                                .addSelectedAnnotation());

        return menuItem;
    }

    private JMenuItem addSpanToAnnotationCommand() {
        JMenuItem addSpanToAnnotation = new JMenuItem("Add span");
        addSpanToAnnotation.addActionListener(
                e4 ->
                        view.getController()
                                .getTextSourceManager()
                                .getSelection()
                                .getAnnotationManager()
                                .addSpanToSelectedAnnotation());

        return addSpanToAnnotation;
    }

    private JMenuItem removeSpanFromAnnotationCommand() {


        JMenuItem removeSpanFromSelectedAnnotation =
                new JMenuItem(
                        String.format(
                                "Delete span from %s",
                                view.getController()
                                        .getTextSourceManager()
                                        .getSelection()
                                        .getAnnotationManager()
                                        .getSelection()
                                        .getOwlClass()));
        removeSpanFromSelectedAnnotation.addActionListener(
                e5 ->
                        view.getController()
                                .getTextSourceManager()
                                .getSelection()
                                .getAnnotationManager()
                                .removeSpanFromSelectedAnnotation());

        return removeSpanFromSelectedAnnotation;
    }

    private JMenuItem selectAnnotationCommand(Annotation annotation, Span span) {
        JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + annotation.getOwlClassID());
        selectAnnotationMenuItem.addActionListener(
                e3 ->
                        view.getController()
                                .getTextSourceManager()
                                .getSelection()
                                .getAnnotationManager()
                                .getSelection()
                                .getSpanManager()
                                .setSelection(span));

        return selectAnnotationMenuItem;
    }

    private JMenuItem removeAnnotationCommand() {
        JMenuItem removeAnnotationMenuItem = new JMenuItem(
                "Delete "
                        + view.getController()
                        .getTextSourceManager()
                        .getSelection()
                        .getAnnotationManager()
                        .getSelection()
                        .getOwlClass());

        removeAnnotationMenuItem.addActionListener(
                e4 -> {
                    if (JOptionPane.showConfirmDialog(
                            view,
                            "Are you sure you want to remove the selected annotation?",
                            "Remove Annotation",
                            JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                        view.getController()
                                .getTextSourceManager()
                                .getSelection()
                                .getAnnotationManager()
                                .addSelectedAnnotation();
                    }
                });

        return removeAnnotationMenuItem;
    }

    public void chooseAnnotation(Set<Span> spansContainingLocation) {
        // Menu items to select and remove annotations
        spansContainingLocation.forEach(
                span -> add(selectAnnotationCommand(span.getAnnotation(), span)));

        show(e.getComponent(), e.getX(), e.getY());
    }

    public void showPopUpMenu(int release_offset) {


        Annotation selectedAnnotation =
                view.getController()
                        .getTextSourceManager()
                        .getSelection()
                        .getAnnotationManager()
                        .getSelection();
        Span selectedSpan =
                view.getController()
                        .getTextSourceManager()
                        .getSelection()
                        .getAnnotationManager()
                        .getSelection()
                        .getSpanManager()
                        .getSelection();

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
                    .getTextSourceManager()
                    .getSelection()
                    .getAnnotationManager()
                    .getSelection()
                    != null) {
                add(addSpanToAnnotationCommand());
            }
        } else if (selectedAnnotation != null
                && selectedSpan.getStart() <= release_offset
                && release_offset <= selectedSpan.getEnd()) {
            add(removeAnnotationCommand());
            if (view.getController()
                    .getTextSourceManager()
                    .getSelection()
                    .getAnnotationManager()
                    .getSelection()
                    .getSpanManager()
                    .getSelection()
                    != null
                    && view.getController()
                    .getTextSourceManager()
                    .getSelection()
                    .getAnnotationManager()
                    .getSelection()
                    != null
                    && view.getController()
                    .getTextSourceManager()
                    .getSelection()
                    .getAnnotationManager()
                    .getSelection()
                    .getSpanManager()
                    .size()
                    > 1) {
                add(removeSpanFromAnnotationCommand());
            }
            add(reassignOWLClassCommand());

        } else {
            return;
        }

        show(e.getComponent(), e.getX(), e.getY());
    }
}
