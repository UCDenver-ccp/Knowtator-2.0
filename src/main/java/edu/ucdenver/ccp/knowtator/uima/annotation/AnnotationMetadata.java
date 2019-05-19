/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.annotation;

import edu.ucdenver.ccp.knowtator.uima.annotation.metadata.AnnotationMetadataProperty;
import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.jcas.cas.TOP;

/**
 * A class to store annotation metadata, provenance, etc. Updated by JCasGen Fri Apr 06 16:53:13 MDT
 * 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
@SuppressWarnings("unchecked")
public class AnnotationMetadata extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "edu.ucdenver.ccp.knowtator.uima.annotation.AnnotationMetadata";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(AnnotationMetadata.class);
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

  /** The constant _FeatName_confidence. */
  public static final String _FeatName_confidence = "confidence";
  /** The constant _FeatName_metadataProperties. */
  public static final String _FeatName_metadataProperties = "metadataProperties";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_confidence =
      TypeSystemImpl.createCallSite(AnnotationMetadata.class, "confidence");
  private static final MethodHandle _FH_confidence = _FC_confidence.dynamicInvoker();
  private static final CallSite _FC_metadataProperties =
      TypeSystemImpl.createCallSite(AnnotationMetadata.class, "metadataProperties");
  private static final MethodHandle _FH_metadataProperties =
      _FC_metadataProperties.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected AnnotationMetadata() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public AnnotationMetadata(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Annotation metadata.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public AnnotationMetadata(JCas jcas) {
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
  // * Feature: confidence

  /**
   * getter for confidence - gets
   *
   * @return value of the feature
   * @generated
   */
  public float getConfidence() {
    return _getFloatValueNc(wrapGetIntCatchException(_FH_confidence));
  }

  /**
   * setter for confidence - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setConfidence(float v) {
    _setFloatValueNfc(wrapGetIntCatchException(_FH_confidence), v);
  }

  // *--------------*
  // * Feature: metadataProperties

  /**
   * getter for metadataProperties - gets
   *
   * @return value of the feature
   * @generated
   */
  public FSArray getMetadataProperties() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_metadataProperties)));
  }

  /**
   * setter for metadataProperties - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setMetadataProperties(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_metadataProperties), v);
  }

  /**
   * indexed getter for metadataProperties - gets an indexed value -
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public AnnotationMetadataProperty getMetadataProperties(int i) {
    return (AnnotationMetadataProperty)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_metadataProperties)))).get(i));
  }

  /**
   * indexed setter for metadataProperties - sets an indexed value -
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setMetadataProperties(int i, AnnotationMetadataProperty v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_metadataProperties)))).set(i, v);
  }
}
