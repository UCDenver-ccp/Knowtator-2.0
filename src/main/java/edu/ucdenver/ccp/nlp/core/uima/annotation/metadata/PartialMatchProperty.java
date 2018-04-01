

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 18:02:20 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.annotation.metadata;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas; 
import org.apache.uima.jcas.JCasRegistry;




/** A subclass of False Positive Property. This property is used to indicate an annotation that was scored as a False Positive during an evaluation, but is a partial match to the Gold Standard. That is, what is present is correct and the reason it was scored as a False Positive is because it is missing something.
 * Updated by JCasGen Sat Mar 31 18:49:37 MDT 2018
 * XML source: E:/Documents/RoomNumberAnnotator/desc/KnowtatorAnnotatorDescriptor.xml
 * @generated */
public class PartialMatchProperty extends TruePositiveProperty {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.annotation.metadata.PartialMatchProperty";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(PartialMatchProperty.class);
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
   


  /* Feature Adjusted Offsets */

   
  /** Never called.  Disable default constructor
   * @generated */
  protected PartialMatchProperty() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public PartialMatchProperty(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public PartialMatchProperty(JCas jcas) {
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
     
}

    