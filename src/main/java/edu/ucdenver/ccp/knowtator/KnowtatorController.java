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

package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.actions.ActionUnperformableException;
import edu.ucdenver.ccp.knowtator.io.BasicIO;
import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.*;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSourceCollection;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The Knowtator class. Contains all of the model managers. It is used to interface between the view and the model. Also handles
 * loading and saving of the project.
 *
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorController extends ProjectManager implements KnowtatorObjectInterface {
	@SuppressWarnings("unused")
	private static final Logger log = Logger.getLogger(KnowtatorController.class);
	private final List<BaseKnowtatorModel> models;
	private final SelectionModel selectionModel;
	private final FilterModel filterModel;
	private final TextSourceCollection textSourceCollection;
	private final ProfileCollection profileCollection;
	private final OWLModel owlModel;
	private final TreeMap<String, KnowtatorDataObjectInterface> idRegistry;


	/**
	 * The constructor initializes all of the models and managers
	 */
	public KnowtatorController() {
		super();

		idRegistry = new TreeMap<>();

		models = new ArrayList<>();
		textSourceCollection = new TextSourceCollection(this);
		owlModel = new OWLModel(this);
		profileCollection = new ProfileCollection(this);
		filterModel = new FilterModel();
		selectionModel = new SelectionModel();

		models.add(owlModel);
		models.add(profileCollection);
		models.add(textSourceCollection);
		models.add(filterModel);
		models.add(selectionModel);
	}

	/**
	 * Load project from non-standard directory structure
	 * @param profilesLocation Directory of profile files
	 * @param ontologiesLocation Directory of ontology files
	 * @param articlesLocation Directory of article files
	 * @param annotationsLocation Directory of annotation files
	 * @param projectLocation Output directory for project to save to
	 */
	void importProject(File profilesLocation, File ontologiesLocation, File articlesLocation, File annotationsLocation, File projectLocation) {
		try {
			setSaveLocation(projectLocation);
			makeProjectStructure();
			importToManager(profilesLocation, profileCollection, ".xml");
			importToManager(ontologiesLocation, owlModel, ".obo");
			importToManager(articlesLocation, textSourceCollection, ".txt");
			importToManager(annotationsLocation, textSourceCollection, ".xml");

		} catch (IOException e) {
			e.printStackTrace();
		}
		loadProject();
	}

	/**
	 * Saves the project. Overridden here because the OWL model needs to be saved as well.
	 *
	 * @param ioUtilClass The IOUtil to use to save the IO class. This specifies the output format
	 * @param basicIO     The IO class to save
	 * @param file        The file to save to
	 * @param <I>         The IO class
	 * @see edu.ucdenver.ccp.knowtator.ProjectManager
	 */
	@Override
	public <I extends BasicIO> void saveToFormat(Class<? extends BasicIOUtil<I>> ioUtilClass, I basicIO, File file) {
		if (isNotLoading()) {
			owlModel.save();
		}
		super.saveToFormat(ioUtilClass, basicIO, file);
	}

	/**
	 * @return The filter model
	 */
	public FilterModel getFilterModel() {
		return filterModel;
	}

	/**
	 * @return A list of the models that are instances of BaseKnowtatorManager
	 */
	@Override
	List<BaseKnowtatorManager> getManagers() {
		return models.stream().filter(model -> model instanceof BaseKnowtatorManager).map(model -> (BaseKnowtatorManager) model).collect(Collectors.toList());
	}

	/**
	 * @return The OWL model
	 */
	public OWLModel getOWLModel() {
		return owlModel;
	}

	/**
	 * @return The profile collection
	 */
	public ProfileCollection getProfileCollection() {
		return profileCollection;
	}

	/**
	 * @return The selection model
	 */
	public SelectionModel getSelectionModel() {
		return selectionModel;
	}

	/**
	 * @return The text source collection
	 */
	public TextSourceCollection getTextSourceCollection() {
		return textSourceCollection;
	}

	/**
	 * This method ensures that all objects in the model will have a unique ID. If an object if provided with priority,
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
	 * Disposes the model. Clears the id registry.
	 */
	@Override
	public void dispose() {
		models.forEach(KnowtatorObjectInterface::dispose);
		idRegistry.clear();
	}

	/**
	 * Resets the model to its initial condition.
	 *
	 * @param owlWorkspace Protege's OWL workspace to be passed to the OWL model.
	 */
	public void reset(OWLWorkspace owlWorkspace) {
		owlModel.setOwlWorkSpace(owlWorkspace);
		models.forEach(BaseKnowtatorModel::reset);
	}

	/**
	 *
	 */
	@Override
	public void save() {

	}

	/**
	 *
	 */
	@Override
	public void load() {

	}

	/**
	 * @param args Unused
	 */
	public static void main(String[] args) {
		log.warn("Knowtator");
	}
}
