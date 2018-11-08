package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

import javax.swing.undo.UndoableEdit;

public class KnowtatorActions {



    public static class SetFontSizeAction extends AbstractKnowtatorAction {

        private KnowtatorView view;
        private int previousFontSize;
        private int fontSize;

        public SetFontSizeAction(KnowtatorView view, int fontSize) {
            super("Set font size");
            this.view = view;
            previousFontSize = view.getKnowtatorTextPane().getFont().getSize();
            this.fontSize = fontSize;
        }

        @Override
        public void execute() {
            view.getKnowtatorTextPane().setFontSize(fontSize);
        }

        @Override
        public UndoableEdit getEdit() {
            return new KnowtatorEdit("Set font size") {
                @Override
                public void undo() {
                    view.getKnowtatorTextPane().setFontSize(previousFontSize);
                }

                @Override
                public void redo() {
                    view.getKnowtatorTextPane().setFontSize(fontSize);
                }
            };
        }
    }
}
