package edu.ucdenver.ccp.knowtator.commands;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class ProjectCommands {

    private KnowtatorManager manager;

    public ProjectCommands(KnowtatorManager manager) {

        this.manager = manager;
    }

    public JMenu getFileMenu() {

        JMenu menu = new JMenu("File");

        JMenuItem save = new JMenuItem("Save Project");
        save.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
            FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
            fileChooser.setFileFilter(fileFilter);

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                manager.getXmlUtil().write(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        JMenuItem open = new JMenuItem("Open project");
        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setAcceptAllFileFilterUsed(false);

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File directory = fileChooser.getSelectedFile();
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        switch (Files.getFileExtension(file.getAbsolutePath())) {
                            case ("txt"):
                                manager.getKnowtatorView().getTextViewer().addNewDocument(file.getAbsolutePath(), false);
                            case ("xml"):
                                manager.getXmlUtil().read(file.getAbsolutePath(), false);
                        }
                    }
                }
            }
        });

        JMenuItem load = new JMenuItem("Load annotations");
        load.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
            fileChooser.setFileFilter(fileFilter);

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manager.getXmlUtil().read(fileChooser.getSelectedFile().getAbsolutePath(), false);
            }
        });

        menu.add(save);
        menu.add(open);
        menu.add(load);

        return menu;

    }
}
