/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.assertion;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP;

/**
 * Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPTriple extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "edu.ucdenver.ccp.knowtator.uima.assertion.CCPTriple";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPTriple.class);
  /**
   * The constant type.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int type = typeIndexID;

  /** The constant _FeatName_annotator. */
  public static final String _FeatName_annotator = "annotator";

  /** The constant _FeatName_tripleID. */
  /* *******************
   *   Feature Offsets *
   * *******************/
  public static final String _FeatName_tripleID = "tripleID";
  /** The constant _FeatName_object. */
  public static final String _FeatName_object = "object";
  /** The constant _FeatName_subject. */
  public static final String _FeatName_subject = "subject";
  /** The constant _FeatName_property. */
  public static final String _FeatName_property = "property";
  /** The constant _FeatName_quantifier. */
  public static final String _FeatName_quantifier = "quantifier";
  /** The constant _FeatName_quantifierValue. */
  public static final String _FeatName_quantifierValue = "quantifierValue";
  /* Feature Adjusted Offsets */
  private static final CallSite _FC_annotator =
      TypeSystemImpl.createCallSite(CCPTriple.class, "annotator");
  private static final MethodHandle _FH_annotator = _FC_annotator.dynamicInvoker();
  private static final CallSite _FC_tripleID =
      TypeSystemImpl.createCallSite(CCPTriple.class, "tripleID");
  private static final MethodHandle _FH_tripleID = _FC_tripleID.dynamicInvoker();
  private static final CallSite _FC_object =
      TypeSystemImpl.createCallSite(CCPTriple.class, "object");
  private static final MethodHandle _FH_object = _FC_object.dynamicInvoker();
  private static final CallSite _FC_subject =
      TypeSystemImpl.createCallSite(CCPTriple.class, "subject");
  private static final MethodHandle _FH_subject = _FC_subject.dynamicInvoker();
  private static final CallSite _FC_property =
      TypeSystemImpl.createCallSite(CCPTriple.class, "property");
  private static final MethodHandle _FH_property = _FC_property.dynamicInvoker();
  private static final CallSite _FC_quantifier =
      TypeSystemImpl.createCallSite(CCPTriple.class, "quantifier");
  private static final MethodHandle _FH_quantifier = _FC_quantifier.dynamicInvoker();
  private static final CallSite _FC_quantifierValue =
      TypeSystemImpl.createCallSite(CCPTriple.class, "quantifierValue");
  private static final MethodHandle _FH_quantifierValue = _FC_quantifierValue.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPTriple() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPTriple(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp triple.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPTriple(JCas jcas) {
    super(jcas);
    readObject();
  }

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
  // * Feature: annotator

  /**
   * getter for annotator - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getAnnotator() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_annotator));
  }

  /**
   * setter for annotator - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotator(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_annotator), v);
  }

  // *--------------*
  // * Feature: tripleID

  /**
   * getter for tripleID - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getTripleID() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_tripleID));
  }

  /**
   * setter for tripleID - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setTripleID(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_tripleID), v);
  }

  // *--------------*
  // * Feature: object

  /**
   * getter for object - gets
   *
   * @return value of the feature
   * @generated
   */
  public CCPVertex getObject() {
    return (CCPVertex) (_getFeatureValueNc(wrapGetIntCatchException(_FH_object)));
  }

  /**
   * setter for object - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setObject(CCPVertex v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_object), v);
  }

  // *--------------*
  // * Feature: subject

  /**
   * getter for subject - gets
   *
   * @return value of the feature
   * @generated
   */
  public CCPVertex getSubject() {
    return (CCPVertex) (_getFeatureValueNc(wrapGetIntCatchException(_FH_subject)));
  }

  /**
   * setter for subject - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSubject(CCPVertex v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_subject), v);
  }

  // *--------------*
  // * Feature: property

  /**
   * getter for property - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getProperty() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_property));
  }

  /**
   * setter for property - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setProperty(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_property), v);
  }

  // *--------------*
  // * Feature: quantifier

  /**
   * getter for quantifier - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getQuantifier() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_quantifier));
  }

  /**
   * setter for quantifier - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setQuantifier(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_quantifier), v);
  }

  // *--------------*
  // * Feature: quantifierValue

  /**
   * getter for quantifierValue - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getQuantifierValue() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_quantifierValue));
  }

  /**
   * setter for quantifierValue - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setQuantifierValue(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_quantifierValue), v);
  }
}
