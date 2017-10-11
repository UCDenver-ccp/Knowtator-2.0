package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.xml.XmlUtil;
import org.protege.editor.core.ui.view.DisposableAction;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LoadTextAnnotationsCommand extends DisposableAction {
    private KnowtatorView view;

    public LoadTextAnnotationsCommand(KnowtatorView view) {
        super("Load Annotations", KnowtatorIcons.getIcon(KnowtatorIcons.LOAD_ANNOTATIONS_ICON));
        this.view = view;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Load annotations");
    }

    @Override
    public void dispose() {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter fileFilter = new FileNameExtensionFilter(" XML", "xml");
        fileChooser.setFileFilter(fileFilter);

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                view.getXmlUtil().read(new FileInputStream(new File(fileChooser.getSelectedFile().getAbsolutePath())));
            } catch (IOException | SAXException | ParserConfigurationException e1) {
                e1.printStackTrace();
            }
        }
    }
}
