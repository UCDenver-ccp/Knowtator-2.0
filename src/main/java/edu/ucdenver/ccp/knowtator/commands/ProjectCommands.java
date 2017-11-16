package edu.ucdenver.ccp.knowtator.commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;

public class ProjectCommands {

    private KnowtatorManager manager;

    public ProjectCommands(KnowtatorManager manager) {

        this.manager = manager;
    }

    public KnowtatorCommand getSaveProjectCommand() {
        return new KnowtatorCommand(manager, "Save Project", KnowtatorIcons.SAVE_PROJECT_ICON, "Save project") {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
                FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
                fileChooser.setFileFilter(fileFilter);

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    manager.getXmlUtil().write(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        };
    }
}
