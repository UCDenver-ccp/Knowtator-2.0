

   
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
import org.apache.uima.jcas.cas.StringArray;


/** The CCPDocumentInformation annotation includes document metadata such as the document ID, document collection ID, secondary document IDs, document size, etc.
 * Updated by JCasGen Sat Mar 31 19:49:28 MDT 2018
 * XML source: E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml
 * @generated */
public class CCPDocumentInformation extends TOP {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.annotation.CCPDocumentInformation";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPDocumentInformation.class);
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
   
  public final static String _FeatName_documentID = "documentID";
  public final static String _FeatName_documentCollectionID = "documentCollectionID";
  public final static String _FeatName_documentSize = "documentSize";
  public final static String _FeatName_secondaryDocumentIDs = "secondaryDocumentIDs";
  public final static String _FeatName_classificationType = "classificationType";
  public final static String _FeatName_encoding = "encoding";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_documentID = TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "documentID");
  private final static MethodHandle _FH_documentID = _FC_documentID.dynamicInvoker();
  private final static CallSite _FC_documentCollectionID = TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "documentCollectionID");
  private final static MethodHandle _FH_documentCollectionID = _FC_documentCollectionID.dynamicInvoker();
  private final static CallSite _FC_documentSize = TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "documentSize");
  private final static MethodHandle _FH_documentSize = _FC_documentSize.dynamicInvoker();
  private final static CallSite _FC_secondaryDocumentIDs = TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "secondaryDocumentIDs");
  private final static MethodHandle _FH_secondaryDocumentIDs = _FC_secondaryDocumentIDs.dynamicInvoker();
  private final static CallSite _FC_classificationType = TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "classificationType");
  private final static MethodHandle _FH_classificationType = _FC_classificationType.dynamicInvoker();
  private final static CallSite _FC_encoding = TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "encoding");
  private final static MethodHandle _FH_encoding = _FC_encoding.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPDocumentInformation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPDocumentInformation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPDocumentInformation(JCas jcas) {
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
  //* Feature: documentID

  /** getter for documentID - gets The document ID is a String representing a unique identifier for a particular document within a particular document collection.
   * @generated
   * @return value of the feature 
   */
  public String getDocumentID() { return _getStringValueNc(wrapGetIntCatchException(_FH_documentID));}
    
  /** setter for documentID - sets The document ID is a String representing a unique identifier for a particular document within a particular document collection. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentID(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_documentID), v);
  }    
    
   
    
  //*--------------*
  //* Feature: documentCollectionID

  /** getter for documentCollectionID - gets The document collection ID is an Integer that uniquely identifies a particular document collection.
   * @generated
   * @return value of the feature 
   */
  public int getDocumentCollectionID() { return _getIntValueNc(wrapGetIntCatchException(_FH_documentCollectionID));}
    
  /** setter for documentCollectionID - sets The document collection ID is an Integer that uniquely identifies a particular document collection. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentCollectionID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_documentCollectionID), v);
  }    
    
   
    
  //*--------------*
  //* Feature: documentSize

  /** getter for documentSize - gets The size of a document is logged as the number of characters it contains.
   * @generated
   * @return value of the feature 
   */
  public int getDocumentSize() { return _getIntValueNc(wrapGetIntCatchException(_FH_documentSize));}
    
  /** setter for documentSize - sets The size of a document is logged as the number of characters it contains. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentSize(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_documentSize), v);
  }    
    
   
    
  //*--------------*
  //* Feature: secondaryDocumentIDs

  /** getter for secondaryDocumentIDs - gets This StringArray is used for secondary document ID storage. For example, in the biomedical domain, a particular document might be associated with a PubMed ID, however it might also have a deprecated Medline ID, or perhaps a PubMed Central ID, either of which could be stored in this StringArray. It is recommended that the type of ID along with the ID itself be stored, e.g. "MedlineID:12345".
   * @generated
   * @return value of the feature 
   */
  public StringArray getSecondaryDocumentIDs() { return (StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_secondaryDocumentIDs)));}
    
  /** setter for secondaryDocumentIDs - sets This StringArray is used for secondary document ID storage. For example, in the biomedical domain, a particular document might be associated with a PubMed ID, however it might also have a deprecated Medline ID, or perhaps a PubMed Central ID, either of which could be stored in this StringArray. It is recommended that the type of ID along with the ID itself be stored, e.g. "MedlineID:12345". 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSecondaryDocumentIDs(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_secondaryDocumentIDs), v);
  }    
    
    
  /** indexed getter for secondaryDocumentIDs - gets an indexed value - This StringArray is used for secondary document ID storage. For example, in the biomedical domain, a particular document might be associated with a PubMed ID, however it might also have a deprecated Medline ID, or perhaps a PubMed Central ID, either of which could be stored in this StringArray. It is recommended that the type of ID along with the ID itself be stored, e.g. "MedlineID:12345".
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public String getSecondaryDocumentIDs(int i) {
     return ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_secondaryDocumentIDs)))).get(i);} 

  /** indexed setter for secondaryDocumentIDs - sets an indexed value - This StringArray is used for secondary document ID storage. For example, in the biomedical domain, a particular document might be associated with a PubMed ID, however it might also have a deprecated Medline ID, or perhaps a PubMed Central ID, either of which could be stored in this StringArray. It is recommended that the type of ID along with the ID itself be stored, e.g. "MedlineID:12345".
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSecondaryDocumentIDs(int i, String v) {
    ((StringArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_secondaryDocumentIDs)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: classificationType

  /** getter for classificationType - gets This String provides a means for classifying a particular document.
   * @generated
   * @return value of the feature 
   */
  public String getClassificationType() { return _getStringValueNc(wrapGetIntCatchException(_FH_classificationType));}
    
  /** setter for classificationType - sets This String provides a means for classifying a particular document. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassificationType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_classificationType), v);
  }    
    
   
    
  //*--------------*
  //* Feature: encoding

  /** getter for encoding - gets 
   * @generated
   * @return value of the feature 
   */
  public String getEncoding() { return _getStringValueNc(wrapGetIntCatchException(_FH_encoding));}
    
  /** setter for encoding - sets  
   * @generated
   * @param v value to set into the feature 
   */
  public void setEncoding(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_encoding), v);
  }    
    
  }

    