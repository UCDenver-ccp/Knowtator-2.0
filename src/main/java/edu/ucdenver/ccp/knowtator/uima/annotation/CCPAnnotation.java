/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.annotation;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.tcas.Annotation;

/**
 * The superclass for all CCP annotations. Updated by JCasGen Fri Apr 06 16:53:13 MDT 2018 XML
 * source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
@SuppressWarnings({"WeakerAccess", "deprecation"})
public class CCPAnnotation extends Annotation {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "edu.ucdenver.ccp.knowtator.uima.annotation.CCPAnnotation";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPAnnotation.class);
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

  /** The constant _FeatName_annotationMetadata. */
  public static final String _FeatName_annotationMetadata = "annotationMetadata";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_annotationMetadata =
      TypeSystemImpl.createCallSite(CCPAnnotation.class, "annotationMetadata");
  private static final MethodHandle _FH_annotationMetadata =
      _FC_annotationMetadata.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPAnnotation() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPAnnotation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp annotation.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPAnnotation(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * Instantiates a new Ccp annotation.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   * @generated
   */
  public CCPAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
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
  // * Feature: annotationMetadata

  /**
   * getter for annotationMetadata - gets Stores metadata for an annotation.
   *
   * @return value of the feature
   * @generated
   */
  public AnnotationMetadata getAnnotationMetadata() {
    return (AnnotationMetadata)
        (_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationMetadata)));
  }

  /**
   * setter for annotationMetadata - sets Stores metadata for an annotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotationMetadata(AnnotationMetadata v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_annotationMetadata), v);
  }
}
