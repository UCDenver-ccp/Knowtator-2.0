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

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;

import java.io.File;
import java.io.IOException;

/**
 * The Knowtator class. Contains all of the model managers. It is used to interface between the view and the model. Also handles
 * loading and saving of the project.
 *
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorModel extends OWLModel {


	private static Logger log = Logger.getLogger(KnowtatorModel.class);

	/**
	 * The constructor initializes all of the models and managers
	 */
	public KnowtatorModel(File projectLocation, OWLWorkspace owlWorkspace) throws IOException {
		super(projectLocation, owlWorkspace);
	}


//	private void setupListeners() {
//
//		new TextBoundModelListener(this) {
//			@Override
//			public void respondToConceptAnnotationModification() {
//
//			}
//
//			@Override
//			public void respondToSpanModification() {
//
//			}
//
//			@Override
//			public void respondToGraphSpaceModification() {
//
//			}
//
//			@Override
//			public void respondToGraphSpaceCollectionFirstAdded() {
//
//			}
//
//			@Override
//			public void respondToGraphSpaceCollectionEmptied() {
//
//			}
//
//			@Override
//			public void respondToGraphSpaceRemoved() {
//
//			}
//
//			@Override
//			public void respondToGraphSpaceAdded() {
//
//			}
//
//			@Override
//			public void respondToGraphSpaceSelection(SelectionEvent<GraphSpace> event) {
//
//			}
//
//			@Override
//			public void respondToConceptAnnotationCollectionEmptied() {
//
//			}
//
//			@Override
//			public void respondToConceptAnnotationRemoved() {
//
//			}
//
//			@Override
//			public void respondToConceptAnnotationAdded() {
//
//			}
//
//			@Override
//			public void respondToConceptAnnotationCollectionFirstAdded() {
//
//			}
//
//			@Override
//			public void respondToSpanCollectionFirstAdded() {
//
//			}
//
//			@Override
//			public void respondToSpanCollectionEmptied() {
//
//			}
//
//			@Override
//			public void respondToSpanRemoved() {
//
//			}
//
//			@Override
//			public void respondToSpanAdded() {
//
//			}
//
//			@Override
//			public void respondToSpanSelection(SelectionEvent<Span> event) {
//
//			}
//
//			@Override
//			public void respondToConceptAnnotationSelection(SelectionEvent<ConceptAnnotation> event) {
//				event.getNew()
//						.map(ConceptAnnotation::getOwlClass)
//						.filter(owlClass -> isNotLoading())
//						.ifPresent(owlClass -> setSelectedOWLEntity(owlClass));
//			}
//
//			@Override
//			public void respondToTextSourceSelection(SelectionEvent<TextSource> event) {
//
//			}
//
//			@Override
//			public void respondToTextSourceAdded() {
//
//			}
//
//			@Override
//			public void respondToTextSourceRemoved() {
//
//			}
//
//			@Override
//			public void respondToTextSourceCollectionEmptied() {
//
//			}
//
//			@Override
//			public void respondToTextSourceCollectionFirstAdded() {
//
//			}
//		};
//	}


//	/**
//	 * Load project from non-standard directory structure
//	 *
//	 * @param profilesLocation    Directory of profile files
//	 * @param ontologiesLocation  Directory of ontology files
//	 * @param articlesLocation    Directory of article files
//	 * @param annotationsLocation Directory of annotation files
//	 * @param projectLocation     Output directory for project to save to
//	 */
//	public void importProject(File profilesLocation, File ontologiesLocation, File articlesLocation, File annotationsLocation, File projectLocation) {
//		try {
//			setSaveLocation(projectLocation);
//			makeProjectStructure();
//			importToManager(profilesLocation, profileCollection, ".xml");
//			importToManager(ontologiesLocation, this, ".obo");
//			importToManager(articlesLocation, textSourceCollection, ".txt");
//			importToManager(annotationsLocation, textSourceCollection, ".xml");
//
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		loadProject();
//	}
//	/**
//	 * Disposes the model. Clears the id registry.
//	 */
//	@Override
//	public void dispose() {
//		super.dispose();
//		profileCollection.dispose();
//		textSourceCollection.dispose();
//		profileCollection.dispose();
//		modelListeners.clear();
//		idRegistry.clear();
//	}
//
//	@Override
//	public void reset() {
//		setupListeners();
//	}
//
//	@Override
//	public void finishLoad() {
//
//	}
//
//	/**
//	 * Resets the model to its initial condition.
//	 *
//	 * @param owlWorkspace Protege's OWL workspace to be passed to the OWL model.
//	 */
//	public void reset(OWLWorkspace owlWorkspace) {
//		setOwlWorkSpace(owlWorkspace);
//		reset();
//		profileCollection.reset();
//		textSourceCollection.reset();
//	}

	/**
	 * @param args Unused
	 */
	public static void main(String[] args) {
		log.info("Knowtator");
	}


//	/**
//	 * Loads the project from the location defined by project location
//	 */
//	public void loadProject() {
//		if (projectLocation.exists()) {
//			makeProjectStructure();
//			isLoading = true;
//			load();
//			setRenderRDFSLabel();
//			profileCollection.load();
//			textSourceCollection.load();
//			resetRenderRDFS();
//			isLoading = false;
//			profileCollection.finishLoad();
//			textSourceCollection.finishLoad();
//		}
//	}

//	/**
//	 * Used to load the managers of a project with directories that are not all directly under the project directory
//	 *
//	 * @param directory The directory containing files to be loaded
//	 * @param manager   The manager to be loaded to
//	 * @param extension The extension of the files to be loaded. For example, the profile manager should load profiles from files ending with .xml
//	 * @throws IOException Thrown if the directory does not exist
//	 */
//	private void importToManager(File directory, BaseKnowtatorManager manager, String extension) throws IOException {
//		if (directory != null && directory.exists()) {
//			Files.newDirectoryStream(
//					Paths.get(directory.toURI()), path -> path.toString().endsWith(extension))
//					.forEach(
//							fileName -> {
//								try {
//									Files.copy(fileName,
//											new File(manager.getSaveLocation(), fileName.getFileName().toFile().getName()).toPath());
//								} catch (IOException e) {
//									e.printStackTrace();
//								}
//							});
//		}
//	}
}
