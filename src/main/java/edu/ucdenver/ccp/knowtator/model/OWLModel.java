/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.search.SearchDialogPanel;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.swing.*;
import javax.swing.undo.UndoManager;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public abstract class OWLModel extends UndoManager implements Serializable, BaseKnowtatorManager {
	@SuppressWarnings("unused")
	private static final Logger log = LogManager.getLogger(OWLModel.class);

	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<OWLWorkspace> owlWorkSpace;

	private List<IRI> annotationIRIs;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<OWLClass> testClass;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<OWLObjectProperty> testProperty;
	@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
	private Optional<OWLOntologyManager> owlOntologyManager;
	File ontologiesLocation;

	public File getOntologiesLocation() {
		return ontologiesLocation;
	}

	OWLModel() {
		owlWorkSpace = Optional.empty();
		owlOntologyManager = Optional.empty();
		testClass = Optional.empty();
		testProperty = Optional.empty();
		annotationIRIs = null;
	}

	public Optional<OWLClass> getOWLClassByID(@Nonnull String classID) {
		if (owlWorkSpace.isPresent()) {
			return owlWorkSpace.map(owlWorkspace -> owlWorkspace.getOWLModelManager().getOWLEntityFinder().getOWLClass(classID));
		} else {
			if (owlOntologyManager.isPresent()) {
				for (OWLOntology ontology : owlOntologyManager.get().getOntologies()) {
					Optional<OWLClass> owlClassOptional = ontology.getClassesInSignature().stream()
							.filter(owlClass -> owlClass.getIRI().getShortForm().equals(classID) ||
									ontology.getAnnotationAssertionAxioms(owlClass.getIRI()).stream()
											.anyMatch(owlAnnotationAssertionAxiom -> owlAnnotationAssertionAxiom
													.getValue().asLiteral().transform(OWLLiteral::getLiteral)
													.transform(label -> label.equals(classID)).or(false)))
							.findFirst();
					if (owlClassOptional.isPresent()) {
						return owlClassOptional;
					}
				}
			}
			return Optional.empty();
		}

	}

	public Map<String, OWLClass> getOWLClassesByIDs(Set<String> classIDs) {
		Map<String, OWLClass> owlClassList = new HashMap<>();

		if (owlWorkSpace.isPresent()) {
			classIDs.forEach(classID -> owlWorkSpace.map(owlWorkspace -> owlWorkspace.getOWLModelManager().getOWLEntityFinder().getOWLClass(classID)).ifPresent(owlClass -> owlClassList.put(classID, owlClass)));
		} else {

			if (owlOntologyManager.isPresent()) {
				for (OWLOntology ontology : owlOntologyManager.get().getOntologies()) {
					for (OWLClass owlClass : ontology.getClassesInSignature()) {
						Set<OWLAnnotationAssertionAxiom> anntotationAssertionAxioms = ontology.getAnnotationAssertionAxioms(owlClass.getIRI());
						List<String> labels = anntotationAssertionAxioms.stream()
								.map(anntotationAssertionAxiom -> anntotationAssertionAxiom.getValue().asLiteral().transform(OWLLiteral::getLiteral))
								.filter(com.google.common.base.Optional::isPresent).map(com.google.common.base.Optional::get).collect(Collectors.toList());
						for (String classID : classIDs) {
							if (owlClass.getIRI().getShortForm().equals(classID) || labels.stream().anyMatch(classID::equals)) {
								owlClassList.put(classID, owlClass);
							}
						}
					}
				}
			}
		}
		return owlClassList;
	}

	public Optional<OWLObjectProperty> getOWLObjectPropertyByID(@Nonnull String propertyID) {
		if (owlWorkSpace.isPresent()) {
			return owlWorkSpace.map(owlWorkSpace -> owlWorkSpace.getOWLModelManager().getOWLEntityFinder().getOWLObjectProperty(propertyID));
		} else {

			if (owlOntologyManager.isPresent()) {
				for (OWLOntology ontology : owlOntologyManager.get().getOntologies()) {
					Optional<OWLObjectProperty> owlObjectPropertyOptional = ontology.getObjectPropertiesInSignature().stream()
							.filter(owlClass -> owlClass.getIRI().getShortForm().equals(propertyID))
							.findFirst();
					if (owlObjectPropertyOptional.isPresent()) {
						return owlObjectPropertyOptional;
					}
				}
			}
			return Optional.empty();
		}
	}


	public Optional<OWLObjectProperty> getSelectedOWLObjectProperty() {
		if (testProperty.isPresent()) {
			return testProperty;
		} else {
			return owlWorkSpace
					.filter(owlWorkspace -> owlWorkspace.getOWLSelectionModel().getSelectedEntity() instanceof OWLObjectProperty)
					.map(owlWorkspace -> (OWLObjectProperty) owlWorkspace.getOWLSelectionModel().getSelectedEntity());
		}
	}

	public Optional<OWLClass> getSelectedOWLClass() {
		if (testClass.isPresent()) {
			return testClass;
		}
		return owlWorkSpace
				.filter(owlWorkspace -> owlWorkspace.getOWLSelectionModel().getSelectedEntity() instanceof OWLClass)
				.map(owlWorkspace -> (OWLClass) owlWorkspace.getOWLSelectionModel().getSelectedEntity());
	}

	public void setRenderRDFSLabel() {
		if (annotationIRIs == null) {
			owlWorkSpace.ifPresent(owlWorkspace -> {
				IRI labelIRI = owlWorkspace.getOWLModelManager().getOWLDataFactory().getRDFSLabel().getIRI();
				annotationIRIs = OWLRendererPreferences.getInstance().getAnnotationIRIs();
				OWLRendererPreferences.getInstance().setAnnotations(Collections.singletonList(labelIRI));

				owlWorkspace.getOWLModelManager().refreshRenderer();
			});

		}
	}

	public void resetRenderRDFS() {
		if (annotationIRIs != null) {
			owlWorkSpace.ifPresent(owlWorkspace -> {
				OWLRendererPreferences.getInstance().setAnnotations(annotationIRIs);
				owlWorkspace.getOWLModelManager().refreshRenderer();
				annotationIRIs = null;
			});
		}
	}


	public Set<OWLClass> getOWLCLassDescendants(OWLClass cls) {
		return owlWorkSpace.map(owlWorkspace -> owlWorkspace
				.getOWLModelManager()
				.getOWLHierarchyManager()
				.getOWLClassHierarchyProvider()
				.getDescendants(cls)).orElse(new HashSet<>());
	}

	public Set<OWLObjectProperty> getOWLObjectPropertyDescendants(OWLObjectProperty owlObjectProperty) {
		return owlWorkSpace.map(owlWorkspace -> owlWorkspace
				.getOWLModelManager()
				.getOWLHierarchyManager()
				.getOWLObjectPropertyHierarchyProvider()
				.getDescendants(owlObjectProperty)).orElse(new HashSet<>());
	}

	public String getOWLEntityRendering(@Nonnull OWLEntity owlEntity) {
		return owlWorkSpace.map(owlWorkspace -> owlWorkspace.getOWLModelManager().getOWLEntityRenderer().render(owlEntity)).orElse(owlEntity.getIRI().getShortForm());
	}

	public void setOwlWorkSpace(OWLWorkspace owlWorkSpace) {
		this.owlWorkSpace = Optional.ofNullable(owlWorkSpace);
	}

	public void searchForString(String stringToSearch) {
		owlWorkSpace.ifPresent(owlWorkspace -> {
			JDialog dialog = SearchDialogPanel.createDialog(null, owlWorkspace.getOWLEditorKit());
			Arrays.stream(dialog.getContentPane().getComponents()).forEach(component -> {
				if (component instanceof AugmentedJTextField) {
					((AugmentedJTextField) component).setText(stringToSearch);
				}
			});

			dialog.setVisible(true);
		});
	}

	public void setDebug() {
		owlOntologyManager = Optional.of(org.semanticweb.owlapi.apibinding.OWLManager.createOWLOntologyManager());

		testClass = owlOntologyManager.map(owlOntologyManager -> owlOntologyManager.getOWLDataFactory()
				.getOWLClass(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#IceCream")));
		testProperty = owlOntologyManager.map(owlOntologyManager -> owlOntologyManager.getOWLDataFactory()
				.getOWLObjectProperty(IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#HasIngredient")));
	}


	@Override
	public void dispose() {
		annotationIRIs = null;
	}

	@Override
	public void load() {
		if (ontologiesLocation != null) {
			log.info("Loading ontologies");

			File file = ontologiesLocation;
			if (file.isDirectory()) {
				try {
					for (Path path1 : Files.newDirectoryStream(Paths.get(file.toURI()), path -> path.toString().endsWith(".owl"))) {
						String ontologyLocation = path1.toFile().toURI().toString();

						if (owlWorkSpace.isPresent()) {
							owlWorkSpace.ifPresent(owlWorkspace -> {
								List<String> ontologies = owlWorkspace.getOWLModelManager().getActiveOntologies().stream().map(ontology -> {
									OWLOntologyID ontID = ontology.getOntologyID();
									//noinspection Guava
									com.google.common.base.Optional<IRI> ontIRI = ontID.getOntologyIRI();
									if (ontIRI.isPresent()) {
										return ontIRI.get().toURI().toString();
									} else {
										return null;
									}
								}).collect(Collectors.toList());
								if (!ontologies.contains(ontologyLocation)) {
									log.info(String.format("Loading ontology: %s", ontologyLocation));
									try {
										OWLOntology newOntology =
												owlWorkspace
														.getOWLModelManager()
														.getOWLOntologyManager()
														.loadOntology((IRI.create(ontologyLocation)));
										owlWorkspace.getOWLModelManager().setActiveOntology(newOntology);
									} catch (OWLOntologyCreationException ignored) {
									}
								}
							});
						} else {

							owlOntologyManager.ifPresent(owlOntologyManager -> {
								try {
									owlOntologyManager.loadOntology(IRI.create(ontologyLocation));
								} catch (OWLOntologyCreationException ignored) {
								}
							});
						}
					}
				} catch (IOException e) {
					log.warn("Could not load ontologies");
				}
			}
		}
	}

	@Override
	public void save() {
		owlWorkSpace.ifPresent(owlWorkSpace -> {
			try {
				owlWorkSpace.getOWLModelManager().save();
			} catch (OWLOntologyStorageException e) {
				e.printStackTrace();
			}
		});
	}

	public void addOntologyChangeListener(OWLOntologyChangeListener listener) {
		owlWorkSpace.ifPresent(owlWorkspace -> owlWorkspace.getOWLModelManager().addOntologyChangeListener(listener));
		owlOntologyManager.ifPresent(owlOntologyManager -> owlOntologyManager.addOntologyChangeListener(listener));
	}

	public void removeOntologyChangeListener(OWLOntologyChangeListener listener) {
		owlWorkSpace.ifPresent(owlWorkspace -> owlWorkspace.getOWLModelManager().removeOntologyChangeListener(listener));
		owlOntologyManager.ifPresent(owlOntologyManager -> owlOntologyManager.removeOntologyChangeListener(listener));
	}

	public void removeOWLModelManagerListener(OWLModelManagerListener listener) {
		owlWorkSpace.ifPresent(owlWorkspace -> owlWorkspace.getOWLModelManager().removeListener(listener));
	}

	public void addOWLModelManagerListener(OWLModelManagerListener listener) {
		owlWorkSpace.ifPresent(owlWorkSpace -> owlWorkSpace.getOWLModelManager().addListener(listener));
	}

	public void setSelectedOWLEntity(OWLEntity owlEntity) {
		owlWorkSpace.ifPresent(owlWorkspace -> owlWorkspace.getOWLSelectionModel().setSelectedEntity(owlEntity));
	}

	boolean renderChangeInProgress() {
		return annotationIRIs != null;
	}

	public Comparator<OWLObject> getOWLObjectComparator() {
		return owlWorkSpace.map(owlWorkspace -> owlWorkspace.getOWLModelManager().getOWLObjectComparator())
				.orElse(Comparator.comparing(Object::toString));
	}

	public Optional<OWLOntologyManager> getOwlOntologyManager() {
		return owlOntologyManager;
	}

	public boolean isWorkSpaceSet() {
		return owlWorkSpace.isPresent();
	}
}



