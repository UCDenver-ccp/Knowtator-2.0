package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class OpenDocumentCommand extends DisposableAction {

    private JTabbedPane tabbedPane;
    private MechAnICView view;

    public OpenDocumentCommand(MechAnICView view) {
        super("Open Document", MechAnICIcons.getIcon(MechAnICIcons.OPEN_DOCUMENT_ICON));
        this.view = view;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Open a text document");
        this.tabbedPane = view.getTabbedPane();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter fileFilter = new FileNameExtensionFilter("Plain text", "txt");
        fileChooser.setFileFilter(fileFilter);
        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            openFile(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void openFile(String fileName) {
        try {
            MechAnICTextViewer textViewer = new MechAnICTextViewer(view);
            textViewer.setName(fileName);
            JScrollPane sp = new JScrollPane(textViewer);
            if (tabbedPane.getTabCount() == 1 && tabbedPane.getTitleAt(0).equals("Untitled")) {
                tabbedPane.setComponentAt(0, sp);
            } else {
                tabbedPane.add(sp);
            }
            tabbedPane.setTitleAt(tabbedPane.getTabCount() - 1, fileName);

            textViewer.read(new BufferedReader(new FileReader(fileName)), fileName);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void dispose() {

    }
}
