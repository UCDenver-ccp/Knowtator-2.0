package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ProfileMenu extends JMenu implements ProfileListener {

    public static Logger log = Logger.getLogger(ProfileMenu.class);

    private KnowtatorManager manager;

    public ProfileMenu(KnowtatorManager manager) {
        super("Profile");
        this.manager = manager;

        add(newProfile());
        addSeparator();
        add(assignColorToClassMenu());
    }

    private JMenuItem assignColorToClassMenu() {

        JMenuItem assignColorToClass = new JMenuItem("Assign color to current class");
        assignColorToClass.addActionListener(e -> {
            String classID = manager.getOWLAPIDataExtractor().getSelectedOwlClassID();
            String[] descendants = manager.getOWLAPIDataExtractor().getSelectedOwlClassDescendants();
            if (classID != null) {

                Profile profile = manager.getProfileManager().getCurrentProfile();

                pickAColor(classID, descendants, profile);

                manager.colorChangedEvent();
            }

        });
        return assignColorToClass;
    }

    private static void pickAColor(String classID, String[] descendants, Profile profile) {
        Color c = JColorChooser.showDialog(null, "Pick a color for " + classID, Color.CYAN);
        if (c != null) {
            profile.addColor(classID, c);

            if (JOptionPane.showConfirmDialog(null, "Assign color to descendants of " + classID + "?") == JOptionPane.OK_OPTION) {
                if (descendants != null) {
                    Arrays.stream(descendants).forEach(descendant -> profile.addColor(descendant, c));
                }
            }
        }
    }

    private JMenuItem newProfile() {
        JMenuItem newAnnotator = new JMenuItem("New profile");
        newAnnotator.addActionListener(e -> {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Load profile from test_project(knowtator)?", "New profile", JOptionPane.YES_NO_CANCEL_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(manager.getProjectManager().getProjectLocation());

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    manager.getProjectManager().loadFromFormat(KnowtatorXMLUtil.class, manager.getProfileManager(), fileChooser.getSelectedFile());
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
        removeAll();

        add(newProfile());

        ProfileManager profileManager = manager.getProfileManager();

        if (profileManager.getProfiles().size() > 1) {
            JMenu switchProfileMenu = new JMenu("Switch profile");
            JMenu removeProfileMenu = new JMenu("Remove profile");
            for (Profile profile : profileManager.getProfiles().values()) {

                JCheckBoxMenuItem switchAnnotator = new JCheckBoxMenuItem(profile.getId());
                switchAnnotator.addActionListener(e -> profileManager.switchAnnotator(profile));
                if (manager.getProfileManager().getCurrentProfile().equals(profile)) {
                    switchAnnotator.setState(true);
                } else switchAnnotator.setState(false);
                switchProfileMenu.add(switchAnnotator);

                JMenuItem removeAnnotator = new JMenuItem(profile.getId());
                removeAnnotator.addActionListener(e -> profileManager.removeProfile(profile));
                removeProfileMenu.add(removeAnnotator);
            }
            add(switchProfileMenu);
            add(removeProfileMenu);
        }

        addSeparator();
        add(assignColorToClassMenu());
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

    @Override
    public void colorChanged() {

    }
}
