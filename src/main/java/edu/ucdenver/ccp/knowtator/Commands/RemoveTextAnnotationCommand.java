package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("PackageAccessibility")
public class RemoveTextAnnotationCommand extends DisposableAction {

    private JTabbedPane tabbedPane;

    public RemoveTextAnnotationCommand(KnowtatorView view) {
        super("Add TextAnnotation", KnowtatorIcons.getIcon(KnowtatorIcons.REMOVE_TEXT_ANNOTATION_ICON));
        this.tabbedPane = view.getTextViewer();

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add an annotation");

    }

    @Override
    public void dispose() {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        addTextAnnotation();
    }

    private void addTextAnnotation() {
        KnowtatorTextPane textViewer = (KnowtatorTextPane)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
        Integer spanStart = textViewer.getSelectionStart();
        Integer spanEnd = textViewer.getSelectionEnd();
        textViewer.getTextAnnotationManager().removeTextAnnotation(spanStart, spanEnd, textViewer);
    }

}
