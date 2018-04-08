package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import org.apache.log4j.Logger;

import javax.swing.*;

public class ProfileMenu extends JMenu implements ProfileListener {

    public static Logger log = Logger.getLogger(ProfileMenu.class);

    private KnowtatorController controller;

    ProfileMenu(KnowtatorController controller) {
        super("Profile");
        this.controller = controller;

        add(newProfile());
    }





    private JMenuItem newProfile() {
        JMenuItem newAnnotator = new JMenuItem("New profile");
        newAnnotator.addActionListener(e -> {
            int dialogResult = JOptionPane.showConfirmDialog(controller.getView(), "Load profile from test_project(knowtator)?", "New profile", JOptionPane.YES_NO_CANCEL_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(controller.getProjectManager().getProjectLocation());

                if (fileChooser.showSaveDialog(controller.getView()) == JFileChooser.APPROVE_OPTION) {
                    controller.getProjectManager().loadFromFormat(KnowtatorXMLUtil.class, controller.getProfileManager(), fileChooser.getSelectedFile());
                }



            } else if (dialogResult == JOptionPane.NO_OPTION) {


                JTextField field1 = new JTextField();
                Object[] message = {
                        "Profile name", field1,
                };
                int option = JOptionPane.showConfirmDialog(controller.getView(), message, "Enter profile name", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    String annotator = field1.getText();
                    controller.getProfileManager().addNewProfile(annotator);
                }
            }
        });

        return newAnnotator;
    }

    private void updateMenus() {
        removeAll();

        add(newProfile());

        ProfileManager profileManager = controller.getProfileManager();

        if (profileManager.getProfiles().size() > 1) {
            JMenu switchProfileMenu = new JMenu("Switch profile");
            JMenu removeProfileMenu = new JMenu("Remove profile");
            for (Profile profile : profileManager.getProfiles().values()) {

                JCheckBoxMenuItem switchAnnotator = new JCheckBoxMenuItem(profile.getId());
                switchAnnotator.addActionListener(e -> profileManager.switchAnnotator(profile));
                if (controller.getSelectionManager().getActiveProfile().equals(profile)) {
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
