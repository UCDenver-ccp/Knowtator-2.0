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

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ConceptAnnotationCollectionTest {
	private static final Logger log = Logger.getLogger(ConceptAnnotationCollectionTest.class);


	private final String[] projectFileNames = new String[]{"test_project", "old_project"};
	private final String[] articleFileNames = new String[]{"document1", "document2"};
	private String[] articleContent = new String[]{
			"This is a test document.",
			"A second test document has appeared!"
	};
	private String[] profileFileNames = new String[]{"profile1", "profile2"};
	private ConceptAnnotationCollection conceptAnnotationCollection;
	private Profile profile;
	private TextSource textSource;
	private KnowtatorController controller;

	private File getProjectFile(String projectName) {
		return new File(getClass().getResource(String.format(
				"/%s/%s.knowtator",
				projectName,
				projectName)
		).getFile());
	}

	private File getArticleFile(String projectName, String articleName) {
		return new File(getClass().getResource(String.format(
				"/%s/Articles/%s.txt",
				projectName,
				articleName)
		).getFile());
	}

	public void setUp() {
		Map<String, File> tempProjectDirs = new HashMap<>();
		try {

			for (String projectFileName : projectFileNames) {
				File projectDirectory = getProjectFile(projectFileName).getParentFile();
				File tempProjectDir = Files.createTempDir();
				FileUtils.copyDirectory(projectDirectory, tempProjectDir);
				tempProjectDirs.put(projectFileName, tempProjectDir);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		controller = new KnowtatorController();

		int projectID = 0;
		int articleID = 0;
		String projectFileName = projectFileNames[projectID];
		File project = tempProjectDirs.get(projectFileName);
		String articleName = articleFileNames[articleID];

		try {
			controller.setSaveLocation(project);
			controller.loadProject();
		} catch (IOException e) {
			e.printStackTrace();
		}

		textSource = controller.getTextSourceCollection().stream().filter(textSource1 -> textSource1.getId().equals(articleName)).findAny().get();
		conceptAnnotationCollection = textSource.getConceptAnnotationCollection();
		profile = controller.getProfileCollection().getDefaultProfile();

	}

	@Test
	public void addAnnotation() {
		setUp();
		ConceptAnnotation conceptAnnotation1 = new ConceptAnnotation(controller, textSource, "mention_3", null, "class_2", "class_2", profile, "identity", "");
		conceptAnnotationCollection.add(conceptAnnotation1);
		int numAnnotations = conceptAnnotationCollection.size();
		int numSpans = conceptAnnotationCollection.getSpans(null).size();

		assert numAnnotations == 3;
		assert numSpans == 3;
	}

	@Test
	public void addSpanToAnnotation() {
		setUp();

		ConceptAnnotation conceptAnnotation1 = new ConceptAnnotation(controller, textSource, "mention_3", null, "class_2", "class_2", profile, "identity", "");
		conceptAnnotationCollection.add(conceptAnnotation1);
		Span span1 = new Span(controller, textSource, conceptAnnotation1, null, 1, 6);
		conceptAnnotation1.getSpanCollection().add(span1);

		int numAnnotations = conceptAnnotationCollection.size();
		int numSpans = conceptAnnotationCollection.getSpans(null).size();

		assert numAnnotations == 3;
		assert numSpans == 4;
	}

	@Test
	public void removeAnnotation() {
		setUp();
		conceptAnnotationCollection.remove(conceptAnnotationCollection.get("mention_1"));

		int numAnnotations = conceptAnnotationCollection.size();
		int numSpans = conceptAnnotationCollection.getSpans(null).size();

		assert numAnnotations == 1;
		try {
			assert numSpans == 1;
		} catch (AssertionError ae) {
			conceptAnnotationCollection.getSpans(null).forEach((span -> log.warn(String.format("%s, %s", span, span.getConceptAnnotation()))));
			throw new AssertionError();
		}
	}

	@Test
	public void removeSpanFromAnnotation() {
		setUp();
		ConceptAnnotation conceptAnnotation1 = new ConceptAnnotation(controller, textSource, "mention_3", null, "class_2", "class_2", profile, "identity", "");
		conceptAnnotationCollection.add(conceptAnnotation1);
		Span span1 = new Span(controller, textSource, conceptAnnotation1, null, 1, 6);
		conceptAnnotation1.getSpanCollection().add(span1);

		span1.getConceptAnnotation().getSpanCollection().remove(span1);
		int numAnnotations = conceptAnnotationCollection.size();
		int numSpans = conceptAnnotationCollection.getSpans(null).size();

		assert numAnnotations == 3;
		try {
			assert numSpans == 3;
		} catch (AssertionError ae) {
			conceptAnnotationCollection.getSpans(null).forEach((span -> log.warn(String.format("%s, %s", span, span.getConceptAnnotation()))));
			throw new AssertionError();
		}
	}
}
