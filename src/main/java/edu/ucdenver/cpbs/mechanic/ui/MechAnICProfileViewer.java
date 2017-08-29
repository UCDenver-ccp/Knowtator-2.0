package edu.ucdenver.cpbs.mechanic.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class MechAnICProfileViewer extends JPanel {

    private final JLabel profileLabel;

    public MechAnICProfileViewer() {
        super(new GridLayout(10, 2));
        Border border = BorderFactory.createTitledBorder("Profiles");
        setBorder(border);

        profileLabel = new JLabel();
        add(profileLabel);
    }

    public JLabel getProfileLabel() {
        return profileLabel;
    }
}
