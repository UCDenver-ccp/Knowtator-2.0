package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorController;

import javax.swing.*;

public class ViewMenu extends JMenu {

    private KnowtatorController controller;

    public ViewMenu(KnowtatorController controller) {
        super("View");
        this.controller = controller;

        add(showForCurrentProfileCommand());
    }



    private JCheckBoxMenuItem showForCurrentProfileCommand() {
        JCheckBoxMenuItem showForCurrentProfile = new JCheckBoxMenuItem("Show only annotations for current profile");
        showForCurrentProfile.addActionListener(e -> {
            if (showForCurrentProfile.getState()) controller.profileFilterEvent(true);
            else controller.profileFilterEvent(false);
        });

        return  showForCurrentProfile;
    }


}
