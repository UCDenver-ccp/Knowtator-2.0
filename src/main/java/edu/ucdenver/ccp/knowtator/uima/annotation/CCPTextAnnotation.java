/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.annotation;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;

/**
 * The CCP TextAnnotation extends the base annotation class to include an annotation ID, the
 * capability for multiple annotation spans, a link to the annotator responsible for generating the
 * annotation, membership to annotation sets, and a link to a class mention which defines the class
 * of this annotation. Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018 XML source:
 * E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
@SuppressWarnings("unchecked")
public class CCPTextAnnotation extends CCPAnnotation {

  /**
   * The constant _TypeName.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final String _TypeName =
      "edu.ucdenver.ccp.knowtator.uima.annotation.CCPTextAnnotation";

  /**
   * The constant typeIndexID.
   *
   * @generated
   * @ordered
   */
  @SuppressWarnings("hiding")
  public static final int typeIndexID = JCasRegistry.register(CCPTextAnnotation.class);
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

  /** The constant _FeatName_annotationID. */
  public static final String _FeatName_annotationID = "annotationID";
  /** The constant _FeatName_annotator. */
  public static final String _FeatName_annotator = "annotator";
  /** The constant _FeatName_documentSectionID. */
  public static final String _FeatName_documentSectionID = "documentSectionID";
  /** The constant _FeatName_annotationSets. */
  public static final String _FeatName_annotationSets = "annotationSets";
  /** The constant _FeatName_numberOfSpans. */
  public static final String _FeatName_numberOfSpans = "numberOfSpans";
  /** The constant _FeatName_spans. */
  public static final String _FeatName_spans = "spans";
  /** The constant _FeatName_owlClass. */
  public static final String _FeatName_owlClass = "owlClass";

  /* Feature Adjusted Offsets */
  private static final CallSite _FC_annotationID =
      TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "annotationID");
  private static final MethodHandle _FH_annotationID = _FC_annotationID.dynamicInvoker();
  private static final CallSite _FC_annotator =
      TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "annotator");
  private static final MethodHandle _FH_annotator = _FC_annotator.dynamicInvoker();
  private static final CallSite _FC_documentSectionID =
      TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "documentSectionID");
  private static final MethodHandle _FH_documentSectionID = _FC_documentSectionID.dynamicInvoker();
  private static final CallSite _FC_annotationSets =
      TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "annotationSets");
  private static final MethodHandle _FH_annotationSets = _FC_annotationSets.dynamicInvoker();
  private static final CallSite _FC_numberOfSpans =
      TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "numberOfSpans");
  private static final MethodHandle _FH_numberOfSpans = _FC_numberOfSpans.dynamicInvoker();
  private static final CallSite _FC_spans =
      TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "spans");
  private static final MethodHandle _FH_spans = _FC_spans.dynamicInvoker();
  private static final CallSite _FC_owlClass =
      TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "owlClass");
  private static final MethodHandle _FH_owlClass = _FC_owlClass.dynamicInvoker();

  /**
   * Never called. Disable default constructor
   *
   * @generated
   */
  protected CCPTextAnnotation() {
    /* intentionally empty block */
  }

  /**
   * Internal - constructor used by generator
   *
   * @param type the type of this Feature Structure
   * @param casImpl the CAS this Feature Structure belongs to
   * @generated
   */
  public CCPTextAnnotation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }

  /**
   * Instantiates a new Ccp text annotation.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @generated
   */
  public CCPTextAnnotation(JCas jcas) {
    super(jcas);
    readObject();
  }

  /**
   * Instantiates a new Ccp text annotation.
   *
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA
   * @generated
   */
  public CCPTextAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
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
  // * Feature: annotationID

  /**
   * getter for annotationID - gets The annotation ID provides a means for identifying a particular
   * annotation. Setting this ID is optional. The default value should be -1.
   *
   * @return value of the feature
   * @generated
   */
  public int getAnnotationID() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_annotationID));
  }

  /**
   * setter for annotationID - sets The annotation ID provides a means for identifying a particular
   * annotation. Setting this ID is optional. The default value should be -1.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotationID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_annotationID), v);
  }

  // *--------------*
  // * Feature: annotator

  /**
   * getter for annotator - gets The annotator was responsible for generating this annotation.
   *
   * @return value of the feature
   * @generated
   */
  public CCPAnnotator getAnnotator() {
    return (CCPAnnotator) (_getFeatureValueNc(wrapGetIntCatchException(_FH_annotator)));
  }

  /**
   * setter for annotator - sets The annotator was responsible for generating this annotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotator(CCPAnnotator v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_annotator), v);
  }

  // *--------------*
  // * Feature: documentSectionID

  /**
   * getter for documentSectionID - gets The document section ID is optionally used to log what
   * section of a document this annotation is from. Values can be specified by the user. See
   * edu.uchsc.ccp.util.nlp.document.DocumentSectionTypes for a few common sections.
   *
   * @return value of the feature
   * @generated
   */
  public int getDocumentSectionID() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_documentSectionID));
  }

  /**
   * setter for documentSectionID - sets The document section ID is optionally used to log what
   * section of a document this annotation is from. Values can be specified by the user. See
   * edu.uchsc.ccp.util.nlp.document.DocumentSectionTypes for a few common sections.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setDocumentSectionID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_documentSectionID), v);
  }

  // *--------------*
  // * Feature: annotationSets

  /**
   * getter for annotationSets - gets Annotation Sets provide an arbitrary means of categorizing and
   * clustering annotations into groups.
   *
   * @return value of the feature
   * @generated
   */
  public FSArray getAnnotationSets() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSets)));
  }

  /**
   * setter for annotationSets - sets Annotation Sets provide an arbitrary means of categorizing and
   * clustering annotations into groups.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setAnnotationSets(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_annotationSets), v);
  }

  /**
   * indexed getter for annotationSets - gets an indexed value - Annotation Sets provide an
   * arbitrary means of categorizing and clustering annotations into groups.
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public CCPAnnotationSet getAnnotationSets(int i) {
    return (CCPAnnotationSet)
        (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSets)))).get(i));
  }

  /**
   * indexed setter for annotationSets - sets an indexed value - Annotation Sets provide an
   * arbitrary means of categorizing and clustering annotations into groups.
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setAnnotationSets(int i, CCPAnnotationSet v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSets)))).set(i, v);
  }

  // *--------------*
  // * Feature: numberOfSpans

  /**
   * getter for numberOfSpans - gets The number of spans comprising this annotation. The CCP
   * TextAnnotation allows the use of multiple spans for a single annotation.
   *
   * @return value of the feature
   * @generated
   */
  public int getNumberOfSpans() {
    return _getIntValueNc(wrapGetIntCatchException(_FH_numberOfSpans));
  }

  /**
   * setter for numberOfSpans - sets The number of spans comprising this annotation. The CCP
   * TextAnnotation allows the use of multiple spans for a single annotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setNumberOfSpans(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_numberOfSpans), v);
  }

  // *--------------*
  // * Feature: spans

  /**
   * getter for spans - gets This FSArray stores the CCPSpans which comprise this annotation. It
   * should be noted that for an annotation with multiple spans, the default begin and end fields
   * are set to the beginning of the first span and the end of the final span, respectively.
   *
   * @return value of the feature
   * @generated
   */
  public FSArray getSpans() {
    return (FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_spans)));
  }

  /**
   * setter for spans - sets This FSArray stores the CCPSpans which comprise this annotation. It
   * should be noted that for an annotation with multiple spans, the default begin and end fields
   * are set to the beginning of the first span and the end of the final span, respectively.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setSpans(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_spans), v);
  }

  /**
   * indexed getter for spans - gets an indexed value - This FSArray stores the CCPSpans which
   * comprise this annotation. It should be noted that for an annotation with multiple spans, the
   * default begin and end fields are set to the beginning of the first span and the end of the
   * final span, respectively.
   *
   * @param i index in the array to get
   * @return value of the element at index i
   * @generated
   */
  public CCPSpan getSpans(int i) {
    return (CCPSpan) (((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_spans)))).get(i));
  }

  /**
   * indexed setter for spans - sets an indexed value - This FSArray stores the CCPSpans which
   * comprise this annotation. It should be noted that for an annotation with multiple spans, the
   * default begin and end fields are set to the beginning of the first span and the end of the
   * final span, respectively.
   *
   * @param i index in the array to set
   * @param v value to set into the array
   * @generated
   */
  public void setSpans(int i, CCPSpan v) {
    ((FSArray) (_getFeatureValueNc(wrapGetIntCatchException(_FH_spans)))).set(i, v);
  }

  // *--------------*
  // * Feature: owlClass

  /**
   * getter for owlClass - gets The CCP Class indicates the type (or class) for this annotation.
   *
   * @return value of the feature
   * @generated
   */
  public String getOwlClass() {
    return _getStringValueNc(wrapGetIntCatchException(_FH_owlClass));
  }

  /**
   * setter for owlClass - sets The CCP Class indicates the type (or class) for this annotation.
   *
   * @param v value to set into the feature
   * @generated
   */
  public void setOwlClass(String v) {
    _setStringValueNfc(wrapGetIntCatchException(_FH_owlClass), v);
  }
}
