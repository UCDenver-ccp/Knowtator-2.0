package edu.ucdenver.ccp.knowtator.ui.menus;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.io.txt.KnowtatorDocumentHandler;
import org.apache.commons.io.FilenameUtils;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

public class FileMenu extends JMenu {

    private KnowtatorManager manager;

    public FileMenu(KnowtatorManager manager) {
        super("File");
        this.manager = manager;

        add(saveAction());
        add(openAction());
        add(loadAction());

    }

    private JMenuItem openAction() {

        JMenuItem open = new JMenuItem("Open");
        open.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                File directory = fileChooser.getSelectedFile();
                File[] files = directory.listFiles();
                if (files != null) {
                    for (File file : files) {
                        switch (Files.getFileExtension(file.getAbsolutePath())) {
                            case ("txt"):
                                String content = KnowtatorDocumentHandler.read(file.getAbsolutePath(), false);
                                String docID = FilenameUtils.getBaseName(file.getAbsolutePath());
                                manager.getTextSourceManager().addTextSource(docID, content);
                            case ("xml"):
                                manager.getXmlUtil().read(file.getAbsolutePath(), false);
                        }
                    }
                }
            }
        });

        return open;

    }

    private JMenuItem loadAction() {


        JMenuItem load = new JMenuItem("Load annotations");
        load.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileFilter fileFilter = new FileNameExtensionFilter(manager.getConfigProperties().getFormat().toUpperCase(), manager.getConfigProperties().getFormat());
            fileChooser.setFileFilter(fileFilter);

            if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                manager.getXmlUtil().read(fileChooser.getSelectedFile().getAbsolutePath(), false);
            }
        });

        return load;
    }

    private JMenuItem saveAction() {
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

        return save;
    }
}
