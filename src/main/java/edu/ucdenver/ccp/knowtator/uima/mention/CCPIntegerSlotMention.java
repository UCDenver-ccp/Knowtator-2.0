/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.mention;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.IntegerArray;

/**
 * Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPIntegerSlotMention extends CCPPrimitiveSlotMention {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "edu.ucdenver.ccp.knowtator.uima.mention.CCPIntegerSlotMention";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPIntegerSlotMention.class);
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

  /** The constant _FeatName_slotValues. */
  public static final String _FeatName_slotValues = "slotValues";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_slotValues =
      TypeSystemImpl.createCallSite(CCPIntegerSlotMention.class, "slotValues");
  private static final MethodHandle _FH_slotValues = _FC_slotValues.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPIntegerSlotMention() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPIntegerSlotMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp integer slot mention.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPIntegerSlotMention(JCas jcas) {
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
  // * Feature: slotValues

  /**
   * getter for slotValues - gets
   *
   * @return value of the feature
   * @generated
   */
  public IntegerArray getSlotValues() {
    return (IntegerArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)));
  }

  /**
   * setter for slotValues - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSlotValues(IntegerArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_slotValues), v);
  }

  /**
   * indexed getter for slotValues - gets an indexed value -
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public int getSlotValues(int i) {
    return ((IntegerArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)))).get(i);
  }

  /**
   * indexed setter for slotValues - sets an indexed value -
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setSlotValues(int i, int v) {
    ((IntegerArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)))).set(i, v);
  }
}
