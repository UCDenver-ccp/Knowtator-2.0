package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;

import javax.swing.*;
import java.io.File;

class IAAMenu extends JMenu {

    private KnowtatorController controller;
    private JCheckBoxMenuItem classIAAChoice;
    private JCheckBoxMenuItem spanIAAChoice;
    private JCheckBoxMenuItem classAndSpanIAAChoice;


    IAAMenu(KnowtatorController controller) {
        super("IAA");
        this.controller = controller;

        classIAAChoice = new JCheckBoxMenuItem("Class");
        spanIAAChoice = new JCheckBoxMenuItem("Span");
        classAndSpanIAAChoice = new JCheckBoxMenuItem("Class and Span");

        add(classIAAChoice);
        add(spanIAAChoice);
        add(classAndSpanIAAChoice);
        addSeparator();
        add(getRunIAACommand());

    }


    private JMenuItem getRunIAACommand() {
        JMenuItem runIAA = new JMenuItem("Run IAA");

        runIAA.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(controller.getProjectManager().getProjectLocation());
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //
            // disable the "All files" option.
            //
            fileChooser.setAcceptAllFileFilterUsed(false);
            if (fileChooser.showSaveDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                File outputDirectory = fileChooser.getSelectedFile();

                try {
                    KnowtatorIAA knowtatorIAA = new KnowtatorIAA(outputDirectory, controller);

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
}

