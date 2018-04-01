

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 19:49:28 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.mention;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;


/** A slot mention is deemed "complex" when its slot filler is a class mention as opposed to a String (See non-complex slot mention for String fillers). An example of a complex slot mention is the "transported entity" slot for the protein-transport class which would be filled with a protein class mention.
 * Updated by JCasGen Sat Mar 31 19:49:28 MDT 2018
 * XML source: E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml
 * @generated */
@SuppressWarnings("ALL")
public class CCPComplexSlotMention extends CCPSlotMention {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.mention.CCPComplexSlotMention";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPComplexSlotMention.class);
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
   
  public final static String _FeatName_classMentions = "classMentions";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_classMentions = TypeSystemImpl.createCallSite(CCPComplexSlotMention.class, "classMentions");
  private final static MethodHandle _FH_classMentions = _FC_classMentions.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPComplexSlotMention() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPComplexSlotMention(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPComplexSlotMention(JCas jcas) {
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
  //* Feature: classMentions

  /** getter for classMentions - gets The class mentions which are the slot fillers for this complex slot.
   * @generated
   * @return value of the feature 
   */
  public FSArray getClassMentions() { return (FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_classMentions)));}
    
  /** setter for classMentions - sets The class mentions which are the slot fillers for this complex slot. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassMentions(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_classMentions), v);
  }    
    
    
  /** indexed getter for classMentions - gets an indexed value - The class mentions which are the slot fillers for this complex slot.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public CCPClassMention getClassMentions(int i) {
     return (CCPClassMention)(((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_classMentions)))).get(i));} 

  /** indexed setter for classMentions - sets an indexed value - The class mentions which are the slot fillers for this complex slot.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setClassMentions(int i, CCPClassMention v) {
    ((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_classMentions)))).set(i, v);
  }  
  }

    