package edu.ucdenver.ccp.knowtator.model.owl;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.listeners.DebugListener;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OWLAPIDataExtractor implements Serializable, DebugListener, OWLSelectionModelListener {
  @SuppressWarnings("unused")
  private static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);

  private OWLWorkspace owlWorkSpace;
	private KnowtatorController controller;

	public OWLAPIDataExtractor(KnowtatorController controller) {
		this.controller = controller;
		controller.addDebugListener(this);
	}
  //
  //	private OWLAnnotationProperty getOWLAnnotationPropertyByName()
  //			throws OWLWorkSpaceNotSetException, OWLAnnotationPropertyNotFoundException {
  //		OWLAnnotationProperty result = getWorkSpace()
  //				.getOWLModelManager()
  //				.getOWLEntityFinder()
  //				.getOWLAnnotationProperty("name");
  //		if (result == null) {
  //			throw new OWLAnnotationPropertyNotFoundException();
  //		} else {
  //			return result;
  //		}
  //	}

  private String extractAnnotation(OWLEntity ent, OWLAnnotationProperty annotationProperty)
      throws OWLWorkSpaceNotSetException, OWLAnnotationNotFoundException {
    try {
      return EntitySearcher.getAnnotations(
              ent, getWorkSpace().getOWLModelManager().getActiveOntology(), annotationProperty)
          .stream()
          .map(owlAnnotation -> ((OWLLiteral) owlAnnotation.getValue()).getLiteral())
          .collect(Collectors.toList())
          .get(0);
    } catch (IndexOutOfBoundsException e) {
      throw new OWLAnnotationNotFoundException();
    }
  }

  private String getOwlEntID(OWLEntity ent) {
    return ent.getIRI().getShortForm();
  }

  public OWLClass getOWLClassByID(String classID) throws OWLWorkSpaceNotSetException {
    return getWorkSpace().getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
  }

  public OWLObjectProperty getOWLObjectPropertyByID(String classID)
      throws OWLWorkSpaceNotSetException, OWLObjectPropertyNotFoundException {
    OWLObjectProperty property =
        getWorkSpace().getOWLModelManager().getOWLEntityFinder().getOWLObjectProperty(classID);
    if (property == null) {
      throw new OWLObjectPropertyNotFoundException();
    } else {
      return property;
    }
  }

  public Set<OWLClass> getDescendants(OWLClass cls) throws OWLWorkSpaceNotSetException {

    return getWorkSpace()
        .getOWLModelManager()
        .getOWLHierarchyManager()
        .getOWLClassHierarchyProvider()
        .getDescendants(cls);
  }
//
//  public OWLObjectProperty getSelectedProperty() throws OWLWorkSpaceNotSetException {
//    return getWorkSpace().getOWLSelectionModel().getLastSelectedObjectProperty();
//  }

  public String getOWLEntityRendering(OWLEntity owlEntity) throws OWLWorkSpaceNotSetException {
    return getWorkSpace().getOWLModelManager().getOWLEntityRenderer().render(owlEntity);
  }

  public void setUpOWL(OWLWorkspace owlWorkSpace) {
	  log.warn("OWLAPIDataExtractor: setup OWL API connection");
    this.owlWorkSpace = owlWorkSpace;
    owlWorkSpace.getOWLSelectionModel().addListener(this);
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

  @Override
  public void setDebug() {
    OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
//    OWLWorkspace workspace = new OWLWorkspace();
//    OWLEditorKitFactory editorKitFactory = new OWLEditorKitFactory();
//    OWLEditorKit editorKit = new OWLEditorKit(editorKitFactory);
//    workspace.setup(editorKit);
//    workspace.initialise();
    OWLDataFactory factory = manager.getOWLDataFactory();

    IRI iri = IRI.create("test");
    OWLClass testClass = factory.getOWLClass(iri);
    controller.getSelectionManager().setSelectedOWLClass(testClass);

    iri = IRI.create("property_test");
    OWLObjectProperty  objectProperty = factory.getOWLObjectProperty(iri);
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
