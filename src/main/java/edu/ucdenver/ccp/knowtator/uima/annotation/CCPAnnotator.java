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
 * The annotator object contains information which is used to determine who/what generated an
 * annotation. Updated by JCasGen Fri Apr 06 16:53:13 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPAnnotator extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "edu.ucdenver.ccp.knowtator.uima.annotation.CCPAnnotator";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPAnnotator.class);
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

  /** The constant _FeatName_annotatorID. */
  public static final String _FeatName_annotatorID = "annotatorID";
  /** The constant _FeatName_firstName. */
  public static final String _FeatName_firstName = "firstName";
  /** The constant _FeatName_lastName. */
  public static final String _FeatName_lastName = "lastName";
  /** The constant _FeatName_affiliation. */
  public static final String _FeatName_affiliation = "affiliation";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_annotatorID =
      TypeSystemImpl.createCallSite(CCPAnnotator.class, "annotatorID");
  private static final MethodHandle _FH_annotatorID = _FC_annotatorID.dynamicInvoker();
  private static final CallSite _FC_firstName =
      TypeSystemImpl.createCallSite(CCPAnnotator.class, "firstName");
  private static final MethodHandle _FH_firstName = _FC_firstName.dynamicInvoker();
  private static final CallSite _FC_lastName =
      TypeSystemImpl.createCallSite(CCPAnnotator.class, "lastName");
  private static final MethodHandle _FH_lastName = _FC_lastName.dynamicInvoker();
  private static final CallSite _FC_affiliation =
      TypeSystemImpl.createCallSite(CCPAnnotator.class, "affiliation");
  private static final MethodHandle _FH_affiliation = _FC_affiliation.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPAnnotator() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPAnnotator(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp annotator.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPAnnotator(JCas jcas) {
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
  // * Feature: annotatorID

  /**
   * getter for annotatorID - gets This Integer should be a unique ID for a particular annotator.
   *
   * @return value of the feature
   * @generated
   */
  public int getAnnotatorID() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_annotatorID));
  }

  /**
   * setter for annotatorID - sets This Integer should be a unique ID for a particular annotator.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotatorID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_annotatorID), v);
  }

  // *--------------*
  // * Feature: firstName

  /**
   * getter for firstName - gets The first name of the annotator. Use of this field is optional as
   * the annotator ID is primarily used for determining the source of an annotation.
   *
   * @return value of the feature
   * @generated
   */
  public String getFirstName() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_firstName));
  }

  /**
   * setter for firstName - sets The first name of the annotator. Use of this field is optional as
   * the annotator ID is primarily used for determining the source of an annotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setFirstName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_firstName), v);
  }

  // *--------------*
  // * Feature: lastName

  /**
   * getter for lastName - gets The last name of the annotator. Use of this field is optional as the
   * annotator ID is primarily used for determining the source of an annotation.
   *
   * @return value of the feature
   * @generated
   */
  public String getLastName() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_lastName));
  }

  /**
   * setter for lastName - sets The last name of the annotator. Use of this field is optional as the
   * annotator ID is primarily used for determining the source of an annotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setLastName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_lastName), v);
  }

  // *--------------*
  // * Feature: affiliation

  /**
   * getter for affiliation - gets The affiliation of the annotator. Use of this field is optional
   * as the annotator ID is primarily used for determining the source of an annotation.
   *
   * @return value of the feature
   * @generated
   */
  public String getAffiliation() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_affiliation));
  }

  /**
   * setter for affiliation - sets The affiliation of the annotator. Use of this field is optional
   * as the annotator ID is primarily used for determining the source of an annotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAffiliation(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_affiliation), v);
  }
}
