package edu.ucdenver.ccp.knowtator;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.Profiles.AnnotatorManager;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotation;
import edu.ucdenver.ccp.knowtator.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.ccp.knowtator.listeners.DocumentListener;
import edu.ucdenver.ccp.knowtator.listeners.TextAnnotationListener;
import edu.ucdenver.ccp.knowtator.owl.OWLSelectionModel;
import edu.ucdenver.ccp.knowtator.owl.OntologyTranslator;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorGraphViewer;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextPane;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextViewer;
import edu.ucdenver.ccp.knowtator.xml.XmlUtil;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.awt.dnd.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorView extends AbstractOWLClassViewComponent implements DropTargetListener {
    public static final Logger log = Logger.getLogger(KnowtatorView.class);
    public AnnotatorManager annotatorManager;
    public OWLSelectionModel selectionModel;
    public TextAnnotationManager textAnnotationManager;
    public XmlUtil xmlUtil;
    public KnowtatorTextViewer textViewer;
    public KnowtatorGraphViewer graphViewer;
    public List<TextAnnotationListener> textAnnotationListeners;
    public List<DocumentListener> documentListeners;

    /**
     *
     */
    //TODO add a POS highlighter
    @Override
    public void initialiseClassView() {

        /*
        Initialize the managers, models, and utils
         */
        textAnnotationManager = new TextAnnotationManager(this);
        selectionModel = new OWLSelectionModel();  //helps get the selected OWL API classes
        annotatorManager = new AnnotatorManager(this);  //manipulates profiles and highlighters
        xmlUtil = new XmlUtil(this);  //reads and writes to XML

        textAnnotationListeners = new ArrayList<>();
        documentListeners = new ArrayList<DocumentListener>() {
            {
                add(textAnnotationManager);
            }
        };

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

    public AnnotatorManager getAnnotatorManager() {
        return annotatorManager;
    }

    public OWLSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public void loadOntologyFromLocation(String classID) {

        List<String> ontologies = getOWLModelManager().getActiveOntologies().stream().map(ontology -> {
            OWLOntologyID ontID = ontology.getOntologyID();
            @SuppressWarnings("Guava") Optional<IRI> ontIRI = ontID.getOntologyIRI();
            if(ontIRI.isPresent()) {
                return ontIRI.get().toURI().toString();
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        String ontologyLocation = OntologyTranslator.translate(classID);
        if (!ontologies.contains(ontologyLocation)) {
            ((OWLModelManagerImpl) getOWLModelManager()).loadOntologyFromPhysicalURI(URI.create(OntologyTranslator.whichOntologyToUse(ontologyLocation)));
        }
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

    public XmlUtil getXmlUtil() {
        return xmlUtil;
    }

    public TextAnnotationManager getTextAnnotationManager() {
        return textAnnotationManager;
    }
    public KnowtatorTextViewer getTextViewer() {
        return textViewer;
    }
    public KnowtatorGraphViewer getGraphViewer() {
        return graphViewer;
    }

    public void textAnnotationsChangedEvent() {
        for (TextAnnotationListener listener : textAnnotationListeners) {
            listener.textAnnotationsChanged();
        }
    }

    @SuppressWarnings("unused")
    public void textAnnotationsChangedEvent(TextAnnotation newAnnotation) {
        for (TextAnnotationListener listener : textAnnotationListeners) {
            listener.textAnnotationsChanged(newAnnotation);
        }
    }

    public void documentChangedEvent(KnowtatorTextPane textPane) {
        for (DocumentListener listener : documentListeners) {
            listener.documentChanged(textPane);
        }
    }
}
