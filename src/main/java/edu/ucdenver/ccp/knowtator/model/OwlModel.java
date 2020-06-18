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

import com.mxgraph.view.mxGraph;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.RelationAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.swing.JDialog;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.owl.model.OWLWorkspace;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.OWLSelectionModel;
import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.prefix.OWLEntityPrefixedNameRenderer;
import org.protege.editor.owl.ui.renderer.OWLEntityRendererImpl;
import org.protege.editor.owl.ui.renderer.OWLRendererPreferences;
import org.protege.editor.owl.ui.renderer.plugin.RendererPlugin;
import org.protege.editor.owl.ui.search.SearchDialogPanel;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.HasAnnotationPropertiesInSignature;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLNamedObject;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

/**
 * The type Owl model.
 */
public abstract class OwlModel extends BaseModel implements Serializable {
  @SuppressWarnings("unused")
  private static final Logger log = LogManager.getLogger(OwlModel.class);

  private final OWLOntologyManager owlOntologyManager;

  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  private final Optional<OWLWorkspace> owlWorkSpace;
  private List<IRI> iris;

  /**
   * Instantiates a new Owl model.
   *
   * @param projectLocation the project location
   * @param owlWorkspace    the owl workspace
   * @throws IOException the io exception
   */
  OwlModel(File projectLocation, OWLWorkspace owlWorkspace) throws IOException {
    super(projectLocation);
    this.owlOntologyManager = OWLManager.createOWLOntologyManager();

    owlWorkSpace = Optional.ofNullable(owlWorkspace);
  }

  /**
   * Gets owl class by id.
   *
   * @param classID the class id
   * @return the owl class by id
   */
  public Optional<OWLClass> getOwlClassById(@Nonnull String classID) {
    if (owlWorkSpace.isPresent()) {
      return owlWorkSpace.flatMap(
          owlWorkspace ->
              owlWorkspace.getOWLModelManager().getOWLEntityFinder().getEntities(IRI.create(classID)).stream()
                  .filter(owlEntity -> owlEntity instanceof OWLClass)
                  .map(owlEntity -> (OWLClass) owlEntity)
                  .findAny());
    } else {
      for (OWLOntology ontology : owlOntologyManager.getOntologies()) {
        Optional<OWLClass> owlClassOptional =
            ontology.getClassesInSignature().stream()
                .filter(
                    owlClass ->
                        owlClass.getIRI().toString().equals(classID)
                            || owlClass.getIRI().getShortForm().equals(classID)
                            || ontology.getAnnotationAssertionAxioms(owlClass.getIRI()).stream()
                            .anyMatch(
                                owlAnnotationAssertionAxiom ->
                                    getAnnotationLiteral(owlAnnotationAssertionAxiom)
                                        .map(label -> label.equals(classID))
                                        .orElse(false)))
                .findFirst();
        if (owlClassOptional.isPresent()) {
          return owlClassOptional;
        }
      }
      return Optional.empty();
    }
  }

  private Optional<String> getAnnotationLiteral(
      OWLAnnotationAssertionAxiom owlAnnotationAssertionAxiom) {
    return Optional.ofNullable(
        owlAnnotationAssertionAxiom
            .getValue()
            .asLiteral()
            .transform(OWLLiteral::getLiteral)
            .orNull());
  }

  /**
   * Gets owl object property by id.
   *
   * @param propertyID the property id
   * @return the owl object property by id
   */
  public Optional<OWLObjectProperty> getOwlObjectPropertyById(@Nonnull String propertyID) {
    if (owlWorkSpace.isPresent()) {
      return owlWorkSpace.map(
          owlWorkSpace ->
              owlWorkSpace
                  .getOWLModelManager()
                  .getOWLEntityFinder()
                  .getOWLObjectProperty(propertyID));
    } else {
      for (OWLOntology ontology : owlOntologyManager.getOntologies()) {
        Optional<OWLObjectProperty> owlObjectPropertyOptional =
            ontology.getObjectPropertiesInSignature().stream()
                .filter(owlClass -> owlClass.getIRI().getShortForm().equals(propertyID))
                .findFirst();
        if (owlObjectPropertyOptional.isPresent()) {
          return owlObjectPropertyOptional;
        }
      }
      return Optional.empty();
    }
  }

  /**
   * Gets selected owl object property.
   *
   * @return the selected owl object property
   */
  public Optional<String> getSelectedOwlObjectProperty() {
    if (owlWorkSpace.isPresent()) {
      return owlWorkSpace
          .map(OWLWorkspace::getOWLSelectionModel)
          .map(OWLSelectionModel::getSelectedEntity)
          .filter(owlEntity -> owlEntity instanceof OWLObjectProperty)
          .map(OWLEntity::toStringID);
    } else {
      return getSelectedTextSource()
          .flatMap(TextSource::getSelectedGraphSpace)
          .map(mxGraph::getSelectionCell)
          .filter(o -> o instanceof RelationAnnotation)
          .map(o -> (RelationAnnotation) o)
          .map(RelationAnnotation::getProperty);
    }
  }

  /**
   * Gets selected owl class.
   *
   * @return the selected owl class
   */
  public Optional<String> getSelectedOwlClass() {
    if (owlWorkSpace.isPresent()) {
      return owlWorkSpace
          .filter(
              owlWorkspace ->
                  owlWorkspace.getOWLSelectionModel().getSelectedEntity() instanceof OWLClass)
          .map(owlWorkspace -> (OWLClass) owlWorkspace.getOWLSelectionModel().getSelectedEntity())
          .map(OWLEntity::toStringID);
    } else {
      return getSelectedTextSource()
          .flatMap(TextSource::getSelectedAnnotation)
          .map(ConceptAnnotation::getOwlClass);
    }
  }

  /**
   * Gets owlclass descendants.
   *
   * @param cls the cls
   * @return the owlclass descendants
   */
  public Set<String> getOwlClassDescendants(String cls) {
    return owlWorkSpace
        .map(
            owlWorkspace ->
                owlWorkspace.getOWLModelManager().getOWLHierarchyManager()
                    .getOWLClassHierarchyProvider()
                    .getDescendants(getOwlDataFactory().getOWLClass(IRI.create(cls))).stream()
                    .map(OWLEntity::toStringID)
                    .collect(Collectors.toSet()))
        .orElse(new HashSet<>());
  }

  /**
   * Gets owl object property descendants.
   *
   * @param owlObjectProperty the owl object property
   * @return the owl object property descendants
   */
  public Set<String> getOwlObjectPropertyDescendants(String owlObjectProperty) {
    return owlWorkSpace
        .map(
            owlWorkspace ->
                owlWorkspace.getOWLModelManager().getOWLHierarchyManager()
                    .getOWLObjectPropertyHierarchyProvider()
                    .getDescendants(
                        getOwlDataFactory().getOWLObjectProperty(IRI.create(owlObjectProperty)))
                    .stream()
                    .map(OWLEntity::toStringID)
                    .collect(Collectors.toSet()))
        .orElse(new HashSet<>());
  }

  /**
   * Gets owl entity rendering.
   *
   * @param owlEntity the owl entity
   * @return the owl entity rendering
   */
  public String getOwlEntityRendering(String owlEntity) {
    IRI iri = IRI.create(owlEntity);
    return owlWorkSpace
        .map(owlWorkspace -> owlWorkspace.getOWLModelManager().getOWLEntityRenderer().render(iri))
        .orElse(iri.getShortForm());
  }

  /**
   * Search for string.
   *
   * @param stringToSearch the string to search
   */
  public void searchForString(String stringToSearch) {
    owlWorkSpace.ifPresent(
        owlWorkspace -> {
          JDialog dialog = SearchDialogPanel.createDialog(null, owlWorkspace.getOWLEditorKit());
          Arrays.stream(dialog.getContentPane().getComponents())
              .forEach(
                  component -> {
                    if (component instanceof AugmentedJTextField) {
                      ((AugmentedJTextField) component).setText(stringToSearch);
                    }
                  });

          dialog.setVisible(true);
        });
  }

  /**
   * Load.
   */
  public void load() {
    log.info("Loading ontologies");
    try {
      Files.list(ontologiesLocation.toPath())
          .filter(path -> path.toString().endsWith(".owl"))
          .map(path -> path.toFile().toURI().toString())
          .forEach(
              ontologyLocation -> {
                if (owlWorkSpace.isPresent()) {
                  loadWhenProtegeIsPresent(ontologyLocation);
                } else {
                  try {
                    owlOntologyManager.loadOntology(IRI.create(ontologyLocation));
                  } catch (OWLOntologyCreationException ignored) {
                    // Ok if ontology couldn't be made
                  }
                }
              });
    } catch (IOException e) {
      log.warn("Could not load ontologies");
    }
  }

  private void loadWhenProtegeIsPresent(String ontologyLocation) {
    owlWorkSpace.ifPresent(
        owlWorkspace -> {
          boolean alreadyLoaded =
              owlWorkspace.getOWLModelManager().getActiveOntologies().stream()
                  .map(ontology -> ontology.getOntologyID().getOntologyIRI())
                  .filter(com.google.common.base.Optional::isPresent)
                  .map(com.google.common.base.Optional::get)
                  .map(iri -> iri.toURI().toString())
                  .allMatch(ontology -> ontology.equals(ontologyLocation));

          if (!alreadyLoaded) {
            log.info(String.format("Loading ontology: %s", ontologyLocation));
            try {
              OWLOntology newOntology =
                  owlWorkspace
                      .getOWLModelManager()
                      .getOWLOntologyManager()
                      .loadOntology((IRI.create(ontologyLocation)));
              owlWorkspace.getOWLModelManager().setActiveOntology(newOntology);
            } catch (OWLOntologyCreationException ignored) {
              // Ok if ontology couldn't be made
            }
          }
        });
  }

  @Override
  public void save() {
    owlWorkSpace.ifPresent(
        owlWorkSpace -> {
          try {
            owlWorkSpace.getOWLModelManager().save();
          } catch (OWLOntologyStorageException e) {
            e.printStackTrace();
          }
        });
  }

  /**
   * Add ontology change listener.
   *
   * @param listener the listener
   */
  public void addOntologyChangeListener(OWLOntologyChangeListener listener) {
    if (owlWorkSpace.isPresent()) {
      owlWorkSpace.get().getOWLModelManager().addOntologyChangeListener(listener);
    } else {
      owlOntologyManager.addOntologyChangeListener(listener);
    }
  }

  /**
   * Remove ontology change listener.
   *
   * @param listener the listener
   */
  public void removeOntologyChangeListener(OWLOntologyChangeListener listener) {
    if (owlWorkSpace.isPresent()) {
      owlWorkSpace.get().getOWLModelManager().removeOntologyChangeListener(listener);
    } else {
      owlOntologyManager.removeOntologyChangeListener(listener);
    }
  }

  /**
   * Remove owl model manager listener.
   *
   * @param listener the listener
   */
  public void removeOwlModelManagerListener(OWLModelManagerListener listener) {
    owlWorkSpace.ifPresent(
        owlWorkspace -> owlWorkspace.getOWLModelManager().removeListener(listener));
  }

  /**
   * Add owl model manager listener.
   *
   * @param listener the listener
   */
  public void addOwlModelManagerListener(OWLModelManagerListener listener) {
    owlWorkSpace.ifPresent(owlWorkSpace -> owlWorkSpace.getOWLModelManager().addListener(listener));
  }

  /**
   * Add owl model manager listener.
   *
   * @param listener the listener
   */
  public void addOwlSelectionModelListener(OWLSelectionModelListener listener) {
    owlWorkSpace.ifPresent(owlWorkSpace -> owlWorkSpace.getOWLSelectionModel().addListener(listener));
  }

  public void setSelectedOwlClass(String owlClass) {
    setSelectedOwlEntity(owlOntologyManager.getOWLDataFactory().getOWLClass(IRI.create(owlClass)));
  }

  /**
   * Sets selected owl entity.
   *
   * @param owlEntity the owl entity
   */
  private void setSelectedOwlEntity(OWLEntity owlEntity) {
    if (isNotLoading()) {
      owlWorkSpace.ifPresent(
          owlWorkspace -> owlWorkspace.getOWLSelectionModel().setSelectedEntity(owlEntity));
    }
  }

  /**
   * Gets owl ontology manager.
   *
   * @return the owl ontology manager
   */
  public OWLOntologyManager getOwlOntologyManager() {
    return owlOntologyManager;
  }

  private OWLDataFactory getOwlDataFactory() {
    return owlWorkSpace
        .map(owlWorkspace -> owlWorkspace.getOWLModelManager().getOWLDataFactory())
        .orElse(owlOntologyManager.getOWLDataFactory());
  }

  public void setSelectedOwlObjectProperty(String property) {
    setSelectedOwlEntity(getOwlDataFactory().getOWLObjectProperty(IRI.create(property)));
  }

  public IRI[] getOWLAnnotationProperties() {
    return owlWorkSpace
        .map(owlWorkspace -> owlWorkspace.getOWLModelManager().getOntologies().stream()
            .map(HasAnnotationPropertiesInSignature::getAnnotationPropertiesInSignature)
            .flatMap(Set::stream)
            .map(OWLNamedObject::getIRI)
            .toArray(IRI[]::new))
        .orElseGet(() -> owlOntologyManager.getOntologies().stream()
            .map(HasAnnotationPropertiesInSignature::getAnnotationPropertiesInSignature)
            .flatMap(Set::stream)
            .map(OWLNamedObject::getIRI)
            .toArray(IRI[]::new));
  }

  protected boolean isMyRendererPlugin(RendererPlugin plugin) {
    return plugin.getRendererClassName().equals("org.protege.editor.owl.ui.prefix.OWLEntityPrefixedNameRenderer");
  }

  void setRendering() {

    owlWorkSpace.ifPresent(owlWorkspace -> {
      OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
      preferences.setRendererPlugin(preferences.getRendererPluginByClassName(OWLEntityPrefixedNameRenderer.class.getName()));
      for (RendererPlugin plugin : preferences.getRendererPlugins()) {
        log.warn(plugin.getRendererClassName());
        if (isMyRendererPlugin(plugin)) {
          preferences.setRendererPlugin(plugin);
          owlWorkspace.getOWLModelManager().refreshRenderer();
          break;
        }
      }
    });
  }

  void resetRendering() {
    owlWorkSpace.ifPresent(owlWorkspace -> {
      OWLRendererPreferences preferences = OWLRendererPreferences.getInstance();
      preferences.setRendererPlugin(preferences.getRendererPluginByClassName(OWLEntityRendererImpl.class.getName()));
      owlWorkspace.getOWLModelManager().refreshRenderer();
    });
  }
}
