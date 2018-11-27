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

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.actions.KnowtatorCollectionActionsTest;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.io.IOException;

class KnowtatorViewTest {
	private static final KnowtatorView view = new KnowtatorView();

	@Test
	void loadProject() throws IOException {
		KnowtatorController controller = TestingHelpers.getLoadedController();
		TestingHelpers.checkDefaultCollectionValues(controller);
		view.loadProject(controller.getProjectLocation(), null);
		TestingHelpers.checkDefaultCollectionValues(KnowtatorView.CONTROLLER);
	}

	@Test
	void testActions() throws IOException {
		KnowtatorController controller = TestingHelpers.getLoadedController();
		TestingHelpers.checkDefaultCollectionValues(controller);
		KnowtatorView.CONTROLLER.setDebug();
		view.loadProject(controller.getProjectLocation(), null);
		TestingHelpers.checkDefaultCollectionValues(KnowtatorView.CONTROLLER);
		KnowtatorCollectionActionsTest test = new KnowtatorCollectionActionsTest();
		test.setController(KnowtatorView.CONTROLLER);
		test.removeConceptAnnotationAction();
		test.removeGraphSpaceAction();
		test.removeProfileAction();
		test.removeSpanAction();
		for (int i = 0; i < KnowtatorView.CONTROLLER.getTextSourceCollection().size(); i++) {
			KnowtatorView.CONTROLLER.getTextSourceCollection().selectNext();
			view.getKnowtatorTextPane().refreshHighlights();
		}
		int defaultExpectedHighlighters = TestingHelpers.defaultExpectedHighlighters;
		TestingHelpers.defaultExpectedHighlighters = TestingHelpers.defaultExpectedHighlighters + 2;
		test.removeTextSourceAction();
		test.addConceptAnnotationAction();
		test.addGraphSpaceAction();
		test.addProfileAction();
		test.addSpanAction();
		test.addTextSourceAction();
		view.loadProject(controller.getProjectLocation(), null);
		TestingHelpers.defaultExpectedHighlighters = defaultExpectedHighlighters;

	}

	@Test
	void textSourceButtonActivation() throws IOException {
		KnowtatorView view = new KnowtatorView();
		assert !view.textSourceButtons.stream()
				.map(Component::isEnabled).reduce(false, (a, b) -> a || b);
		KnowtatorController controller = TestingHelpers.getLoadedController();
		view.loadProject(controller.getProjectLocation(), null);
		assert view.textSourceButtons.stream()
				.map(Component::isEnabled).reduce(true, (a, b) -> a && b);
	}
}