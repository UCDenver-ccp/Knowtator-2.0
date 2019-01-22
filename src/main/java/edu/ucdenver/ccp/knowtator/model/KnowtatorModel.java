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

import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.OldKnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.collection.ProfileCollection;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLWorkspace;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The Knowtator class. Contains all of the model managers. It is used to interface between the view and the model. Also handles
 * loading and saving of the project.
 *
 * @author Harrison Pielke-Lombardo
 */
public class KnowtatorModel extends OWLModel {


	private static final Logger log = Logger.getLogger(KnowtatorModel.class);

	/**
	 * The constructor initializes all of the models and managers
	 */
	public KnowtatorModel(File projectLocation, OWLWorkspace owlWorkspace) throws IOException {
		super(projectLocation, owlWorkspace);
		textSources = new TextSourceCollection(this);
		profiles = new ProfileCollection(this);
	}

	@Override
	public void load() {
		super.load();
		try {
			loading = true;
			setRenderRDFSLabel();
			log.info("Loading profiles");
			KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
			OldKnowtatorXMLUtil oldXmlUtil = new OldKnowtatorXMLUtil();
			Files.list(getProfilesLocation().toPath())
					.filter(path -> path.toString().endsWith(".xml"))
					.map(Path::toFile)
					.forEach(file -> xmlUtil.readToProfileCollection(this, file));

			log.info("Loading annotations");
			Files.list(getAnnotationsLocation().toPath())
					.filter(path -> path.toString().endsWith(".xml"))
					.map(Path::toFile)
					.peek(file -> xmlUtil.readToTextSourceCollection(this, file))
					.forEach(file -> oldXmlUtil.readToTextSourceCollection(this, file));

			profiles.first().ifPresent(profiles::setSelection);
			textSources.first().ifPresent(textSources::setSelection);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			resetRenderRDFS();
			loading = false;
		}
	}

	/**
	 * Takes a class capable of IO and a file, and loads it with the appropriate IOUtil for that extension
	 *
	 * @param file The file to load
	 */
	public void loadWithAppropriateFormat(File file) {
		String[] splitOnDots = file.getName().split("\\.");
		String extension = splitOnDots[splitOnDots.length - 1];

		switch (extension) {
			case "xml":
				KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
				xmlUtil.readToTextSourceCollection(this, file);
				break;
			case "ann":
				BratStandoffUtil standoffUtil = new BratStandoffUtil();
				standoffUtil.readToTextSourceCollection(this, file);
				break;
			case "a1":
				standoffUtil = new BratStandoffUtil();
				standoffUtil.readToTextSourceCollection(this, file);
				break;
		}
	}

	/**
	 * @param args Unused
	 */
	public static void main(String[] args) {
		log.info("Knowtator");
	}

//	public void writeWithAppropriateFormat(File file) {
//		String[] splitOnDots = file.getName().split("\\.");
//		String extension = splitOnDots[splitOnDots.length - 1];
//
//		switch (extension) {
//			case "xml":
//				KnowtatorXMLUtil xmlUtil = new KnowtatorXMLUtil();
//				xmlUtil.writeFromTextSourceCollection(getTextSources(), file);
//				break;
//			case "ann":
//				BratStandoffUtil standoffUtil = new BratStandoffUtil();
//				standoffUtil.writeFromTextSourceCollection(this, file);
//				break;
//			case "a1":
//				standoffUtil = new BratStandoffUtil();
//				standoffUtil.write();
//				standoffUtil.readToTextSourceCollection(this, file);
//				break;
//		}
//	}


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
