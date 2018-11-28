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

package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.view.actions.KnowtatorCollectionActionsTests;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

class KnowtatorViewTests {
	private static final KnowtatorView view = new KnowtatorView();

	@Test
	void loadProjectTest() throws IOException {
		KnowtatorModel controller = TestingHelpers.getLoadedController();
		TestingHelpers.checkDefaultCollectionValues(controller);
		view.loadProject(controller.getProjectLocation(), null);
		TestingHelpers.checkDefaultCollectionValues(KnowtatorView.MODEL);
	}

	@Test
	void testActionsTest() throws IOException {
		KnowtatorModel controller = TestingHelpers.getLoadedController();
		TestingHelpers.checkDefaultCollectionValues(controller);
		KnowtatorView.MODEL.setDebug();
		view.loadProject(controller.getProjectLocation(), null);
		TestingHelpers.checkDefaultCollectionValues(KnowtatorView.MODEL);
		KnowtatorCollectionActionsTests test = new KnowtatorCollectionActionsTests();
		test.setController(KnowtatorView.MODEL);
		test.removeConceptAnnotationActionTest();
		test.removeGraphSpaceActionTest();
		test.removeProfileActionTest();
		test.removeSpanActionTest();
		for (int i = 0; i < KnowtatorView.MODEL.getTextSources().size(); i++) {
			KnowtatorView.MODEL.selectNextTextSource();
			view.getKnowtatorTextPane().refreshHighlights();
		}
		test.removeTextSourceActionTest();
		test.addConceptAnnotationActionTest();
		test.addGraphSpaceActionTest();
		test.addProfileActionTest();
		test.addSpanActionTest();
		test.addTextSourceActionTest();
		view.loadProject(controller.getProjectLocation(), null);

	}

	@Test
	void textSourceButtonActivationTest() throws IOException {
		KnowtatorView view = new KnowtatorView();
		assert !view.textSourceButtons.stream()
				.map(Component::isEnabled).reduce(false, (a, b) -> a || b);
		KnowtatorModel controller = TestingHelpers.getLoadedController();
		view.loadProject(controller.getProjectLocation(), null);
		assert view.textSourceButtons.stream()
				.map(Component::isEnabled).reduce(true, (a, b) -> a && b);
	}
}