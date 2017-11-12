package edu.ucdenver.ccp.knowtator;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.annotation.annotator.AnnotatorManager;
import edu.ucdenver.ccp.knowtator.annotation.text.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.text.AnnotationManager;
import edu.ucdenver.ccp.knowtator.io.xml.XmlUtil;
import edu.ucdenver.ccp.knowtator.listeners.AnnotationListener;
import edu.ucdenver.ccp.knowtator.listeners.DocumentListener;
import edu.ucdenver.ccp.knowtator.listeners.OwlSelectionListener;
import edu.ucdenver.ccp.knowtator.owl.OntologyTranslator;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.text.KnowtatorTextPane;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.OWLModelManagerImpl;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorManager implements OwlSelectionListener {
    public static final Logger log = Logger.getLogger(KnowtatorManager.class);
    public AnnotatorManager annotatorManager;
//    public OWLSelectionModel selectionModel;
    public AnnotationManager annotationManager;
    public XmlUtil xmlUtil;

    public List<AnnotationListener> annotationListeners;
    public List<DocumentListener> documentListeners;
    public List<OwlSelectionListener> owlSelectionListeners;
    public OWLModelManagerImpl owlModelManager;
    public OWLWorkspace owlWorkspace;
    public BasicKnowtatorView view;
    private ConfigProperties configProperties;


    public KnowtatorManager(OWLModelManagerImpl owlModelManager, OWLWorkspace owlWorkspace) {
        this.owlModelManager = owlModelManager;
        this.owlWorkspace = owlWorkspace;

        initManagers();
        loadConfig();
        initListeners();
    }

    public KnowtatorManager() {
                /*
        Initialize the managers, models, and utils
         */
        initManagers();
        loadConfig();
        initListeners();
    }

    public void initManagers() {
        annotationManager = new AnnotationManager(this);
        annotatorManager = new AnnotatorManager(this);  //manipulates annotatorMap and highlighters
        xmlUtil = new XmlUtil(this);  //reads and writes to XML
    }

    public void initListeners() {
        annotationListeners = new ArrayList<>();
        documentListeners = new ArrayList<>();
        documentListeners.add(annotationManager);
        owlSelectionListeners = new ArrayList<>();
        owlSelectionListeners.add(this);
    }





    public AnnotatorManager getAnnotatorManager() {
        return annotatorManager;
    }

    public void loadOntologyFromLocation(String classID) {
        List<String> ontologies = owlModelManager.getActiveOntologies().stream().map(ontology -> {
            OWLOntologyID ontID = ontology.getOntologyID();
            @SuppressWarnings("Guava") Optional<IRI> ontIRI = ontID.getOntologyIRI();
            if (ontIRI.isPresent()) {
                return ontIRI.get().toURI().toString();
            } else {
                return null;
            }
        }).collect(Collectors.toList());

        String ontologyLocation = OntologyTranslator.translate(classID);
        if (!ontologies.contains(ontologyLocation)) {
            owlModelManager.loadOntologyFromPhysicalURI(URI.create(OntologyTranslator.whichOntologyToUse(ontologyLocation)));
        }

    }

    public void setView(BasicKnowtatorView view) {
        this.view = view;
    }

    public XmlUtil getXmlUtil() {
        return xmlUtil;
    }
    public AnnotationManager getAnnotationManager() {
        return annotationManager;
    }


    public OWLModelManager getOwlModelManager() {
        return owlModelManager;
    }
    public List<AnnotationListener> getAnnotationListeners() {
        return annotationListeners;
    }

    public OWLWorkspace getOwlWorkspace() {
        return owlWorkspace;
    }

    public void owlSelectionChangedEvent(OWLEntity owlEntity) {
        for (OwlSelectionListener listener : owlSelectionListeners) {
            listener.owlEntitySelectionChanged(owlEntity);
        }
    }

    public void annotationsChangedEvent() {
        for (AnnotationListener listener : annotationListeners) {
            listener.annotationsChanged();
        }
    }

    public void annotationsChangedEvent(Annotation annotation) {
        for (AnnotationListener listener : annotationListeners) {
            listener.annotationsChanged(annotation);
        }
    }

    public void documentChangedEvent(KnowtatorTextPane textPane) {
        for (DocumentListener listener : documentListeners) {
            listener.documentChanged(textPane);
        }
    }

    public BasicKnowtatorView getKnowtatorView() {
        return view;
    }

    public void loadConfig() {
        configProperties = new ConfigProperties();
        xmlUtil.read("config.xml", true);
    }

    @Override
    public void owlClassSelectionChanged(OWLClass cls) {

    }

    @Override
    public void owlEntitySelectionChanged(OWLEntity owlEntity) {
        if (view != null) {
            if (view.getView() != null) {
                if (view.getView().isSyncronizing()) {
                    owlWorkspace.getOWLSelectionModel().setSelectedEntity(owlEntity);
                }
            }
        }
    }

    public ConfigProperties getConfigProperties() {
        return configProperties;
    }

    public static void main(String[] args) {
        KnowtatorManager manager = new KnowtatorManager();
        manager.simpleTest();
    }

    public void simpleTest() {
        getXmlUtil().read("file/test_annotations.xml", true);
        getXmlUtil().write(configProperties.getDefaultSaveLocation() + "test_annotation_output.xml");
    }
}
