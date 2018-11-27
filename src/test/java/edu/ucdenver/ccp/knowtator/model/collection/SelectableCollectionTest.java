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

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.junit.jupiter.api.Test;

class SelectableCollectionTest {
	private final static KnowtatorController controller = TestingHelpers.getLoadedController();

	@Test
	void selectNext() {
		TextSource textSource = controller.getTextSourceCollection().get("document1");
		assert textSource.equals(controller.getTextSourceCollection().getSelection().get());
		controller.getTextSourceCollection().selectNext();
		controller.getTextSourceCollection().selectNext();
		assert !textSource.equals(controller.getTextSourceCollection().getSelection().get());
		controller.getTextSourceCollection().selectNext();
		assert textSource.equals(controller.getTextSourceCollection().getSelection().get());
	}

	@Test
	void selectPrevious() {
		TextSource textSource = controller.getTextSourceCollection().get("document1");
		assert textSource.equals(controller.getTextSourceCollection().getSelection().get());
		controller.getTextSourceCollection().selectPrevious();
		controller.getTextSourceCollection().selectPrevious();
		assert !textSource.equals(controller.getTextSourceCollection().getSelection().get());
		controller.getTextSourceCollection().selectPrevious();
		assert textSource.equals(controller.getTextSourceCollection().getSelection().get());
	}
}