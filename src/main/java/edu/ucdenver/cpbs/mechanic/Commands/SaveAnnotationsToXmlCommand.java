//package edu.ucdenver.cpbs.mechanic.Commands;
//
//import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
//import edu.ucdenver.cpbs.mechanic.xml.XmlUtil;
//import org.protege.editor.core.ui.view.DisposableAction;
//
//import javax.swing.*;
//import javax.swing.filechooser.FileFilter;
//import javax.swing.filechooser.FileNameExtensionFilter;
//import java.awt.event.ActionEvent;
//import java.io.FileWriter;
//import java.io.IOException;
//
//public class SaveAnnotationsToXmlCommand extends DisposableAction {
//
//    private JTabbedPane tabbedPane;
//    private XmlUtil xmlUtil;
//
//    public SaveAnnotationsToXmlCommand(JTabbedPane tabbedPane, XmlUtil xmlUtil) {
//        super("Save to XML", MechAnICIcons.getIcon(MechAnICIcons.SAVE_ANNOTATIONS_ICON));
//        this.tabbedPane = tabbedPane;
//        this.xmlUtil = xmlUtil;
//
//        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Save annotations to XML file");
//
//
//    }
//
//    @Override
//    public void dispose() {
//
//    }
//
//    @Override
//    public void actionPerformed(ActionEvent e) {
//        JFileChooser fileChooser = new JFileChooser();
//        FileFilter fileFilter = new FileNameExtensionFilter(" XML", "xml");
//        fileChooser.setFileFilter(fileFilter);
//        if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
//            FileWriter fw;
//            try {
//
//                fw = new FileWriter(fileChooser.getSelectedFile().getAbsolutePath());
//                xmlUtil.writeTextAnnotationsToXML(fw);
//                fw.close();
//            } catch (IOException | NoSuchFieldException e1) {
//                e1.printStackTrace();
//            }
//        }
//    }
//}
