package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ProjectMenu extends JMenu {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(ProjectMenu.class);

    private KnowtatorController controller;

    public ProjectMenu(KnowtatorController controller) {
        super("Project");
        this.controller = controller;


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
            if (controller.getProjectManager().getProjectLocation() == null) {
                JOptionPane.showMessageDialog(null, "Not in a project. Please create or load " +
                        "a project first. (Project -> New Project or Project -> Load Project)");
            } else {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(controller.getProjectManager().getAnnotationsLocation());
                FileFilter fileFilter = new FileNameExtensionFilter("XML", "knowtator");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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

                if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
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

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                controller.getProjectManager().saveProject();
                log.warn("1: " + fileChooser.getSelectedFile());

//            closeProject(view, fileChooser.getSelectedFile());
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
                JOptionPane.showMessageDialog(null, String.format( "Something went wrong trying to save your project:/n %s",
                        e1.getMessage()));
                e1.printStackTrace();
            }
        });

        return save;
    }
}
