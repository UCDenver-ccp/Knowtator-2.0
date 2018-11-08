package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorColorChooser;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OWLActions {
    public static class ReassignOWLClassAction extends AbstractKnowtatorAction {

        private final OWLClass oldOwlClass;
        private ConceptAnnotation conceptAnnotation;
        private final OWLClass newOwlClass;

        public ReassignOWLClassAction(KnowtatorController controller) throws NoSelectionException, ActionUnperformableException {
            super("Reassign OWL class");

            this.conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();

            oldOwlClass = conceptAnnotation.getOwlClass();
            OWLEntity owlEntity = controller.getOWLModel().getSelectedOWLEntity();
            if (owlEntity instanceof OWLClass) {
                this.newOwlClass = (OWLClass) owlEntity;
            } else {
                throw new ActionUnperformableException();
            }
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

    public static void assignColorToClass(KnowtatorView view, Object owlClass) {
        if (owlClass == null) {
            try {
                owlClass =
                        view.getController()
                                .getTextSourceCollection().getSelection()
                                .getConceptAnnotationCollection()
                                .getSelection()
                                .getOwlClass();
            } catch (NoSelectionException e) {
                e.printStackTrace();
            }
        }
        if (owlClass != null) {
            Set<Object> owlClasses = new HashSet<>();
            owlClasses.add(owlClass);

            JColorChooser colorChooser = new KnowtatorColorChooser();

            final Color[] finalC = {null};
            JDialog dialog = JColorChooser.createDialog(view, "Pick a color for " + owlClass, true, colorChooser,
                    e -> finalC[0] = colorChooser.getColor(), null);


            dialog.setVisible(true);

            Color c = finalC[0];
            if (c != null) {

                view.getController().getProfileCollection().getSelection().addColor(owlClass, c);


                if (owlClass instanceof OWLClass) {
                    if (JOptionPane.showConfirmDialog(
                            view, "Assign color to descendants of " + owlClass + "?")
                            == JOptionPane.OK_OPTION) {

                        owlClasses.addAll(
                                view.getController()
                                        .getOWLModel()
                                        .getDescendants((OWLClass) owlClass));
                    }

                    ColorChangeAction action = new ColorChangeAction(view.getController().getProfileCollection().getSelection(), owlClasses, c);
                    view.getController().registerAction(action);
                }


            }
        }

    }

    static class ColorChangeAction extends AbstractKnowtatorAction {


        private final ColorChangeEdit edit;
        private Map<Object, Color> oldColorAssignments;
        private Profile profile;
        private Set<Object> owlClasses;
        private Color color;

        ColorChangeAction(Profile profile, Set<Object> owlClasses, Color color) {
            super("Change color");
            this.profile = profile;
            this.owlClasses = owlClasses;
            this.color = color;

            oldColorAssignments = new HashMap<>();
            owlClasses.forEach(owlClass -> {
                Color oldColor = profile.getColors().get(owlClass);
                if (oldColor != null) {
                    oldColorAssignments.put(owlClass, oldColor);
                }
            });

            edit = new ColorChangeEdit(profile, oldColorAssignments, color);
        }

        @Override
        public void execute() {
            owlClasses.forEach(owlClass -> profile.addColor(owlClass, color));
        }

        @Override
        public UndoableEdit getEdit() {
            return edit;
        }
    }

    static class ColorChangeEdit extends KnowtatorEdit {

        private final Profile profile;
        private final Color color;
        private Map<Object, Color> oldColorAssignments;

        ColorChangeEdit(Profile profile, Map<Object, Color> oldColorAssignments, Color color) {
            super("Set color for OWL class");
            this.profile = profile;
            this.oldColorAssignments = oldColorAssignments;
            this.color = color;
        }

        @Override
        public void undo() {

            super.undo();
            oldColorAssignments.forEach(profile::addColor);
        }

        @Override
        public void redo() {
            super.redo();
            oldColorAssignments.keySet().forEach(owlClass -> profile.addColor(owlClass, color));
        }
    }
}
