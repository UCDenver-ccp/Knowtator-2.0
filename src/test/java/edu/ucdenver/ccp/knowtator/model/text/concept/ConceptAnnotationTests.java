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

package edu.ucdenver.ccp.knowtator.model.text.concept;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.junit.jupiter.api.Test;

public class ConceptAnnotationTests {
	private final static KnowtatorModel controller = TestingHelpers.getLoadedController();

	@Test
	public void getSizeTest() {
		TextSource textSource = controller.getTextSource().get();
		ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
		assert conceptAnnotation.getSize() == 4;
	}

	@Test
	public void getSpannedTextTest() {
		TextSource textSource = controller.getTextSource().get();
		ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
		assert conceptAnnotation.getSpannedText().equals("This");
	}

	@Test
	public void containsTest() {
		TextSource textSource = controller.getTextSource().get();
		ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().first();
		assert conceptAnnotation.contains(0);
		assert conceptAnnotation.contains(2);
		assert conceptAnnotation.contains(3);
		assert !conceptAnnotation.contains(4);
		assert !conceptAnnotation.contains(5);
		assert !conceptAnnotation.contains(100);
		assert !conceptAnnotation.contains(-1);
	}
}