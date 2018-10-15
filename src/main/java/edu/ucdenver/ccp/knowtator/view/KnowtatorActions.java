package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

class KnowtatorActions {
    static void showMainMenuDialog(KnowtatorView view) {
        MenuDialog menuDialog = new MenuDialog(SwingUtilities.getWindowAncestor(view), view);
        menuDialog.pack();
        menuDialog.setVisible(true);
    }

    static void showGraphViewer(GraphViewDialog graphViewDialog) {
        graphViewDialog.setVisible(true);
    }

    static void assignColorToClassButton(KnowtatorView view) {
        OWLEntity owlClass = view.getController().getOWLModel().getSelectedOWLEntity();
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
        if (owlClass instanceof OWLClass) {
            Color c = JColorChooser.showDialog(view, "Pick a color for " + owlClass, Color.CYAN);
            if (c != null) {
                view.getController().getProfileCollection().getSelection().addColor(owlClass, c);

                if (JOptionPane.showConfirmDialog(
                        view, "Assign color to descendants of " + owlClass + "?")
                        == JOptionPane.OK_OPTION) {
                    Set<OWLClass> descendants =
                            view.getController()
                                    .getOWLModel()
                                    .getDescendants((OWLClass) owlClass);

                    for (OWLClass descendant : descendants) {
                        view.getController().getProfileCollection()
                                .getSelection()
                                .addColor(descendant, c);
                    }
                }
            }
        }

    }
}
