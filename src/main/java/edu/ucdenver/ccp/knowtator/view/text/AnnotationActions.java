package edu.ucdenver.ccp.knowtator.view.text;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class AnnotationActions {
    public static void addAnnotation(KnowtatorView view) {
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

    public static void removeAnnotation(KnowtatorView view) {
        if (view.getController()
                .getTextSourceCollection().getSelection()
                .getConceptAnnotationCollection()
                .getSelection().getSpanCollection()
                .size()
                > 1) {
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
                    view.getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection()
                            .removeSelectedAnnotation();
                    break;
                case 1:
                    view.getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection()
                            .removeSpanFromSelectedAnnotation();
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
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection()
                        .removeSelectedAnnotation();
            }
        }
    }
}
