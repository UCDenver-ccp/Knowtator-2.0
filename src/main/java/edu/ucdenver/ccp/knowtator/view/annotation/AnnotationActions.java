package edu.ucdenver.ccp.knowtator.view.annotation;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;

public class AnnotationActions {
    public static void addAnnotation(KnowtatorView view, TextSource textSource) {
        try {
            ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();
            String[] buttons = {"Add new concept", "Add span to concept", "Cancel"};
            int response =
                    JOptionPane.showOptionDialog(
                            view,
                            "Choose an option",
                            "Add ConceptAnnotation",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            buttons,
                            2);

            switch (response) {
                case 0:
                    textSource
                            .getConceptAnnotationCollection()
                            .addSelectedAnnotation();
                    break;
                case 1:
                    conceptAnnotation.getSpanCollection().addSpan(null,
                            view.getController().getSelectionModel().getStart(),
                            view.getController().getSelectionModel().getEnd());
                    break;
                case 2:
                    break;
            }
        } catch (NoSelectionException e) {
            textSource
                    .getConceptAnnotationCollection()
                    .addSelectedAnnotation();
        }

    }

    public static void removeAnnotation(KnowtatorView view, TextSource textSource, ConceptAnnotation annotation) {
        try {
            Span span = annotation.getSpanCollection().getSelection();
            String[] buttons = {"Remove annotation", "Remove span from annotation", "Cancel"};
            int response =
                    JOptionPane.showOptionDialog(
                            view,
                            "Choose an option",
                            "Remove Annotation",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.QUESTION_MESSAGE,
                            null,
                            buttons,
                            2);

            switch (response) {
                case 0:
                    textSource.getConceptAnnotationCollection().removeSelected();
                    break;
                case 1:
                    annotation.getSpanCollection().remove(span);
                    break;
                case 2:
                    break;
            }
        } catch (NoSelectionException e) {
            if (JOptionPane.showConfirmDialog(
                    view,
                    "Are you sure you want to remove the selected annotation?",
                    "Remove Annotation",
                    JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                textSource.getConceptAnnotationCollection().removeSelected();
            }
        }
    }

    public static void reassignOWLClass(KnowtatorView view, ConceptAnnotation conceptAnnotation) {
        OWLEntity selectedOWLEntity = view.getController().getOWLModel().getSelectedOWLEntity();
        if (selectedOWLEntity instanceof OWLClass) {
            conceptAnnotation.setOwlClass((OWLClass) selectedOWLEntity);
            conceptAnnotation.getTextSource().getConceptAnnotationCollection().change(conceptAnnotation);
        }
    }
}
