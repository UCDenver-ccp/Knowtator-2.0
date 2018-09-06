

   
/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:52 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.mention;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;


/**
 * Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018
 * XML source: E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 * @generated */
public class CCPStringSlotMention extends CCPPrimitiveSlotMention {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.knowtator.uima.mention.CCPStringSlotMention";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPStringSlotMention.class);
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
   
  public final static String _FeatName_slotValues = "slotValues";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_slotValues = TypeSystemImpl.createCallSite(CCPStringSlotMention.class, "slotValues");
  private final static MethodHandle _FH_slotValues = _FC_slotValues.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPStringSlotMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPStringSlotMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPStringSlotMention(JCas jcas) {
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
  //* Feature: slotValues

  /** getter for slotValues - gets 
   * @generated
   * @return value of the feature 
   */
  public StringArray getSlotValues() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)));}
    
  /** setter for slotValues - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSlotValues(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_slotValues), v);
  }    
    
    
  /** indexed getter for slotValues - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getSlotValues(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)))).get(i);} 

  /** indexed setter for slotValues - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSlotValues(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)))).set(i, v);
  }  
  }

    