/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.semanticweb.HermiT.ReasonerFactory;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.OWLEntityRemover;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

@SuppressWarnings("EmptyMethod")
class OwlModelTest {

  private KnowtatorModel model;
  private OWLOntologyManager owlOntologyManager;
  private OWLDataFactory dataFactory;
  private OWLOntology ontology;
  private OWLReasoner reasoner;

  @BeforeEach
  void setup() throws IOException {

    model = TestingHelpers.getLoadedModel();

    owlOntologyManager = model.getOwlOntologyManager();
    dataFactory = owlOntologyManager.getOWLDataFactory();

    ontology = owlOntologyManager.getOntology(IRI.create("http://www.co-ode.org/ontologies/pizza"));

    OWLReasonerFactory reasonerFactory = new ReasonerFactory();
    reasoner = reasonerFactory.createReasoner(Objects.requireNonNull(ontology));
  }

  @Test
  void addOwlClassTest() {
    OWLClass class1 = dataFactory.getOWLClass(IRI.create("X"));
    OWLClass class2 = model.getOwlClassById("IceCream").get();
    OWLAxiom axiom = dataFactory.getOWLSubClassOfAxiom(class1, class2);

    owlOntologyManager.addAxiom(ontology, axiom);
    TestingHelpers.checkDefaultCollectionValues(model);
    owlOntologyManager.removeAxiom(ontology, axiom);
    TestingHelpers.checkDefaultCollectionValues(model);
  }

  @Test
  void changeOwlClassIriTest() {
    OWLEntityRenamer renamer =
        new OWLEntityRenamer(owlOntologyManager, Collections.singleton(ontology));
    OWLClass class2 = model.getOwlClassById("Pizza").get();
    assert ontology.containsClassInSignature(class2.getIRI());

    Map<OWLEntity, IRI> entityToIriMap = new HashMap<>();
    entityToIriMap.put(class2, IRI.create(class2.getIRI().getNamespace(), "BetterPizza"));

    TestingHelpers.testOwlAction(
        model,
        renamer.changeIRI(entityToIriMap),
        TestingHelpers.defaultExpectedTextSources,
        TestingHelpers.defaultExpectedConceptAnnotations,
        TestingHelpers.defaultExpectedStructureAnnotations,
        TestingHelpers.defaultExpectedSpans,
        TestingHelpers.defaultExpectedGraphSpaces,
        TestingHelpers.defaultExpectedStructureGraphSpaces,
        TestingHelpers.defaultExpectedProfiles,
        TestingHelpers.defaultExpectedHighlighters,
        TestingHelpers.defaultExpectedAnnotationNodes,
        TestingHelpers.defaultExpectedStructureAnnotationNodes,
        TestingHelpers.defaultExpectedTriples,
        TestingHelpers.defaultExpectedStructureTriples);

    ConceptAnnotation conceptAnnotation =
        model.getSelectedTextSource().get().getSelectedAnnotation().get();

    assert !ontology.containsClassInSignature(class2.getIRI());
    assert ontology.containsClassInSignature(conceptAnnotation.getOwlClass().getIRI());
    assert conceptAnnotation.getOwlClass().equals(model.getOwlClassById("BetterPizza").get());
  }

  @Test
  void changeOwlObjectPropertyIriTest() {
    OWLEntityRenamer renamer =
        new OWLEntityRenamer(owlOntologyManager, Collections.singleton(ontology));
    OWLObjectProperty property = model.getOwlObjectPropertyById("hasBase").get();
    assert ontology.containsObjectPropertyInSignature(property.getIRI());

    Map<OWLEntity, IRI> entityToIriMap = new HashMap<>();
    entityToIriMap.put(property, IRI.create(property.getIRI().getNamespace(), "betterHasBass"));

    TestingHelpers.testOwlAction(
        model,
        renamer.changeIRI(entityToIriMap),
        TestingHelpers.defaultExpectedTextSources,
        TestingHelpers.defaultExpectedConceptAnnotations,
        TestingHelpers.defaultExpectedStructureAnnotations,
        TestingHelpers.defaultExpectedSpans,
        TestingHelpers.defaultExpectedGraphSpaces,
        TestingHelpers.defaultExpectedStructureGraphSpaces,
        TestingHelpers.defaultExpectedProfiles,
        TestingHelpers.defaultExpectedHighlighters,
        TestingHelpers.defaultExpectedAnnotationNodes,
        TestingHelpers.defaultExpectedStructureAnnotationNodes,
        TestingHelpers.defaultExpectedTriples,
        TestingHelpers.defaultExpectedStructureTriples);

    GraphSpace graphSpace = model.getSelectedTextSource().get().getSelectedGraphSpace().get();
    RelationAnnotation relationAnnotation =
        (RelationAnnotation) graphSpace.getChildEdges(graphSpace.getDefaultParent())[0];

    assert !ontology.containsObjectPropertyInSignature(property.getIRI());
    assert ontology.containsObjectPropertyInSignature(relationAnnotation.getProperty().getIRI());
    assert relationAnnotation
        .getProperty()
        .equals(model.getOwlObjectPropertyById("betterHasBass").get());
  }

  @Test
  void removeOwlClassTest() {
    OWLClass class2 = model.getOwlClassById("Pizza").get();
    assert ontology.containsClassInSignature(class2.getIRI());
    OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
    class2.accept(remover);
    TestingHelpers.testOwlAction(
        model,
        remover.getChanges(),
        TestingHelpers.defaultExpectedTextSources,
        TestingHelpers.defaultExpectedConceptAnnotations - 2,
        TestingHelpers.defaultExpectedStructureAnnotations,
        TestingHelpers.defaultExpectedSpans - 2,
        TestingHelpers.defaultExpectedGraphSpaces,
        TestingHelpers.defaultExpectedStructureGraphSpaces,
        TestingHelpers.defaultExpectedProfiles,
        TestingHelpers.defaultExpectedHighlighters,
        TestingHelpers.defaultExpectedAnnotationNodes - 3,
        TestingHelpers.defaultExpectedStructureAnnotationNodes,
        TestingHelpers.defaultExpectedTriples - 2,
        TestingHelpers.defaultExpectedStructureTriples);
    assert !ontology.containsClassInSignature(class2.getIRI());
  }

  @Test
  void removeObjectPropertyTest() {
    OWLObjectProperty owlObjectProperty = model.getOwlObjectPropertyById("hasBase").get();
    assert ontology.containsObjectPropertyInSignature(owlObjectProperty.getIRI());
    OWLEntityRemover remover = new OWLEntityRemover(Collections.singleton(ontology));
    owlObjectProperty.accept(remover);
    TestingHelpers.testOwlAction(
        model,
        remover.getChanges(),
        TestingHelpers.defaultExpectedTextSources,
        TestingHelpers.defaultExpectedConceptAnnotations,
        TestingHelpers.defaultExpectedStructureAnnotations,
        TestingHelpers.defaultExpectedSpans,
        TestingHelpers.defaultExpectedGraphSpaces,
        TestingHelpers.defaultExpectedStructureGraphSpaces,
        TestingHelpers.defaultExpectedProfiles,
        TestingHelpers.defaultExpectedHighlighters,
        TestingHelpers.defaultExpectedAnnotationNodes,
        TestingHelpers.defaultExpectedStructureAnnotationNodes,
        TestingHelpers.defaultExpectedTriples - 2,
        TestingHelpers.defaultExpectedStructureTriples);
    assert !ontology.containsObjectPropertyInSignature(owlObjectProperty.getIRI());
  }

  @Test
  void moveOwlClassTest() {
    OWLClass class1 = model.getOwlClassById("Food").get();
    OWLClass class2 = model.getOwlClassById("Pizza").get();
    OWLClass class3 = model.getOwlClassById("Thing").get();

    assert isSubClassTest(class1, class2);

    Set<OWLSubClassOfAxiom> subclassAxioms = ontology.getSubClassAxiomsForSubClass(class2);
    OWLAxiom axiom2 = dataFactory.getOWLSubClassOfAxiom(class2, class3);

    List<OWLOntologyChange> changes = new ArrayList<>();
    subclassAxioms.forEach(
        owlSubClassOfAxiom -> changes.add(new RemoveAxiom(ontology, owlSubClassOfAxiom)));
    changes.add(new AddAxiom(ontology, axiom2));

    TestingHelpers.testOwlAction(
        model,
        changes,
        TestingHelpers.defaultExpectedTextSources,
        TestingHelpers.defaultExpectedConceptAnnotations,
        TestingHelpers.defaultExpectedStructureAnnotations,
        TestingHelpers.defaultExpectedSpans,
        TestingHelpers.defaultExpectedGraphSpaces,
        TestingHelpers.defaultExpectedStructureGraphSpaces,
        TestingHelpers.defaultExpectedProfiles,
        TestingHelpers.defaultExpectedHighlighters,
        TestingHelpers.defaultExpectedAnnotationNodes,
        TestingHelpers.defaultExpectedStructureAnnotationNodes,
        TestingHelpers.defaultExpectedTriples,
        TestingHelpers.defaultExpectedStructureTriples);

    assert !isSubClassTest(class1, class2);
    assert isSubClassTest(class3, class2);

    ConceptAnnotation conceptAnnotation =
        model.getSelectedTextSource().get().getSelectedAnnotation().get();
    assert conceptAnnotation.getOwlClass().equals(class2);
  }

  boolean isSubClassTest(OWLClass potentialSuperClass, OWLClass owlClass) {
    reasoner.flush();
    Set<OWLClass> subclasses = reasoner.getSubClasses(potentialSuperClass, false).getFlattened();
    return subclasses.contains(owlClass);
  }

  boolean isSubObjectPropertyTest(
      OWLObjectProperty potentialSuperObjectProperty, OWLObjectProperty objectProperty) {
    reasoner.flush();
    List<OWLObjectProperty> subclasses =
        reasoner.getSubObjectProperties(potentialSuperObjectProperty, true).getFlattened().stream()
            .filter(owlObjectPropertyExpression -> !owlObjectPropertyExpression.isAnonymous())
            .map(OWLObjectPropertyExpression::asOWLObjectProperty)
            .collect(Collectors.toList());

    return subclasses.contains(objectProperty);
  }

  @Test
  void moveOwlObjectPropertyTest() {
    OWLObjectProperty property1 = model.getOwlObjectPropertyById("hasIngredient").get();
    OWLObjectProperty property2 = model.getOwlObjectPropertyById("hasBase").get();
    OWLObjectProperty property3 = model.getOwlObjectPropertyById("isIngredientOf").get();

    assert isSubObjectPropertyTest(property1, property2);

    Set<OWLSubObjectPropertyOfAxiom> subclassAxioms =
        ontology.getObjectSubPropertyAxiomsForSubProperty(property2);
    OWLAxiom axiom2 = dataFactory.getOWLSubObjectPropertyOfAxiom(property2, property3);

    List<OWLOntologyChange> changes = new ArrayList<>();
    subclassAxioms.forEach(
        owlSubClassOfAxiom -> changes.add(new RemoveAxiom(ontology, owlSubClassOfAxiom)));
    changes.add(new AddAxiom(ontology, axiom2));

    TestingHelpers.testOwlAction(
        model,
        changes,
        TestingHelpers.defaultExpectedTextSources,
        TestingHelpers.defaultExpectedConceptAnnotations,
        TestingHelpers.defaultExpectedStructureAnnotations,
        TestingHelpers.defaultExpectedSpans,
        TestingHelpers.defaultExpectedGraphSpaces,
        TestingHelpers.defaultExpectedStructureGraphSpaces,
        TestingHelpers.defaultExpectedProfiles,
        TestingHelpers.defaultExpectedHighlighters,
        TestingHelpers.defaultExpectedAnnotationNodes,
        TestingHelpers.defaultExpectedStructureAnnotationNodes,
        TestingHelpers.defaultExpectedTriples,
        TestingHelpers.defaultExpectedStructureTriples);

    assert isSubObjectPropertyTest(property1, property2);
    assert isSubObjectPropertyTest(property3, property2);

    GraphSpace graphSpace = model.getSelectedTextSource().get().getSelectedGraphSpace().get();
    RelationAnnotation relationAnnotation =
        (RelationAnnotation) graphSpace.getChildEdges(graphSpace.getDefaultParent())[0];
    assert relationAnnotation.getProperty().equals(property2);
  }

  @Test
  void moveSuperClassTest() {}

  @Test
  void moveSuperObjectPropertyTest() {}
}
