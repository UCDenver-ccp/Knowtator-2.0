package edu.ucdenver.ccp.knowtator.view;

class SearchActions {
    static void findText(KnowtatorView view, String textToFind) {
        view.getController()
                .getOWLModel()
                .searchForString(textToFind);
    }

    static void findNextMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations, boolean isRegex) {
        view.getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, isRegex, true);
    }

    static void findPreviousMatch(KnowtatorView view, String textToFind, boolean isCaseSensitive, boolean isOnlyInAnnotations, boolean isRegex) {
        view.getKnowtatorTextPane().search(textToFind, isCaseSensitive, isOnlyInAnnotations, isRegex, false);
    }
}
