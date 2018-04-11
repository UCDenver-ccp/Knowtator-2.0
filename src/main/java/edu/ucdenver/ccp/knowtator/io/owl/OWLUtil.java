package edu.ucdenver.ccp.knowtator.io.owl;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.OWLAPIDataExtractor;
import edu.ucdenver.ccp.knowtator.model.Savable;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

public class OWLUtil implements BasicIOUtil {

	private static final Logger log = Logger.getLogger(OWLUtil.class);

	@Override
	public void read(Savable savable, File file) throws IOException {
		if (savable instanceof OWLAPIDataExtractor) {
			if (file.isDirectory()) {
				Files.newDirectoryStream(Paths.get(file.toURI()), path -> path.toString().endsWith(".owl"))
						.forEach(
								path1 ->
										loadOntologyFromLocation(
												(OWLAPIDataExtractor) savable, path1.toFile().toURI().toString()));
			}
		}
	}

	private void loadOntologyFromLocation(
			OWLAPIDataExtractor dataExtractor, String ontologyLocation) {
		OWLWorkspace workSpace = dataExtractor.getWorkSpace();
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

	@Override
	public void write(Savable savable, File file) {
	}
}
