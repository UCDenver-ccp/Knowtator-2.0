package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

public class SearchActions {
    public static void findText(KnowtatorView view, String textToFind) {
        view.getController()
                .getOWLModel()
                .searchForString(textToFind);
    }

    public static void findNextMatch(KnowtatorView view) {
        view.getKnowtatorTextPane().search(true);
    }

    public static void findPreviousMatch(KnowtatorView view) {
        view.getKnowtatorTextPane().search(false);
    }
}
