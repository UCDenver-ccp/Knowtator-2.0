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
import javax.swing.undo.UndoableEdit;
import java.util.ArrayList;
import java.util.Arrays;

public class AnnotationActions {
    public static void addConceptAnnotation(KnowtatorView view, TextSource textSource) {
        KnowtatorAction action = null;
        OWLEntity owlClass = view.getController().getOWLModel().getSelectedOWLEntity();
        try {
            ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();

            ArrayList<KnowtatorAction> actions = new ArrayList<>();

            if (owlClass instanceof OWLClass) {
                actions.add(new AddConceptAnnotationAction(view.getController(), textSource, (OWLClass) owlClass));
            }
            actions.add(new AddSpanAction(view.getController(), conceptAnnotation));


            String[] buttons = (String[]) actions.stream().map(UndoableEdit::getPresentationName).toArray();
            int response = JOptionPane.showOptionDialog(view, "Choose an option", "New Concept Annotation or Span", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, buttons, 2);
            action = actions.get(response);

        } catch (NoSelectionException e) {
            if (owlClass instanceof OWLClass) {
                action = new AddConceptAnnotationAction(view.getController(), textSource, (OWLClass) owlClass);
            }

        }
        if (action != null) {
            action.execute();
            view.getController().addEdit(action);
        }
    }

    public static void removeConceptAnnotation(KnowtatorView view, TextSource textSource, ConceptAnnotation conceptAnnotation, Span span) {
        ArrayList<KnowtatorAction> actions = new ArrayList<>(
                Arrays.asList(
                        new RemoveConceptAnnotationAction(textSource, conceptAnnotation),
                        new RemoveSpanAction(conceptAnnotation, span)
                )
        );

        String[] buttons = (String[]) actions.stream().map(UndoableEdit::getPresentationName).toArray();
        int response = JOptionPane.showOptionDialog(view, "Choose an option", "Remove Concept Annotation", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, buttons, 2);

        KnowtatorAction action = actions.get(response);
        action.execute();
        view.getController().addEdit(action);
    }

    public static void reassignOWLClass(KnowtatorView view, ConceptAnnotation conceptAnnotation) {
        OWLEntity owlEntity = view.getController().getOWLModel().getSelectedOWLEntity();
        if (owlEntity instanceof OWLClass) {
            ReassignOWLClassAction action = new ReassignOWLClassAction(conceptAnnotation, (OWLClass) owlEntity);
            action.execute();
            view.getController().addEdit(action);
        }
    }

    static class AddConceptAnnotationAction extends KnowtatorAction {
        private final TextSource textSource;
        private final AddSpanAction spanAction;
        private ConceptAnnotation newConceptAnnotation;

        AddConceptAnnotationAction(KnowtatorController controller, TextSource textSource, OWLClass owlClass) {
            super("Add concept annotation");
            Profile annotator = controller.getProfileCollection().getSelection();

            String owlClassID = controller.getOWLModel().getOWLEntityRendering(owlClass);
            String id = null;
            String owlClassLabel = null;
            String annotationType = "identity";
            this.textSource = textSource;

            newConceptAnnotation = new ConceptAnnotation(controller, id, owlClass, owlClassID, owlClassLabel, annotator, annotationType, textSource);
            spanAction = new AddSpanAction(controller, newConceptAnnotation);
            addEdit(spanAction);
        }

        @Override
        public void undo() {
            super.undo();
            textSource.getConceptAnnotationCollection().remove(newConceptAnnotation);
        }

        @Override
        public void redo() {
            super.redo();
            textSource.getConceptAnnotationCollection().add(newConceptAnnotation);

        }

        @Override
        public void execute() {
            spanAction.execute();
            super.execute();
        }
    }

    private static class AddSpanAction extends KnowtatorAction {

        private ConceptAnnotation conceptAnnotation;
        private Span newSpan;

        AddSpanAction(KnowtatorController controller, ConceptAnnotation conceptAnnotation) {
            super("Add span to concept annotation");
            this.conceptAnnotation = conceptAnnotation;
            String id = null;
            int start = controller.getSelectionModel().getStart();
            int end = controller.getSelectionModel().getEnd();

            newSpan = new Span(controller, conceptAnnotation.getTextSource(), conceptAnnotation, id, start, end);

        }

        @Override
        public void undo() {
            conceptAnnotation.getSpanCollection().remove(newSpan);
        }

        @Override
        public void redo() {
            conceptAnnotation.getSpanCollection().add(newSpan);
        }
    }

    public static class RemoveConceptAnnotationAction extends KnowtatorAction {
        private final TextSource textSource;
        private ConceptAnnotation oldAnnotation;

        public RemoveConceptAnnotationAction(TextSource textSource, ConceptAnnotation annotation) {
            super("Remove concept annotation");
            this.textSource = textSource;
            oldAnnotation = annotation;
        }

        @Override
        public void undo() {
            super.undo();
            textSource.getConceptAnnotationCollection().add(oldAnnotation);
        }

        @Override
        public void redo() {
            super.redo();
            textSource.getConceptAnnotationCollection().remove(oldAnnotation);
        }
    }

    private static class RemoveSpanAction extends KnowtatorAction {
        private final ConceptAnnotation annotation;
        private final Span span;

        RemoveSpanAction(ConceptAnnotation annotation, Span span) {
            super("Remove span from concept annotation");
            this.annotation = annotation;
            this.span = span;
        }

        @Override
        public void undo() {
            super.undo();
            annotation.getSpanCollection().add(span);
        }

        @Override
        public void redo() {
            super.redo();
            annotation.getSpanCollection().remove(span);
        }
    }

    private static class ReassignOWLClassAction extends KnowtatorAction {

        private final OWLClass oldOwlClass;
        private ConceptAnnotation conceptAnnotation;
        private final OWLClass newOwlClass;

        ReassignOWLClassAction(ConceptAnnotation conceptAnnotation, OWLClass newOwlClass) {
            super("Reassign OWL class");
            oldOwlClass = conceptAnnotation.getOwlClass();
            this.conceptAnnotation = conceptAnnotation;
            this.newOwlClass = newOwlClass;
        }

        @Override
        public void undo() {
            super.undo();
            conceptAnnotation.setOwlClass(oldOwlClass);
        }

        @Override
        public void redo() {
            super.redo();
            conceptAnnotation.setOwlClass(newOwlClass);
        }
    }
}
