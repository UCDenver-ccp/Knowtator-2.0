package edu.ucdenver.ccp.knowtator.commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;

public class IAACommands {

    private KnowtatorManager manager;

    public IAACommands(KnowtatorManager manager) {

        this.manager = manager;
    }


    public KnowtatorCommand getRunIAACommand() {
        return new KnowtatorCommand(manager, "Run IAA", KnowtatorIcons.RUN_IAA_ICON, "Run Inter-profile agreement") {

            @Override
            public void actionPerformed(ActionEvent e) {

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


                        ArrayList<JCheckBox> checkBoxes = new ArrayList<JCheckBox>() {
                            {
                                add(new JCheckBox(new RunnableAction("Class IAA") {
                                    @Override
                                    public void run() {
                                        try {
                                            knowtatorIAA.runClassIAA();
                                        } catch (IAAException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }));
                                add(new JCheckBox(new RunnableAction("Span IAA") {
                                    @Override
                                    public void run() {
                                        try {
                                            knowtatorIAA.runSpanIAA();
                                        } catch (IAAException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }));
                                add(new JCheckBox(new RunnableAction("Class and Span IAA") {
                                    public void run() {
                                        try {
                                            knowtatorIAA.runClassAndSpanIAA();
                                        } catch (IAAException e1) {
                                            e1.printStackTrace();
                                        }
                                    }
                                }));
                            }
                        };

                        JPanel checkBoxInterpretter = new JPanel();
                        for (JCheckBox checkBox : checkBoxes) {
                            checkBoxInterpretter.add(checkBox);
                        }

                        JOptionPane.showConfirmDialog(null, checkBoxInterpretter);

                        for (JCheckBox checkBox : checkBoxes) {
                            if (checkBox.isSelected()) {
                                ((RunnableAction)checkBox.getAction()).run();
                            }
                        }


                        knowtatorIAA.closeHTML();
                    } catch (IAAException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        };
    }
}

