package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.xml.XmlUtil;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
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

public class LoadAnnotationsCommand extends DisposableAction {

    private JTabbedPane tabbedPane;
    private XmlUtil xmlUtil;

    public LoadAnnotationsCommand(JTabbedPane tabbedPane, XmlUtil xmlUtil) {
        super("Load Annotations", KnowtatorIcons.getIcon(KnowtatorIcons.LOAD_ANNOTATIONS_ICON));
        this.tabbedPane = tabbedPane;
        this.xmlUtil = xmlUtil;
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

        KnowtatorTextPane textViewer = (KnowtatorTextPane)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();

        if (fileChooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            try {
                xmlUtil.loadTextAnnotationsFromXML(new FileInputStream(new File(fileChooser.getSelectedFile().getAbsolutePath())));
            } catch (IOException | SAXException | ParserConfigurationException e1) {
                e1.printStackTrace();
            }
        }
    }
}
