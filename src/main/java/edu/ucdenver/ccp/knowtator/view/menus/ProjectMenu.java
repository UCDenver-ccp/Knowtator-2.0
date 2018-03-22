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
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

@SuppressWarnings("Duplicates")
public class ProjectMenu extends JMenu {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(ProjectMenu.class);

    private KnowtatorManager manager;

    public ProjectMenu(KnowtatorManager manager) {
        super("Project");
        this.manager = manager;


        add(newProjectCommand());
        add(openProjectCommand());
        add(saveProjectCommand());
        addSeparator();
        add(addDocumentCommand());
        add(importAnnotationsCommand());

    }

    private JMenuItem importAnnotationsCommand() {
        JMenuItem menuItem = new JMenuItem("Import Annotations");
        menuItem.addActionListener(e -> {
            if (manager.getProjectManager().getProjectLocation() == null) {
                JOptionPane.showMessageDialog(null, "Not in a project. Please create or load " +
                        "a project first. (Project -> New Project or Project -> Load Project)");
            } else {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(manager.getProjectManager().getAnnotationsLocation());
                FileFilter fileFilter = new FileNameExtensionFilter("XML", "xml");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    manager.getProjectManager().importAnnotations(fileChooser.getSelectedFile());
                }
            }
        });
        return menuItem;
    }

    private JMenuItem addDocumentCommand() {
        JMenuItem menuItem = new JMenuItem("Add Document");
        menuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(manager.getProjectManager().getArticlesLocation());

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manager.getProjectManager().addDocument(fileChooser.getSelectedFile());
            }
        });
        return menuItem;

    }

    private JMenuItem newProjectCommand() {
        JMenuItem menuItem = new JMenuItem("New Project");
        menuItem.addActionListener(e -> {
            String projectName = JOptionPane.showInputDialog("Enter project name");

            if (projectName != null && !projectName.equals("")) {

                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                    File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
                    manager.getProjectManager().newProject(projectDirectory);
                }
            }
        });

        return menuItem;
    }

    private JMenuItem openProjectCommand() {

        JMenuItem open = new JMenuItem("Open Project");
        open.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();
            javax.swing.filechooser.FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manager.getProjectManager().saveProject();
                log.warn("1: " + fileChooser.getSelectedFile());
                //TODO: Figure out how to close project before loading
//            closeProject(view, fileChooser.getSelectedFile());
                manager.getProjectManager().loadProject(fileChooser.getSelectedFile());
            }
        });

        return open;

    }

    private JMenuItem saveProjectCommand() {
        JMenuItem save = new JMenuItem("Save Project");
        save.addActionListener(e -> {
            try {
                manager.getProjectManager().saveProject();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(null, String.format( "Something went wrong trying to save your project:/n %s",
                        e1.getMessage()));
            }
        });

        return save;
    }
}
