package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.actions.ProjectActions;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;

@SuppressWarnings("Duplicates")
public class ProjectMenu extends JMenu {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(ProjectMenu.class);

    private KnowtatorManager manager;

    public ProjectMenu(KnowtatorManager manager) {
        super("Project");
        this.manager = manager;


        add(openProjectCommand());
        add(saveProjectCommand());
        addSeparator();
        add(openDocumentCommand());
//        add(loadAction());

    }

    private JMenuItem openDocumentCommand() {
        JMenuItem openDocument = new JMenuItem("Open Document");
        openDocument.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manager.getTextSourceManager().addTextSource(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });
        return openDocument;
    }

    private JMenuItem openProjectCommand() {

        JMenuItem open = new JMenuItem("Open Project");
        open.addActionListener(e -> ProjectActions.loadProject(manager));

        return open;

    }

    private JMenuItem saveProjectCommand() {
        JMenuItem save = new JMenuItem("Save Project");
        save.addActionListener(e -> ProjectActions.saveProject(manager));

        return save;
    }
}
