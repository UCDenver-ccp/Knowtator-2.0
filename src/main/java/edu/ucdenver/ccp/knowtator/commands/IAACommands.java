package edu.ucdenver.ccp.knowtator.commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class IAACommands {

    private KnowtatorManager manager;

    public IAACommands(KnowtatorManager manager) {

        this.manager = manager;
    }


    public KnowtatorCommand getRunIAACommand() {
        return new KnowtatorCommand(manager, "Run IAA", KnowtatorIcons.RUN_IAA_ICON, "Run Inter-annotator agreement") {

            @Override
            public void actionPerformed(ActionEvent e) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                //
                // disable the "All files" option.
                //
                fileChooser.setAcceptAllFileFilterUsed(false);
                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File outputDirectory = fileChooser.getSelectedFile();

                    try {
                        KnowtatorIAA knowtatorIAA = new KnowtatorIAA(outputDirectory, manager);

                        Object[] options = {"Class IAA", "Span IAA", "Class and Span IAA", "Subclass IAA", "Feature Matcher IAA"};
                        int response = JOptionPane.showOptionDialog(null, "Choose which type of IAA to perform", "Run IAA",
                                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE,
                                null, options, options[0]);

                        //TODO Should not be cases but checkboxes so multiple IAAs can be selected
                        switch (response) {
                            case 0:
                                knowtatorIAA.runClassIAA();
                            case 1:
                                knowtatorIAA.runSpanIAA();
                            case 2:
                                knowtatorIAA.runClassAndSpanIAA();
//                          case 3:
//                              knowtatorIAA.runSubclassIAA();
//                            case 4:
//                                knowtatorIAA.runFeatureMatcherIAA();

                                knowtatorIAA.closeHTML();
                        }
                    } catch (IAAException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
    }
}

