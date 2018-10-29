package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.view.KnowtatorColorChooser;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
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
                    action.execute();
                    view.getController().addEdit(action);
                }


            }
        }

    }

    static class ColorChangeAction extends KnowtatorAction {

        private final Profile profile;
        private final Set<Object> owlClasses;
        private final Color color;
        private Map<Object, Color> oldColorAssignments;

        ColorChangeAction(Profile profile, Set<Object> owlClasses, Color color) {
            super( "Set color for OWL class");
            this.profile = profile;
            this.owlClasses = owlClasses;
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
            oldColorAssignments = new HashMap<>();
            owlClasses.forEach(owlClass -> {
                Color oldColor = profile.getColors().get(owlClass);
                if (oldColor != null) {
                    oldColorAssignments.put(owlClass, color);
                }
                profile.addColor(owlClass, color);
            });
        }
    }
}
