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
import edu.ucdenver.ccp.knowtator.model.collection.SelectionEvent;
import edu.ucdenver.ccp.knowtator.model.collection.TextBoundModelListener;
import edu.ucdenver.ccp.knowtator.model.profile.ColorListener;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollection;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollectionListener;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformableException;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;

import javax.annotation.Nonnull;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;

/**
 * The Knowtator class. Contains all of the modelactions managers. It is used to interface between the view and the modelactions. Also handles
 * loading and saving of the project.
 *
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorModel extends ProjectManager implements CaretListener {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(KnowtatorModel.class);
	private final List<BaseKnowtatorManager> models;
	private final TextSourceCollection textSourceCollection;
	private final ProfileCollection profileCollection;
	private final TreeMap<String, KnowtatorDataObjectInterface> idRegistry;
	private final List<FilterModelListener> filterModelListeners;
	private boolean isFilterByOWLClass;
	private boolean isFilterByProfile;


	/**
	 * The constructor initializes all of the models and managers
	 */
	public KnowtatorModel() {
		super();

		idRegistry = new TreeMap<>();

		models = new ArrayList<>();
		textSourceCollection = new TextSourceCollection(this);
		profileCollection = new ProfileCollection(this);

		filterModelListeners = new ArrayList<>();
		isFilterByOWLClass = false;
		isFilterByProfile = false;
		start = 0;
		end = 0;

		models.add(profileCollection);
		models.add(textSourceCollection);

		setupListeners();
	}

	@Override
	public void caretUpdate(CaretEvent e) {
		setStart(Math.min(e.getDot(), e.getMark()));
		setEnd(Math.max(e.getDot(), e.getMark()));
	}

	public int getStart() {
		return start;
	}

	private void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	private void setEnd(int end) {
		this.end = end;
	}

	private int start;
	private int end;

	private void setupListeners() {

		new TextBoundModelListener(this) {
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
				event.getNew()
						.map(ConceptAnnotation::getOwlClass)
						.ifPresent(owlClass -> setSelectedOWLEntity(owlClass));
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

	public void addFilterModelListener(FilterModelListener listener) {
		filterModelListeners.add(listener);
	}

	public boolean isFilter(FilterType filter) {
		switch (filter) {
			case PROFILE:
				return isFilterByProfile;
			case OWLCLASS:
				return isFilterByOWLClass;
		}
		return false;
	}


	/**
	 * Load project from non-standard directory structure
	 *
	 * @param profilesLocation    Directory of profile files
	 * @param ontologiesLocation  Directory of ontology files
	 * @param articlesLocation    Directory of article files
	 * @param annotationsLocation Directory of annotation files
	 * @param projectLocation     Output directory for project to save to
	 */
	public void importProject(File profilesLocation, File ontologiesLocation, File articlesLocation, File annotationsLocation, File projectLocation) {
		try {
			setSaveLocation(projectLocation);
			makeProjectStructure();
			importToManager(profilesLocation, profileCollection, ".xml");
			importToManager(ontologiesLocation, this, ".obo");
			importToManager(articlesLocation, textSourceCollection, ".txt");
			importToManager(annotationsLocation, textSourceCollection, ".xml");

		} catch (IOException e) {
			e.printStackTrace();
		}
		loadProject();
	}

	/**
	 * Saves the project. Overridden here because the OWL modelactions needs to be saved as well.
	 *
	 * @param ioUtilClass The IOUtil to use to save the IO class. This specifies the output format
	 * @param basicIO     The IO class to save
	 * @param file        The file to save to
	 * @param <I>         The IO class
	 * @see ProjectManager
	 */
	@Override
	public <I extends BasicIO> void saveToFormat(Class<? extends BasicIOUtil<I>> ioUtilClass, I basicIO, File file) {
		if (isNotLoading()) {
			save();
		}
		super.saveToFormat(ioUtilClass, basicIO, file);
	}

	/**
	 * @return A list of the models that are instances of BaseKnowtatorManager
	 */
	@Override
	List<BaseKnowtatorManager> getManagers() {
		return models;
	}

	/**
	 * @return The profile collection
	 */
	public ProfileCollection getProfileCollection() {
		return profileCollection;
	}

	/**
	 * @return The text source collection
	 */
	public Optional<TextSource> getSelectedTextSource() {
		return textSourceCollection.getSelection();
	}

	/**
	 * This method ensures that all objects in the modelactions will have a unique ID. If an object if provided with priority,
	 * its id will be kept and any other object already verified will have its ID changed. IDs are changed to the form
	 * textSourceID-int where textSourceID is the ID of the object's encompassing text source and int is the next
	 * number not used in any other IDs in the same encompassing text source.
	 *
	 * @param id          The proposed id for the object
	 * @param obj         The knowtator object
	 * @param hasPriority True if the object should have priority over preexisting objects
	 */
	public void verifyId(String id, KnowtatorDataObjectInterface obj, Boolean hasPriority) {
		String verifiedId = id;

		if (hasPriority && idRegistry.keySet().contains(id)) {
			verifyId(id, idRegistry.get(id), false);
		} else {
			int i = idRegistry.size();

			while (verifiedId == null || idRegistry.keySet().contains(verifiedId)) {
				if (obj instanceof KnowtatorTextBoundDataObjectInterface && ((KnowtatorTextBoundDataObjectInterface) obj).getTextSource() != null) {
					verifiedId = ((KnowtatorTextBoundDataObjectInterface) obj).getTextSource().getId() + "-" + i;
				} else {
					verifiedId = Integer.toString(i);
				}
				i++;
			}
		}
		idRegistry.put(verifiedId, obj);
		obj.setId(id == null ? verifiedId : id);

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

	/**
	 * Disposes the modelactions. Clears the id registry.
	 */
	@Override
	public void dispose() {
		super.dispose();
		models.forEach(BaseKnowtatorManager::dispose);
		filterModelListeners.clear();
		idRegistry.clear();
	}

	@Override
	public void reset() {
		setupListeners();
	}

	@Override
	public void finishLoad() {

	}

	/**
	 * Resets the modelactions to its initial condition.
	 *
	 * @param owlWorkspace Protege's OWL workspace to be passed to the OWL modelactions.
	 */
	public void reset(OWLWorkspace owlWorkspace) {
		setOwlWorkSpace(owlWorkspace);
		models.forEach(BaseKnowtatorManager::reset);
	}

	/**
	 *
	 */
	@Override
	public void save() {
		super.save();
	}

	/**
	 *
	 */
	@Override
	public void load() {
		super.load();
	}

	/**
	 * @param args Unused
	 */
	public static void main(String[] args) {
		log.info("Knowtator");
	}

	public File getArticlesLocation() {
		return textSourceCollection.getArticlesLocation();
	}

	public File getAnnotationsLocation() {
		return textSourceCollection.getAnnotationsLocation();
	}

	public void addTextSourceCollectionListener(TextSourceCollectionListener listener) {
		textSourceCollection.addCollectionListener(listener);
	}

	public void removeTextSourceCollectionListener(TextSourceCollectionListener listener) {
		textSourceCollection.removeCollectionListener(listener);
	}

	public TextSourceCollection getTextSources() {
		return textSourceCollection;
	}

	public void selectNextTextSource() {
		textSourceCollection.selectNext();
	}

	public void selectPreviousTextSource() {
		textSourceCollection.selectPrevious();
	}

	public void selectFirstTextSource() {
		textSourceCollection.setSelection(textSourceCollection.first());
	}

	public void setFilter(FilterType filter, boolean isFilter) {
		switch (filter) {
			case PROFILE:
				isFilterByProfile = isFilter;
				filterModelListeners.forEach(listener -> listener.profileFilterChanged(isFilterByProfile));
				break;
			case OWLCLASS:
				isFilterByOWLClass = isFilter;
				filterModelListeners.forEach(l -> l.owlClassFilterChanged(isFilterByOWLClass));
				break;
		}
	}

	public int getNumberOfTextSources() {
		return textSourceCollection.size();
	}

	public int getNumberOfProfiles() {
		return profileCollection.size();
	}

	public Optional<Profile> getSelectedProfile() {
		return profileCollection.getSelection();
	}

	public Optional<Profile> getProfile(String profileID) {
		return profileCollection.get(profileID);
	}

	public Profile getDefaultProfile() {
		return profileCollection.getDefaultProfile();
	}

	public void addProfile(Profile profile) {
		profileCollection.add(profile);
	}

	public void addProfileCollectionListener(ProfileCollectionListener listener) {
		profileCollection.addCollectionListener(listener);
	}

	public void addColorListener(ColorListener listener) {
		profileCollection.addColorListener(listener);
	}
}
