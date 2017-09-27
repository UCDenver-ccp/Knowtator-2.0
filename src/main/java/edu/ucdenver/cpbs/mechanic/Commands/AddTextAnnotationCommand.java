package edu.ucdenver.cpbs.mechanic.Commands;

import edu.ucdenver.cpbs.mechanic.MechAnICSelectionModel;
import edu.ucdenver.cpbs.mechanic.MechAnICView;
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

    private JTabbedPane tabbedPane;
    private MechAnICSelectionModel selectionModel;

    public AddTextAnnotationCommand(MechAnICView view) {
        super("Add TextAnnotation", MechAnICIcons.getIcon(MechAnICIcons.ADD_TEXT_ANNOTATION_ICON));
        this.tabbedPane = view.getTextViewerTabbedPane();
        this.selectionModel = view.getSelectionModel();

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
        if (cls != null) {
            MechAnICTextViewer textViewer = (MechAnICTextViewer) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
            Integer spanStart = textViewer.getSelectionStart();
            Integer spanEnd = textViewer.getSelectionEnd();

            try {
                textViewer.getTextAnnotationManager().addTextAnnotation(cls, spanStart, spanEnd);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        } else {
            log.error("No OWLClass selected");
        }
    }

}
