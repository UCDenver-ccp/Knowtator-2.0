package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("PackageAccessibility")
public class RemoveTextAnnotationCommand extends DisposableAction {

    public KnowtatorTextViewer textViewer;

    public RemoveTextAnnotationCommand(KnowtatorView view) {
        super("Add TextAnnotation", KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE_TEXT_ANNOTATION_ICON));
        textViewer = view.getTextViewer();

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add an annotation");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        removeTextAnnotation();
    }


    public void removeTextAnnotation() {
        KnowtatorTextPane textPane = textViewer.getSelectedTextPane();
        Integer spanStart = textPane.getSelectionStart();
        Integer spanEnd = textPane.getSelectionEnd();
//        textViewer.getTextAnnotationManager().removeTextAnnotation(spanStart, spanEnd, textViewer);
    }

}
