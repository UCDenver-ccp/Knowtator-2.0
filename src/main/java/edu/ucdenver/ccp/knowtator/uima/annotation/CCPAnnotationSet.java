/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.annotation;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP;

/**
 * The annotation set provides a means for arbitrarily categorizing or clustering groups of
 * annotations. Annotations can be associated with multiple annotation groups. Examples of use
 * include, defining Gold Standard annotation sets, and delineating between the use of different
 * parameters during annotation, among others. Each annotation set is associated with a unique ID, a
 * name and a description. Updated by JCasGen Fri Apr 06 16:53:13 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPAnnotationSet extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "edu.ucdenver.ccp.knowtator.uima.annotation.CCPAnnotationSet";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPAnnotationSet.class);
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

  /** The constant _FeatName_annotationSetID. */
  public static final String _FeatName_annotationSetID = "annotationSetID";
  /** The constant _FeatName_annotationSetName. */
  public static final String _FeatName_annotationSetName = "annotationSetName";
  /** The constant _FeatName_annotationSetDescription. */
  public static final String _FeatName_annotationSetDescription = "annotationSetDescription";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_annotationSetID =
      TypeSystemImpl.createCallSite(CCPAnnotationSet.class, "annotationSetID");
  private static final MethodHandle _FH_annotationSetID = _FC_annotationSetID.dynamicInvoker();
  private static final CallSite _FC_annotationSetName =
      TypeSystemImpl.createCallSite(CCPAnnotationSet.class, "annotationSetName");
  private static final MethodHandle _FH_annotationSetName = _FC_annotationSetName.dynamicInvoker();
  private static final CallSite _FC_annotationSetDescription =
      TypeSystemImpl.createCallSite(CCPAnnotationSet.class, "annotationSetDescription");
  private static final MethodHandle _FH_annotationSetDescription =
      _FC_annotationSetDescription.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPAnnotationSet() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPAnnotationSet(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp annotation set.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPAnnotationSet(JCas jcas) {
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
  // * Feature: annotationSetID

  /**
   * getter for annotationSetID - gets An integer uniquely identifying a particular annotation set.
   *
   * @return value of the feature
   * @generated
   */
  public int getAnnotationSetID() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_annotationSetID));
  }

  /**
   * setter for annotationSetID - sets An integer uniquely identifying a particular annotation set.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotationSetID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_annotationSetID), v);
  }

  // *--------------*
  // * Feature: annotationSetName

  /**
   * getter for annotationSetName - gets The name of the annotation set.
   *
   * @return value of the feature
   * @generated
   */
  public String getAnnotationSetName() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_annotationSetName));
  }

  /**
   * setter for annotationSetName - sets The name of the annotation set.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotationSetName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_annotationSetName), v);
  }

  // *--------------*
  // * Feature: annotationSetDescription

  /**
   * getter for annotationSetDescription - gets A textual description of an annotation set.
   *
   * @return value of the feature
   * @generated
   */
  public String getAnnotationSetDescription() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_annotationSetDescription));
  }

  /**
   * setter for annotationSetDescription - sets A textual description of an annotation set.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotationSetDescription(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_annotationSetDescription), v);
  }
}
