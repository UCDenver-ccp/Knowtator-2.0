package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICView;
import edu.ucdenver.cpbs.mechanic.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.cpbs.mechanic.MechAnICSelectionModel;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICIcons;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import org.apache.log4j.Logger;
import org.protege.editor.core.ui.view.DisposableAction;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 *
 */
@SuppressWarnings("PackageAccessibility")
public class AddTextAnnotationCommand extends DisposableAction {

    private static final Logger log = Logger.getLogger(MechAnICView.class);

    private TextAnnotationManager textAnnotationManager;
    private JTabbedPane tabbedPane;
    private MechAnICSelectionModel selectionModel;

    /**
     * @param textAnnotationManager The annotation management tool
     * @param tabbedPane The tabbedPane containing the TextViewer
     * @param selectionModel Protege view SelectionModel
     */
    public AddTextAnnotationCommand(TextAnnotationManager textAnnotationManager, JTabbedPane tabbedPane, MechAnICSelectionModel selectionModel) {
        super("Add TextAnnotation", MechAnICIcons.getIcon(MechAnICIcons.ADD_TEXT_ANNOTATION_ICON));
        this.textAnnotationManager = textAnnotationManager;
        this.tabbedPane = tabbedPane;
        this.selectionModel = selectionModel;

        this.putValue(AbstractAction.SHORT_DESCRIPTION, "Add an annotation");

    }

    /**
     *
     */
    @Override
    public void dispose() {

    }

    /**
     * @param e Action event placeholder
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        addTextAnnotation();
    }

    /**
     *
     */
    private void addTextAnnotation() {
        OWLClass cls = selectionModel.getSelectedClass();
        try {
            MechAnICTextViewer textViewer = (MechAnICTextViewer) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
            Integer spanStart = textViewer.getSelectionStart();
            Integer spanEnd = textViewer.getSelectionEnd();
            String spannedText = textViewer.getSelectedText();
            try {
                textAnnotationManager.addTextAnnotation(cls, spanStart, spanEnd, spannedText);
                textAnnotationManager.highlightAnnotation(spanStart, spanEnd, textViewer, textAnnotationManager.getProfileManager().getCurrentHighlighterName());
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } catch (NullPointerException e) {
            log.error("No OWLClass selected");
        }
    }

}
