package edu.ucdenver.ccp.knowtator.Commands;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class OpenDocumentCommand extends DisposableAction {

    private KnowtatorTextViewer textViewer;

    public OpenDocumentCommand(KnowtatorView view) {
        super("Open Document", KnowtatorIcons.getIcon(KnowtatorIcons.OPEN_DOCUMENT_ICON));
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Open a text document");
        this.textViewer = view.getTextViewer();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        //
        // disable the "All files" option.
        //
        fileChooser.setAcceptAllFileFilterUsed(false);

        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            File directory = fileChooser.getSelectedFile();
            File[] files = directory.listFiles();
            if (files != null) {
                List<String> articleFileNames = new ArrayList<>();
                List<String> annotationFileNames = new ArrayList<>();
                for (File file : files) {
                    switch (Files.getFileExtension(file.getAbsolutePath())) {
                        case ("txt"):
                            articleFileNames.add(file.getAbsolutePath());
                        case ("xml"):
                            annotationFileNames.add(file.getAbsolutePath());
                    }
                }
                textViewer.addDocuments(articleFileNames, annotationFileNames, false);
            }
        }
    }


    @Override
    public void dispose() {

    }
}
