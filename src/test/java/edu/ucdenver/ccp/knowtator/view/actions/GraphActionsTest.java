/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.view.actions;

import edu.ucdenver.ccp.knowtator.TestingHelpers;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.object.Quantifier;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
import edu.ucdenver.ccp.knowtator.view.actions.graph.GraphActions;
import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class GraphActionsTest {

  private static KnowtatorModel model;

  @BeforeAll
  static void setUp() {
    try {
      model = TestingHelpers.getLoadedModel();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  void removeSelectedAnnotationNodeTest() throws ActionUnperformable {
    TextSource textSource = model.getTextSources().getOnlySelected().get();
    textSource.getGraphSpaces().selectNext();
    GraphSpace graphSpace = textSource.getGraphSpaces().getOnlySelected().get();
    Object cell = graphSpace.getModel().getChildAt(graphSpace.getDefaultParent(), 0);
    graphSpace.setSelectionCell(cell);
    TestingHelpers.testKnowtatorAction(
        model,
        new GraphActions.RemoveCellsAction(model, graphSpace),
        TestingHelpers.defaultCounts.copy(0, 0, 0, 0, 0, 0, -1, -1, 0));
  }

  @Test
  void removeSelectedTripleTest() throws ActionUnperformable {
    TextSource textSource = model.getTextSources().get("document1").get();
    textSource.getGraphSpaces().selectNext();
    GraphSpace graphSpace = textSource.getGraphSpaces().getOnlySelected().get();
    Object cell = graphSpace.getModel().getChildAt(graphSpace.getDefaultParent(), 2);
    graphSpace.setSelectionCell(cell);
    TestingHelpers.testKnowtatorAction(
        model,
        new GraphActions.RemoveCellsAction(model, graphSpace),
        TestingHelpers.defaultCounts.copy(0,0, 0, 0, 0, 0, 0, -1, 0));
  }

  @Test
  void addAnnotationNodeTest() throws ActionUnperformable {
    TextSource textSource = model.getTextSources().get("document1").get();
    textSource.getGraphSpaces().selectNext();
    textSource.getGraphSpaces().selectNext();
    GraphSpace graphSpace = textSource.getGraphSpaces().getOnlySelected().get();
    ConceptAnnotation conceptAnnotation = textSource.getConceptAnnotations().getOnlySelected().get();
    TestingHelpers.testKnowtatorAction(
        model,
        new GraphActions.AddAnnotationNodeAction(null, model, graphSpace, conceptAnnotation),
        TestingHelpers.defaultCounts.copy(0, 0, 0, 0, 0, 0, 1, 0, 0));
  }

  @Test
  void addTripleTest() throws ActionUnperformable {
    TextSource textSource = model.getTextSources().get("document1").get();
    textSource.getGraphSpaces().selectNext();
    textSource.getGraphSpaces().selectNext();
    GraphSpace graphSpace = textSource.getGraphSpaces().getOnlySelected().get();
    AnnotationNode source =
        (AnnotationNode) graphSpace.getChildVertices(graphSpace.getDefaultParent())[0];
    AnnotationNode target =
        (AnnotationNode) graphSpace.getChildVertices(graphSpace.getDefaultParent())[1];
    String property = "hasBase";
    TestingHelpers.testKnowtatorAction(
        model,
        new GraphActions.AddTripleAction(
            model, source, target, property, Quantifier.some, null, false, "", graphSpace),
        TestingHelpers.defaultCounts.copy(0, 0, 0, 0, 0, 0, 0, 1, 0));
  }

  @Test
  void applyLayoutTest() throws ActionUnperformable {
    // TODO: This test only makes sure that the layout application doesn't change to graph space
    // model. It needs to check the positions
    TextSource textSource = model.getTextSources().get("document1").get();
    textSource.getGraphSpaces().selectNext();
    GraphSpace graphSpace = textSource.getGraphSpaces().getOnlySelected().get();
    TestingHelpers.testKnowtatorAction(
        model,
        new GraphActions.ApplyLayoutAction(null, model, graphSpace, false),
        TestingHelpers.defaultCounts);
  }
}
