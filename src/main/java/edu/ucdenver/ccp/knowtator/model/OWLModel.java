package edu.ucdenver.ccp.knowtator.model;

import com.google.common.base.Optional;
import edu.ucdenver.ccp.knowtator.Savable;
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

public class OWLModel implements Serializable, Savable {
    @SuppressWarnings("unused")
    private static final Logger log = LogManager.getLogger(OWLModel.class);

    private OWLWorkspace owlWorkSpace;
    private File ontologiesLocation;
    private List<IRI> iris;

    public OWLEntity getSelectedOWLEntity() {
        try {
            return getWorkSpace().getOWLSelectionModel().getSelectedEntity();
        } catch (OWLWorkSpaceNotSetException e) {
            return null;
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

            IRI labelIRI = getWorkSpace().getOWLModelManager().getOWLDataFactory().getRDFSLabel().getIRI();
            iris = OWLRendererPreferences.getInstance().getAnnotationIRIs();
            OWLRendererPreferences.getInstance().setAnnotations(Collections.singletonList(labelIRI));

            getWorkSpace().getOWLModelManager().refreshRenderer();
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

    public void resetRenderRDFS() {

        try {
            if (getWorkSpace() != null) {
                OWLRendererPreferences.getInstance().setAnnotations(iris);
                getWorkSpace().getOWLModelManager().refreshRenderer();
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
                if (component instanceof AugmentedJTextField) {
                    ((AugmentedJTextField) component).setText(stringToSearch);
                }
            });

            dialog.setVisible(true);
        } catch (OWLWorkSpaceNotSetException ignored) {

        }
    }

//    @Override
//    public void setDebug() {
//        OWLOntologyManager manager = org.semanticweb.owlapi.apibinding.OWLManager.createOWLOntologyManager();
//        //    OWLWorkspace workspace = new OWLWorkspace();
//        //    OWLEditorKitFactory editorKitFactory = new OWLEditorKitFactory();
//        //    OWLEditorKit editorKit = new OWLEditorKit(editorKitFactory);
//        //    workspace.setup(editorKit);
//        //    workspace.initialise();
//        OWLDataFactory factory = manager.getOWLDataFactory();
//
//        IRI iri = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#DomainConcept");
//        OWLEntity testClass = factory.getOWLClass(iri);
////        setSelectedOWLEntity(testClass);
//
//        iri = IRI.create("http://www.co-ode.org/ontologies/pizza/pizza.owl#HasCountryOfOrigin");
//        OWLObjectProperty objectProperty = factory.getOWLObjectProperty(iri);
//    }


    public void dispose() {
        setRenderRDFSLabel();
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

    private class OWLWorkSpaceNotSetException extends Exception {
    }
}



