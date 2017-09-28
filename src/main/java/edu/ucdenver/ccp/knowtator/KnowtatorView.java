package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.ui.KnowtatorGraphViewer;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextViewer;
import edu.ucdenver.ccp.knowtator.xml.XmlUtil;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import java.awt.dnd.*;
import java.io.IOException;


/**
 * @author Harrison Pielke-Lombardo
 */
@SuppressWarnings("PackageAccessibility")
public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {

    ProfileManager profileManager;
    KnowtatorSelectionModel selectionModel;
    XmlUtil xmlUtil;
    KnowtatorTextViewer textViewer;
    KnowtatorGraphViewer graphViewer;

    public KnowtatorTextViewer getTextViewer() {
        return textViewer;
    }
    public KnowtatorGraphViewer getGraphViewer() {
        return graphViewer;
    }
    /**
     *
     */
    //TODO add a POS highlighter
    @Override
    public void initialiseClassView() {

        /*
        Initialize the managers, models, and utils
         */
        selectionModel = new KnowtatorSelectionModel();  //helps get the selected OWL API classes
        profileManager = new ProfileManager(this);  //manipulates profiles and highlighters
        xmlUtil = new XmlUtil(this);  //reads and writes to XML

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);
    }

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {
        selectionModel.setSelectedClass(selectedClass);
        return selectedClass;
    }

    public void setGlobalSelection1(OWLEntity selObj) {
        setGlobalSelection(selObj);
    }

    public ProfileManager getProfileManager() {
        return profileManager;
    }

    public KnowtatorSelectionModel getSelectionModel() {
        return selectionModel;
    }

    @Override
    public void disposeView() {

    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {

    }

    public static void main(String[] args) throws IOException { }

    public XmlUtil getXmlUtil() {
        return xmlUtil;
    }
}
