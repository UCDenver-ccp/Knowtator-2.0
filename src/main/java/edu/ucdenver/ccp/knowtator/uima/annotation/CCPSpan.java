

   
/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.annotation;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;


/** The span object holds span information.This is a supplement to the default UIMA annotation which cannot handle multi-span annotations.
 * Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018
 * XML source: E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 * @generated */
public class CCPSpan extends TOP {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.knowtator.uima.annotation.CCPSpan";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPSpan.class);
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
   
  public final static String _FeatName_spanStart = "spanStart";
  public final static String _FeatName_spanEnd = "spanEnd";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_spanStart = TypeSystemImpl.createCallSite(CCPSpan.class, "spanStart");
  private final static MethodHandle _FH_spanStart = _FC_spanStart.dynamicInvoker();
    private final static CallSite _FC_spanEnd = TypeSystemImpl.createCallSite(CCPSpan.class, "spanEnd");
  private final static MethodHandle _FH_spanEnd = _FC_spanEnd.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPSpan() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPSpan(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPSpan(JCas jcas) {
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
  //* Feature: spanStart

  /** getter for spanStart - gets The character offset for the start of the span.
   * @generated
   * @return value of the feature 
   */
  public int getSpanStart() { return _getIntValueNc(wrapGetIntCatchException(_FH_spanStart));}
    
  /** setter for spanStart - sets The character offset for the start of the span. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpanStart(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_spanStart), v);
  }    
    
   
    
  //*--------------*
  //* Feature: spanEnd

  /** getter for spanEnd - gets The character offset for the end of the span.
   * @generated
   * @return value of the feature 
   */
  public int getSpanEnd() { return _getIntValueNc(wrapGetIntCatchException(_FH_spanEnd));}
    
  /** setter for spanEnd - sets The character offset for the end of the span. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpanEnd(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_spanEnd), v);
  }    
    
  }

    