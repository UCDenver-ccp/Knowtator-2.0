package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ProjectMenu extends JMenu implements ProjectListener {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(ProjectMenu.class);

    private KnowtatorController controller;

    public ProjectMenu(KnowtatorController controller) {
        super("Project");
        this.controller = controller;

        add(newProjectCommand());
        add(openRecentCommand());
        add(openProjectCommand());
        add(saveProjectCommand());
        addSeparator();
        add(addDocumentCommand());
        add(importAnnotationsCommand());
        addSeparator();
        add(new IAAMenu(controller));
        addSeparator();
        add(new ProfileMenu(controller));

    }

    private JMenu openRecentCommand() {
        JMenu menu = new JMenu("Open recent ...");

        String recentProjectName = controller.getPrefs().get("Last Project", null);

        if (recentProjectName != null) {
            File recentProject = new File(recentProjectName);
            JMenuItem recentProjectMenuItem = new JMenuItem(recentProject.getName());
            recentProjectMenuItem.addActionListener(e -> {
                if (controller.getProjectManager().isProjectLoaded() && JOptionPane.showConfirmDialog(controller.getView(), "Save changes to Knowtator project?", "Save Project", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    controller.getProjectManager().saveProject();
                }

                controller.getProjectManager().closeProject(controller.getView(), recentProject);
                controller.getProjectManager().loadProject(recentProject);
            });

            menu.add(recentProjectMenuItem);
        }

        return menu;
    }

    private JMenuItem importAnnotationsCommand() {
        JMenuItem menuItem = new JMenuItem("Import Annotations");
        menuItem.addActionListener(e -> {
            if (controller.getProjectManager().getProjectLocation() == null) {
                JOptionPane.showMessageDialog(controller.getView(), "Not in a project. Please create or load " +
                        "a project first. (Project -> New Project or Project -> Load Project)");
            } else {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(controller.getProjectManager().getAnnotationsLocation());
                FileFilter fileFilter = new FileNameExtensionFilter("XML", "knowtator");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showOpenDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                    controller.getProjectManager().loadFromFormat(KnowtatorXMLUtil.class, fileChooser.getSelectedFile());
                }
            }
        });
        return menuItem;
    }

    private JMenuItem addDocumentCommand() {
        JMenuItem menuItem = new JMenuItem("Add Document");
        menuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(controller.getProjectManager().getArticlesLocation());

            if (fileChooser.showOpenDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                controller.getProjectManager().addDocument(fileChooser.getSelectedFile());
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
                fileChooser.setDialogTitle("Select project root");
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

                if (fileChooser.showOpenDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                    File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
                    controller.getProjectManager().newProject(projectDirectory);
                }
            }
        });

        return menuItem;
    }

    private JMenuItem openProjectCommand() {

        JMenuItem open = new JMenuItem("Open Project");
        open.addActionListener(e -> {

            JFileChooser fileChooser = new JFileChooser();
            FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
            fileChooser.setFileFilter(fileFilter);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

            if (fileChooser.showOpenDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                if (controller.getProjectManager().isProjectLoaded() && JOptionPane.showConfirmDialog(controller.getView(), "Save changes to Knowtator project?", "Save Project", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                    controller.getProjectManager().saveProject();
                }

                controller.getProjectManager().closeProject(controller.getView(), fileChooser.getSelectedFile());
                controller.getProjectManager().loadProject(fileChooser.getSelectedFile());
            }
        });

        return open;

    }

    private JMenuItem saveProjectCommand() {
        JMenuItem save = new JMenuItem("Save Project");
        save.addActionListener(e -> {
            try {
                controller.getProjectManager().saveProject();
            } catch (Exception e1) {
                JOptionPane.showMessageDialog(controller.getView(), String.format("Something went wrong trying to save your project:/n %s",
                        e1.getMessage()));
                e1.printStackTrace();
            }
        });

        return save;
    }

    @Override
    public void projectLoaded() {
        removeAll();
        add(newProjectCommand());
        add(openRecentCommand());
        add(openProjectCommand());
        add(saveProjectCommand());
        addSeparator();
        add(addDocumentCommand());
        add(importAnnotationsCommand());
        addSeparator();
        add(new IAAMenu(controller));
        addSeparator();
        add(new ProfileMenu(controller));
    }
}
