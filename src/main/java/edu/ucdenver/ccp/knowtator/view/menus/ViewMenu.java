package edu.ucdenver.ccp.knowtator.view.menus;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;

import javax.swing.*;

public class ViewMenu extends JMenu {

    private KnowtatorManager manager;

    public ViewMenu(KnowtatorManager manager) {
        super("View");
        this.manager = manager;

        add(showForCurrentProfileCommand());
    }



    private JCheckBoxMenuItem showForCurrentProfileCommand() {
        JCheckBoxMenuItem showForCurrentProfile = new JCheckBoxMenuItem("Show only annotations for current profile");
        showForCurrentProfile.addActionListener(e -> {
            if (showForCurrentProfile.getState()) manager.profileFilterEvent(true);
            else manager.profileFilterEvent(false);
        });

        return  showForCurrentProfile;
    }


}
