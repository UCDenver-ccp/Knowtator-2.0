package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MenuActions {

    public static void changeProfileFilter(KnowtatorView view, boolean isFilterByProfile) {
        view.getController().getFilterModel().setFilterByProfile(isFilterByProfile);
    }

    public static void changeOWLClassFilter(KnowtatorView view, boolean isFilterByOWLClass) {
        view.getController().getFilterModel().setFilterByOWLClass(isFilterByOWLClass);
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
        try {
            TextSource textSource = view.getController().getTextSourceCollection().getSelection();
            JFileChooser fileChooser = new JFileChooser(view.getController().getSaveLocation());
            fileChooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
            fileChooser.setSelectedFile(
                    new File(
                            textSource
                                    .getId() + "_annotations.png"));
            if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                textSource
                        .getConceptAnnotationCollection()
                        .setSelection(null);
                BufferedImage image = view.getKnowtatorTextPane().getScreenShot();
                try {
                    ImageIO.write(image, "png", fileChooser.getSelectedFile());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (NoSelectionException e) {
            e.printStackTrace();
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


}
