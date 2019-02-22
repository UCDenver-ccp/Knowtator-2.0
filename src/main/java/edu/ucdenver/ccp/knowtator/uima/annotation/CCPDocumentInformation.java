/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.annotation;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.StringArray;
import org.apache.uima.jcas.cas.TOP;

/**
 * The CCPDocumentInformation annotation includes document metadata such as the document ID,
 * document collection ID, secondary document IDs, document size, etc. Updated by JCasGen Fri Apr 06
 * 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPDocumentInformation extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "edu.ucdenver.ccp.knowtator.uima.annotation.CCPDocumentInformation";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPDocumentInformation.class);
  /**
   * The constant type.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int type = typeIndexID;
  /**
   * Gets type index id.
   *
   * @return index of the type
   * @generated
   */
  @Override
  public int getTypeIndexID() {
    return typeIndexID;
  }

  /* *******************
   *   Feature Offsets *
   * *******************/

  /** The constant _FeatName_documentID. */
  public static final String _FeatName_documentID = "documentID";
  /** The constant _FeatName_documentCollectionID. */
  public static final String _FeatName_documentCollectionID = "documentCollectionID";
  /** The constant _FeatName_documentSize. */
  public static final String _FeatName_documentSize = "documentSize";
  /** The constant _FeatName_secondaryDocumentIDs. */
  public static final String _FeatName_secondaryDocumentIDs = "secondaryDocumentIDs";
  /** The constant _FeatName_classificationType. */
  public static final String _FeatName_classificationType = "classificationType";
  /** The constant _FeatName_encoding. */
  public static final String _FeatName_encoding = "encoding";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_documentID =
      TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "documentID");
  private static final MethodHandle _FH_documentID = _FC_documentID.dynamicInvoker();
  private static final CallSite _FC_documentCollectionID =
      TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "documentCollectionID");
  private static final MethodHandle _FH_documentCollectionID =
      _FC_documentCollectionID.dynamicInvoker();
  private static final CallSite _FC_documentSize =
      TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "documentSize");
  private static final MethodHandle _FH_documentSize = _FC_documentSize.dynamicInvoker();
  private static final CallSite _FC_secondaryDocumentIDs =
      TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "secondaryDocumentIDs");
  private static final MethodHandle _FH_secondaryDocumentIDs =
      _FC_secondaryDocumentIDs.dynamicInvoker();
  private static final CallSite _FC_classificationType =
      TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "classificationType");
  private static final MethodHandle _FH_classificationType =
      _FC_classificationType.dynamicInvoker();
  private static final CallSite _FC_encoding =
      TypeSystemImpl.createCallSite(CCPDocumentInformation.class, "encoding");
  private static final MethodHandle _FH_encoding = _FC_encoding.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPDocumentInformation() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPDocumentInformation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp document information.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPDocumentInformation(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   *
   * <!-- begin-user-doc -->
   * Write your own initialization here
   * <!-- end-user-doc -->
   *
   * @generated modifiable
   */
  private void readObject() {
    /*default - does nothing empty block */
  }

  // *--------------*
  // * Feature: documentID

  /**
   * getter for documentID - gets The document ID is a String representing a unique identifier for a
   * particular document within a particular document collection.
   *
   * @return value of the feature
   * @generated
   */
  public String getDocumentID() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_documentID));
  }

  /**
   * setter for documentID - sets The document ID is a String representing a unique identifier for a
   * particular document within a particular document collection.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setDocumentID(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_documentID), v);
  }

  // *--------------*
  // * Feature: documentCollectionID

  /**
   * getter for documentCollectionID - gets The document collection ID is an Integer that uniquely
   * identifies a particular document collection.
   *
   * @return value of the feature
   * @generated
   */
  public int getDocumentCollectionID() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_documentCollectionID));
  }

  /**
   * setter for documentCollectionID - sets The document collection ID is an Integer that uniquely
   * identifies a particular document collection.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setDocumentCollectionID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_documentCollectionID), v);
  }

  // *--------------*
  // * Feature: documentSize

  /**
   * getter for documentSize - gets The size of a document is logged as the number of characters it
   * contains.
   *
   * @return value of the feature
   * @generated
   */
  public int getDocumentSize() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_documentSize));
  }

  /**
   * setter for documentSize - sets The size of a document is logged as the number of characters it
   * contains.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setDocumentSize(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_documentSize), v);
  }

  // *--------------*
  // * Feature: secondaryDocumentIDs

  /**
   * getter for secondaryDocumentIDs - gets This StringArray is used for secondary document ID
   * storage. For example, in the biomedical domain, a particular document might be associated with
   * a PubMed ID, however it might also have a deprecated Medline ID, or perhaps a PubMed Central
   * ID, either of which could be stored in this StringArray. It is recommended that the type of ID
   * along with the ID itself be stored, e.g. "MedlineID:12345".
   *
   * @return value of the feature
   * @generated
   */
  public StringArray getSecondaryDocumentIDs() {
    return (StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_secondaryDocumentIDs)));
  }

  /**
   * setter for secondaryDocumentIDs - sets This StringArray is used for secondary document ID
   * storage. For example, in the biomedical domain, a particular document might be associated with
   * a PubMed ID, however it might also have a deprecated Medline ID, or perhaps a PubMed Central
   * ID, either of which could be stored in this StringArray. It is recommended that the type of ID
   * along with the ID itself be stored, e.g. "MedlineID:12345".
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSecondaryDocumentIDs(StringArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_secondaryDocumentIDs), v);
  }

  /**
   * indexed getter for secondaryDocumentIDs - gets an indexed value - This StringArray is used for
   * secondary document ID storage. For example, in the biomedical domain, a particular document
   * might be associated with a PubMed ID, however it might also have a deprecated Medline ID, or
   * perhaps a PubMed Central ID, either of which could be stored in this StringArray. It is
   * recommended that the type of ID along with the ID itself be stored, e.g. "MedlineID:12345".
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public String getSecondaryDocumentIDs(int i) {
    return ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_secondaryDocumentIDs))))
        .get(i);
  }

  /**
   * indexed setter for secondaryDocumentIDs - sets an indexed value - This StringArray is used for
   * secondary document ID storage. For example, in the biomedical domain, a particular document
   * might be associated with a PubMed ID, however it might also have a deprecated Medline ID, or
   * perhaps a PubMed Central ID, either of which could be stored in this StringArray. It is
   * recommended that the type of ID along with the ID itself be stored, e.g. "MedlineID:12345".
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setSecondaryDocumentIDs(int i, String v) {
    ((StringArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_secondaryDocumentIDs))))
        .set(i, v);
  }

  // *--------------*
  // * Feature: classificationType

  /**
   * getter for classificationType - gets This String provides a means for classifying a particular
   * document.
   *
   * @return value of the feature
   * @generated
   */
  public String getClassificationType() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_classificationType));
  }

  /**
   * setter for classificationType - sets This String provides a means for classifying a particular
   * document.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setClassificationType(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_classificationType), v);
  }

  // *--------------*
  // * Feature: encoding

  /**
   * getter for encoding - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getEncoding() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_encoding));
  }

  /**
   * setter for encoding - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setEncoding(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_encoding), v);
  }
}
