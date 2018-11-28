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

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.actions.graph.GraphActions;
import org.junit.jupiter.api.Test;
import org.semanticweb.owlapi.model.OWLObjectProperty;

public class GraphActionsTests {

	private static final KnowtatorModel controller = TestingHelpers.getLoadedController();

	@Test
	public void removeSelectedAnnotationNodeTest() {
		TextSource textSource = controller.getTextSource().get();
		textSource.getGraphSpaceCollection().selectNext();
		GraphSpace graphSpace = textSource.getGraphSpaceCollection().getSelection().get();
		Object cell = graphSpace.getModel().getChildAt(graphSpace.getDefaultParent(), 0);
		graphSpace.setSelectionCell(cell);
		TestingHelpers.testKnowtatorAction(controller, new GraphActions.removeCellsAction(graphSpace),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes - 1,
				TestingHelpers.defaultExpectedTriples - 1);
	}

	@Test
	public void removeSelectedTripleTest() {
		TextSource textSource = controller.getTextSource().get();
		textSource.getGraphSpaceCollection().selectNext();
		GraphSpace graphSpace = textSource.getGraphSpaceCollection().getSelection().get();
		Object cell = graphSpace.getModel().getChildAt(graphSpace.getDefaultParent(), 2);
		graphSpace.setSelectionCell(cell);
		TestingHelpers.testKnowtatorAction(controller, new GraphActions.removeCellsAction(graphSpace),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples - 1);
	}

	@Test
	public void addAnnotationNodeTest() {
		TextSource textSource = controller.getTextSource().get();
		textSource.getGraphSpaceCollection().selectNext();
		textSource.getConceptAnnotationCollection().selectNext();
		GraphSpace graphSpace = textSource.getGraphSpaceCollection().getSelection().get();
		ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection().get();
		TestingHelpers.testKnowtatorAction(controller, new GraphActions.AddAnnotationNodeAction(null, graphSpace, conceptAnnotation),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes + 1,
				TestingHelpers.defaultExpectedTriples);
	}

	@Test
	public void addTripleTest() {
		TextSource textSource = controller.getTextSource().get();
		textSource.getGraphSpaceCollection().selectNext();
		textSource.getConceptAnnotationCollection().selectNext();
		GraphSpace graphSpace = textSource.getGraphSpaceCollection().getSelection().get();
		AnnotationNode source = (AnnotationNode) graphSpace.getChildVertices(graphSpace.getDefaultParent())[0];
		AnnotationNode target = (AnnotationNode) graphSpace.getChildVertices(graphSpace.getDefaultParent())[1];
		OWLObjectProperty property = controller.getSelectedOWLObjectProperty().get();
		TestingHelpers.testKnowtatorAction(controller, new GraphActions.AddTripleAction(
						source,
						target,
						property, null,
						"some", null,
						false, "",
						graphSpace),
				TestingHelpers.defaultExpectedTextSources,
				TestingHelpers.defaultExpectedConceptAnnotations,
				TestingHelpers.defaultExpectedSpans,
				TestingHelpers.defaultExpectedGraphSpaces,
				TestingHelpers.defaultExpectedProfiles,
				TestingHelpers.defaultExpectedHighlighters,
				TestingHelpers.defaultExpectedAnnotationNodes,
				TestingHelpers.defaultExpectedTriples + 1);
	}

	@Test
	public void applyLayoutTest() {
		//TODO: This test only makes sure that the layout application doesn't change to graph space model. It needs to check the positions
		TextSource textSource = controller.getTextSource().get();
		textSource.getGraphSpaceCollection().selectNext();
		GraphSpace graphSpace = textSource.getGraphSpaceCollection().getSelection().get();
		TestingHelpers.testKnowtatorAction(controller, new GraphActions.applyLayoutAction(null, graphSpace),
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