package edu.ucdenver.ccp.knowtator.ui;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.ui.graph.KnowtatorGraphViewer;
import edu.ucdenver.ccp.knowtator.ui.info.InfoPane;
import edu.ucdenver.ccp.knowtator.ui.text.KnowtatorTextViewer;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;

import java.awt.dnd.*;

public class BasicKnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {

    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    public KnowtatorTextViewer textViewer;
    public KnowtatorManager manager;
    public KnowtatorGraphViewer graphViewer;
    public InfoPane infoPane;

    @Override
    public void initialiseClassView() {

        manager = new KnowtatorManager((OWLModelManagerImpl) getOWLModelManager(), getOWLWorkspace());
        manager.setView(this);

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        log.warn("Initialized Knowtator");
    }



    @Override
    protected OWLClass updateView(OWLClass selectedClass) {
//        manager.getSelectionModel().setSelectedClass(selectedClass);
        return selectedClass;
    }

    @Override
    public void disposeView() {

    }

    @Override
    public void dragEnter(DropTargetDragEvent e) {

    }

    @Override
    public void dragOver(DropTargetDragEvent e) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent e) {

    }

    @Override
    public void dragExit(DropTargetEvent e) {

    }

    @Override
    public void drop(DropTargetDropEvent e) {

    }

    public KnowtatorTextViewer getTextViewer() {
        return textViewer;
    }
    public KnowtatorGraphViewer getGraphViewer() {
        return graphViewer;
    }

}
