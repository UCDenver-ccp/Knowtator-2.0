package edu.ucdenver.ccp.knowtator.model.owl;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.DebugListener;
import edu.ucdenver.ccp.knowtator.listeners.OWLSetupListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.search.SearchDialogPanel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class OWLAPIDataExtractor implements Serializable, DebugListener, OWLSelectionModelListener {
  @SuppressWarnings("unused")
  private static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);

  private OWLWorkspace owlWorkSpace;
  private KnowtatorController controller;
  private List<OWLSetupListener> owlSetupListeners;

  public OWLAPIDataExtractor(KnowtatorController controller) {
    this.controller = controller;
    owlSetupListeners = new ArrayList<>();
    controller.addDebugListener(this);
  }

  public void addOWLSetupListener(OWLSetupListener listener) {
    owlSetupListeners.add(listener);
  }

  public OWLClass getOWLClassByID(String classID) throws OWLWorkSpaceNotSetException, OWLClassNotFoundException {
    setRenderRDFSLabel();

    OWLClass owlClass = getWorkSpace().getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
    if (owlClass == null) {
      throw new OWLClassNotFoundException();
    } else {
      return owlClass;
    }
  }

  public OWLObjectProperty getOWLObjectPropertyByID(String propertyID)
      throws OWLWorkSpaceNotSetException, OWLObjectPropertyNotFoundException {
    setRenderRDFSLabel();
    OWLObjectProperty property = getWorkSpace()
        .getOWLModelManager()
        .getOWLEntityFinder()
        .getOWLObjectProperty(propertyID);
    if (property == null) {
      throw  new OWLObjectPropertyNotFoundException();
    } else {
      return property;
    }
  }

  private void setRenderRDFSLabel() throws OWLWorkSpaceNotSetException {
    IRI labelIRI = getWorkSpace().getOWLModelManager().getOWLDataFactory().getRDFSLabel().getIRI();
    OWLRendererPreferences.getInstance()
            .setAnnotations(
                    Collections.singletonList(
                            labelIRI));
    getWorkSpace().getOWLModelManager().refreshRenderer();
    log.warn("Renderer set to RDFSLabel");
  }

  public Set<OWLClass> getDescendants(OWLClass cls) throws OWLWorkSpaceNotSetException {
    return getWorkSpace()
        .getOWLModelManager()
        .getOWLHierarchyManager()
        .getOWLClassHierarchyProvider()
        .getDescendants(cls);
  }

  public String getOWLEntityRendering(OWLEntity owlEntity, Boolean renderWithRDFSLabel) throws OWLWorkSpaceNotSetException, OWLEntityNullException {
    if (owlEntity == null) {
      throw new OWLEntityNullException();
    }
    if (renderWithRDFSLabel) {
      setRenderRDFSLabel();
    }
    return getWorkSpace().getOWLModelManager().getOWLEntityRenderer().render(owlEntity);
  }

  public void setUpOWL(OWLWorkspace owlWorkSpace) {
    log.warn("OWLAPIDataExtractor: setup OWL API connection");
    this.owlWorkSpace = owlWorkSpace;
    owlWorkSpace.getOWLSelectionModel().addListener(this);
    owlSetupListeners.forEach(OWLSetupListener::owlSetup);
  }

  public OWLWorkspace getWorkSpace() throws OWLWorkSpaceNotSetException {
    if (owlWorkSpace == null) {
      throw new OWLWorkSpaceNotSetException();
    } else {
      return owlWorkSpace;
    }
  }

  public void read(File file) throws IOException, OWLWorkSpaceNotSetException {
    if (file.isDirectory()) {
      for (Path path1 :
          Files.newDirectoryStream(
              Paths.get(file.toURI()), path -> path.toString().endsWith(".owl"))) {
        loadOntologyFromLocation(path1.toFile().toURI().toString());
      }
    }
  }

  private void loadOntologyFromLocation(String ontologyLocation)
      throws OWLWorkSpaceNotSetException {
    OWLWorkspace workSpace = getWorkSpace();
    List<String> ontologies =
        workSpace
            .getOWLModelManager()
            .getActiveOntologies()
            .stream()
            .map(
                ontology -> {
                  OWLOntologyID ontID = ontology.getOntologyID();
                  //noinspection Guava
                  Optional<IRI> ontIRI = ontID.getOntologyIRI();
                  if (ontIRI.isPresent()) {
                    return ontIRI.get().toURI().toString();
                  } else {
                    return null;
                  }
                })
            .collect(Collectors.toList());

    //        String ontologyLocation = OntologyTranslator.translate(classID);
    if (!ontologies.contains(ontologyLocation)) {
      log.warn("Loading ontology: " + ontologyLocation);
      try {
        OWLOntology newOntology =
            workSpace
                .getOWLModelManager()
                .getOWLOntologyManager()
                .loadOntology((IRI.create(ontologyLocation)));
        workSpace.getOWLModelManager().setActiveOntology(newOntology);
      } catch (OWLOntologyCreationException e) {
        log.warn("Knowtator: OWLAPIDataExtractor: Ontology already loaded");
      }
    }
  }

  public void searchForString(String stringToSearch) throws OWLWorkSpaceNotSetException {
    JDialog dialog = SearchDialogPanel.createDialog(null, getWorkSpace().getOWLEditorKit());
    Arrays.stream(dialog.getContentPane().getComponents()).forEach(component -> {
      if (component instanceof AugmentedJTextField) {
        ((AugmentedJTextField) component).setText(stringToSearch);
      }
    });

    getWorkSpace().showSearchDialog();
  }

  @Override
  public void setDebug() {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
    //    OWLWorkspace workspace = new OWLWorkspace();
    //    OWLEditorKitFactory editorKitFactory = new OWLEditorKitFactory();
    //    OWLEditorKit editorKit = new OWLEditorKit(editorKitFactory);
    //    workspace.setup(editorKit);
    //    workspace.initialise();
    OWLDataFactory factory = manager.getOWLDataFactory();

    IRI iri = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#DomainConcept");
    OWLClass testClass = factory.getOWLClass(iri);
    controller.getSelectionManager().setSelectedOWLClass(testClass);

    iri = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#HasCountryOfOrigin");
    OWLObjectProperty objectProperty = factory.getOWLObjectProperty(iri);
    controller.getSelectionManager().setSelectedOWLObjectProperty(objectProperty);
  }

  @Override
  public void selectionChanged() {
    OWLEntity ent = null;
    try {
      ent = getWorkSpace().getOWLSelectionModel().getSelectedEntity();
    } catch (OWLWorkSpaceNotSetException e) {
      e.printStackTrace();
    }
    if (ent instanceof OWLObjectProperty) {
      controller.getSelectionManager().setSelectedOWLObjectProperty((OWLObjectProperty) ent);
    } else if (ent instanceof OWLClass) {
      controller.getSelectionManager().setSelectedOWLClass((OWLClass) ent);
    }
  }
}
