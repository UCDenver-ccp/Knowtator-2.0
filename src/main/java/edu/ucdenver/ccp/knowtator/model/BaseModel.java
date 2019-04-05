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

import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextBoundModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import javax.annotation.Nonnull;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.undo.UndoManager;

/** The type Base model. */
public abstract class BaseModel extends UndoManager implements CaretListener, Savable {

  private final File projectLocation;
  private final File annotationsLocation;
  private final File articlesLocation;
  private final File profilesLocation;
  final File structuresLocation;

  /** The Ontologies location. */
  final File ontologiesLocation;

  private final Set<ModelListener> modelListeners;
  private final Map<FilterType, Boolean> filters;
  private Selection selection;

  /** The Text sources. */
  TextSourceCollection textSources;

  /** The Profiles. */
  ProfileCollection profiles;

  // TODO: Make sure idregistry reflects contents of model
  private final Map<String, ModelObject> idRegistry;
  /** The Loading. */
  boolean loading;

  private Boolean isStructureMode;
  private Set<StructureModeListener> structureModeListeners;

  /**
   * Instantiates a new Base model.
   *
   * @param projectLocation the project location
   * @throws IOException the io exception
   */
  BaseModel(File projectLocation) throws IOException {
    idRegistry = new TreeMap<>();

    modelListeners = new HashSet<>();
    structureModeListeners = new HashSet<>();
    filters = new HashMap<>();
    filters.put(FilterType.PROFILE, false);
    filters.put(FilterType.OWLCLASS, false);
    loading = false;
    isStructureMode = false;

    selection = new Selection(0, 0);

    if (projectLocation.isFile()) {
      if (projectLocation.getName().endsWith(".knowtator")) {
        projectLocation = new File(projectLocation.getParent());
      } else {
        throw new IOException();
      }
    }
    if (projectLocation.exists() && projectLocation.isDirectory()) {
      if (Files.list(projectLocation.toPath())
          .noneMatch(path -> path.toString().endsWith(".knowtator"))) {
        Files.createFile(
            new File(projectLocation, String.format("%s.knowtator", projectLocation.getName()))
                .toPath());
      }
      this.projectLocation = projectLocation;
      articlesLocation = new File(projectLocation, "Articles");
      annotationsLocation = new File(projectLocation, "Annotations");
      profilesLocation = (new File(projectLocation, "Profiles"));
      ontologiesLocation = new File(projectLocation, "Ontologies");
      structuresLocation = new File(projectLocation, "Structures");
      Files.createDirectories(projectLocation.toPath());
      Files.createDirectories(articlesLocation.toPath());
      Files.createDirectories(annotationsLocation.toPath());
      Files.createDirectories(profilesLocation.toPath());
      Files.createDirectories(ontologiesLocation.toPath());
      Files.createDirectories(structuresLocation.toPath());
    } else {
      throw new IOException();
    }
  }

  @Override
  public void caretUpdate(CaretEvent e) {
    int start = Math.min(e.getDot(), e.getMark());
    int end = Math.max(e.getDot(), e.getMark());
    selection = new Selection(start, end);
  }

  /** Select next text source. */
  public void selectNextTextSource() {
    textSources.selectNext();
  }

  /** Select previous text source. */
  public void selectPreviousTextSource() {
    textSources.selectPrevious();
  }

  /** Select first text source. */
  public void selectFirstTextSource() {
    textSources.first().ifPresent(textSources::setSelection);
  }

  /**
   * Gets number of text sources.
   *
   * @return the number of text sources
   */
  public int getNumberOfTextSources() {
    return textSources.size();
  }

  /**
   * Gets number of profiles.
   *
   * @return the number of profiles
   */
  public int getNumberOfProfiles() {
    return profiles.size();
  }

  /**
   * Gets annotations location.
   *
   * @return the annotations location
   */
  public File getAnnotationsLocation() {
    return annotationsLocation;
  }

  /**
   * Is filter boolean.
   *
   * @param filterType the filter type
   * @return the boolean
   */
  public boolean isFilter(FilterType filterType) {
    return filters.get(filterType);
  }

  /**
   * Gets selected profile.
   *
   * @return the selected profile
   */
  public Optional<Profile> getSelectedProfile() {
    return profiles.getSelection();
  }

  /**
   * Gets profile.
   *
   * @param profileID the profile id
   * @return the profile
   */
  public Optional<Profile> getProfile(String profileID) {
    return profiles.get(profileID);
  }

  /**
   * Gets default profile.
   *
   * @return the default profile
   */
  public Profile getDefaultProfile() {
    return profiles.getDefaultProfile();
  }

  /**
   * This method ensures that all objects in the model will have a unique ID. If an object if
   * provided with priority, its id will be kept and any other object already verified will have its
   * ID changed. IDs are changed to the form textSourceID-int where textSourceID is the ID of the
   * object's encompassing text source and int is the next number not used in any other IDs in the
   * same encompassing text source.
   *
   * @param id The proposed id for the object
   * @param modelObject The knowtator object
   * @param hasPriority True if the object should have priority over preexisting objects
   */
  public String verifyId(String id, ModelObject modelObject, boolean hasPriority) {
    String verifiedId = id;

    if (hasPriority && idRegistry.keySet().contains(id)) {
      ModelObject mo = idRegistry.get(id);
      modelObject.setId(verifyId(id, mo, false));
    } else {
      int i = idRegistry.size();

      while (verifiedId == null || idRegistry.keySet().contains(verifiedId)) {
        if (modelObject instanceof TextBoundModelObject) {
          verifiedId =
              String.format(
                  "%s-%d", ((TextBoundModelObject) modelObject).getTextSource().getId(), i);
        } else {
          verifiedId = Integer.toString(i);
        }
        i++;
      }
    }
    idRegistry.put(verifiedId, modelObject);
    return id == null ? verifiedId : id;
  }

  /**
   * Registers an action and adds its edit to the undo manager.
   *
   * @param action An executable and undoable action
   * @throws ActionUnperformable the action unperformable exception
   */
  public void registerAction(@Nonnull AbstractKnowtatorAction action) throws ActionUnperformable {
    action.execute();
    addEdit(action);
  }

  /**
   * Gets profiles.
   *
   * @return the profiles
   */
  public ProfileCollection getProfiles() {
    return profiles;
  }

  /**
   * Gets save location.
   *
   * @return the save location
   */
  public File getSaveLocation() {
    return projectLocation;
  }

  /**
   * Add profile.
   *
   * @param profile the profile
   */
  public void addProfile(Profile profile) {
    profiles.add(profile);
  }

  /**
   * Gets articles location.
   *
   * @return the articles location
   */
  public File getArticlesLocation() {
    return articlesLocation;
  }

  /**
   * Gets selected text source.
   *
   * @return the selected text source
   */
  public Optional<TextSource> getSelectedTextSource() {
    return textSources.getSelection();
  }

  public Optional<ConceptAnnotation> getSelectedConceptAnnotation() {
    return getSelectedTextSource().flatMap(TextSource::getSelectedAnnotation);
  }

  public Optional<GraphSpace> getSelectedGraphSpace() {
    if (isStructureMode) {
      return getSelectedTextSource().flatMap(TextSource::getSelectedStructureGraphSpace);
    } else {
      return getSelectedTextSource().flatMap(TextSource::getSelectedGraphSpace);
    }
  }

  /**
   * Gets profiles location.
   *
   * @return the profiles location
   */
  File getProfilesLocation() {
    return profilesLocation;
  }

  /** Dispose. */
  public void dispose() {
    profiles.dispose();
    textSources.dispose();
  }

  /**
   * Gets project location.
   *
   * @return the project location
   */
  public File getProjectLocation() {
    return projectLocation;
  }

  /**
   * Gets text sources.
   *
   * @return the text sources
   */
  public TextSourceCollection getTextSources() {
    return textSources;
  }

  /**
   * Gets selection.
   *
   * @return the selection
   */
  public Selection getSelection() {
    return selection;
  }

  /**
   * Is not loading boolean.
   *
   * @return the boolean
   */
  public boolean isNotLoading() {
    return !loading;
  }

  /**
   * Sets filter.
   *
   * @param filterType the filter type
   * @param isFilter the is filter
   */
  public void setFilter(FilterType filterType, boolean isFilter) {
    filters.put(filterType, isFilter);
    modelListeners.forEach(ModelListener::filterChangedEvent);
  }

  public void setStructureMode(Boolean isStructureMode) {
    this.isStructureMode = isStructureMode;
    fireStructureModeChangedEvent();
  }

  private void fireStructureModeChangedEvent() {
    structureModeListeners.forEach(StructureModeListener::structureModeChanged);
  }

  /**
   * Add model listener.
   *
   * @param listener the listener
   */
  public void addModelListener(ModelListener listener) {
    modelListeners.add(listener);
  }

  /**
   * Remove model listener.
   *
   * @param listener the listener
   */
  public void removeModelListener(ModelListener listener) {
    modelListeners.remove(listener);
  }

  /** Fire color changed. */
  public void fireColorChanged() {
    modelListeners.forEach(ModelListener::colorChangedEvent);
  }

  /**
   * Fire model event.
   *
   * @param event the event
   */
  public void fireModelEvent(ChangeEvent<ModelObject> event) {
    modelListeners.forEach(modelListener -> modelListener.modelChangeEvent(event));
  }

  public void addStructureModeListener(StructureModeListener listener) {
    structureModeListeners.add(listener);
  }
}
