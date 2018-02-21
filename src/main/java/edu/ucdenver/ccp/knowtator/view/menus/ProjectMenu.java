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
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;

@SuppressWarnings("Duplicates")
public class ProjectMenu extends JMenu {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(ProjectMenu.class);

    private KnowtatorManager manager;
    private KnowtatorView view;

    public ProjectMenu(KnowtatorManager manager, KnowtatorView view) {
        super("Project");
        this.manager = manager;
        this.view = view;


        add(newProjectCommand());
        add(openProjectCommand());
        add(saveProjectCommand());
        addSeparator();
        add(addDocumentCommand());
        add(importAnnotationsCommand());

    }

    private JMenuItem importAnnotationsCommand() {
        JMenuItem menuItem = new JMenuItem("Import Annotations");
        menuItem.addActionListener(e -> manager.getProjectManager().importAnnotations());
        return menuItem;
    }

    private JMenuItem addDocumentCommand() {
        JMenuItem menuItem = new JMenuItem("Add Document");
        menuItem.addActionListener(e -> manager.getProjectManager().addDocument());
        return menuItem;

    }

    private JMenuItem newProjectCommand() {
        JMenuItem menuItem = new JMenuItem("New Project");
        menuItem.addActionListener(e -> {
            String projectName = JOptionPane.showInputDialog("Enter project name");
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
                manager.getProjectManager().newProject(projectDirectory);
            }
        });

        return menuItem;
    }

    private JMenuItem openProjectCommand() {

        JMenuItem open = new JMenuItem("Open Project");
        open.addActionListener(e -> manager.getProjectManager().loadProject(view));

        return open;

    }

    private JMenuItem saveProjectCommand() {
        JMenuItem save = new JMenuItem("Save Project");
        save.addActionListener(e -> manager.getProjectManager().saveProject());

        return save;
    }
}
