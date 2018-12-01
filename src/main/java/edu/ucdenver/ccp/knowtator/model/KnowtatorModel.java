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
