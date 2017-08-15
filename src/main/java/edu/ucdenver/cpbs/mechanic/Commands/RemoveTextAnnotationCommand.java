package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.cpbs.mechanic.MechAnICSelectionModel;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.protege.editor.core.ui.view.DisposableAction;

import javax.swing.*;
import java.awt.event.ActionEvent;

@SuppressWarnings("PackageAccessibility")
public class RemoveTextAnnotationCommand extends DisposableAction {

    private TextAnnotationManager textAnnotationManager;
    private JTabbedPane tabbedPane;
    private MechAnICSelectionModel selectionModel;

    public RemoveTextAnnotationCommand(TextAnnotationManager textAnnotationManager, JTabbedPane tabbedPane, MechAnICSelectionModel selectionModel) {
        super("Add TextAnnotation", MechAnICIcons.getIcon(MechAnICIcons.REMOVE_TEXT_ANNOTATION_ICON));
        this.textAnnotationManager = textAnnotationManager;
        this.tabbedPane = tabbedPane;
        this.selectionModel = selectionModel;

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
        textAnnotationManager.removeTextAnnotation(spanStart, spanEnd, textViewer);
    }

}
