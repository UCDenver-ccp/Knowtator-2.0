package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;

import javax.swing.*;

class AnnotationActions {
    static void addAnnotation(KnowtatorView view) {
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
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection()
                        .addSelectedAnnotation();
                break;
            case 1:
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection()
                        .addSpanToSelectedAnnotation();
                break;
            case 2:
                break;
        }
    }

    static void removeAnnotation(KnowtatorView view) {
        TextSource textSource = view.getController().getTextSourceCollection().getSelection();
        ConceptAnnotation annotation = textSource.getConceptAnnotationCollection().getSelection();
        if (annotation.getSpanCollection().size() > 1) {
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
                    textSource.getConceptAnnotationCollection().removeSpanFromSelectedAnnotation();
                    break;
                case 2:
                    break;
            }
        } else {
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
}
