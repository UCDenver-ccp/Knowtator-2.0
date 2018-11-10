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

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.search.SearchDialogPanel;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class OWLModel implements Serializable, BaseKnowtatorManager, DebugListener {
    @SuppressWarnings("unused")
    private static final Logger log = LogManager.getLogger(OWLModel.class);

    private OWLWorkspace owlWorkSpace;
    private File ontologiesLocation;
    private List<IRI> iris;
    private final KnowtatorController controller;
    private OWLClass testClass;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private OWLObjectProperty testProperty;

    public OWLModel(KnowtatorController controller) {
        this.controller = controller;
        controller.addDebugListener(this);
        iris = null;

        new TextBoundModelListener(controller) {
            @Override
            public void respondToConceptAnnotationModification() {

            }

            @Override
            public void respondToSpanModification() {

            }

            @Override
            public void respondToGraphSpaceModification() {

            }

            @Override
            public void respondToGraphSpaceCollectionFirstAdded() {

            }

            @Override
            public void respondToGraphSpaceCollectionEmptied() {

            }

            @Override
            public void respondToGraphSpaceRemoved() {

            }

            @Override
            public void respondToGraphSpaceAdded() {

            }

            @Override
            public void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {

            }

            @Override
            public void respondToConceptAnnotationCollectionEmptied() {

            }

            @Override
            public void respondToConceptAnnotationRemoved() {

            }

            @Override
            public void respondToConceptAnnotationAdded() {

            }

            @Override
            public void respondToConceptAnnotationCollectionFirstAdded() {

            }

            @Override
            public void respondToSpanCollectionFirstAdded() {

            }

            @Override
            public void respondToSpanCollectionEmptied() {

            }

            @Override
            public void respondToSpanRemoved() {

            }

            @Override
            public void respondToSpanAdded() {

            }

            @Override
            public void respondToSpanSelection(SelectionEvent<Span> event) {

            }

            @Override
            public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
                if (event.getNew() != null && event.getNew().getOwlClass() != null) {
                    setSelectedOWLEntity(event.getNew().getOwlClass());
                }
            }

            @Override
            public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {

            }

            @Override
            public void respondToTextSourceAdded() {

            }

            @Override
            public void respondToTextSourceRemoved() {

            }

            @Override
            public void respondToTextSourceCollectionEmptied() {

            }

            @Override
            public void respondToTextSourceCollectionFirstAdded() {

            }
        };
    }

    public OWLObjectProperty getSelectedOWLObjectProperty() throws NoSelectedOWLPropertyException {
        if (controller.isDebug()) {
            return testProperty;
        } else {
            try {
                OWLEntity owlEntity = getWorkSpace().getOWLSelectionModel().getSelectedEntity();
                if (owlEntity instanceof OWLObjectProperty) {
                    return (OWLObjectProperty) owlEntity;
                } else {
                    throw new NoSelectedOWLPropertyException();
                }
            } catch (OWLWorkSpaceNotSetException e) {
                throw new NoSelectedOWLPropertyException();
            }
        }
    }

    public OWLClass getSelectedOWLClass() throws NoSelectedOWLClassException {
        if (controller.isDebug()) {
            return testClass;
        }
        try {
            OWLEntity owlEntity = getWorkSpace().getOWLSelectionModel().getSelectedEntity();
            if (owlEntity instanceof OWLClass) {
                return (OWLClass) owlEntity;
            } else {
                throw new NoSelectedOWLClassException();
            }
        } catch (OWLWorkSpaceNotSetException e) {
            throw new NoSelectedOWLClassException();
        }
    }

    public OWLClass getOWLClassByID(String classID) {
        try {
            return getWorkSpace().getOWLModelManager().getOWLEntityFinder().getOWLClass(classID);
        } catch (OWLWorkSpaceNotSetException e) {
            return null;
        }
    }

    public OWLObjectProperty getOWLObjectPropertyByID(String propertyID) {
        try {
            return getWorkSpace()
                    .getOWLModelManager()
                    .getOWLEntityFinder()
                    .getOWLObjectProperty(propertyID);
        } catch (OWLWorkSpaceNotSetException e) {
            return null;
        }
    }

    public void setRenderRDFSLabel() {
        try {
            if (iris == null) {
                IRI labelIRI = getWorkSpace().getOWLModelManager().getOWLDataFactory().getRDFSLabel().getIRI();
                iris = OWLRendererPreferences.getInstance().getAnnotationIRIs();
                OWLRendererPreferences.getInstance().setAnnotations(Collections.singletonList(labelIRI));

                getWorkSpace().getOWLModelManager().refreshRenderer();
            }
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

    public void resetRenderRDFS() {
        try {
            if (iris != null) {
                OWLRendererPreferences.getInstance().setAnnotations(iris);
                getWorkSpace().getOWLModelManager().refreshRenderer();
                iris = null;
            }
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }


    public Set<OWLClass> getDescendants(OWLClass cls) {
        try {
            return getWorkSpace()
                    .getOWLModelManager()
                    .getOWLHierarchyManager()
                    .getOWLClassHierarchyProvider()
                    .getDescendants(cls);
        } catch (OWLWorkSpaceNotSetException e) {
            return new HashSet<>();
        }
    }

    public String getOWLEntityRendering(OWLEntity owlEntity) {
        try {
            if (owlEntity == null) {
                return null;
            }
            return getWorkSpace().getOWLModelManager().getOWLEntityRenderer().render(owlEntity);
        } catch (OWLWorkSpaceNotSetException e) {
            return null;
        }
    }

    public void setOwlWorkSpace(OWLWorkspace owlWorkSpace) {
        this.owlWorkSpace = owlWorkSpace;
    }

    public void searchForString(String stringToSearch) {
        try {
            JDialog dialog = SearchDialogPanel.createDialog(null, getWorkSpace().getOWLEditorKit());
            Arrays.stream(dialog.getContentPane().getComponents()).forEach(component -> {
                log.warn(component);
                if (component instanceof AugmentedJTextField) {
                    ((AugmentedJTextField) component).setText(stringToSearch);
                }
            });

            dialog.setVisible(true);
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
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
        testClass = factory.getOWLClass(iri);

        iri = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#HasCountryOfOrigin");
        testProperty = factory.getOWLObjectProperty(iri);
    }


    @Override
    public void dispose() {
        iris = null;
    }

    @Override
    public File getSaveLocation() {
        return ontologiesLocation;
    }

    @Override
    public void setSaveLocation(File newSaveLocation) throws IOException {
        this.ontologiesLocation = new File(newSaveLocation, "Ontologies");
        Files.createDirectories(ontologiesLocation.toPath());
    }

    @Override
    public void finishLoad() {

    }


    @Override
    public void load() {
        if (getSaveLocation() != null) {
            log.warn("Loading ontologies");
            try {
                File file = getSaveLocation();
                if (file.isDirectory()) {
                    for (Path path1 :
                            Files.newDirectoryStream(
                                    Paths.get(file.toURI()), path -> path.toString().endsWith(".owl"))) {
                        String ontologyLocation = path1.toFile().toURI().toString();
                        List<String> ontologies =
                                getWorkSpace()
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
                                        getWorkSpace()
                                                .getOWLModelManager()
                                                .getOWLOntologyManager()
                                                .loadOntology((IRI.create(ontologyLocation)));
                                getWorkSpace().getOWLModelManager().setActiveOntology(newOntology);
                            } catch (OWLOntologyCreationException e) {
                                log.warn("Knowtator: OWLModel: Ontology already loaded");
                            }
                        }
                    }
                }
            } catch (IOException | OWLWorkSpaceNotSetException e) {
                log.warn("Could not load ontologies");
            }
        }
    }

    @Override
    public void save() {
        try {
            getWorkSpace().getOWLModelManager().save();
        } catch (OWLWorkSpaceNotSetException | OWLOntologyStorageException ignored) {

        }
    }

    public void addOntologyChangeListener(OWLOntologyChangeListener listener) {
        try {
            getWorkSpace().getOWLModelManager().addOntologyChangeListener(listener);
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

    public void removeOntologyChangeListener(OWLOntologyChangeListener listener) {
        try {
            getWorkSpace().getOWLModelManager().removeOntologyChangeListener(listener);
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

    public void removeOWLModelManagerListener(OWLModelManagerListener listener) {
        try {
            getWorkSpace().getOWLModelManager().removeListener(listener);
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

    public void addOWLModelManagerListener(OWLModelManagerListener listener) {
        try {
            getWorkSpace().getOWLModelManager().addListener(listener);
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

    private OWLWorkspace getWorkSpace() throws OWLWorkSpaceNotSetException {
        if (owlWorkSpace == null) {
            throw new OWLWorkSpaceNotSetException();
        } else {
            return owlWorkSpace;
        }
    }

    public boolean isWorkSpaceSet() {
        return owlWorkSpace != null;
    }

    public static void processOntologyChanges(@Nonnull List<? extends OWLOntologyChange> changes, OWLEntityCollector addedCollector, OWLEntityCollector removedCollector) {
        for (OWLOntologyChange chg : changes) {
            if (chg.isAxiomChange()) {
                OWLAxiomChange axChg = (OWLAxiomChange) chg;
                if (axChg.getAxiom().getAxiomType() == AxiomType.DECLARATION) {
                    if (axChg instanceof AddAxiom) {
                        axChg.getAxiom().accept(addedCollector);
                    } else {
                        axChg.getAxiom().accept(removedCollector);
                    }
                }
            }
        }
    }

    public void setSelectedOWLEntity(OWLEntity owlEntity) {
        try {
            getWorkSpace().getOWLSelectionModel().setSelectedEntity(owlEntity);
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

    public boolean renderChangeInProgress() {
        return iris != null;
    }

    @Override
    public void reset() {

    }

    public Comparator<OWLObject> getOWLObjectComparator() {
        try {
            return getWorkSpace().getOWLModelManager().getOWLObjectComparator();
        } catch (OWLWorkSpaceNotSetException e) {
            return Comparator.comparing(Object::toString);
        }
    }

    private class OWLWorkSpaceNotSetException extends Exception {
    }
}



