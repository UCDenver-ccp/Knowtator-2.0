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
import java.util.ArrayList;
import java.util.Arrays;

public class AnnotationActions {
    public static void addConceptAnnotation(KnowtatorView view, TextSource textSource) {
        UndoableAction action;
        try {
            ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();
            ArrayList<UndoableAction> actions = new ArrayList<>(
                    Arrays.asList(
                            new AddConceptAnnotationAction(view.getController(), textSource),
                            new AddSpanAction(view.getController(), conceptAnnotation),
                            new UndoableAction.CancelAction()
                    )
            );


            String[] buttons = (String[]) actions.stream().map(UndoableAction::getActionText).toArray();
            int response = JOptionPane.showOptionDialog(view, "Choose an option", "New Span", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 2);

            action = actions.get(response);
        } catch (NoSelectionException e) {
            action = new AddConceptAnnotationAction(view.getController(), textSource);

        }
        view.getController().registerUndoEvent(action);
    }

    public static void removeConceptAnnotation(KnowtatorView view, TextSource textSource, ConceptAnnotation conceptAnnotation, Span span) {
        ArrayList<UndoableAction> actions = new ArrayList<>(
                Arrays.asList(
                        new RemoveConceptAnnotationAction(textSource, conceptAnnotation),
                        new RemoveSpanAction(conceptAnnotation, span),
                        new UndoableAction.CancelAction()
                )
        );

        String[] buttons = (String[]) actions.stream().map(UndoableAction::getActionText).toArray();
        int response = JOptionPane.showOptionDialog(view, "Choose an option", "Remove Concept Annotation", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, 2);

        UndoableAction action = actions.get(response);

        view.getController().registerUndoEvent(action);
    }

    public static void reassignOWLClass(KnowtatorView view, ConceptAnnotation conceptAnnotation) {
        OWLEntity owlEntity = view.getController().getOWLModel().getSelectedOWLEntity();
        if (owlEntity instanceof OWLClass) {
            ReassignOWLClassAction action = new ReassignOWLClassAction(conceptAnnotation, (OWLClass) owlEntity);
            view.getController().registerUndoEvent(action);
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
            super(true, "Add concept annotation");
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

        AddSpanAction(KnowtatorController controller, ConceptAnnotation conceptAnnotation) {
            super(true, "Add span to concept annotation");
            this.conceptAnnotation = conceptAnnotation;

            id = null;
            start = controller.getSelectionModel().getStart();
            end = controller.getSelectionModel().getEnd();

        }

        @Override
        void reverse() {
            conceptAnnotation.getSpanCollection().remove(newSpan);
        }

        @Override
        void execute() {
            newSpan = conceptAnnotation.getSpanCollection().addSpan(id, start, end);
        }
    }

    private static class RemoveConceptAnnotationAction extends UndoableAction {
        private final TextSource textSource;
        private ConceptAnnotation oldAnnotation;

        RemoveConceptAnnotationAction(TextSource textSource, ConceptAnnotation annotation) {
            super(true, "Remove concept annotation");
            this.textSource = textSource;
            oldAnnotation = annotation;
        }

        @Override
        void reverse() {
            textSource.getConceptAnnotationCollection().add(oldAnnotation);
        }

        @Override
        void execute() {
            textSource.getConceptAnnotationCollection().remove(oldAnnotation);
        }
    }

    private static class RemoveSpanAction extends UndoableAction {
        private final ConceptAnnotation annotation;
        private final Span span;

        RemoveSpanAction(ConceptAnnotation annotation, Span span) {
            super(true, "Remove span from concept annotation");
            this.annotation = annotation;
            this.span = span;
        }

        @Override
        void reverse() {
            annotation.getSpanCollection().add(span);
        }

        @Override
        void execute() {
            annotation.getSpanCollection().remove(span);
        }
    }

    private static class ReassignOWLClassAction extends UndoableAction {

        private final OWLClass oldOwlClass;
        private ConceptAnnotation conceptAnnotation;
        private final OWLClass newOwlClass;

        ReassignOWLClassAction(ConceptAnnotation conceptAnnotation, OWLClass newOwlClass) {
            super(true, "Reassign OWL class");
            oldOwlClass = conceptAnnotation.getOwlClass();
            this.conceptAnnotation = conceptAnnotation;
            this.newOwlClass = newOwlClass;
        }

        @Override
        void reverse() {
            conceptAnnotation.setOwlClass(oldOwlClass);
        }

        @Override
        void execute() {
            conceptAnnotation.setOwlClass(newOwlClass);
        }
    }
}
