package edu.ucdenver.ccp.knowtator.ui;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class KnowtatorProfileViewer extends JPanel {

    public final JLabel profileLabel;

    public KnowtatorProfileViewer() {
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
