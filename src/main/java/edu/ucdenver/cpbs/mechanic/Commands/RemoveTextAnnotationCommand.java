package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("PackageAccessibility")
public class RemoveTextAnnotationCommand extends DisposableAction {

    private JTabbedPane tabbedPane;

    public RemoveTextAnnotationCommand(MechAnICView view) {
        super("Add TextAnnotation", MechAnICIcons.getIcon(MechAnICIcons.REMOVE_TEXT_ANNOTATION_ICON));
        this.tabbedPane = view.getTextViewerTabbedPane();

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
        MechAnICTextViewer textViewer = (MechAnICTextViewer)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
        Integer spanStart = textViewer.getSelectionStart();
        Integer spanEnd = textViewer.getSelectionEnd();
        textViewer.getTextAnnotationManager().removeTextAnnotation(spanStart, spanEnd, textViewer);
    }

}
