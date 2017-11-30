package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.owl.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.profile.Profile;
import edu.ucdenver.ccp.knowtator.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProfileMenu extends JMenu implements ProfileListener {

    public static Logger log = Logger.getLogger(ProfileMenu.class);

    private KnowtatorManager manager;
    private BasicKnowtatorView view;
    private JMenu switchAnnotatorMenu;
    private JMenu removeAnnotatorMenu;

    public ProfileMenu(KnowtatorManager manager, BasicKnowtatorView view) {
        super("Profile");
        this.manager = manager;
        this.view = view;

        switchAnnotatorMenu = new JMenu("Switch profile");
        removeAnnotatorMenu = new JMenu("Remove profile");

        add(switchAnnotatorMenu);
        add(newAnnotator());
        add(assignColorToClassMenu());
        add(removeAnnotatorMenu);
    }

    private JMenuItem assignColorToClassMenu() {

        JMenuItem assignColorToClass = new JMenuItem("Assign color to current class");
        assignColorToClass.addActionListener(e -> {
            String classID = OWLAPIDataExtractor.getSelectedOwlClassID(view);
            String className = OWLAPIDataExtractor.getSelectedOwlClassName(view);
            Profile currentProfile = manager.getProfileManager().getCurrentProfile();

            currentProfile.getColors().remove(classID);

            Color c = currentProfile.getColor(classID, className);


        });
        return assignColorToClass;
    }

    private JMenuItem newAnnotator() {
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
                Object[] message = {
                        "Profile name", field1,
                };
                int option = JOptionPane.showConfirmDialog(null, message, "Enter profile name", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String annotator = field1.getText();
                    manager.getProfileManager().addNewProfile(annotator);
                }
            }
        });

        return newAnnotator;
    }

    private void updateMenus() {
        switchAnnotatorMenu.removeAll();
        removeAnnotatorMenu.removeAll();
        ProfileManager profileManager = manager.getProfileManager();
        for (Profile profile : profileManager.getProfiles().values()) {

            JCheckBoxMenuItem switchAnnotator = new JCheckBoxMenuItem(profile.getProfileID());
            switchAnnotator.addActionListener(e -> profileManager.switchAnnotator(profile));
            if (manager.getProfileManager().getCurrentProfile().equals(profile)) {
                switchAnnotator.setState(true);
            } else switchAnnotator.setState(false);
            switchAnnotatorMenu.add(switchAnnotator);

            JMenuItem removeAnnotator = new JMenuItem(profile.getProfileID());
            removeAnnotator.addActionListener(e -> profileManager.removeProfile(profile));
            removeAnnotatorMenu.add(removeAnnotator);
        }
    }

    @Override
    public void profileAdded(Profile profile) {
        updateMenus();
    }

    @Override
    public void profileRemoved() {
        updateMenus();
    }

    @Override
    public void profileSelectionChanged(Profile profile) {
        updateMenus();
    }

    @Override
    public void profileFilterSelectionChanged(boolean filterByProfile) {

    }
}
