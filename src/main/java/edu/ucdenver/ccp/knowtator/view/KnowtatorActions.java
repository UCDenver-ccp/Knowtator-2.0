package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import edu.ucdenver.ccp.knowtator.view.text.KnowtatorTextPane;
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

    static void selectPreviousTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectPrevious();
    }

    static void selectNextTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectNext();
    }

    static void addTextSource(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getArticlesLocation());

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.getController().getTextSourceCollection().addDocument(fileChooser.getSelectedFile());
        }
    }

    static void removeTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().removeActiveTextSource();
    }

    static void setFontSize(KnowtatorView view, int fontSize) {
        view.getTextView().getKnowtatorTextPane().setFontSize(fontSize);
    }



    static void assignColorToClassButton(KnowtatorView view) {
        OWLEntity owlClass = view.getController().getOWLModel().getSelectedOWLEntity();
        if (owlClass == null) {
            owlClass =
                    view.getController()
                            .getTextSourceCollection().getSelection()
                            .getConceptAnnotationCollection()
                            .getSelection()
                            .getOwlClass();
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

    static void selectNextSpan(KnowtatorView view) {
        view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getNextSpan();
    }

    static void selectPreviousSpan(KnowtatorView view) {
        view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getPreviousSpan();
    }

    static void modifySelectedSpan(KnowtatorView view, String startOrEnd, String growOrShrink) {
        ConceptAnnotationCollection conceptAnnotationCollection = view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection();
        switch (startOrEnd) {
            case "start":
                switch (growOrShrink) {
                    case "grow":
                        conceptAnnotationCollection.growSelectedSpanStart();
                        break;
                    case "shrink":
                        conceptAnnotationCollection.shrinkSelectedSpanStart();
                        break;
                }
                break;
            case "end":
            switch (growOrShrink) {
                    case "grow":
                        conceptAnnotationCollection.growSelectedSpanEnd();
                        break;
                    case "shrink":
                        conceptAnnotationCollection.shrinkSelectedSpanEnd();
                        break;
                }
                break;
        }
    }

    static void modifySelection(KnowtatorView view, String startOrEnd, String growOrShrink) {
        KnowtatorTextPane textPane = view.getTextView().getKnowtatorTextPane();
        switch (startOrEnd) {
            case "start":
                switch (growOrShrink) {
                    case "grow":
                        textPane.growStart();
                        break;
                    case "shrink":
                        textPane.shrinkStart();
                        break;
                }
                break;
            case "end":
            switch (growOrShrink) {
                    case "grow":
                        textPane.growEnd();
                        break;
                    case "shrink":
                        textPane.shrinkEnd();
                        break;
                }
                break;
        }
    }

    static void findText(KnowtatorView view, String textToFind) {
        view.getController()
                .getOWLModel()
                .searchForString(textToFind);
    }

    static void findNextMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations, boolean isRegex) {
        view.getTextView().getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, isRegex, true);
    }

    static void findPreviousMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations, boolean isRegex) {
        view.getTextView().getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, isRegex, false);
    }


}
