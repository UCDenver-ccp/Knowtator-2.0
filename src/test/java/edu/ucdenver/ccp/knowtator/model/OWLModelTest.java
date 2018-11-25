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

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.junit.jupiter.api.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import java.util.*;

class OWLModelTest {

	private static final KnowtatorController controller = TestingHelpers.getLoadedController();
	private static OWLOntologyManager owlOntologyManager;
	private static OWLDataFactory dataFactory;
	private OWLReasonerFactory reasonerFactory = new ReasonerFactory();
	static {
		try {
			owlOntologyManager = controller.getOWLModel().getOwlOntologyManager();
			dataFactory = owlOntologyManager.getOWLDataFactory();
		} catch (OWLModel.OWLOntologyManagerNotSetException e) {
			e.printStackTrace();
		}
	}

	private OWLOntology ontology = owlOntologyManager.getOntology(IRI.create("http://www.co-ode.org/ontologies/pizza"));
	private OWLReasoner reasoner = reasonerFactory.createReasoner(Objects.requireNonNull(ontology));

	@Test
	void addOWLClass() {
		OWLClass class1 = dataFactory.getOWLClass(IRI.create("X"));
		OWLClass class2 = controller.getOWLModel().getOWLClassByID("IceCream");
		OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(class1, class2);

		owlOntologyManager.addAxiom(ontology, axiom);
		TestingHelpers.checkDefaultCollectionValues(controller);
		owlOntologyManager.removeAxiom(ontology, axiom);
		TestingHelpers.checkDefaultCollectionValues(controller);
	}

	@Test
	void changeOWLClassIRI() throws NoSelectionException, OWLModel.OWLOntologyManagerNotSetException {
		OWLEntityRenamer renamer = new OWLEntityRenamer(owlOntologyManager, Collections.singleton(ontology));
		OWLClass class2 = controller.getOWLModel().getOWLClassByID("Pizza");
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

		ConceptAnnotation conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();

		assert !ontology.containsClassInSignature(class2.getIRI());
		assert ontology.containsClassInSignature(conceptAnnotation.getOwlClass().getIRI());
		assert conceptAnnotation.getOwlClass().equals(controller.getOWLModel().getOWLClassByID("BetterPizza"));
	}

	@Test
	void changeOWLObjectPropertyIRI() {

	}

	@Test
	void removeOWLClass() throws OWLModel.OWLOntologyManagerNotSetException {
		OWLClass class2 = controller.getOWLModel().getOWLClassByID("Pizza");
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
	void removeObjectProperty() throws OWLModel.OWLOntologyManagerNotSetException {
		OWLObjectProperty owlObjectProperty = controller.getOWLModel().getOWLObjectPropertyByID("isIngredientOf");
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
	void moveOWLClass() throws OWLModel.OWLOntologyManagerNotSetException, NoSelectionException {
		OWLClass class1 = controller.getOWLModel().getOWLClassByID("Food");
		OWLClass class2 = controller.getOWLModel().getOWLClassByID("Pizza");
		OWLClass class3 = controller.getOWLModel().getOWLClassByID("Thing");

		assert isSubclass(class1, class2);

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


		assert !isSubclass(class1, class2);
		assert isSubclass(class3, class2);

		ConceptAnnotation conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();
		assert conceptAnnotation.getOwlClass().equals(class2);
	}

	boolean isSubclass(OWLClass potentialSuperClass, OWLClass owlClass) {
		reasoner.flush();
		Set<OWLClass> subclasses = reasoner.getSubClasses(potentialSuperClass, false).getFlattened();
		return subclasses.contains(owlClass);
	}


	@Test
	void moveOWLObjectProperty() {

	}

	@Test
	void moveSuperClass() {

	}

	@Test
	void moveSuperObjectProperty() {

	}

}