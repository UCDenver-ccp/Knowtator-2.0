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

import edu.ucdenver.ccp.knowtator.io.BasicIO;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import edu.ucdenver.ccp.knowtator.model.object.ModelObject;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextBoundModelObject;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformableException;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;

import javax.annotation.Nonnull;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.undo.UndoManager;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public abstract class BaseModel extends UndoManager implements CaretListener, Savable {
	private final File projectLocation;
	private final File annotationsLocation;
	private final File articlesLocation;
	private final File profilesLocation;
	final File ontologiesLocation;

	private final Set<ModelListener> modelListeners;
	private final Map<FilterType, Boolean> filters;
	private Selection selection;

	private final TextSourceCollection textSources;
	private final ProfileCollection profiles;

	private final Map<String, ModelObject> idRegistry;
	private boolean loading;

	public BaseModel(File projectLocation) throws IOException {
		idRegistry = new TreeMap<>();

		modelListeners = new HashSet<>();
		filters = new HashMap<>();
		filters.put(FilterType.PROFILE, false);
		filters.put(FilterType.OWLCLASS, false);
		loading = false;

		selection = new Selection(0, 0);

		if (projectLocation.isFile()) {
			if (projectLocation.getName().endsWith(".knowtator")) {
				projectLocation = new File(projectLocation.getParent());
			} else {
				throw new IOException();
			}
		}
		if (projectLocation.exists() &&
				projectLocation.isDirectory() &&
				Files.list(projectLocation.toPath()).map(Path::toString).
						anyMatch(name -> name.endsWith(".knowtator"))) {
			if (Files.list(projectLocation.toPath()).noneMatch(path -> path.toString().endsWith(".knowtator"))) {
				Files.createFile(new File(projectLocation, String.format("%s.knowtator", projectLocation.getName())).toPath());
			}
			this.projectLocation = projectLocation;
			articlesLocation = new File(projectLocation, "Articles");
			annotationsLocation = new File(projectLocation, "Annotations");
			profilesLocation = (new File(projectLocation, "Profiles"));
			ontologiesLocation = new File(projectLocation, "Ontologies");
			Files.createDirectories(projectLocation.toPath());
			Files.createDirectories(articlesLocation.toPath());
			Files.createDirectories(annotationsLocation.toPath());
			Files.createDirectories(profilesLocation.toPath());
			Files.createDirectories(ontologiesLocation.toPath());
		} else {
			throw new IOException();
		}

		textSources = new TextSourceCollection(this);
		profiles = new ProfileCollection(this);


	}

	@Override
	public void caretUpdate(CaretEvent e) {
		int start = Math.min(e.getDot(), e.getMark());
		int end = Math.max(e.getDot(), e.getMark());
		selection = new Selection(start, end);
	}

	/**
	 * Saves the project. Overridden here because the OWL model needs to be saved as well.
	 *
	 * @param ioUtilClass The IOUtil to use to save the IO class. This specifies the output format
	 * @param basicIO     The IO class to save
	 * @param file        The file to save to
	 * @param <I>         The IO class
	 */
	public <I extends BasicIO> void saveToFormat(Class<? extends BasicIOUtil<I>> ioUtilClass, I basicIO, File file) {
		if (!loading) {
			try {
				setRenderRDFSLabel();
				BasicIOUtil<I> util = ioUtilClass.getDeclaredConstructor().newInstance();
				util.write(basicIO, file);
			} catch (InstantiationException
					| IllegalAccessException
					| InvocationTargetException
					| NoSuchMethodException e) {
				e.printStackTrace();
			} finally {
				resetRenderRDFS();
			}
		}
	}

	protected abstract void resetRenderRDFS();

	public void selectNextTextSource() {
		textSources.selectNext();
	}

	public void selectPreviousTextSource() {
		textSources.selectPrevious();
	}

	public void selectFirstTextSource() {
		textSources.first().ifPresent(textSources::setSelection);
	}

	public int getNumberOfTextSources() {
		return textSources.size();
	}

	public int getNumberOfProfiles() {
		return profiles.size();
	}


	protected abstract void setRenderRDFSLabel();

	public abstract void setSelectedOWLEntity(OWLEntity owlEntity);

	public File getAnnotationsLocation() {
		return annotationsLocation;
	}

	public boolean isFilter(FilterType filterType) {
		return filters.get(filterType);
	}

	public Optional<Profile> getSelectedProfile() {
		return profiles.getSelection();
	}

	public abstract Optional<OWLClass> getSelectedOWLClass();

	public abstract Collection<? extends OWLClass> getOWLCLassDescendants(OWLClass owlClass);

	public abstract Map<String, OWLClass> getOWLClassesByIDs(Set<String> classIDs);

	public Optional<Profile> getProfile(String profileID) {
		return profiles.get(profileID);
	}

	public Profile getDefaultProfile() {
		return profiles.getDefaultProfile();
	}

	public abstract String getOWLEntityRendering(OWLEntity owlEntity);

	/**
	 * This method ensures that all objects in the model will have a unique ID. If an object if provided with priority,
	 * its id will be kept and any other object already verified will have its ID changed. IDs are changed to the form
	 * textSourceID-int where textSourceID is the ID of the object's encompassing text source and int is the next
	 * number not used in any other IDs in the same encompassing text source.
	 *
	 * @param id          The proposed id for the object
	 * @param modelObject The knowtator object
	 * @param hasPriority True if the object should have priority over preexisting objects
	 */
	public void verifyId(String id, ModelObject modelObject, boolean hasPriority) {
		String verifiedId = id;

		if (hasPriority && idRegistry.keySet().contains(id)) {
			verifyId(id, idRegistry.get(id), false);
		} else {
			int i = idRegistry.size();

			while (verifiedId == null || idRegistry.keySet().contains(verifiedId)) {
				if (modelObject instanceof TextBoundModelObject) {
					verifiedId = String.format("%s-%d", ((TextBoundModelObject) modelObject).getTextSource().getId(), i);
				} else {
					verifiedId = Integer.toString(i);
				}
				i++;
			}
		}
		idRegistry.put(verifiedId, modelObject);
		modelObject.setId(id == null ? verifiedId : id);
	}

	/**
	 * Registers an action and adds its edit to the undo manager
	 *
	 * @param action An executable and undoable action
	 */
	public void registerAction(@Nonnull AbstractKnowtatorAction action) {
		try {
			action.execute();
			addEdit(action.getEdit());
		} catch (ActionUnperformableException e) {
			e.printStackTrace();
		}
	}

	public ProfileCollection getProfileCollection() {
		return profiles;
	}

	public File getSaveLocation() {
		return projectLocation;
	}

	public void addProfile(Profile profile) {
		profiles.add(profile);
	}

	public abstract Optional<OWLClass> getOWLClassByID(String classID);

	public abstract void addOntologyChangeListener(OWLOntologyChangeListener listener);

	public abstract void addOWLModelManagerListener(OWLModelManagerListener listener);

	public abstract Optional<OWLObjectProperty> getOWLObjectPropertyByID(String propertyID);

	public abstract void removeOntologyChangeListener(OWLOntologyChangeListener listener);

	public abstract void removeOWLModelManagerListener(OWLModelManagerListener listener);

	public File getArticlesLocation() {
		return articlesLocation;
	}

	public Optional<TextSource> getSelectedTextSource() {
		return textSources.getSelection();
	}

	public File getProfilesLocation() {
		return profilesLocation;
	}

	/**
	 * Loads data into the IO class using the IOUtil. The IOUtil specifies the input format.
	 *
	 * @param ioClass The IOUtil to use to load the IO class. This specifies the input format
	 * @param basicIO The IO class to load
	 * @param file    The file to load from
	 * @param <I>     the IO class
	 */
	private <I extends BasicIO> void loadFromFormat(Class<? extends BasicIOUtil<I>> ioClass, I basicIO, File file) {
		try {
			BasicIOUtil<I> util = ioClass.getDeclaredConstructor().newInstance();
			util.read(basicIO, file);
		} catch (InstantiationException
				| IllegalAccessException
				| InvocationTargetException
				| NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Takes a class capable of IO and a file, and loads it with the appropriate IOUtil for that extension
	 *
	 * @param basicIO A class capable of IO
	 * @param file    The file to load
	 */
	public void loadWithAppropriateFormat(BasicIO basicIO, File file) {
		String[] splitOnDots = file.getName().split("\\.");
		String extension = splitOnDots[splitOnDots.length - 1];

		switch (extension) {
			case "xml":
				loadFromFormat(KnowtatorXMLUtil.class, (KnowtatorXMLIO) basicIO, file);
				break;
			case "ann":
				loadFromFormat(BratStandoffUtil.class, (BratStandoffIO) basicIO, file);
				break;
			case "a1":
				loadFromFormat(BratStandoffUtil.class, (BratStandoffIO) basicIO, file);
				break;
		}
	}

	@Override
	public void load() throws IOException {
		try {
			loading = true;
			profiles.load();
			textSources.load();
			profiles.first().ifPresent(profiles::setSelection);
			textSources.first().ifPresent(textSources::setSelection);
		} finally {
			loading = false;
		}
	}

	public void dispose() {
		profiles.dispose();
		textSources.dispose();
	}

	public File getProjectLocation() {
		return projectLocation;
	}

	public TextSourceCollection getTextSources() {
		return textSources;
	}

	public Selection getSelection() {
		return selection;
	}

	public boolean isNotLoading() {
		return !loading;
	}

	public void setFilter(FilterType filterType, boolean isFilter) {
		filters.put(filterType, isFilter);
		modelListeners.forEach(ModelListener::filterChangedEvent);
	}

	public void addModelListener(ModelListener listener) {
		modelListeners.add(listener);
	}

	public void removeModelListener(ModelListener listener) {
		modelListeners.remove(listener);
	}

	public void fireColorChanged() {
		modelListeners.forEach(ModelListener::colorChangedEvent);
	}

	public void fireModelEvent(ChangeEvent<ModelObject> event) {
		modelListeners.forEach(modelListener -> modelListener.modelChangeEvent(event));
	}
}
