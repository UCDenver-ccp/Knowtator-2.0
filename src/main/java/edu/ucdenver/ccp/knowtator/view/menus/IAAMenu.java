/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.menus;

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
            fileChooser.setCurrentDirectory(manager.getProjectManager().getProjectLocation());
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

