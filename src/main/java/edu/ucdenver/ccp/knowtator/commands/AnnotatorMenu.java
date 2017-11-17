package edu.ucdenver.ccp.knowtator.commands;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.annotation.profile.Profile;
import edu.ucdenver.ccp.knowtator.annotation.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.io.File;
import java.util.Set;

public class AnnotatorMenu extends JMenu implements ProfileListener {

    public static Logger log = Logger.getLogger(AnnotatorMenu.class);

    private KnowtatorManager manager;
    JMenu switchAnnotatorMenu;
    JMenu removeAnnotatorMenu;

    public AnnotatorMenu(KnowtatorManager manager) {
        super("Profile");
        this.manager = manager;

        JMenuItem assignColorToClass = new JMenuItem("Assign color to current class");
        assignColorToClass.addActionListener(e -> {
            String className = OWLAPIDataExtractor.getSelectedClassName(manager);
            Profile currentProfile = manager.getProfileManager().getCurrentProfile();

            currentProfile.getColors().remove(className);

            currentProfile.getColor(className);

            Set<OWLClass> decendents = OWLAPIDataExtractor.getDecendents(manager, className);
            if (decendents != null) {
                for (OWLClass decendent : decendents) {
                    currentProfile.getColor(OWLAPIDataExtractor.getClassNameByOWLClass(manager, decendent));
                }
            }
        });

        JMenuItem newAnnotator = new JMenuItem("New profile");
        newAnnotator.addActionListener(e -> {
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
                        "Profile name", field1,
                        "Profile ID", field2,
                };
                int option = JOptionPane.showConfirmDialog(null, message, "Enter profile name and ID", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String annotator = field1.getText();
                    String annotatorID = field2.getText();
                    manager.getProfileManager().addNewAnnotator(annotator, annotatorID);
                }
            }
        });

        updateMenus();

        add(switchAnnotatorMenu);
        add(newAnnotator);
        add(assignColorToClass);
        add(removeAnnotatorMenu);
    }

    public void updateMenus() {
        switchAnnotatorMenu = new JMenu("Switch profile");
        removeAnnotatorMenu = new JMenu("Remove profile");
        ProfileManager profileManager = manager.getProfileManager();
        for (String annotatorName : profileManager.getAnnotators().keySet()) {
            log.warn(annotatorName);
            JMenuItem switchAnnotator = new JMenuItem(annotatorName);
            switchAnnotator.addActionListener(e -> profileManager.switchAnnotator(annotatorName));
            switchAnnotatorMenu.add(switchAnnotator);

            JMenuItem removeAnnotator = new JMenuItem(annotatorName);
            removeAnnotator.addActionListener(e -> profileManager.removeAnnotator(annotatorName));
            removeAnnotatorMenu.add(removeAnnotator);
        }
    }

    @Override
    public void profileChanged(Profile profile) {
        updateMenus();
    }
}
