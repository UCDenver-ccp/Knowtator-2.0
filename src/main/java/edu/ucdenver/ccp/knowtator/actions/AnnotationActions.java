package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;

public class AnnotationActions {
    public static void addConceptAnnotation(KnowtatorView view, TextSource textSource) {
        UndoableAction action = null;
        try {
            ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();
            String[] buttons = {"Add new concept annotation", "Add span to selected concept annotation", "Cancel"};
            int response =
                    JOptionPane.showOptionDialog(
                            view,
                            "Choose an option",
                            "New Span",
                            JOptionPane.DEFAULT_OPTION,
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            buttons,
                            2);

            switch (response) {
                case 0:
                    action = new AddConceptAnnotationAction(view.getController(), textSource);
                    break;
                case 1:
                    action = new AddSpanAction(view.getController(), conceptAnnotation);
                    view.getController().registerUndoEvent(action);
                    break;
                case 2:
                    break;
            }
        } catch (NoSelectionException e) {
            action = new AddConceptAnnotationAction(view.getController(), textSource);

        }
        view.getController().registerUndoEvent(action);
    }

    public static void removeConceptAnnotation(KnowtatorView view, TextSource textSource, ConceptAnnotation annotation) {
        try {
            Span span = annotation.getSpanCollection().getSelection();
            String[] buttons = {"Remove concept annotation", "Remove span from concept annotation", "Cancel"};
            int response =
                    JOptionPane.showOptionDialog(
                            view,
                            "Choose an option",
                            "Remove Concept Annotation",
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
                    "Are you sure you want to remove the selected concept annotation?",
                    "Remove Concept Annotation",
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

    static class AddConceptAnnotationAction extends UndoableAction {
        private final TextSource textSource;
        private String id;
        private OWLClass owlClass;
        private String owlClassID;
        private String owlClassLabel;
        private Profile annotator;
        private String type;
        private int start;
        private int end;
        private ConceptAnnotation newConceptAnnotation;

        AddConceptAnnotationAction(KnowtatorController controller, TextSource textSource) {
            super(true);
            OWLEntity owlEntity = controller.getOWLModel().getSelectedOWLEntity();
            if (owlEntity instanceof OWLClass) {
                owlClass = (OWLClass) owlEntity;
                annotator = controller.getProfileCollection().getSelection();
                start = controller.getSelectionModel().getStart();
                end = controller.getSelectionModel().getEnd();

                owlClassID = controller.getOWLModel().getOWLEntityRendering(owlClass);
            }
            id = null;
            owlClassLabel = null;
            type = "identity";
            this.textSource = textSource;
        }

        @Override
        void reverse() {
            textSource.getConceptAnnotationCollection().remove(newConceptAnnotation);
        }

        @Override
        void execute() {
            newConceptAnnotation = textSource.getConceptAnnotationCollection().addAnnotation(id, owlClass, owlClassID, owlClassLabel, annotator, type);
            newConceptAnnotation.getSpanCollection().addSpan(null, start, end);
        }
    }

    private static class AddSpanAction extends UndoableAction {
        private final String id;
        private final int start;
        private final int end;
        private ConceptAnnotation conceptAnnotation;
        private Span newSpan;

        public AddSpanAction(KnowtatorController controller, ConceptAnnotation conceptAnnotation) {
            super(true);
            this.conceptAnnotation = conceptAnnotation;

            id = null;
            start = controller.getSelectionModel().getStart();
            end = controller.getSelectionModel().getEnd();

        }

        @Override
        void reverse() {
            conceptAnnotation.getSpanCollection().remove( newSpan);
        }

        @Override
        void execute() {
            newSpan = conceptAnnotation.getSpanCollection().addSpan(id, start, end);
        }
    }
}
