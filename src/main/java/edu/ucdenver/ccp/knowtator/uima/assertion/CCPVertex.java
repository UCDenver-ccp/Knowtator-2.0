/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.assertion;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP;

/**
 * Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPVertex extends TOP {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName = "edu.ucdenver.ccp.knowtator.uima.assertion.CCPVertex";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPVertex.class);
  /**
   * The constant type.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int type = typeIndexID;

  /** The constant _FeatName_annotation. */
  public static final String _FeatName_annotation = "annotation";

  /** The constant _FeatName_vertexID. */
  /* *******************
   *   Feature Offsets *
   * *******************/
  public static final String _FeatName_vertexID = "vertexID";
  /* Feature Adjusted Offsets */
  private static final CallSite _FC_annotation =
      TypeSystemImpl.createCallSite(CCPVertex.class, "annotation");
  private static final MethodHandle _FH_annotation = _FC_annotation.dynamicInvoker();
  private static final CallSite _FC_vertexID =
      TypeSystemImpl.createCallSite(CCPVertex.class, "vertexID");
  private static final MethodHandle _FH_vertexID = _FC_vertexID.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPVertex() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPVertex(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp vertex.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPVertex(JCas jcas) {
    super(jcas);
    readObject();
  }

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
  // * Feature: annotation

  /**
   * getter for annotation - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getAnnotation() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_annotation));
  }

  /**
   * setter for annotation - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotation(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_annotation), v);
  }

  // *--------------*
  // * Feature: vertexID

  /**
   * getter for vertexID - gets
   *
   * @return value of the feature
   * @generated
   */
  public String getVertexID() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_vertexID));
  }

  /**
   * setter for vertexID - sets
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setVertexID(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_vertexID), v);
  }
}
