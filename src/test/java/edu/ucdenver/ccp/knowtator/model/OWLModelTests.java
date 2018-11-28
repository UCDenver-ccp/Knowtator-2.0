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

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.text.graph.RelationAnnotation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.util.*;
import java.util.stream.Collectors;

public class OWLModelTests {

	private static KnowtatorModel controller;
	private static OWLOntologyManager owlOntologyManager;
	private static OWLDataFactory dataFactory;
	private static OWLOntology ontology;
	private static OWLReasoner reasoner;

	@BeforeEach
	public void setup() {

		controller = TestingHelpers.getLoadedController();
		OWLReasonerFactory reasonerFactory = new ReasonerFactory();
		owlOntologyManager = controller.getOwlOntologyManager().get();
		dataFactory = owlOntologyManager.getOWLDataFactory();

		ontology = owlOntologyManager.getOntology(IRI.create("http://www.co-ode.org/ontologies/pizza"));
		reasoner = reasonerFactory.createReasoner(Objects.requireNonNull(ontology));
	}

	@Test
	public void addOWLClassTest() {
		OWLClass class1 = dataFactory.getOWLClass(IRI.create("X"));
		OWLClass class2 = controller.getOWLClassByID("IceCream").get();
		OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(class1, class2);

		owlOntologyManager.addAxiom(ontology, axiom);
		TestingHelpers.checkDefaultCollectionValues(controller);
		owlOntologyManager.removeAxiom(ontology, axiom);
		TestingHelpers.checkDefaultCollectionValues(controller);
	}

	@Test
	public void changeOWLClassIRITest() {
		OWLEntityRenamer renamer = new OWLEntityRenamer(owlOntologyManager, Collections.singleton(ontology));
		OWLClass class2 = controller.getOWLClassByID("Pizza").get();
		assert ontology.containsClassInSignature(class2.getIRI());

		Map<OWLEntity, IRI> entityToIRIMap = new HashMap<>();
		entityToIRIMap.put(class2, IRI.create(class2.getIRI().getNamespace(), "BetterPizza"));

		TestingHelpers.testOWLAction(controller,
				renamer.changeIRI(entityToIRIMap),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);

		ConceptAnnotation conceptAnnotation = controller.getTextSource().get().getConceptAnnotationCollection().getSelection().get();

		assert !ontology.containsClassInSignature(class2.getIRI());
		assert ontology.containsClassInSignature(conceptAnnotation.getOwlClass().get().getIRI());
		assert conceptAnnotation.getOwlClass().equals(controller.getOWLClassByID("BetterPizza"));
	}

	@Test
	public void changeOWLObjectPropertyIRITest() {
		OWLEntityRenamer renamer = new OWLEntityRenamer(owlOntologyManager, Collections.singleton(ontology));
		OWLObjectProperty property = controller.getOWLObjectPropertyByID("hasBase").get();
		assert ontology.containsObjectPropertyInSignature(property.getIRI());

		Map<OWLEntity, IRI> entityToIRIMap = new HashMap<>();
		entityToIRIMap.put(property, IRI.create(property.getIRI().getNamespace(), "betterHasBass"));

		TestingHelpers.testOWLAction(controller,
				renamer.changeIRI(entityToIRIMap),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);

		GraphSpace graphSpace = controller.getTextSource().get().getGraphSpaceCollection().getSelection().get();
		RelationAnnotation relationAnnotation = (RelationAnnotation) graphSpace.getChildEdges(graphSpace.getDefaultParent())[0];

		assert !ontology.containsObjectPropertyInSignature(property.getIRI());
		assert ontology.containsObjectPropertyInSignature(relationAnnotation.getProperty().getIRI());
		assert relationAnnotation.getProperty().equals(controller.getOWLObjectPropertyByID("betterHasBass").get());
	}

	@Test
	public void removeOWLClassTest() {
		OWLClass class2 = controller.getOWLClassByID("Pizza").get();
		assert ontology.containsClassInSignature(class2.getIRI());
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
		class2.accept(remover);
		TestingHelpers.testOWLAction(controller,
				remover.getChanges(),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations - 2,
				TestingHelpers.defaultExpectedSpans - 2,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes - 3,
				TestingHelpers.defaultExpectedTriples - 2);
		assert !ontology.containsClassInSignature(class2.getIRI());
	}

	@Test
	public void removeObjectPropertyTest() {
		OWLObjectProperty owlObjectProperty = controller.getOWLObjectPropertyByID("hasBase").get();
		assert ontology.containsObjectPropertyInSignature(owlObjectProperty.getIRI());
		OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
		owlObjectProperty.accept(remover);
		TestingHelpers.testOWLAction(controller,
				remover.getChanges(),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples - 2);
		assert !ontology.containsObjectPropertyInSignature(owlObjectProperty.getIRI());
	}

	@Test
	public void moveOWLClassTest() {
		OWLClass class1 = controller.getOWLClassByID("Food").get();
		OWLClass class2 = controller.getOWLClassByID("Pizza").get();
		OWLClass class3 = controller.getOWLClassByID("Thing").get();

		assert isSubClassTest(class1, class2);

		Set<OWLSubClassOfAxiom> subclassAxioms = ontology.getSubClassAxiomsForSubClass(class2);
		OWLAxiom axiom2 = dataFactory.getOWLSubClassOfAxiom(class2, class3);

		List<OWLOntologyChange> changes = new ArrayList<>();
		subclassAxioms.forEach(owlSubClassOfAxiom -> changes.add(new RemoveAxiom(ontology, owlSubClassOfAxiom)));
		changes.add(new AddAxiom(ontology, axiom2));

		TestingHelpers.testOWLAction(controller,
				changes,
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);


		assert !isSubClassTest(class1, class2);
		assert isSubClassTest(class3, class2);

		ConceptAnnotation conceptAnnotation = controller.getTextSource().get().getConceptAnnotationCollection().getSelection().get();
		assert conceptAnnotation.getOwlClass().get().equals(class2);
	}

	public boolean isSubClassTest(OWLClass potentialSuperClass, OWLClass owlClass) {
		reasoner.flush();
		Set<OWLClass> subclasses = reasoner.getSubClasses(potentialSuperClass, false).getFlattened();
		return subclasses.contains(owlClass);
	}

	public boolean isSubObjectPropertyTest(OWLObjectProperty potentialSuperObjectProperty, OWLObjectProperty objectProperty) {
		reasoner.flush();
		List<OWLObjectProperty> subclasses = reasoner.getSubObjectProperties(potentialSuperObjectProperty, true).getFlattened().stream()
				.filter(owlObjectPropertyExpression -> !owlObjectPropertyExpression.isAnonymous())
				.map(OWLObjectPropertyExpression::asOWLObjectProperty).collect(Collectors.toList());

		return subclasses.contains(objectProperty);
	}


	@Test
	public void moveOWLObjectPropertyTest() {
		OWLObjectProperty property1 = controller.getOWLObjectPropertyByID("hasIngredient").get();
		OWLObjectProperty property2 = controller.getOWLObjectPropertyByID("hasBase").get();
		OWLObjectProperty property3 = controller.getOWLObjectPropertyByID("isIngredientOf").get();

		assert isSubObjectPropertyTest(property1, property2);

		Set<OWLSubObjectPropertyOfAxiom> subclassAxioms = ontology.getObjectSubPropertyAxiomsForSubProperty(property2);
		OWLAxiom axiom2 = dataFactory.getOWLSubObjectPropertyOfAxiom(property2, property3);

		List<OWLOntologyChange> changes = new ArrayList<>();
		subclassAxioms.forEach(owlSubClassOfAxiom -> changes.add(new RemoveAxiom(ontology, owlSubClassOfAxiom)));
		changes.add(new AddAxiom(ontology, axiom2));

		TestingHelpers.testOWLAction(controller,
				changes,
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);


		assert isSubObjectPropertyTest(property1, property2);
		assert isSubObjectPropertyTest(property3, property2);

		GraphSpace graphSpace = controller.getTextSource().get().getGraphSpaceCollection().getSelection().get();
		RelationAnnotation relationAnnotation = (RelationAnnotation) graphSpace.getChildEdges(graphSpace.getDefaultParent())[0];
		assert relationAnnotation.getProperty().equals(property2);
	}

	@Test
	public void moveSuperClassTest() {

	}

	@Test
	public void moveSuperObjectPropertyTest() {

	}

}