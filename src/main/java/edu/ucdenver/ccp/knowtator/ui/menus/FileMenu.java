package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.io.txt.KnowtatorDocumentHandler;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("Duplicates")
public class FileMenu extends JMenu {
    @SuppressWarnings("unused")
    private static Logger log = LogManager.getLogger(FileMenu.class);

    private KnowtatorManager manager;

    public FileMenu(KnowtatorManager manager) {
        super("File");
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
                String content = KnowtatorDocumentHandler.read(fileChooser.getSelectedFile().getAbsolutePath(), false);
                String docID = FilenameUtils.getBaseName(fileChooser.getSelectedFile().getAbsolutePath());
                manager.getTextSourceManager().addTextSource(docID, content);
            }
        });
        return openDocument;
    }

    private JMenuItem openProjectCommand() {

        JMenuItem open = new JMenuItem("Open Project");
        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
            FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
            fileChooser.setFileFilter(fileFilter);
            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manager.getXmlUtil().read(fileChooser.getSelectedFile().getAbsolutePath(), false);

            }
        });

        return open;

    }

//    private JMenuItem loadAction() {
//
//
//        JMenuItem load = new JMenuItem("Load annotations");
//        load.addActionListener(e -> {
//            JFileChooser fileChooser = new JFileChooser();
//            FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
//            fileChooser.setFileFilter(fileFilter);
//
//            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
//                manager.getXmlUtil().read(fileChooser.getSelectedFile().getAbsolutePath(), false);
//            }
//        });
//
//        return load;
//    }

    private JMenuItem saveProjectCommand() {
        JMenuItem save = new JMenuItem("Save Project");
        save.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));
            FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
            fileChooser.setFileFilter(fileFilter);

            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {

                // TODO: Provide save options
                List<JCheckBox> textSourceOptions = new ArrayList<>(); // Which textSources to save
                List<JCheckBox> profileOptions = new ArrayList<>();  // Annotations for which profile to save
                List<JCheckBox> annotationOptions = new ArrayList<>();  // Just annotations, annotations and assertions, etc.



                manager.getXmlUtil().write(fileChooser.getSelectedFile().getAbsolutePath());
                manager.getConfigProperties().setDefaultSaveLocation(fileChooser.getSelectedFile().getAbsolutePath());
            }
        });

        return save;
    }
}
