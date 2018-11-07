package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorColorChooser;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class KnowtatorActions {
    public static void showMainMenuDialog(KnowtatorView view) {
        MenuDialog menuDialog = new MenuDialog(SwingUtilities.getWindowAncestor(view), view);
        menuDialog.pack();
        menuDialog.setVisible(true);
    }

    public static void showGraphViewer(GraphViewDialog graphViewDialog) {
        graphViewDialog.setVisible(true);
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
                    oldColorAssignments.put(owlClass, color);
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

    public static class SetFontSizeAction extends AbstractKnowtatorAction {

        private KnowtatorView view;
        private int previousFontSize;
        private int fontSize;

        public SetFontSizeAction(KnowtatorView view, int fontSize) {
            super("Set font size");
            this.view = view;
            previousFontSize = view.getKnowtatorTextPane().getFont().getSize();
            this.fontSize = fontSize;
        }

        @Override
        public void execute() {
            view.getKnowtatorTextPane().setFontSize(fontSize);
        }

        @Override
        public UndoableEdit getEdit() {
            return new KnowtatorEdit("Set font size") {
                @Override
                public void undo() {
                    view.getKnowtatorTextPane().setFontSize(previousFontSize);
                }

                @Override
                public void redo() {
                    view.getKnowtatorTextPane().setFontSize(fontSize);
                }
            };
        }
    }
}
