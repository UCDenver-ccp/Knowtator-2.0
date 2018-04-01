

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 18:02:20 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.annotation;

import edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.AnnotationMetadataProperty;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;


/** A class to store annotation metadata, provenance, etc.
 * Updated by JCasGen Sat Mar 31 18:49:36 MDT 2018
 * XML source: E:/Documents/RoomNumberAnnotator/desc/KnowtatorAnnotatorDescriptor.xml
 * @generated */
public class AnnotationMetadata extends TOP {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.annotation.AnnotationMetadata";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(AnnotationMetadata.class);
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
   
  public final static String _FeatName_confidence = "confidence";
  public final static String _FeatName_metadataProperties = "metadataProperties";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_confidence = TypeSystemImpl.createCallSite(AnnotationMetadata.class, "confidence");
  private final static MethodHandle _FH_confidence = _FC_confidence.dynamicInvoker();
  private final static CallSite _FC_metadataProperties = TypeSystemImpl.createCallSite(AnnotationMetadata.class, "metadataProperties");
  private final static MethodHandle _FH_metadataProperties = _FC_metadataProperties.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected AnnotationMetadata() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public AnnotationMetadata(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public AnnotationMetadata(JCas jcas) {
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
  //* Feature: confidence

  /** getter for confidence - gets 
   * @generated
   * @return value of the feature 
   */
  public float getConfidence() { return _getFloatValueNc(wrapGetIntCatchException(_FH_confidence));}
    
  /** setter for confidence - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setConfidence(float v) {
    _setFloatValueNfc(wrapGetIntCatchException(_FH_confidence), v);
  }    
    
   
    
  //*--------------*
  //* Feature: metadataProperties

  /** getter for metadataProperties - gets 
   * @generated
   * @return value of the feature 
   */
  public FSArray getMetadataProperties() { return (FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_metadataProperties)));}
    
  /** setter for metadataProperties - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setMetadataProperties(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_metadataProperties), v);
  }    
    
    
  /** indexed getter for metadataProperties - gets an indexed value - 
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public AnnotationMetadataProperty getMetadataProperties(int i) {
     return (AnnotationMetadataProperty)(((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_metadataProperties)))).get(i));} 

  /** indexed setter for metadataProperties - sets an indexed value - 
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setMetadataProperties(int i, AnnotationMetadataProperty v) {
    //noinspection unchecked
    ((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_metadataProperties)))).set(i, v);
  }  
  }

    