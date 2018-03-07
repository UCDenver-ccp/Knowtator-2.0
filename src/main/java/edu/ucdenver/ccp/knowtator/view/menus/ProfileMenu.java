/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.listeners.ProfileListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.model.xml.XmlUtil;
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

                pickAColor(classID, descendants, profile, manager.getProfileManager());

                manager.colorChangedEvent();
            }

        });
        return assignColorToClass;
    }

    public static Color pickAColor(String classID, String[] descendants, Profile profile, ProfileManager profileManager) {
        Color c = null;
        if (profile.getColor(classID) == null) {
            c = JColorChooser.showDialog(null, "Pick a color for " + classID, Color.CYAN);
            if (c != null) {
                profileManager.getCurrentProfile().addColor(classID, c);

                if (JOptionPane.showConfirmDialog(null, "Assign color to descendents of " + classID + "?") == JOptionPane.OK_OPTION) {
                    if (descendants != null) {
                        Color finalC = c;
                        Arrays.stream(descendants).forEach(descendant -> profile.addColor(descendant, finalC));
                    }
                }
            }
        }
        return c;
    }

    private JMenuItem newProfile() {
        JMenuItem newAnnotator = new JMenuItem("New profile");
        newAnnotator.addActionListener(e -> {
            int dialogResult = JOptionPane.showConfirmDialog(null, "Load profile from test_project(xml)?", "New profile", JOptionPane.YES_NO_CANCEL_OPTION);
            if(dialogResult == JOptionPane.YES_OPTION) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setCurrentDirectory(manager.getProjectManager().getProjectLocation());

                if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                    XmlUtil.readXML(manager.getProfileManager(), fileChooser.getSelectedFile());
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
