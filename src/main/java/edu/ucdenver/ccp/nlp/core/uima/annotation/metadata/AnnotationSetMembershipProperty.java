

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 18:02:20 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.annotation.metadata;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;


import edu.ucdenver.ccp.nlp.core.uima.annotation.CCPAnnotationSet;


/** A metadata property for declaring AnnotationSet membership. This will eventually replace the annotationSets field that is currently part of the CCPTextAnnotation class.
 * Updated by JCasGen Sat Mar 31 18:49:37 MDT 2018
 * XML source: E:/Documents/RoomNumberAnnotator/desc/KnowtatorAnnotatorDescriptor.xml
 * @generated */
public class AnnotationSetMembershipProperty extends AnnotationMetadataProperty {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationSetMembershipProperty";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnnotationSetMembershipProperty.class);
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
   
  public final static String _FeatName_annotationSet = "annotationSet";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_annotationSet = TypeSystemImpl.createCallSite(AnnotationSetMembershipProperty.class, "annotationSet");
  private final static MethodHandle _FH_annotationSet = _FC_annotationSet.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected AnnotationSetMembershipProperty() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public AnnotationSetMembershipProperty(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AnnotationSetMembershipProperty(JCas jcas) {
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
  //* Feature: annotationSet

  /** getter for annotationSet - gets 
   * @generated
   * @return value of the feature 
   */
  public CCPAnnotationSet getAnnotationSet() { return (CCPAnnotationSet)(_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSet)));}
    
  /** setter for annotationSet - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotationSet(CCPAnnotationSet v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_annotationSet), v);
  }    
    
  }

    