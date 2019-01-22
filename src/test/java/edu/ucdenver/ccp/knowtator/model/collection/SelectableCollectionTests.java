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

package edu.ucdenver.ccp.knowtator.model.collection;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import org.junit.jupiter.api.Test;

import java.io.IOException;

class SelectableCollectionTests {
	private static KnowtatorModel controller;

	static {
		try {
			controller = TestingHelpers.getLoadedModel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Test
	void selectNextTest() {
		TextSource textSource = controller.getTextSources().get("document1").get();
		assert textSource.equals(controller.getSelectedTextSource().get());
		controller.selectNextTextSource();
		controller.selectNextTextSource();
		assert !textSource.equals(controller.getSelectedTextSource().get());
		controller.selectNextTextSource();
		assert textSource.equals(controller.getSelectedTextSource().get());
	}

	@Test
	void selectPreviousTest() {
		TextSource textSource = controller.getTextSources().get("document1").get();
		assert textSource.equals(controller.getSelectedTextSource().get());
		controller.selectPreviousTextSource();
		controller.selectPreviousTextSource();
		assert !textSource.equals(controller.getSelectedTextSource().get());
		controller.selectPreviousTextSource();
		assert textSource.equals(controller.getSelectedTextSource().get());
	}
}