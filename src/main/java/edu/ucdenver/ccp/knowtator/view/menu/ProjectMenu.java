package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.selection.ActiveTextSourceNotSetException;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.prefs.BackingStoreException;

public class ProjectMenu extends JMenu implements ProjectListener {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(ProjectMenu.class);

    private JCheckBoxMenuItem classIAAChoice;
    private JCheckBoxMenuItem spanIAAChoice;
    private JCheckBoxMenuItem classAndSpanIAAChoice;
    private KnowtatorView view;

    public ProjectMenu(KnowtatorView view) {
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
        add(saveTextViewerAsPNG());
        addSeparator();
        add(profileMenu());
        add(removeProfileMenu());
        addSeparator();
        add(IAAMenu());
        addSeparator();
        add(attemptOWLRenderConnectionCommand());
        add(debugMenuItem());
    }

    private static BufferedImage getScreenShot(Component component) {

        BufferedImage image =
                new BufferedImage(component.getWidth(), component.getHeight(), BufferedImage.TYPE_INT_RGB);
        // call the Component's paint method, using
        // the Graphics object of the image.
        component.paint(image.getGraphics()); // alternately use .printAll(..)
        return image;
    }

    private JMenuItem saveTextViewerAsPNG() {
        JMenuItem menuItem = new JMenuItem("Save text annotations as PNG");
        menuItem.addActionListener(
                e -> {
                    try {
                        JFileChooser fileChooser =
                                new JFileChooser(view.getController().getSaveLocation(null));
                        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG", "png"));
                        fileChooser.setSelectedFile(
                                new File(
                                        view.getController().getSelectionManager().getActiveTextSource().getId()
                                                + "_annotations.png"));
                        if (fileChooser.showSaveDialog(view) == JFileChooser.APPROVE_OPTION) {
                            view.getController()
                                    .getSelectionManager()
                                    .getActiveTextSource()
                                    .getAnnotationManager()
                                    .setSelectedAnnotation(null, null);
                            BufferedImage image = getScreenShot(view.getKnowtatorTextPane());
                            try {
                                ImageIO.write(image, "png", fileChooser.getSelectedFile());
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            }
                        }
                    } catch (ActiveTextSourceNotSetException ignored) {

                    }
                });

        return menuItem;
    }

    private JMenuItem attemptOWLRenderConnectionCommand() {
        JMenuItem menuItem = new JMenuItem("Connect annotations using current rendering");
        menuItem.addActionListener(e -> view.getController().getOWLManager().setUpOWL());

        return menuItem;
    }

    private JMenuItem debugMenuItem() {
        JCheckBoxMenuItem menuItem = new JCheckBoxMenuItem("Debug");
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
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(view.getController().getTextSourceManager().getAnnotationsLocation());
                    fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                    if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                        view.getController()
                                .saveToFormat(BratStandoffUtil.class, view.getController().getTextSourceManager(), fileChooser.getSelectedFile());
                    }

                });

        return menuItem;
    }

    private JMenu openRecentCommand() {
        JMenu menu = new JMenu("Open recent ...");

        String recentProjectName = view.getPrefs().get("Last Project", null);

        if (recentProjectName != null) {
            File recentProject = new File(recentProjectName);
            JMenuItem recentProjectMenuItem = new JMenuItem(recentProject.getName());
            recentProjectMenuItem.addActionListener(e -> loadProject(recentProject));

            menu.add(recentProjectMenuItem);
        }

        return menu;
    }

    private void loadProject(File file) {
        //    if (view.getController().isProjectLoaded()
        //        && JOptionPane.showConfirmDialog(
        //                view,
        //                "Save changes to Knowtator project?",
        //                "Save Project",
        //                JOptionPane.YES_NO_OPTION)
        //            == JOptionPane.YES_OPTION) {
        //      view.getController().saveProject();
        //    }
        view.reset();
        view.getController().loadProject(file);
        view.getPrefs().put("Last Project", file.getAbsolutePath());
        try {
            view.getPrefs().flush();
        } catch (BackingStoreException e) {
            e.printStackTrace();
        }
    }

    private JMenuItem importAnnotationsCommand() {
        JMenuItem menuItem = new JMenuItem("Import Annotations");
        menuItem.addActionListener(
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(view.getController().getTextSourceManager().getAnnotationsLocation());

                    FileFilter fileFilter =
                            new FileNameExtensionFilter("Annotation File (XML, ann, a1)", "xml", "ann", "a1");
                    fileChooser.setFileFilter(fileFilter);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                    if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                        view.getController().loadWithAppropriateFormat(view.getController().getTextSourceManager(), fileChooser.getSelectedFile());
                    }

                });
        return menuItem;
    }

    private JMenuItem addDocumentCommand() {
        JMenuItem menuItem = new JMenuItem("Add Document");
        menuItem.addActionListener(
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(view.getController().getTextSourceManager().getArticlesLocation());

                    if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
                        view.getController().addDocument(fileChooser.getSelectedFile());
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
                            view.reset();
                            view.getController().newProject(projectDirectory);
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
                        view.getController().saveProject();
                    } catch (Exception e1) {
                        JOptionPane.showMessageDialog(
                                view,
                                String.format(
                                        "Something went wrong trying to save your project:\n %s", e1.getMessage()));
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
                    JTextField field1 = new JTextField();
                    Object[] message = {
                            "Profile name", field1,
                    };
                    int option =
                            JOptionPane.showConfirmDialog(
                                    view, message, "Enter profile name", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        String annotator = field1.getText();
                        view.getController().getProfileManager().addProfile(annotator);
                    }
                });

        return newAnnotator;
    }

    private JMenuItem removeProfileMenu() {
        JMenuItem removeProfileMenu = new JMenuItem("Remove profile");
        removeProfileMenu.addActionListener(
                e -> view.getController().getProfileManager().removeActiveProfile());

        return removeProfileMenu;
    }

    private JMenuItem getRunIAACommand() {
        JMenuItem runIAA = new JMenuItem("Run IAA");

        runIAA.addActionListener(
                e -> {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(view.getController().getSaveLocation(null));
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
