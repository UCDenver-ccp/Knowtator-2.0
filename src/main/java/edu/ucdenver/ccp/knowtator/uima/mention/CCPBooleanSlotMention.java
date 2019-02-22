/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.mention;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;

/**
 * Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPBooleanSlotMention extends CCPPrimitiveSlotMention {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "edu.ucdenver.ccp.knowtator.uima.mention.CCPBooleanSlotMention";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPBooleanSlotMention.class);
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

  /** The constant _FeatName_slotValue. */
  public static final String _FeatName_slotValue = "slotValue";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_slotValue =
      TypeSystemImpl.createCallSite(CCPBooleanSlotMention.class, "slotValue");
  private static final MethodHandle _FH_slotValue = _FC_slotValue.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPBooleanSlotMention() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPBooleanSlotMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp boolean slot mention.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPBooleanSlotMention(JCas jcas) {
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
  // * Feature: slotValue

  /**
   * getter for slotValue - gets
   *
   * @return value of the feature
   * @generated
   */
  public boolean getSlotValue() {
    return _getBooleanValueNc(wrapGetIntCatchException(_FH_slotValue));
  }

  /**
   * setter for slotValue - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSlotValue(boolean v) {
    _setBooleanValueNfc(wrapGetIntCatchException(_FH_slotValue), v);
  }
}
