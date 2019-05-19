/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.mention;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;

/**
 * The superclass for all CCP Mentions (class mention, complex slot mention, and non-complex slot
 * mention) Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
@SuppressWarnings("WeakerAccess")
public class CCPMention extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "edu.ucdenver.ccp.knowtator.uima.mention.CCPMention";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPMention.class);
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

  /** The constant _FeatName_mentionName. */
  public static final String _FeatName_mentionName = "mentionName";
  /** The constant _FeatName_mentionID. */
  public static final String _FeatName_mentionID = "mentionID";
  /** The constant _FeatName_traversalIDs. */
  public static final String _FeatName_traversalIDs = "traversalIDs";
  /** The constant _FeatName_traversalMentionIDs. */
  public static final String _FeatName_traversalMentionIDs = "traversalMentionIDs";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_mentionName =
      TypeSystemImpl.createCallSite(CCPMention.class, "mentionName");
  private static final MethodHandle _FH_mentionName = _FC_mentionName.dynamicInvoker();
  private static final CallSite _FC_mentionID =
      TypeSystemImpl.createCallSite(CCPMention.class, "mentionID");
  private static final MethodHandle _FH_mentionID = _FC_mentionID.dynamicInvoker();
  private static final CallSite _FC_traversalIDs =
      TypeSystemImpl.createCallSite(CCPMention.class, "traversalIDs");
  private static final MethodHandle _FH_traversalIDs = _FC_traversalIDs.dynamicInvoker();
  private static final CallSite _FC_traversalMentionIDs =
      TypeSystemImpl.createCallSite(CCPMention.class, "traversalMentionIDs");
  private static final MethodHandle _FH_traversalMentionIDs =
      _FC_traversalMentionIDs.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPMention() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp mention.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPMention(JCas jcas) {
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
  // * Feature: mentionName

  /**
   * getter for mentionName - gets The name of this mention.
   *
   * @return value of the feature
   * @generated
   */
  public String getMentionName() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_mentionName));
  }

  /**
   * setter for mentionName - sets The name of this mention.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setMentionName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_mentionName), v);
  }

  // *--------------*
  // * Feature: mentionID

  /**
   * getter for mentionID - gets
   *
   * @return value of the feature
   * @generated
   */
  public long getMentionID() {
    return _getLongValueNc(wrapGetIntCatchException(_FH_mentionID));
  }

  /**
   * setter for mentionID - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setMentionID(long v) {
    _setLongValueNfc(wrapGetIntCatchException(_FH_mentionID), v);
  }

  // *--------------*
  // * Feature: traversalIDs

  /**
   * getter for traversalIDs - gets
   *
   * @return value of the feature
   * @generated
   */
  public StringArray getTraversalIDs() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalIDs)));
  }

  /**
   * setter for traversalIDs - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setTraversalIDs(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_traversalIDs), v);
  }

  /**
   * indexed getter for traversalIDs - gets an indexed value -
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public String getTraversalIDs(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalIDs)))).get(i);
  }

  /**
   * indexed setter for traversalIDs - sets an indexed value -
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setTraversalIDs(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalIDs)))).set(i, v);
  }

  // *--------------*
  // * Feature: traversalMentionIDs

  /**
   * getter for traversalMentionIDs - gets
   *
   * @return value of the feature
   * @generated
   */
  public StringArray getTraversalMentionIDs() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalMentionIDs)));
  }

  /**
   * setter for traversalMentionIDs - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setTraversalMentionIDs(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_traversalMentionIDs), v);
  }

  /**
   * indexed getter for traversalMentionIDs - gets an indexed value -
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public String getTraversalMentionIDs(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalMentionIDs))))
        .get(i);
  }

  /**
   * indexed setter for traversalMentionIDs - sets an indexed value -
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setTraversalMentionIDs(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalMentionIDs))))
        .set(i, v);
  }
}
