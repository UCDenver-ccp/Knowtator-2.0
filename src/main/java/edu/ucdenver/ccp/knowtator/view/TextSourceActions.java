package edu.ucdenver.ccp.knowtator.view;

import javax.swing.*;

class TextSourceActions {
    static void selectPreviousTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectPrevious();
    }

    static void selectNextTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectNext();
    }

    static void addTextSource(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getArticlesLocation());

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.getController().getTextSourceCollection().addDocument(fileChooser.getSelectedFile());
        }
    }

    static void removeTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().removeActiveTextSource();
    }

    static void setFontSize(KnowtatorView view, int fontSize) {
        view.getKnowtatorTextPane().setFontSize(fontSize);
    }
}
