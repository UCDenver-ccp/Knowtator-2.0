package edu.ucdenver.cpbs.mechanic.ui;

import edu.ucdenver.cpbs.mechanic.ProfileManager;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MechAnICProfileViewer extends JPanel implements ActionListener {

    private final JLabel profileLabel;
    private ProfileManager profileManager;

    public MechAnICProfileViewer(ProfileManager profileManager) {
        super(new GridLayout(10, 2));
        this.profileManager = profileManager;
        Border border = BorderFactory.createTitledBorder("Profiles");
        setBorder(border);

        profileLabel = new JLabel();
        add(profileLabel);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        profileManager.setCurrentHighlighterName(e.getActionCommand());
    }

    public JLabel getProfileLabel() {
        return profileLabel;
    }
}
