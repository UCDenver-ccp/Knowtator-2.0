package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;

import javax.swing.*;

public class AnnotationMenu extends JMenu {

    private BasicKnowtatorView view;

    public AnnotationMenu(BasicKnowtatorView view) {
        super("Annotation");

        this.view = view;

        add(showForCurrentProfileCommand());
    }

    private JCheckBoxMenuItem showForCurrentProfileCommand() {
        JCheckBoxMenuItem showForCurrentProfile = new JCheckBoxMenuItem("Show only annotations for current profile");
        showForCurrentProfile.addActionListener(e -> {
            if(showForCurrentProfile.getState()) view.profileFilterEvent(true);
            else view.profileFilterEvent(false);
        });

        return  showForCurrentProfile;
    }
}
