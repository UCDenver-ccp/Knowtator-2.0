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

package edu.ucdenver.ccp.knowtator.model.object;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;

/** The type Annotation node. */
public class AnnotationNode extends mxCell
    implements ConceptAnnotationBoundModelObject<AnnotationNode>,
        GraphBoundModelObject<AnnotationNode> {

  private final ConceptAnnotation conceptAnnotation;
  private final TextSource textSource;
  private final GraphSpace graphSpace;
  private final KnowtatorModel model;

  /**
   * Instantiates a new Annotation node.
   *
   * @param id the id
   * @param conceptAnnotation the concept annotation
   * @param x the x
   * @param y the y
   * @param graphSpace the graph space
   */
  public AnnotationNode(
      String id, ConceptAnnotation conceptAnnotation, double x, double y, GraphSpace graphSpace) {
    super(conceptAnnotation.toMultilineString(), new mxGeometry(x, y, 150, 150), "defaultVertex");
    this.textSource = conceptAnnotation.getTextSource();
    this.conceptAnnotation = conceptAnnotation;
    this.graphSpace = graphSpace;
    this.model = conceptAnnotation.getKnowtatorModel();
    model.verifyId(id, this, false);

    setVertex(true);
    setConnectable(true);
  }

  @Override
  public ConceptAnnotation getConceptAnnotation() {
    return conceptAnnotation;
  }

  @Override
  public TextSource getTextSource() {
    return textSource;
  }

  @Override
  public void dispose() {}

  @Override
  public KnowtatorModel getKnowtatorModel() {
    return model;
  }

  @Override
  public int compareTo(AnnotationNode o) {
    return o.equals(this) ? 0 : 1;
  }

  @Override
  public GraphSpace getGraphSpace() {
    return graphSpace;
  }
}
