package edu.ucdenver.ccp.knowtator.commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.annotator.Annotator;
import edu.ucdenver.ccp.knowtator.annotation.annotator.AnnotatorManager;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import org.semanticweb.owlapi.model.OWLClass;
import other.ListDialog;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class AnnotatorCommands {

    private KnowtatorManager manager;

    public AnnotatorCommands(KnowtatorManager manager) {

        this.manager = manager;
    }

    public KnowtatorCommand getAssignHighlighterCommand() {
        return new KnowtatorCommand(manager, "Assign highlighter", KnowtatorIcons.NEW_HIGHLIGHTER_ICON, "Assign highlighter color to selected class") {

            @Override
            public void actionPerformed(ActionEvent e) {
                OWLClass selectedClass = OWLAPIDataExtractor.getSelectedClass(manager);

                Annotator currentAnnotator = manager.getAnnotatorManager().getCurrentAnnotator();
                currentAnnotator.getColor(OWLAPIDataExtractor.getClassNameByOWLClass(manager, selectedClass));

                for(OWLClass decendent: OWLAPIDataExtractor.getDecendents(manager, selectedClass)) {
                    currentAnnotator.getColor(OWLAPIDataExtractor.getClassNameByOWLClass(manager, decendent));
                }
            }

        };
    }

    public KnowtatorCommand getRemoveAnnotatorCommand() {
        return new KnowtatorCommand(manager, "Remove Annotator", KnowtatorIcons.REMOVE_ANNOTATOR_ICON, "Remove an annotator profile") {
            @Override
            public void actionPerformed(ActionEvent e) {
                AnnotatorManager annotatorManager = manager.getAnnotatorManager();
                String annotatorName = ListDialog.showDialog(null, null, "Profiles", "Annotator Chooser", annotatorManager.getAnnotatorNames(), annotatorManager.getAnnotatorNames()[0], null);
                annotatorManager.removeAnnotator(annotatorName);
            }
        };
    }

    public KnowtatorCommand getNewAnnotatorCommand() {
        return new KnowtatorCommand(manager, "New Annotator", KnowtatorIcons.NEW_ANNOTATOR_ICON, "Add new annotator profile") {

            @Override
            public void actionPerformed(ActionEvent e) {
                int dialogResult = JOptionPane.showConfirmDialog (null, "Load profile from file(xml)?","New profile", JOptionPane.YES_NO_CANCEL_OPTION);
                if(dialogResult == JOptionPane.YES_OPTION) {
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setCurrentDirectory(new File(manager.getConfigProperties().getDefaultSaveLocation()));

                    if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                        manager.getXmlUtil().read(fileChooser.getSelectedFile().getAbsolutePath(), false);
                    }

                } else if (dialogResult == JOptionPane.NO_OPTION) {


                    JTextField field1 = new JTextField();
                    JTextField field2 = new JTextField();
                    Object[] message = {
                            "Annotator name", field1,
                            "Annotator ID", field2,
                    };
                    int option = JOptionPane.showConfirmDialog(null, message, "Enter annotator name and ID", JOptionPane.OK_CANCEL_OPTION);
                    if (option == JOptionPane.OK_OPTION) {
                        String annotator = field1.getText();
                        String annotatorID = field2.getText();
                        manager.getAnnotatorManager().addNewAnnotator(annotator, annotatorID);
                    }
                }

            }
        };
    }

    public KnowtatorCommand getSwitchProfileCommand() {
        return new KnowtatorCommand(manager, "Switch Annotator", KnowtatorIcons.SWITCH_PROFILE_ICON, "Switch between annotators") {

            @Override
            public void actionPerformed(ActionEvent e) {
                AnnotatorManager annotatorManager = manager.getAnnotatorManager();
                String annotatorName = ListDialog.showDialog(null, null, "Profiles", "Annotator Chooser", annotatorManager.getAnnotatorNames(), annotatorManager.getAnnotatorNames()[0], null);
                annotatorManager.switchAnnotator(annotatorName);
            }
        };
    }
}
