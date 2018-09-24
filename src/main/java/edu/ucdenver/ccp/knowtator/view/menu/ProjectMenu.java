package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.KnowtatorViewComponent;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ProjectMenu extends JMenu implements KnowtatorViewComponent {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(ProjectMenu.class);

    private JCheckBoxMenuItem classIAAChoice;
    private JCheckBoxMenuItem spanIAAChoice;
    private JCheckBoxMenuItem classAndSpanIAAChoice;
    private KnowtatorView view;

    public ProjectMenu(KnowtatorView view) {
        super("Project");
        this.view = view;

        add(importAnnotationsCommand());
        addSeparator();
        add(exportToBratCommand());
        add(saveTextViewerAsPNG());
        addSeparator();
        add(IAAMenu());
        addSeparator();
        add(attemptOWLRenderConnectionCommand());
        add(debugMenuItem());
    }

    private static BufferedImage getScreenShot(Component component) {

        BufferedImage image =
                new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        // call the Component's paint method, using
        // the Graphics object of the image.
        component.paint(image.getGraphics()); // alternately use .printAll(..)
        return image;
    }

    private JMenuItem saveTextViewerAsPNG() {
        JMenuItem menuItem = new JMenuItem("Save text annotations as PNG");
        menuItem.addActionListener(
                e -> {
                    JFileChooser fileChooser =
                            new JFileChooser(view.getController().getSaveLocation());
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
                        BufferedImage image = getScreenShot(view.getKnowtatorTextPane());
                        try {
                            ImageIO.write(image, "png", fileChooser.getSelectedFile());
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                });

        return menuItem;
    }

    private JMenuItem attemptOWLRenderConnectionCommand() {
        JMenuItem menuItem = new JMenuItem("Connect annotations using current rendering");
        menuItem.addActionListener(e -> view.getController().getOWLManager().setUpOWL());

        return menuItem;
    }

    private JMenuItem debugMenuItem() {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem("Debug");
        menuItem.addActionListener(e -> view.getController().setDebug());
        return menuItem;
    }

    private JMenu IAAMenu() {
        JMenu iaaMenu = new JMenu("IAA");

        classIAAChoice = new JCheckBoxMenuItem("Class");
        spanIAAChoice = new JCheckBoxMenuItem("Span");
        classAndSpanIAAChoice = new JCheckBoxMenuItem("Class and Span");

        iaaMenu.add(classIAAChoice);
        iaaMenu.add(spanIAAChoice);
        iaaMenu.add(classAndSpanIAAChoice);
        iaaMenu.add(getRunIAACommand());

        return iaaMenu;
    }

    private JMenuItem exportToBratCommand() {
        JMenuItem menuItem = new JMenuItem("Export to Brat");
        menuItem.addActionListener(
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getAnnotationsLocation());
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                        view.getController()
                                .saveToFormat(BratStandoffUtil.class, view.getController().getTextSourceCollection(), fileChooser.getSelectedFile());
                    }

                });

        return menuItem;
    }

    private JMenuItem importAnnotationsCommand() {
        JMenuItem menuItem = new JMenuItem("Import Annotations");
        menuItem.addActionListener(
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getAnnotationsLocation());

                    FileFilter fileFilter =
                            new FileNameExtensionFilter("ConceptAnnotation File (XML, ann, a1)", "xml", "ann", "a1");
                    fileChooser.setFileFilter(fileFilter);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                        view.getController().loadWithAppropriateFormat(view.getController().getTextSourceCollection(), fileChooser.getSelectedFile());
                    }

                });
        return menuItem;
    }

    private JMenuItem getRunIAACommand() {
        JMenuItem runIAA = new JMenuItem("Run IAA");

        runIAA.addActionListener(
                e -> {
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

                            if (classIAAChoice.getState()) {
                                knowtatorIAA.runClassIAA();
                            }
                            if (spanIAAChoice.getState()) {
                                knowtatorIAA.runSpanIAA();
                            }
                            if (classAndSpanIAAChoice.getState()) {
                                knowtatorIAA.runClassAndSpanIAA();
                            }

                            knowtatorIAA.closeHTML();
                        } catch (IAAException e1) {
                            e1.printStackTrace();
                        }

                    }
                });
        return runIAA;
    }

    @Override
    public void reset() {
        removeAll();
        add(importAnnotationsCommand());
        addSeparator();
        add(IAAMenu());
    }

    @Override
    public void dispose() {

    }
}
