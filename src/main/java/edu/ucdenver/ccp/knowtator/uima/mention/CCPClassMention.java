/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.mention;

import edu.ucdenver.ccp.knowtator.uima.annotation.CCPTextAnnotation;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;

/**
 * The CCP ClassMention is the root of a flexible class structure that can be used to store
 * virtually any frame-based representation of a particular class. Common class mention types
 * include, but are not limited to, such things as entities (protein, cell type, cell line, disease,
 * tissue, etc.) and frames (interaction, transport, regulation, etc.). Updated by JCasGen Fri Apr
 * 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
@SuppressWarnings("unchecked")
public class CCPClassMention extends CCPMention {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "edu.ucdenver.ccp.knowtator.uima.mention.CCPClassMention";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPClassMention.class);
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

  /** The constant _FeatName_slotMentions. */
  public static final String _FeatName_slotMentions = "slotMentions";
  /** The constant _FeatName_ccpTextAnnotation. */
  public static final String _FeatName_ccpTextAnnotation = "ccpTextAnnotation";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_slotMentions =
      TypeSystemImpl.createCallSite(CCPClassMention.class, "slotMentions");
  private static final MethodHandle _FH_slotMentions = _FC_slotMentions.dynamicInvoker();
  private static final CallSite _FC_ccpTextAnnotation =
      TypeSystemImpl.createCallSite(CCPClassMention.class, "ccpTextAnnotation");
  private static final MethodHandle _FH_ccpTextAnnotation = _FC_ccpTextAnnotation.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPClassMention() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPClassMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp class mention.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPClassMention(JCas jcas) {
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
  // * Feature: slotMentions

  /**
   * getter for slotMentions - gets A class mention optionally has slot mentions which represent
   * attributes of that class. These slot mentions are stored in the slotMentions FSArray. There are
   * two types of slot mentions, complex and non-complex. The difference between complex and
   * non-complex slot mentions is simply the type of filler (or slot value) for each. Complex slot
   * mentions are filled with a class mention, whereas non-complex slot mentions are filled by
   * simple Strings.
   *
   * @return value of the feature
   * @generated
   */
  public FSArray getSlotMentions() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_slotMentions)));
  }

  /**
   * setter for slotMentions - sets A class mention optionally has slot mentions which represent
   * attributes of that class. These slot mentions are stored in the slotMentions FSArray. There are
   * two types of slot mentions, complex and non-complex. The difference between complex and
   * non-complex slot mentions is simply the type of filler (or slot value) for each. Complex slot
   * mentions are filled with a class mention, whereas non-complex slot mentions are filled by
   * simple Strings.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSlotMentions(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_slotMentions), v);
  }

  /**
   * indexed getter for slotMentions - gets an indexed value - A class mention optionally has slot
   * mentions which represent attributes of that class. These slot mentions are stored in the
   * slotMentions FSArray. There are two types of slot mentions, complex and non-complex. The
   * difference between complex and non-complex slot mentions is simply the type of filler (or slot
   * value) for each. Complex slot mentions are filled with a class mention, whereas non-complex
   * slot mentions are filled by simple Strings.
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public CCPSlotMention getSlotMentions(int i) {
    return (CCPSlotMention)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_slotMentions)))).get(i));
  }

  /**
   * indexed setter for slotMentions - sets an indexed value - A class mention optionally has slot
   * mentions which represent attributes of that class. These slot mentions are stored in the
   * slotMentions FSArray. There are two types of slot mentions, complex and non-complex. The
   * difference between complex and non-complex slot mentions is simply the type of filler (or slot
   * value) for each. Complex slot mentions are filled with a class mention, whereas non-complex
   * slot mentions are filled by simple Strings.
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setSlotMentions(int i, CCPSlotMention v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_slotMentions)))).set(i, v);
  }

  // *--------------*
  // * Feature: ccpTextAnnotation

  /**
   * getter for ccpTextAnnotation - gets Just as CCPTextAnnotations are linked to a CCPClassMention,
   * it is sometimes useful to be able to follow a CCPClassMention back to its corresponding
   * CCPTextAnnotation.
   *
   * @return value of the feature
   * @generated
   */
  public CCPTextAnnotation getCcpTextAnnotation() {
    return (CCPTextAnnotation)
        (_getFeatureValueNc(wrapGetIntCatchException(_FH_ccpTextAnnotation)));
  }

  /**
   * setter for ccpTextAnnotation - sets Just as CCPTextAnnotations are linked to a CCPClassMention,
   * it is sometimes useful to be able to follow a CCPClassMention back to its corresponding
   * CCPTextAnnotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setCcpTextAnnotation(CCPTextAnnotation v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_ccpTextAnnotation), v);
  }
}
