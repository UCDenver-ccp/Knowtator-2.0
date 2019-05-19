/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.annotation.metadata;

import edu.ucdenver.ccp.knowtator.uima.annotation.CCPAnnotationSet;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * A metadata property for declaring AnnotationSet membership. This will eventually replace the
 * annotationSets field that is currently part of the CCPTextAnnotation class. Updated by JCasGen
 * Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class AnnotationSetMembershipProperty extends AnnotationMetadataProperty {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "edu.ucdenver.ccp.knowtator.uima.annotation.metadata.AnnotationSetMembershipProperty";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID =
      JCasRegistry.register(AnnotationSetMembershipProperty.class);
  /**
   * The constant type.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int type = typeIndexID;
  /**
   * Gets type index id.
   *
   * @return index of the type
   * @generated
   */
  @Override
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /* *******************
   *   Feature Offsets *
   * *******************/

  /** The constant _FeatName_annotationSet. */
  public static final String _FeatName_annotationSet = "annotationSet";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_annotationSet =
      TypeSystemImpl.createCallSite(AnnotationSetMembershipProperty.class, "annotationSet");
  private static final MethodHandle _FH_annotationSet = _FC_annotationSet.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected AnnotationSetMembershipProperty() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public AnnotationSetMembershipProperty(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Annotation set membership property.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public AnnotationSetMembershipProperty(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   *
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable
   */
  private void readObject() {
    /*default - does nothing empty block */
  }

  // *--------------*
  // * Feature: annotationSet

  /**
   * getter for annotationSet - gets
   *
   * @return value of the feature
   * @generated
   */
  public CCPAnnotationSet getAnnotationSet() {
    return (CCPAnnotationSet) (_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSet)));
  }

  /**
   * setter for annotationSet - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotationSet(CCPAnnotationSet v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_annotationSet), v);
  }
}
