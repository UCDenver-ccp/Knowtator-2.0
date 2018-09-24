package edu.ucdenver.ccp.knowtator.model.owl;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.SavableKnowtatorManager;
import edu.ucdenver.ccp.knowtator.listeners.DebugListener;
import edu.ucdenver.ccp.knowtator.listeners.OWLSetupListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.selection.OWLClassSelectionListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.search.SearchDialogPanel;
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

public class OWLManager implements Serializable, DebugListener, OWLSelectionModelListener, ProjectListener, SavableKnowtatorManager {
  @SuppressWarnings("unused")
  private static final Logger log = LogManager.getLogger(OWLManager.class);

  private OWLWorkspace owlWorkSpace;
  private KnowtatorController controller;
  private List<OWLSetupListener> owlSetupListeners;
  private File ontologiesLocation;
  private OWLEntity selectedOWLEntity;
  private List<OWLClassSelectionListener> owlEntityListeners;

  public OWLManager(KnowtatorController controller) {
    this.controller = controller;
    owlEntityListeners = new ArrayList<>();
    controller.addProjectListener(this);
    owlSetupListeners = new ArrayList<>();
    controller.addDebugListener(this);
  }

  public OWLEntity getSelectedOWLEntity() {
    return selectedOWLEntity;
  }

  public void setSelectedOWLEntity(OWLEntity owlClass) {
    selectedOWLEntity = owlClass;
    owlEntityListeners.forEach(listener -> listener.owlEntityChanged(owlClass));
  }

  public void addOWLSetupListener(OWLSetupListener listener) {
    owlSetupListeners.add(listener);
  }

  public OWLClass getOWLClassByID(String classID) throws OWLWorkSpaceNotSetException, OWLClassNotFoundException {
    OWLClass owlClass = getWorkSpace().getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
    if (owlClass == null) {
      throw new OWLClassNotFoundException();
    } else {
      return owlClass;
    }
  }

  public OWLObjectProperty getOWLObjectPropertyByID(String propertyID)
      throws OWLWorkSpaceNotSetException, OWLObjectPropertyNotFoundException {
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

  public void setRenderRDFSLabel() throws OWLWorkSpaceNotSetException {
    IRI labelIRI = getWorkSpace().getOWLModelManager().getOWLDataFactory().getRDFSLabel().getIRI();
    OWLRendererPreferences.getInstance()
            .setAnnotations(
                    Collections.singletonList(
                            labelIRI));

    getWorkSpace().getOWLModelManager().refreshRenderer();
  }

  public Set<OWLClass> getDescendants(OWLClass cls) throws OWLWorkSpaceNotSetException {
    return getWorkSpace()
        .getOWLModelManager()
        .getOWLHierarchyManager()
        .getOWLClassHierarchyProvider()
        .getDescendants(cls);
  }

  public String getOWLEntityRendering(OWLEntity owlEntity) throws OWLWorkSpaceNotSetException, OWLEntityNullException {
    if (owlEntity == null) {
      throw new OWLEntityNullException();
    }
    return getWorkSpace().getOWLModelManager().getOWLEntityRenderer().render(owlEntity);
  }

  public void setUpOWL(OWLWorkspace owlWorkSpace) {
    this.owlWorkSpace = owlWorkSpace;
    owlWorkSpace.getOWLSelectionModel().addListener(this);
    setUpOWL();
  }

  public void setUpOWL() {
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
        log.warn("Knowtator: OWLManager: Ontology already loaded");
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

    dialog.setVisible(true);
  }

  @Override
  public void setDebug() {
    OWLOntologyManager manager = org.semanticweb.owlapi.apibinding.OWLManager.createOWLOntologyManager();
    //    OWLWorkspace workspace = new OWLWorkspace();
    //    OWLEditorKitFactory editorKitFactory = new OWLEditorKitFactory();
    //    OWLEditorKit editorKit = new OWLEditorKit(editorKitFactory);
    //    workspace.setup(editorKit);
    //    workspace.initialise();
    OWLDataFactory factory = manager.getOWLDataFactory();

    IRI iri = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#DomainConcept");
    OWLClass testClass = factory.getOWLClass(iri);
    setSelectedOWLEntity(testClass);

    iri = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#HasCountryOfOrigin");
    OWLObjectProperty objectProperty = factory.getOWLObjectProperty(iri);
    controller.getTextSourceCollection().getSelection()
            .getGraphSpaceCollection().getSelection()
            .getRelationSelectionManager().setSelectedOWLObjectProperty(objectProperty);
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
      controller.getTextSourceCollection().getSelection()
              .getGraphSpaceCollection().getSelection()
              .getRelationSelectionManager().setSelectedOWLObjectProperty((OWLObjectProperty) ent);
    } else if (ent instanceof OWLClass) {
      setSelectedOWLEntity(ent);
    }
  }

  @Override
  public void projectClosed() {

  }

  @Override
  public void projectLoaded() {
    try {
      setRenderRDFSLabel();
    } catch (OWLWorkSpaceNotSetException ignored) {

    }
  }

  public void addOWLEntityListener(OWLClassSelectionListener listener) {
    owlEntityListeners.add(listener);
  }

  public void dispose() {
    owlEntityListeners.clear();
    owlSetupListeners.clear();
    try {
      getWorkSpace().getOWLSelectionModel().removeListener(this);
    } catch (OWLWorkSpaceNotSetException ignored) {

    }
  }

  @Override
  public File getSaveLocation() {
    return ontologiesLocation;
  }

  @Override
  public void setSaveLocation(File newSaveLocation) throws IOException {
    this.ontologiesLocation = newSaveLocation;
    Files.createDirectories(ontologiesLocation.toPath());
  }

  @Override
  public void makeDirectory() throws IOException {
    setSaveLocation(new File(controller.getSaveLocation(), "Ontologies"));
  }

  @Override
  public void load() {
    if (getSaveLocation() != null) {
      log.warn("Loading ontologies");
      try {
        read(getSaveLocation());
      } catch (IOException | OWLWorkSpaceNotSetException e) {
        log.warn("Could not load ontologies");
      }
    }
  }

  @Override
  public void save() {

  }
}



