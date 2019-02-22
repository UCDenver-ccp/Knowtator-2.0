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
 * The span object holds span information.This is a supplement to the default UIMA annotation which
 * cannot handle multi-span annotations. Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPSpan extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "edu.ucdenver.ccp.knowtator.uima.annotation.CCPSpan";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPSpan.class);
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

  /** The constant _FeatName_spanStart. */
  public static final String _FeatName_spanStart = "spanStart";
  /** The constant _FeatName_spanEnd. */
  public static final String _FeatName_spanEnd = "spanEnd";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_spanStart =
      TypeSystemImpl.createCallSite(CCPSpan.class, "spanStart");
  private static final MethodHandle _FH_spanStart = _FC_spanStart.dynamicInvoker();
  private static final CallSite _FC_spanEnd =
      TypeSystemImpl.createCallSite(CCPSpan.class, "spanEnd");
  private static final MethodHandle _FH_spanEnd = _FC_spanEnd.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPSpan() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPSpan(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp span.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPSpan(JCas jcas) {
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
  // * Feature: spanStart

  /**
   * getter for spanStart - gets The character offset for the start of the span.
   *
   * @return value of the feature
   * @generated
   */
  public int getSpanStart() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_spanStart));
  }

  /**
   * setter for spanStart - sets The character offset for the start of the span.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSpanStart(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_spanStart), v);
  }

  // *--------------*
  // * Feature: spanEnd

  /**
   * getter for spanEnd - gets The character offset for the end of the span.
   *
   * @return value of the feature
   * @generated
   */
  public int getSpanEnd() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_spanEnd));
  }

  /**
   * setter for spanEnd - sets The character offset for the end of the span.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSpanEnd(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_spanEnd), v);
  }
}
