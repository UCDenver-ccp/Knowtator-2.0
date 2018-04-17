package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ProjectMenu extends JMenu implements ProjectListener {
  @SuppressWarnings("unused")
  private static Logger log = LogManager.getLogger(ProjectMenu.class);

  private JCheckBoxMenuItem classIAAChoice;
  private JCheckBoxMenuItem spanIAAChoice;
  private JCheckBoxMenuItem classAndSpanIAAChoice;
  private KnowtatorView view;

  public ProjectMenu( KnowtatorView view) {
    super("Project");
    this.view = view;

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
    addSeparator();
    add(debugMenuItem());
  }

  private JMenuItem debugMenuItem() {
    JMenuItem menuItem = new JCheckBoxMenuItem("Debug");
    menuItem.addActionListener(e -> view.getController().setDebug());
    return menuItem;
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
                      new JFileChooser(view.getController().getProjectManager().getAnnotationsLocation());
              fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

              if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                view.getController()
                        .getProjectManager()
                        .saveToFormat(BratStandoffUtil.class, fileChooser.getSelectedFile());
              }
            });

    return menuItem;
  }

  private JMenu openRecentCommand() {
    JMenu menu = new JMenu("Open recent ...");

    String recentProjectName = view.getController().getPrefs().get("Last Project", null);

    if (recentProjectName != null) {
      File recentProject = new File(recentProjectName);
      JMenuItem recentProjectMenuItem = new JMenuItem(recentProject.getName());
      recentProjectMenuItem.addActionListener(e -> loadProject(recentProject));

      menu.add(recentProjectMenuItem);
    }

    return menu;
  }

  private void loadProject(File file) {
    if (view.getController().getProjectManager().isProjectLoaded()
            && JOptionPane.showConfirmDialog(
            view,
            "Save changes to Knowtator project?",
            "Save Project",
            JOptionPane.YES_NO_OPTION)
            == JOptionPane.YES_OPTION) {
      view.getController().getProjectManager().saveProject();
    }

    view.getController().getProjectManager().loadProject(file);
  }

  private JMenuItem importAnnotationsCommand() {
    JMenuItem menuItem = new JMenuItem("Import Annotations");
    menuItem.addActionListener(
            e -> {
              if (view.getController().getProjectManager().getProjectLocation() == null) {
                JOptionPane.showMessageDialog(
                        view,
                        "Not in a project. Please create or load "
                                + "a project first. (Project -> New Project or Project -> Load Project)");
              } else {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(
                        view.getController().getProjectManager().getAnnotationsLocation());
                FileFilter fileFilter = new FileNameExtensionFilter("XML", "knowtator");
                fileChooser.setFileFilter(fileFilter);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                  view.getController()
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
              fileChooser.setCurrentDirectory(view.getController().getProjectManager().getArticlesLocation());

              if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                view.getController().getProjectManager().addDocument(fileChooser.getSelectedFile());
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

                if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                  File projectDirectory = new File(fileChooser.getSelectedFile(), projectName);
                  view.getController().getProjectManager().newProject(projectDirectory);
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

              if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
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
                view.getController().getProjectManager().saveProject();
              } catch (Exception e1) {
                JOptionPane.showMessageDialog(
                        view,
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
                              view,
                              "Load profile from test_project(knowtator)?",
                              "New profile",
                              JOptionPane.YES_NO_CANCEL_OPTION);
              if (dialogResult == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(view.getController().getProjectManager().getProjectLocation());

                if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                  view.getController()
                          .getProjectManager()
                          .loadFromFormat(
                                  KnowtatorXMLUtil.class,
                                  view.getController().getProfileManager(),
                                  fileChooser.getSelectedFile());
                }
              } else if (dialogResult == JOptionPane.NO_OPTION) {
                JTextField field1 = new JTextField();
                Object[] message = {
                        "Profile name", field1,
                };
                int option =
                        JOptionPane.showConfirmDialog(
                                view,
                                message,
                                "Enter profile name",
                                JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                  String annotator = field1.getText();
                  view.getController().getProfileManager().addProfile(annotator);
                }
              }
            });

    return newAnnotator;
  }

  private JMenuItem removeProfileMenu() {
    JMenuItem removeProfileMenu = new JMenuItem("Remove profile");
    removeProfileMenu.addActionListener(e -> view.getController().getProfileManager().removeActiveProfile());

    return removeProfileMenu;
  }

  private JMenuItem getRunIAACommand() {
    JMenuItem runIAA = new JMenuItem("Run IAA");

    runIAA.addActionListener(
            e -> {
              JFileChooser fileChooser = new JFileChooser();
              fileChooser.setCurrentDirectory(view.getController().getProjectManager().getProjectLocation());
              fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
              //
              // disable the "All files" option.
              //
              fileChooser.setAcceptAllFileFilterUsed(false);
              if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                File outputDirectory = fileChooser.getSelectedFile();

                try {
                  KnowtatorIAA knowtatorIAA = new KnowtatorIAA(outputDirectory, view.getController());

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
