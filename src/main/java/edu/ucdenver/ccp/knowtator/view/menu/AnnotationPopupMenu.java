package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
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
        menuItem.addActionListener(e1 -> view.getController().getTextSourceCollection()
                .getSelection().getConceptAnnotationCollection().reassignSelectedOWLClassToSelectedAnnotation());

        return menuItem;
    }

    private JMenuItem addAnnotationCommand() {
        JMenuItem menuItem = new JMenuItem("Add concept");
        menuItem.addActionListener(
                e12 ->
                        view.getController()
                                .getTextSourceCollection()
                                .getSelection()
                                .getConceptAnnotationCollection()
                                .addSelectedAnnotation());

        return menuItem;
    }

    private JMenuItem addSpanToAnnotationCommand() {
        JMenuItem addSpanToAnnotation = new JMenuItem("Add span");
        addSpanToAnnotation.addActionListener(
                e4 ->
                        view.getController()
                                .getTextSourceCollection()
                                .getSelection()
                                .getConceptAnnotationCollection()
                                .addSpanToSelectedAnnotation());

        return addSpanToAnnotation;
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
        removeSpanFromSelectedAnnotation.addActionListener(
                e5 ->
                        view.getController()
                                .getTextSourceCollection()
                                .getSelection()
                                .getConceptAnnotationCollection()
                                .removeSpanFromSelectedAnnotation());

        return removeSpanFromSelectedAnnotation;
    }

    private JMenuItem selectAnnotationCommand(ConceptAnnotation conceptAnnotation, Span span) {
        JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + conceptAnnotation.getOwlClassID());
        selectAnnotationMenuItem.addActionListener(
                e3 ->
                        view.getController()
                                .getTextSourceCollection()
                                .getSelection()
                                .getConceptAnnotationCollection()
                                .getSelection()
                                .getSpanCollection()
                                .setSelection(span));

        return selectAnnotationMenuItem;
    }

    private JMenuItem removeAnnotationCommand() {
        JMenuItem removeAnnotationMenuItem = new JMenuItem(
                "Delete "
                        + view.getController()
                        .getTextSourceCollection()
                        .getSelection()
                        .getConceptAnnotationCollection()
                        .getSelection()
                        .getOwlClass());

        removeAnnotationMenuItem.addActionListener(
                e4 -> {
                    if (JOptionPane.showConfirmDialog(
                            view,
                            "Are you sure you want to remove the selected concept?",
                            "Remove ConceptAnnotation",
                            JOptionPane.YES_NO_OPTION)
                            == JOptionPane.YES_OPTION) {
                        view.getController()
                                .getTextSourceCollection()
                                .getSelection()
                                .getConceptAnnotationCollection()
                                .addSelectedAnnotation();
                    }
                });

        return removeAnnotationMenuItem;
    }

    public void chooseAnnotation(Set<Span> spansContainingLocation) {
        // Menu items to select and remove annotations
        spansContainingLocation.forEach(
                span -> add(selectAnnotationCommand(span.getConceptAnnotation(), span)));

        show(e.getComponent(), e.getX(), e.getY());
    }

    public void showPopUpMenu(int release_offset) {


        ConceptAnnotation selectedConceptAnnotation =
                view.getController()
                        .getTextSourceCollection()
                        .getSelection()
                        .getConceptAnnotationCollection()
                        .getSelection();
        Span selectedSpan =
                view.getController()
                        .getTextSourceCollection()
                        .getSelection()
                        .getConceptAnnotationCollection()
                        .getSelection()
                        .getSpanCollection()
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
                    .getTextSourceCollection()
                    .getSelection()
                    .getConceptAnnotationCollection()
                    .getSelection()
                    != null) {
                add(addSpanToAnnotationCommand());
            }
        } else if (selectedConceptAnnotation != null
                && selectedSpan.getStart() <= release_offset
                && release_offset <= selectedSpan.getEnd()) {
            add(removeAnnotationCommand());
            if (view.getController()
                    .getTextSourceCollection()
                    .getSelection()
                    .getConceptAnnotationCollection()
                    .getSelection()
                    .getSpanCollection()
                    .getSelection()
                    != null
                    && view.getController()
                    .getTextSourceCollection()
                    .getSelection()
                    .getConceptAnnotationCollection()
                    .getSelection()
                    != null
                    && view.getController()
                    .getTextSourceCollection()
                    .getSelection()
                    .getConceptAnnotationCollection()
                    .getSelection()
                    .getSpanCollection()
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
