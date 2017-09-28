package edu.ucdenver.ccp.knowtator.Commands;

import edu.ucdenver.ccp.knowtator.ui.KnowtatorIcons;
import edu.ucdenver.ccp.knowtator.KnowtatorView;
import edu.ucdenver.ccp.knowtator.KnowtatorSelectionModel;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
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

    private static final Logger log = Logger.getLogger(KnowtatorView.class);

    private JTabbedPane tabbedPane;
    private KnowtatorSelectionModel selectionModel;

    public AddTextAnnotationCommand(KnowtatorView view) {
        super("Add TextAnnotation", KnowtatorIcons.getIcon(KnowtatorIcons.ADD_TEXT_ANNOTATION_ICON));
        this.tabbedPane = view.getTextViewer();
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
            KnowtatorTextPane textViewer = (KnowtatorTextPane) ((JScrollPane) tabbedPane.getSelectedComponent()).getViewport().getView();
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
