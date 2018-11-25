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
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.*;

class OWLModelTest {

	private static final KnowtatorController controller = TestingHelpers.getLoadedController();
	private static OWLOntologyManager owlOntologyManager;
	private static OWLDataFactory dataFactory;

	static {
		try {
			owlOntologyManager = controller.getOWLModel().getOwlOntologyManager();
			dataFactory = owlOntologyManager.getOWLDataFactory();
		} catch (OWLModel.OWLOntologyManagerNotSetException e) {
			e.printStackTrace();
		}
	}

	@Test
	void addOWLClass() throws OWLModel.OWLOntologyManagerNotSetException {
		OWLOntology ontology = owlOntologyManager.getOntology(IRI.create("http://www.co-ode.org/ontologies/pizza"));
		assert ontology != null;

		OWLClass class1 = dataFactory.getOWLClass(IRI.create("X"));
		OWLClass class2 = dataFactory.getOWLClass(IRI.create("IceCream"));
		OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(class1, class2);

		controller.getOWLModel().getOwlOntologyManager().addAxiom(ontology, axiom);
		TestingHelpers.checkDefaultCollectionValues(controller);
	}

	@Test
	void reassignOWLClass() {

	}

	@Test
	void reassignOWLObjectProperty() {

	}

	@Test
	void changeOWLClassIRI() {

	}

	@Test
	void changeOWLObjectPropertyIRI() {

	}

	@Test
	void removeOWLClass() {

	}

	@Test
	void removeObjectProperty() {

	}

	@Test
	void moveOWLClass() {

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