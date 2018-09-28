package edu.ucdenver.ccp.knowtator.view.actions;

import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import edu.ucdenver.ccp.knowtator.view.text.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.view.text.graph.GraphViewDialog;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
import java.util.Set;

public class KnowtatorActions {
    public static void openMenu(KnowtatorView view) {
        MenuDialog menuDialog = new MenuDialog(SwingUtilities.getWindowAncestor(view), view);
        menuDialog.pack();
        menuDialog.setVisible(true);
    }

    public static void showGraphViewer(GraphViewDialog graphViewDialog) {
        graphViewDialog.setVisible(true);
    }

    public static void selectPreviousTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectPrevious();
    }

    public static void selectNextTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectNext();
    }

    public static void addTextSource(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getArticlesLocation());

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.getController().getTextSourceCollection().addDocument(fileChooser.getSelectedFile());
        }
    }

    public static void removeTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().removeActiveTextSource();
    }

    public static void setFontSize(KnowtatorView view, int fontSize) {
        view.getKnowtatorTextPane().setFontSize(fontSize);
    }

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
            String[] buttons = {"Remove concept", "Remove span from concept", "Cancel"};
            int response =
                    JOptionPane.showOptionDialog(
                            view,
                            "Choose an option",
                            "Remove ConceptAnnotation",
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
                    "Are you sure you want to remove the selected concept?",
                    "Remove ConceptAnnotation",
                    JOptionPane.YES_NO_OPTION)
                    == JOptionPane.YES_OPTION) {
                view.getController()
                        .getTextSourceCollection().getSelection()
                        .getConceptAnnotationCollection()
                        .removeSelectedAnnotation();
            }
        }
    }

    public static void assignColorToClassButton(KnowtatorView view) {
        OWLEntity owlClass = view.getController().getOWLManager().getSelectedOWLEntity();
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
                    try {
                        Set<OWLClass> descendants =
                                view.getController()
                                        .getOWLManager()
                                        .getDescendants((OWLClass) owlClass);

                        for (OWLClass descendant : descendants) {
                            view.getController().getProfileCollection()
                                    .getSelection()
                                    .addColor(descendant, c);
                        }
                    } catch (OWLWorkSpaceNotSetException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }

    }

    public static void selectNextSpan(KnowtatorView view) {
        view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getNextSpan();
    }

    public static void selectPreviousSpan(KnowtatorView view) {
        view.getController().getTextSourceCollection().getSelection().getConceptAnnotationCollection().getPreviousSpan();
    }

    public static void modifySelectedSpan(KnowtatorView view, String startOrEnd, String growOrShrink) {
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
    }public static void modifySelection(KnowtatorView view, String startOrEnd, String growOrShrink) {
        KnowtatorTextPane textPane = view.getKnowtatorTextPane();
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

    public static void findText(KnowtatorView view, String textToFind) {
        try {
            view.getController()
                    .getOWLManager()
                    .searchForString(textToFind);
        } catch (OWLWorkSpaceNotSetException e1) {
            e1.printStackTrace();

        }
    }

    public static void findNextMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations) {
        view.getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, true);
    }

    public static void findPreviousMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations) {
        view.getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, false);
    }
}
