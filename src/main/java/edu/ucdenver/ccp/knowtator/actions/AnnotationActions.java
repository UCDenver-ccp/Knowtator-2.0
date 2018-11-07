package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
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


            int response = JOptionPane.showOptionDialog(view, "Choose an option", "New Concept Annotation or Span", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, actions.stream().map(KnowtatorAction::getPresentationName).toArray(), 2);
            action = actions.get(response);

        } catch (NoSelectionException e) {
            if (owlClass instanceof OWLClass) {
                action = new AddConceptAnnotationAction(view.getController(), textSource, (OWLClass) owlClass);
            }

        }
        if (action != null) {
            view.getController().registerAction(action);
        }

    }

    public static void removeConceptAnnotation(KnowtatorView view, TextSource textSource, ConceptAnnotation conceptAnnotation, Span span) {
        ArrayList<KnowtatorAction> actions = new ArrayList<>(
                Arrays.asList(
                        new RemoveConceptAnnotationAction(textSource, conceptAnnotation),
                        new RemoveSpanAction(conceptAnnotation, span)
                )
        );

        int response = JOptionPane.showOptionDialog(view, "Choose an option", "Remove Concept Annotation", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, actions.stream().map(KnowtatorAction::getPresentationName).toArray(), 2);

        KnowtatorAction action = actions.get(response);
        view.getController().registerAction(action);
    }

    public static void reassignOWLClass(KnowtatorView view, ConceptAnnotation conceptAnnotation) {
        OWLEntity owlEntity = view.getController().getOWLModel().getSelectedOWLEntity();
        if (owlEntity instanceof OWLClass) {
            ReassignOWLClassAction action = new ReassignOWLClassAction(conceptAnnotation, (OWLClass) owlEntity);
            view.getController().registerAction(action);
        }
    }

    static class AddConceptAnnotationAction extends KnowtatorCollectionAction<ConceptAnnotation> {

        private final KnowtatorController controller;
        private ConceptAnnotation newConceptAnnotation;
        private TextSource textSource;

        AddConceptAnnotationAction(KnowtatorController controller, TextSource textSource, OWLClass owlClass) {
            super(KnowtatorCollectionAction.ADD, textSource.getConceptAnnotationCollection(), "Add concept annotation");
            Profile annotator = controller.getProfileCollection().getSelection();
            String owlClassID = controller.getOWLModel().getOWLEntityRendering(owlClass);
            this.textSource = textSource;
            this.controller = controller;

            newConceptAnnotation = new ConceptAnnotation(controller, null, owlClass, owlClassID, null, annotator, "identity", textSource);

            setObject(newConceptAnnotation);
        }

        @Override
        public void execute() {
            super.execute();
            newConceptAnnotation.getSpanCollection().add(new Span(controller, textSource, newConceptAnnotation, null, controller.getSelectionModel().getStart(), controller.getSelectionModel().getEnd()));
        }
    }

    private static class AddSpanAction extends KnowtatorCollectionAction<Span> {
        AddSpanAction(KnowtatorController controller, ConceptAnnotation conceptAnnotation) {
            super(KnowtatorCollectionAction.ADD, conceptAnnotation.getSpanCollection(), "Add span to concept annotation");

            int start = controller.getSelectionModel().getStart();
            int end = controller.getSelectionModel().getEnd();

            Span newSpan = new Span(controller, conceptAnnotation.getTextSource(), conceptAnnotation, null, start, end);
            setObject(newSpan);

        }
    }

    public static class RemoveConceptAnnotationAction extends KnowtatorCollectionAction<ConceptAnnotation> {
        private TextSource textSource;

        RemoveConceptAnnotationAction(TextSource textSource, ConceptAnnotation annotation) {
            super(KnowtatorCollectionAction.REMOVE, textSource.getConceptAnnotationCollection(), "Remove concept annotation");
            setObject(annotation);
            this.textSource = textSource;
        }

        @Override
        public void execute() {
            ConceptAnnotationCollection collection = textSource.getConceptAnnotationCollection();
            KnowtatorCollectionEdit<ConceptAnnotation> edit = new KnowtatorCollectionEdit<>(KnowtatorCollectionAction.ADD, collection, object, "Add concept annotation");

            switch (actionName) {
                case ADD:
                    collection.remove(edit, object);
                    break;
                case REMOVE:
                    collection.remove(object);
            }
        }
    }

    private static class RemoveSpanAction extends KnowtatorCollectionAction<Span> {
        RemoveSpanAction(ConceptAnnotation annotation, Span span) {
            super(KnowtatorCollectionAction.REMOVE, annotation.getSpanCollection(), "Remove span from concept annotation");
            setObject(span);
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
        public void execute() {
            conceptAnnotation.setOwlClass(newOwlClass);
        }

        @Override
        public UndoableEdit getEdit() {
            return new KnowtatorEdit(getPresentationName()) {
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

            };
        }
    }
}
