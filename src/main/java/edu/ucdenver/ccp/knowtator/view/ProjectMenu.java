package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
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
  private JCheckBoxMenuItem classIAAChoice;
  private JCheckBoxMenuItem spanIAAChoice;
  private JCheckBoxMenuItem classAndSpanIAAChoice;

  ProjectMenu(KnowtatorController controller) {
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
    add(exportToBratCommand());
    addSeparator();
    add(profileMenu());
    add(removeProfileMenu());
    addSeparator();
    add(IAAMenu());
  }

  private JMenu profileMenu() {
    JMenu profileMenu = new JMenu("Profile");
    profileMenu.add(newProfile());
    return profileMenu;
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
              JFileChooser fileChooser =
                      new JFileChooser(controller.getProjectManager().getAnnotationsLocation());
              fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

              if (fileChooser.showOpenDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                controller
                        .getProjectManager()
                        .saveToFormat(BratStandoffUtil.class, fileChooser.getSelectedFile());
              }
            });

    return menuItem;
  }

  private JMenu openRecentCommand() {
    JMenu menu = new JMenu("Open recent ...");

    String recentProjectName = controller.getPrefs().get("Last Project", null);

    if (recentProjectName != null) {
      File recentProject = new File(recentProjectName);
      JMenuItem recentProjectMenuItem = new JMenuItem(recentProject.getName());
      recentProjectMenuItem.addActionListener(e -> loadProject(recentProject));

      menu.add(recentProjectMenuItem);
    }

    return menu;
  }

  private void loadProject(File file) {
    if (controller.getProjectManager().isProjectLoaded()
            && JOptionPane.showConfirmDialog(
            controller.getView(),
            "Save changes to Knowtator project?",
            "Save Project",
            JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION) {
      controller.getProjectManager().saveProject();
    }

    controller.getProjectManager().loadProject(file);
  }

  private JMenuItem importAnnotationsCommand() {
    JMenuItem menuItem = new JMenuItem("Import Annotations");
    menuItem.addActionListener(
            e -> {
              if (controller.getProjectManager().getProjectLocation() == null) {
                JOptionPane.showMessageDialog(
                        controller.getView(),
                        "Not in a project. Please create or load "
                                + "a project first. (Project -> New Project or Project -> Load Project)");
              } else {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(
                        controller.getProjectManager().getAnnotationsLocation());
                FileFilter fileFilter = new FileNameExtensionFilter("XML", "knowtator");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showOpenDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                  controller
                          .getProjectManager()
                          .loadFromFormat(KnowtatorXMLUtil.class, fileChooser.getSelectedFile());
                }
              }
            });
    return menuItem;
  }

  private JMenuItem addDocumentCommand() {
    JMenuItem menuItem = new JMenuItem("Add Document");
    menuItem.addActionListener(
            e -> {
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
    menuItem.addActionListener(
            e -> {
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
    open.addActionListener(
            e -> {
              JFileChooser fileChooser = new JFileChooser();
              FileFilter fileFilter = new FileNameExtensionFilter("Knowtator", "knowtator");
              fileChooser.setFileFilter(fileFilter);
              fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

              if (fileChooser.showOpenDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                loadProject(fileChooser.getSelectedFile());
              }
            });

    return open;
  }

  private JMenuItem saveProjectCommand() {
    JMenuItem save = new JMenuItem("Save Project");
    save.addActionListener(
            e -> {
              try {
                controller.getProjectManager().saveProject();
              } catch (Exception e1) {
                JOptionPane.showMessageDialog(
                        controller.getView(),
                        String.format(
                                "Something went wrong trying to save your project:/n %s", e1.getMessage()));
                e1.printStackTrace();
              }
            });

    return save;
  }

  @Override
  public void projectClosed() {
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
    add(IAAMenu());
    addSeparator();
    add(profileMenu());
  }

  private JMenuItem newProfile() {
    JMenuItem newAnnotator = new JMenuItem("New profile");
    newAnnotator.addActionListener(
            e -> {
              int dialogResult =
                      JOptionPane.showConfirmDialog(
                              controller.getView(),
                              "Load profile from test_project(knowtator)?",
                              "New profile",
                              JOptionPane.YES_NO_CANCEL_OPTION);
              if (dialogResult == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(controller.getProjectManager().getProjectLocation());

                if (fileChooser.showSaveDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                  controller
                          .getProjectManager()
                          .loadFromFormat(
                                  KnowtatorXMLUtil.class,
                                  controller.getProfileManager(),
                                  fileChooser.getSelectedFile());
                }
              } else if (dialogResult == JOptionPane.NO_OPTION) {
                JTextField field1 = new JTextField();
                Object[] message = {
                        "Profile name", field1,
                };
                int option =
                        JOptionPane.showConfirmDialog(
                                controller.getView(),
                                message,
                                "Enter profile name",
                                JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                  String annotator = field1.getText();
                  controller.getProfileManager().addProfile(annotator);
                }
              }
            });

    return newAnnotator;
  }

  private JMenuItem removeProfileMenu() {
    JMenuItem removeProfileMenu = new JMenuItem("Remove profile");
    removeProfileMenu.addActionListener(e -> controller.getProfileManager().removeActiveProfile());

    return removeProfileMenu;
  }

  private JMenuItem getRunIAACommand() {
    JMenuItem runIAA = new JMenuItem("Run IAA");

    runIAA.addActionListener(
            e -> {
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
