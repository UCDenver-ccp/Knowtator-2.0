package edu.ucdenver.ccp.knowtator.view.actions;

import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.menu.ExportDialog;
import edu.ucdenver.ccp.knowtator.view.menu.IAADialog;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import edu.ucdenver.ccp.knowtator.view.text.KnowtatorTextPane;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Set;
import java.util.prefs.BackingStoreException;

public class KnowtatorActions {
    public static void showMainMenuDialog(KnowtatorView view) {
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
        view.getTextView().getKnowtatorTextPane().setFontSize(fontSize);
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
            String[] buttons = {"Remove annotation", "Remove span from annotation", "Cancel"};
            int response =
                    JOptionPane.showOptionDialog(
                            view,
                            "Choose an option",
                            "Remove Annotation",
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
                    "Are you sure you want to remove the selected annotation?",
                    "Remove Annotation",
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

    public static void findText(KnowtatorView view, String textToFind) {
        view.getController()
                .getOWLModel()
                .searchForString(textToFind);
    }

    public static void findNextMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations) {
        view.getTextView().getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, true);
    }

    public static void findPreviousMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations) {
        view.getTextView().getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, false);
    }

    public static void openProject(JDialog parent, KnowtatorView view) {
        File lastProjectFile = new File(view.getPreferences().get("Last Project", null));
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(lastProjectFile.getParentFile());
        fileChooser.setSelectedFile(lastProjectFile);
        FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            parent.dispose();
            view.reset();
            try {
                view.getController().setSaveLocation(fileChooser.getSelectedFile().getParentFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            view.getController().loadProject();
            view.getTextView().getKnowtatorTextPane().refreshHighlights();

            view.getPreferences().put("Last Project", fileChooser.getSelectedFile().getAbsolutePath());

            try {
                view.getPreferences().flush();
            } catch (BackingStoreException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void newProject(JDialog parent, KnowtatorView view) {
        String projectName = JOptionPane.showInputDialog("Enter project name");

        if (projectName != null && !projectName.equals("")) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Select project root");
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

            if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
                parent.dispose();
                File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
                view.reset();
                view.getController().newProject(projectDirectory);
            }
        }
    }

    public static void changeProfileFilter(KnowtatorView view, boolean isFilterByProfile) {
        view.getController().getFilterModel().setFilterByProfile(isFilterByProfile);
    }

    public static void changeOWLClassFilter(KnowtatorView view, boolean isFilterByOWLClass) {
        view.getController().getFilterModel().setFilterByOWLClass(isFilterByOWLClass);
    }

    public static void showExportDialog(JDialog parent, KnowtatorView view) {
        ExportDialog exportDialog = new ExportDialog(parent, view);
        exportDialog.pack();
        exportDialog.setVisible(true);
    }

    public static void showImportDialog(JDialog parent, KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getAnnotationsLocation());

        FileFilter fileFilter =
                new FileNameExtensionFilter("ConceptAnnotation File (XML, ann, a1)", "xml", "ann", "a1");
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            parent.dispose();
            view.getController().loadWithAppropriateFormat(view.getController().getTextSourceCollection(), fileChooser.getSelectedFile());
        }
    }

    public static void showIAADialog(JDialog parent, KnowtatorView view) {
        IAADialog iaaDialog = new IAADialog(parent, view);
        iaaDialog.pack();
        iaaDialog.setVisible(true);
    }

    public static void exportToBrat(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getAnnotationsLocation());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.getController()
                    .saveToFormat(BratStandoffUtil.class, view.getController().getTextSourceCollection(), fileChooser.getSelectedFile());
        }
    }

    public static void exportToPNG(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser(view.getController().getSaveLocation());
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
        fileChooser.setSelectedFile(
                new File(
                        view.getController()
                                .getTextSourceCollection().getSelection()
                                .getId() + "_annotations.png"));
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.getController()
                    .getTextSourceCollection().getSelection()
                    .getConceptAnnotationCollection()
                    .setSelection(null);
            BufferedImage image = view.getTextView().getKnowtatorTextPane().getScreenShot();
            try {
                ImageIO.write(image, "png", fileChooser.getSelectedFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    public static void runIAA(KnowtatorView view, boolean runClass, boolean runSpan, boolean runClassAndSpan) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getSaveLocation());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        fileChooser.setAcceptAllFileFilterUsed(false);
        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
            File outputDirectory = fileChooser.getSelectedFile();

            try {
                KnowtatorIAA knowtatorIAA = new KnowtatorIAA(outputDirectory, view.getController());

                if (runClass) {
                    knowtatorIAA.runClassIAA();
                }
                if (runSpan) {
                    knowtatorIAA.runSpanIAA();
                }
                if (runClassAndSpan) {
                    knowtatorIAA.runClassAndSpanIAA();
                }

                knowtatorIAA.closeHTML();
            } catch (IAAException e1) {
                e1.printStackTrace();
            }

        }
    }

    public static void addProfile(JDialog parent, KnowtatorView view) {
        JTextField field1 = new JTextField();
        Object[] message = {
                "Profile name", field1,
        };
        int option = JOptionPane.showConfirmDialog(parent, message, "Enter profile name", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String annotator = field1.getText();
            view.getController().getProfileCollection().addProfile(annotator);
        }
    }

    public static void removeProfile(KnowtatorView view) {
        view.getController().getProfileCollection().removeActiveProfile();
    }
}
