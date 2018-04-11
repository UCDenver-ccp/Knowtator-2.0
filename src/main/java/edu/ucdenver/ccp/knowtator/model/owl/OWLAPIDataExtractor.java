package edu.ucdenver.ccp.knowtator.model.owl;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class OWLAPIDataExtractor {
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(OWLAPIDataExtractor.class);

	private OWLWorkspace owlWorkSpace;
	private KnowtatorController controller;

	public OWLAPIDataExtractor(KnowtatorController controller) {
		this.controller = controller;
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

	public OWLObjectProperty getSelectedProperty() throws OWLWorkSpaceNotSetException {
		return getWorkSpace().getOWLSelectionModel().getLastSelectedObjectProperty();
	}

	public String getOWLClassID(OWLClass cls) throws OWLWorkSpaceNotSetException {
		try {
			return extractAnnotation(
					cls, getWorkSpace().getOWLModelManager().getOWLDataFactory().getRDFSLabel());
		} catch (OWLAnnotationNotFoundException e) {
			return getOwlEntID(cls);
		}
	}

	public void setUpOWL(OWLWorkspace owlWorkSpace) {
		this.owlWorkSpace = owlWorkSpace;
	}

	private OWLWorkspace getWorkSpace() throws OWLWorkSpaceNotSetException {
		if (owlWorkSpace == null) {
			throw new OWLWorkSpaceNotSetException();
		} else {
			return owlWorkSpace;
		}
	}

	public void read(File file) throws IOException, OWLWorkSpaceNotSetException {
		if (file.isDirectory()) {
			for (Path path1 : Files.newDirectoryStream(Paths.get(file.toURI()), path -> path.toString().endsWith(".owl"))) {
				loadOntologyFromLocation(path1.toFile().toURI().toString());
			}
		}
	}

	private void loadOntologyFromLocation(
			String ontologyLocation) throws OWLWorkSpaceNotSetException {
		OWLWorkspace workSpace = controller.getOWLAPIDataExtractor().getWorkSpace();
		if (workSpace != null) {
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
	}
}
