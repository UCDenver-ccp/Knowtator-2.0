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
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformableException;
import edu.ucdenver.ccp.knowtator.view.actions.KnowtatorCollectionActionsTests;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class KnowtatorViewTests {
	private static KnowtatorView view;

	@BeforeAll
	static void setUp() {
		view = new KnowtatorView();
	}

	@Test
	void loadProjectTest() throws IOException {
		KnowtatorModel controller = TestingHelpers.getLoadedController();
		TestingHelpers.checkDefaultCollectionValues(controller);
		view.loadProject(controller.getProjectLocation(), null);
		TestingHelpers.checkDefaultCollectionValues(view.getModel().get());
	}

	@Test
	void testActionsTest() throws IOException, ActionUnperformableException {
		KnowtatorModel controller = TestingHelpers.getLoadedController();
		TestingHelpers.checkDefaultCollectionValues(controller);
		view.loadProject(controller.getProjectLocation(), null);
		TestingHelpers.checkDefaultCollectionValues(view.getModel().get());
		KnowtatorCollectionActionsTests test = new KnowtatorCollectionActionsTests();
		test.setModel(view.getModel().get());
		test.removeConceptAnnotationActionTest();
		test.removeGraphSpaceActionTest();
		test.removeProfileActionTest();
		test.removeSpanActionTest();
		for (int i = 0; i < view.getModel().get().getTextSources().size(); i++) {
			view.getModel().get().selectNextTextSource();
			view.getTextPane().showTextSource();
		}
		test.removeTextSourceActionTest();
		test.addConceptAnnotationActionTest();
		test.addGraphSpaceActionTest();
		test.addProfileActionTest();
		test.addSpanActionTest();
		test.addTextSourceActionTest();
		view.loadProject(controller.getProjectLocation(), null);

	}

//	@Test
//	void textSourceButtonActivationTest() throws IOException {
//		KnowtatorView view = new KnowtatorView();
//		assert !view.getTextSourceButtons().stream()
//				.map(Component::isEnabled).reduce(false, (a, b) -> a || b);
//		KnowtatorModel controller = TestingHelpers.getLoadedController();
//		view.loadProject(controller.getProjectLocation(), null);
//		view.getKnowtatorModel().get().selectFirstTextSource();
//		assert view.getTextSourceButtons().stream()
//				.map(Component::isEnabled).reduce(true, (a, b) -> a && b);
//	}
}