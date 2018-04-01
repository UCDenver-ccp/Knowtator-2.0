

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 19:49:28 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.mention;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.cas.DoubleArray;


/** 
 * Updated by JCasGen Sat Mar 31 19:49:28 MDT 2018
 * XML source: E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml
 * @generated */
public class CCPDoubleSlotMention extends CCPPrimitiveSlotMention {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.mention.CCPDoubleSlotMention";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPDoubleSlotMention.class);
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
  private final static CallSite _FC_slotValues = TypeSystemImpl.createCallSite(CCPDoubleSlotMention.class, "slotValues");
  private final static MethodHandle _FH_slotValues = _FC_slotValues.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPDoubleSlotMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPDoubleSlotMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPDoubleSlotMention(JCas jcas) {
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
  public DoubleArray getSlotValues() { return (DoubleArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)));}
    
  /** setter for slotValues - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setSlotValues(DoubleArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_slotValues), v);
  }    
    
    
  /** indexed getter for slotValues - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public double getSlotValues(int i) {
     return ((DoubleArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)))).get(i);} 

  /** indexed setter for slotValues - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSlotValues(int i, double v) {
    ((DoubleArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_slotValues)))).set(i, v);
  }  
  }

    