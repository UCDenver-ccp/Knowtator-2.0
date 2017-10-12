package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class NewHighlighterCommand extends DisposableAction {

    public KnowtatorView view;

    public NewHighlighterCommand(KnowtatorView view) {
        super("Add Highlighter Annotator", KnowtatorIcons.getIcon(KnowtatorIcons.NEW_HIGHLIGHTER_ICON));
        this.view = view;
        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add a new highlighter profile");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        view.getAnnotatorManager().getCurrentAnnotator().addHighlighter(view.getSelectionModel().getSelectedClass());
    }

    //TODO removeProfile
}