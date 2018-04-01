

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 19:49:28 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.annotation;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import org.apache.uima.jcas.cas.TOP;


/** The annotation set provides a means for arbitrarily categorizing or clustering groups of annotations. Annotations can be associated with multiple annotation groups. Examples of use include, defining Gold Standard annotation sets, and delineating between the use of different parameters during annotation, among others. Each annotation set is associated with a unique ID, a name and a description.
 * Updated by JCasGen Sat Mar 31 19:49:28 MDT 2018
 * XML source: E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml
 * @generated */
public class CCPAnnotationSet extends TOP {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPAnnotationSet.class);
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
   
  public final static String _FeatName_annotationSetID = "annotationSetID";
  public final static String _FeatName_annotationSetName = "annotationSetName";
  public final static String _FeatName_annotationSetDescription = "annotationSetDescription";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_annotationSetID = TypeSystemImpl.createCallSite(CCPAnnotationSet.class, "annotationSetID");
  private final static MethodHandle _FH_annotationSetID = _FC_annotationSetID.dynamicInvoker();
  private final static CallSite _FC_annotationSetName = TypeSystemImpl.createCallSite(CCPAnnotationSet.class, "annotationSetName");
  private final static MethodHandle _FH_annotationSetName = _FC_annotationSetName.dynamicInvoker();
  private final static CallSite _FC_annotationSetDescription = TypeSystemImpl.createCallSite(CCPAnnotationSet.class, "annotationSetDescription");
  private final static MethodHandle _FH_annotationSetDescription = _FC_annotationSetDescription.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPAnnotationSet() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPAnnotationSet(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPAnnotationSet(JCas jcas) {
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
  //* Feature: annotationSetID

  /** getter for annotationSetID - gets An integer uniquely identifying a particular annotation set.
   * @generated
   * @return value of the feature 
   */
  public int getAnnotationSetID() { return _getIntValueNc(wrapGetIntCatchException(_FH_annotationSetID));}
    
  /** setter for annotationSetID - sets An integer uniquely identifying a particular annotation set. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotationSetID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_annotationSetID), v);
  }    
    
   
    
  //*--------------*
  //* Feature: annotationSetName

  /** getter for annotationSetName - gets The name of the annotation set.
   * @generated
   * @return value of the feature 
   */
  public String getAnnotationSetName() { return _getStringValueNc(wrapGetIntCatchException(_FH_annotationSetName));}
    
  /** setter for annotationSetName - sets The name of the annotation set. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotationSetName(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_annotationSetName), v);
  }    
    
   
    
  //*--------------*
  //* Feature: annotationSetDescription

  /** getter for annotationSetDescription - gets A textual description of an annotation set.
   * @generated
   * @return value of the feature 
   */
  public String getAnnotationSetDescription() { return _getStringValueNc(wrapGetIntCatchException(_FH_annotationSetDescription));}
    
  /** setter for annotationSetDescription - sets A textual description of an annotation set. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotationSetDescription(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_annotationSetDescription), v);
  }    
    
  }

    