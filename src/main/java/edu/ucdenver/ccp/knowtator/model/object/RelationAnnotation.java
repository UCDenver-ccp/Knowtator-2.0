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
import edu.ucdenver.ccp.knowtator.model.collection.event.ChangeEvent;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLObjectProperty;

/** The type Relation annotation. */
public class RelationAnnotation extends mxCell
    implements GraphBoundModelObject<RelationAnnotation> {
  /** The Quantifiers. */

  private final Quantifier quantifier;
  private final String quantifierValue;
  private final Profile annotator;
  private String bratID;
  private final KnowtatorModel model;
  private final Boolean isNegated;
  private final TextSource textSource;
  private OWLObjectProperty property;
  private final GraphSpace graphSpace;
  private final String motivation;

  private final AnnotationNode sourceAnnotationNode;
  private final AnnotationNode targetAnnotationNode;

  @SuppressWarnings("unused")
  private final Logger log = Logger.getLogger(RelationAnnotation.class);

  public GraphSpace getGraphSpace() {
    return graphSpace;
  }

  /**
   * Instantiates a new Relation annotation.
   *
   * @param graphSpace the graph space
   * @param id the id
   * @param source the source
   * @param target the target
   * @param property the property
   * @param annotator the annotator
   * @param quantifier the quantifier
   * @param quantifierValue the quantifier value
   * @param isNegated the is negated
   * @param motivation the motivation
   */
  public RelationAnnotation(
      GraphSpace graphSpace,
      String id,
      AnnotationNode source,
      AnnotationNode target,
      OWLObjectProperty property,
      Profile annotator,
      Quantifier quantifier,
      String quantifierValue,
      Boolean isNegated,
      String motivation) {
    super(null, new mxGeometry(), null);

    this.isNegated = isNegated;
    this.motivation = motivation;
    this.textSource = graphSpace.getTextSource();
    this.model = graphSpace.getKnowtatorModel();
    this.annotator = annotator;
    this.quantifier = quantifier;
    this.quantifierValue = quantifierValue;
    this.graphSpace = graphSpace;

    this.id = model.verifyId(id, this, false);

    getGeometry().setRelative(true);
    setEdge(true);
    setSource(source);
    this.sourceAnnotationNode = source;
    setTarget(target);
    this.targetAnnotationNode = target;
    setProperty(property);
  }

  /**
   * Gets brat id.
   *
   * @return the brat id
   */
  @SuppressWarnings("unused")
  String getBratID() {
    return bratID;
  }

  @Override
  public TextSource getTextSource() {
    return textSource;
  }

  @Override
  public String toString() {
    return String.format("%s", getOwlPropertyRendering());
  }

  /**
   * Sets brat id.
   *
   * @param bratID the brat id
   */
  @SuppressWarnings("unused")
  void setBratID(String bratID) {
    this.bratID = bratID;
  }

  /**
   * Gets owl property rendering.
   *
   * @return the owl property rendering
   */
  public String getOwlPropertyRendering() {
    return model.getOwlEntityRendering(property);
  }

  /**
   * Sets property.
   *
   * @param owlObjectProperty the owl object property
   */
  void setProperty(OWLObjectProperty owlObjectProperty) {
    property = owlObjectProperty;
    setLabel();
    model.fireModelEvent(new ChangeEvent<>(model, null, this));
  }

  /** Sets label. */
  void setLabel() {
    setValue(
        String.format(
            "%s%s%n%s %s",
            isNegated ? "not " : "", getOwlPropertyRendering(), quantifier, quantifierValue));
  }

  @Override
  public void dispose() {}

  @Override
  public KnowtatorModel getKnowtatorModel() {
    return model;
  }

  @Override
  public int compareTo(RelationAnnotation o) {
    int val = GraphBoundModelObject.super.compareTo(o);
    if (val == 0) {
      val = model.getOwlObjectComparator().compare(property, o.getProperty());
      if (val == 0) {
        //noinspection unchecked
        val = sourceAnnotationNode.compareTo(o.getSourceAnnotationNode());
        if (val == 0) {
          //noinspection unchecked
          val = targetAnnotationNode.compareTo(o.getTargetAnnotationNode());
          if (val == 0) {
            val = id.compareTo(o.getId());
          }
        }
      }
    }
    return val;
  }

  private AnnotationNode getTargetAnnotationNode() {
    return targetAnnotationNode;
  }

  private AnnotationNode getSourceAnnotationNode() {
    return sourceAnnotationNode;
  }

  /**
   * Gets property.
   *
   * @return the property
   */
  public OWLObjectProperty getProperty() {
    return property;
  }

  /**
   * Gets annotator.
   *
   * @return the annotator
   */
  public Profile getAnnotator() {
    return annotator;
  }

  /**
   * Gets quantifier.
   *
   * @return the quantifier
   */
  public Quantifier getQuantifier() {
    return quantifier;
  }

  /**
   * Gets quantifier value.
   *
   * @return the quantifier value
   */
  public String getQuantifierValue() {
    return quantifierValue;
  }

  /**
   * Gets is negated.
   *
   * @return the is negated
   */
  public boolean getIsNegated() {
    return isNegated;
  }

  /**
   * Gets motivation.
   *
   * @return the motivation
   */
  public String getMotivation() {
    return motivation;
  }
}
