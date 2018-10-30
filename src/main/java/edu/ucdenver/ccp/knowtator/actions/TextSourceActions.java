package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.*;

public class TextSourceActions {
    public static void selectPreviousTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectPrevious();
    }

    public static void selectNextTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().selectNext();
    }

    public static void addTextSource(KnowtatorView view) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(view.getController().getTextSourceCollection().getArticlesLocation());

        if (fileChooser.showOpenDialog(view) == JFileChooser.APPROVE_OPTION) {
            view.getController().getTextSourceCollection().addDocument(fileChooser.getSelectedFile());
        }
    }

    public static void removeTextSource(KnowtatorView view) {
        view.getController().getTextSourceCollection().removeSelected();
    }

    public static void setFontSize(KnowtatorView view, int fontSize) {
        int previousFontSize = view.getKnowtatorTextPane().getFont().getSize();
        view.getKnowtatorTextPane().setFontSize(fontSize);
        KnowtatorEdit edit = new KnowtatorEdit("Set font size") {
            @Override
            public void undo() {
                view.getKnowtatorTextPane().setFontSize(previousFontSize);
            }

            @Override
            public void redo() {
                view.getKnowtatorTextPane().setFontSize(fontSize);
            }
        };

        view.getController().addEdit(edit);
    }
}
