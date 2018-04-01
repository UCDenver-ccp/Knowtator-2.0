

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 19:49:28 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.mention;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;


/** The superclass for all CCP Mentions (class mention, complex slot mention, and non-complex slot mention)
 * Updated by JCasGen Sat Mar 31 19:49:28 MDT 2018
 * XML source: E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml
 * @generated */
@SuppressWarnings("ALL")
public class CCPMention extends TOP {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.mention.CCPMention";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPMention.class);
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int type = typeIndexID;
  /** @generated
   * @return index of the type  
   */
  @Override
  public              int getTypeIndexID() {return typeIndexID;}
 
 
  /* *******************
   *   Feature Offsets *
   * *******************/ 
   
  public final static String _FeatName_mentionName = "mentionName";
  public final static String _FeatName_mentionID = "mentionID";
  public final static String _FeatName_traversalIDs = "traversalIDs";
  public final static String _FeatName_traversalMentionIDs = "traversalMentionIDs";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_mentionName = TypeSystemImpl.createCallSite(CCPMention.class, "mentionName");
  private final static MethodHandle _FH_mentionName = _FC_mentionName.dynamicInvoker();
  private final static CallSite _FC_mentionID = TypeSystemImpl.createCallSite(CCPMention.class, "mentionID");
  private final static MethodHandle _FH_mentionID = _FC_mentionID.dynamicInvoker();
  private final static CallSite _FC_traversalIDs = TypeSystemImpl.createCallSite(CCPMention.class, "traversalIDs");
  private final static MethodHandle _FH_traversalIDs = _FC_traversalIDs.dynamicInvoker();
  private final static CallSite _FC_traversalMentionIDs = TypeSystemImpl.createCallSite(CCPMention.class, "traversalMentionIDs");
  private final static MethodHandle _FH_traversalMentionIDs = _FC_traversalMentionIDs.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPMention(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** 
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable 
   */
  private void readObject() {/*default - does nothing empty block */}
     
 
    
  //*--------------*
  //* Feature: mentionName

  /** getter for mentionName - gets The name of this mention.
   * @generated
   * @return value of the feature 
   */
  public String getMentionName() { return _getStringValueNc(wrapGetIntCatchException(_FH_mentionName));}
    
  /** setter for mentionName - sets The name of this mention. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setMentionName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_mentionName), v);
  }    
    
   
    
  //*--------------*
  //* Feature: mentionID

  /** getter for mentionID - gets 
   * @generated
   * @return value of the feature 
   */
  public long getMentionID() { return _getLongValueNc(wrapGetIntCatchException(_FH_mentionID));}
    
  /** setter for mentionID - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMentionID(long v) {
    _setLongValueNfc(wrapGetIntCatchException(_FH_mentionID), v);
  }    
    
   
    
  //*--------------*
  //* Feature: traversalIDs

  /** getter for traversalIDs - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getTraversalIDs() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalIDs)));}
    
  /** setter for traversalIDs - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTraversalIDs(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_traversalIDs), v);
  }    
    
    
  /** indexed getter for traversalIDs - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getTraversalIDs(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalIDs)))).get(i);} 

  /** indexed setter for traversalIDs - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTraversalIDs(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalIDs)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: traversalMentionIDs

  /** getter for traversalMentionIDs - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getTraversalMentionIDs() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalMentionIDs)));}
    
  /** setter for traversalMentionIDs - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setTraversalMentionIDs(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_traversalMentionIDs), v);
  }    
    
    
  /** indexed getter for traversalMentionIDs - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getTraversalMentionIDs(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalMentionIDs)))).get(i);} 

  /** indexed setter for traversalMentionIDs - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setTraversalMentionIDs(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_traversalMentionIDs)))).set(i, v);
  }  
  }

    