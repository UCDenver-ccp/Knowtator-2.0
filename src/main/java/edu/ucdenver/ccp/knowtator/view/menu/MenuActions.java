package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

class MenuActions {
    private static final Logger log = Logger.getLogger(MenuActions.class);

    static void openProject(JDialog parent, KnowtatorView view) {
        String lastProjectFileName = view.getPreferences().get("Last Project", null);

        JFileChooser fileChooser = new JFileChooser();
        if (lastProjectFileName != null) {
            File lastProjectFile = new File(lastProjectFileName);
            if (lastProjectFile.exists()) {
                fileChooser.setCurrentDirectory(lastProjectFile.getParentFile());
                fileChooser.setSelectedFile(lastProjectFile);
            }
        }
        FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
        fileChooser.setFileFilter(fileFilter);
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            parent.dispose();
            view.reset();
            try {
                view.getController().setSaveLocation(file.getParentFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            log.warn(String.format("Opening from %s", file.getAbsolutePath()));
            view.getController().loadProject();
            view.getKnowtatorTextPane().refreshHighlights();
            view.getAddTextSourceButton().setEnabled(true);

            view.getPreferences().put("Last Project", file.getAbsolutePath());

            try {
                view.getPreferences().flush();
            } catch (BackingStoreException e1) {
                e1.printStackTrace();
            }
        }
    }

    static void newProject(JDialog parent, KnowtatorView view) {
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

                view.getPreferences().put("Last Project", view.getController().getProjectLocation().getAbsolutePath());
            }
        }
    }

    static void changeProfileFilter(KnowtatorView view, boolean isFilterByProfile) {
        view.getController().getFilterModel().setFilterByProfile(isFilterByProfile);
    }

    static void changeOWLClassFilter(KnowtatorView view, boolean isFilterByOWLClass) {
        view.getController().getFilterModel().setFilterByOWLClass(isFilterByOWLClass);
    }

    static void showExportDialog(JDialog parent, KnowtatorView view) {
        ExportDialog exportDialog = new ExportDialog(parent, view);
        exportDialog.pack();
        exportDialog.setVisible(true);
    }

    static void showImportDialog(JDialog parent, KnowtatorView view) {
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

    static void showIAADialog(JDialog parent, KnowtatorView view) {
        IAADialog iaaDialog = new IAADialog(parent, view);
        iaaDialog.pack();
        iaaDialog.setVisible(true);
    }

    static void exportToBrat(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getAnnotationsLocation());
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.getController()
                    .saveToFormat(BratStandoffUtil.class, view.getController().getTextSourceCollection(), fileChooser.getSelectedFile());
        }
    }

    static void exportToPNG(KnowtatorView view) {
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
            BufferedImage image = view.getKnowtatorTextPane().getScreenShot();
            try {
                ImageIO.write(image, "png", fileChooser.getSelectedFile());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    static void runIAA(KnowtatorView view, boolean runClass, boolean runSpan, boolean runClassAndSpan) {
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


}
