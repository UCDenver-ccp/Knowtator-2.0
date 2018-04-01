

   
/* Apache UIMA v3 - First created by JCasGen Sat Mar 31 19:49:28 MDT 2018 */

package edu.ucdenver.ccp.nlp.core.uima.annotation;

import edu.ucdenver.ccp.nlp.core.uima.mention.CCPClassMention;
import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.FSArray;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;


/** The CCP TextAnnotation extends the base annotation class to include an annotation ID, the capability for multiple annotation spans, a link to the annotator responsible for generating the annotation, membership to annotation sets, and a link to a class mention which defines the class of this annotation.
 * Updated by JCasGen Sat Mar 31 19:49:28 MDT 2018
 * XML source: E:/Documents/Knowtator-2.0/src/main/resources/KnowtatorToUIMAAnnotatorDescriptor.xml
 * @generated */
@SuppressWarnings("ALL")
public class CCPTextAnnotation extends CCPAnnotation {
 
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static String _TypeName = "edu.ucdenver.ccp.nlp.core.uima.annotation.CCPTextAnnotation";
  
  /** @generated
   * @ordered 
   */
  @SuppressWarnings ("hiding")
  public final static int typeIndexID = JCasRegistry.register(CCPTextAnnotation.class);
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
   
  public final static String _FeatName_annotationID = "annotationID";
  public final static String _FeatName_annotator = "annotator";
  public final static String _FeatName_documentSectionID = "documentSectionID";
  public final static String _FeatName_annotationSets = "annotationSets";
  public final static String _FeatName_numberOfSpans = "numberOfSpans";
  public final static String _FeatName_spans = "spans";
  public final static String _FeatName_classMention = "classMention";


  /* Feature Adjusted Offsets */
  private final static CallSite _FC_annotationID = TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "annotationID");
  private final static MethodHandle _FH_annotationID = _FC_annotationID.dynamicInvoker();
  private final static CallSite _FC_annotator = TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "annotator");
  private final static MethodHandle _FH_annotator = _FC_annotator.dynamicInvoker();
  private final static CallSite _FC_documentSectionID = TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "documentSectionID");
  private final static MethodHandle _FH_documentSectionID = _FC_documentSectionID.dynamicInvoker();
  private final static CallSite _FC_annotationSets = TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "annotationSets");
  private final static MethodHandle _FH_annotationSets = _FC_annotationSets.dynamicInvoker();
  private final static CallSite _FC_numberOfSpans = TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "numberOfSpans");
  private final static MethodHandle _FH_numberOfSpans = _FC_numberOfSpans.dynamicInvoker();
  private final static CallSite _FC_spans = TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "spans");
  private final static MethodHandle _FH_spans = _FC_spans.dynamicInvoker();
  private final static CallSite _FC_classMention = TypeSystemImpl.createCallSite(CCPTextAnnotation.class, "classMention");
  private final static MethodHandle _FH_classMention = _FC_classMention.dynamicInvoker();

   
  /** Never called.  Disable default constructor
   * @generated */
  protected CCPTextAnnotation() {/* intentionally empty block */}
    
  /** Internal - constructor used by generator 
   * @generated
   * @param casImpl the CAS this Feature Structure belongs to
   * @param type the type of this Feature Structure 
   */
  public CCPTextAnnotation(TypeImpl type, CASImpl casImpl) {
    super(type, casImpl);
    readObject();
  }
  
  /** @generated
   * @param jcas JCas to which this Feature Structure belongs 
   */
  public CCPTextAnnotation(JCas jcas) {
    super(jcas);
    readObject();   
  } 


  /** @generated
   * @param jcas JCas to which this Feature Structure belongs
   * @param begin offset to the begin spot in the SofA
   * @param end offset to the end spot in the SofA 
  */  
  public CCPTextAnnotation(JCas jcas, int begin, int end) {
    super(jcas);
    setBegin(begin);
    setEnd(end);
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
  //* Feature: annotationID

  /** getter for annotationID - gets The annotation ID provides a means for identifying a particular annotation. Setting this ID is optional. The default value should be -1.
   * @generated
   * @return value of the feature 
   */
  public int getAnnotationID() { return _getIntValueNc(wrapGetIntCatchException(_FH_annotationID));}
    
  /** setter for annotationID - sets The annotation ID provides a means for identifying a particular annotation. Setting this ID is optional. The default value should be -1. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotationID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_annotationID), v);
  }    
    
   
    
  //*--------------*
  //* Feature: annotator

  /** getter for annotator - gets The annotator was responsible for generating this annotation.
   * @generated
   * @return value of the feature 
   */
  public CCPAnnotator getAnnotator() { return (CCPAnnotator)(_getFeatureValueNc(wrapGetIntCatchException(_FH_annotator)));}
    
  /** setter for annotator - sets The annotator was responsible for generating this annotation. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotator(CCPAnnotator v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_annotator), v);
  }    
    
   
    
  //*--------------*
  //* Feature: documentSectionID

  /** getter for documentSectionID - gets The document section ID is optionally used to log what section of a document this annotation is from. Values can be specified by the user. See edu.uchsc.ccp.util.nlp.document.DocumentSectionTypes for a few common sections.
   * @generated
   * @return value of the feature 
   */
  public int getDocumentSectionID() { return _getIntValueNc(wrapGetIntCatchException(_FH_documentSectionID));}
    
  /** setter for documentSectionID - sets The document section ID is optionally used to log what section of a document this annotation is from. Values can be specified by the user. See edu.uchsc.ccp.util.nlp.document.DocumentSectionTypes for a few common sections. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setDocumentSectionID(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_documentSectionID), v);
  }    
    
   
    
  //*--------------*
  //* Feature: annotationSets

  /** getter for annotationSets - gets Annotation Sets provide an arbitrary means of categorizing and clustering annotations into groups.
   * @generated
   * @return value of the feature 
   */
  public FSArray getAnnotationSets() { return (FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSets)));}
    
  /** setter for annotationSets - sets Annotation Sets provide an arbitrary means of categorizing and clustering annotations into groups. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setAnnotationSets(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_annotationSets), v);
  }    
    
    
  /** indexed getter for annotationSets - gets an indexed value - Annotation Sets provide an arbitrary means of categorizing and clustering annotations into groups.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public CCPAnnotationSet getAnnotationSets(int i) {
     return (CCPAnnotationSet)(((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSets)))).get(i));} 

  /** indexed setter for annotationSets - sets an indexed value - Annotation Sets provide an arbitrary means of categorizing and clustering annotations into groups.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setAnnotationSets(int i, CCPAnnotationSet v) {
    ((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_annotationSets)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: numberOfSpans

  /** getter for numberOfSpans - gets The number of spans comprising this annotation. The CCP TextAnnotation allows the use of multiple spans for a single annotation.
   * @generated
   * @return value of the feature 
   */
  public int getNumberOfSpans() { return _getIntValueNc(wrapGetIntCatchException(_FH_numberOfSpans));}
    
  /** setter for numberOfSpans - sets The number of spans comprising this annotation. The CCP TextAnnotation allows the use of multiple spans for a single annotation. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setNumberOfSpans(int v) {
    _setIntValueNfc(wrapGetIntCatchException(_FH_numberOfSpans), v);
  }    
    
   
    
  //*--------------*
  //* Feature: spans

  /** getter for spans - gets This FSArray stores the CCPSpans which comprise this annotation. It should be noted that for an annotation with multiple spans, the default begin and end fields are set to the beginning of the first span and the end of the final span, respectively.
   * @generated
   * @return value of the feature 
   */
  public FSArray getSpans() { return (FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_spans)));}
    
  /** setter for spans - sets This FSArray stores the CCPSpans which comprise this annotation. It should be noted that for an annotation with multiple spans, the default begin and end fields are set to the beginning of the first span and the end of the final span, respectively. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setSpans(FSArray v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_spans), v);
  }    
    
    
  /** indexed getter for spans - gets an indexed value - This FSArray stores the CCPSpans which comprise this annotation. It should be noted that for an annotation with multiple spans, the default begin and end fields are set to the beginning of the first span and the end of the final span, respectively.
   * @generated
   * @param i index in the array to get
   * @return value of the element at index i 
   */
  public CCPSpan getSpans(int i) {
     return (CCPSpan)(((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_spans)))).get(i));} 

  /** indexed setter for spans - sets an indexed value - This FSArray stores the CCPSpans which comprise this annotation. It should be noted that for an annotation with multiple spans, the default begin and end fields are set to the beginning of the first span and the end of the final span, respectively.
   * @generated
   * @param i index in the array to set
   * @param v value to set into the array 
   */
  public void setSpans(int i, CCPSpan v) {
    ((FSArray)(_getFeatureValueNc(wrapGetIntCatchException(_FH_spans)))).set(i, v);
  }  
   
    
  //*--------------*
  //* Feature: classMention

  /** getter for classMention - gets The CCP ClassMention indicates the type (or class) for this annotation.
   * @generated
   * @return value of the feature 
   */
  public CCPClassMention getClassMention() { return (CCPClassMention)(_getFeatureValueNc(wrapGetIntCatchException(_FH_classMention)));}
    
  /** setter for classMention - sets The CCP ClassMention indicates the type (or class) for this annotation. 
   * @generated
   * @param v value to set into the feature 
   */
  public void setClassMention(CCPClassMention v) {
    _setFeatureValueNcWj(wrapGetIntCatchException(_FH_classMention), v);
  }    
    
  }

    