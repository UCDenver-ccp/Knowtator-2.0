package edu.ucdenver.ccp.knowtator.view.menu;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

class ProfileActions {
    static void addProfile(JDialog parent, KnowtatorView view) {
        JTextField field1 = new JTextField();
        Object[] message = {
                "Profile name", field1,
        };
        int option = JOptionPane.showConfirmDialog(parent, message, "Enter profile name", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String annotator = field1.getText();
            view.getController().getProfileCollection().addProfile(annotator);
        }
    }

    static void removeProfile(KnowtatorView view) {
        view.getController().getProfileCollection().removeActiveProfile();
    }
}
