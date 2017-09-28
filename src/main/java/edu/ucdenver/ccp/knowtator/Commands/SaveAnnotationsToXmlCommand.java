package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.xml.XmlUtil;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.FileWriter;
import java.io.IOException;

public class SaveAnnotationsToXmlCommand extends DisposableAction {

    private XmlUtil xmlUtil;

    public SaveAnnotationsToXmlCommand(XmlUtil xmlUtil) {
        super("Save to XML", KnowtatorIcons.getIcon(KnowtatorIcons.SAVE_ANNOTATIONS_ICON));
        this.xmlUtil = xmlUtil;

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Save annotations to XML file");


    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser();
        FileFilter fileFilter = new FileNameExtensionFilter(" XML", "xml");
        fileChooser.setFileFilter(fileFilter);
        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
            FileWriter fw;
            try {

                fw = new FileWriter(fileChooser.getSelectedFile().getAbsolutePath());
                xmlUtil.writeTextAnnotationsToXML(fw);
                fw.close();
            } catch (IOException | NoSuchFieldException e1) {
                e1.printStackTrace();
            }
        }
    }
}
