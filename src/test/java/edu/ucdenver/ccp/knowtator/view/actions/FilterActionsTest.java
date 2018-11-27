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

package edu.ucdenver.ccp.knowtator.view.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.FilterModel;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.model.FilterAction;
import edu.ucdenver.ccp.knowtator.view.actions.model.ReassignOWLClassAction;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FilterActionsTest {
	private static final KnowtatorController controller = TestingHelpers.getLoadedController();

	@BeforeAll
	static void setup() {
		KnowtatorView.CONTROLLER = controller;
	}

	@Test
	void filterAction() throws ActionUnperformableException {
		TestingHelpers.checkDefaultCollectionValues(controller);
		TestingHelpers.testKnowtatorAction(controller, new FilterAction(FilterModel.PROFILE, true),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans - 3,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);
		TestingHelpers.testKnowtatorAction(controller, new FilterAction(FilterModel.PROFILE, false),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);

		TextSource textSource = controller.getTextSourceCollection().getSelection().get();
		ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
		textSource.getConceptAnnotationCollection().setSelection(conceptAnnotation);
		controller.registerAction(new ReassignOWLClassAction(conceptAnnotation));

		TestingHelpers.testKnowtatorAction(controller, new FilterAction(FilterModel.OWLCLASS, true),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans - 4,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);
		TestingHelpers.testKnowtatorAction(controller, new FilterAction(FilterModel.OWLCLASS, false),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples);


	}

}