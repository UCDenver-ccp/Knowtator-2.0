package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;

import javax.swing.*;
import java.io.File;

public class IAAMenu extends JMenu {

    private KnowtatorManager manager;
    private JCheckBoxMenuItem classIAAChoice;
    private JCheckBoxMenuItem spanIAAChoice;
    private JCheckBoxMenuItem classAndSpanIAAChoice;



    public IAAMenu(KnowtatorManager manager) {
        super("IAA");
        this.manager = manager;

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
            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //
            // disable the "All files" option.
            //
            fileChooser.setAcceptAllFileFilterUsed(false);
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File outputDirectory = fileChooser.getSelectedFile();

                try {
                    KnowtatorIAA knowtatorIAA = new KnowtatorIAA(outputDirectory, manager);

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

