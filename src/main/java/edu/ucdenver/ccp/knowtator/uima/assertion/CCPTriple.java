

   
/* Apache UIMA v3 - First created by JCasGen Fri Apr 06 15:53:51 MDT 2018 */

package edu.ucdenver.ccp.knowtator.uima.assertion;

import org.apache.uima.cas.impl.CASImpl;
import org.apache.uima.cas.impl.TypeImpl;
import org.apache.uima.cas.impl.TypeSystemImpl;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.JCasRegistry;
import org.apache.uima.jcas.cas.TOP;

import java.lang.invoke.CallSite;
import java.lang.invoke.MethodHandle;


/**
 * Updated by JCasGen Fri Apr 06 16:53:14 MDT 2018
 * XML source: E:/Documents/GDrive/Projects/Knowtator/KnowtatorStandalone/src/main/resources/CcpTypeSystem.xml
 *
 * @generated
 */
public class CCPTriple extends TOP {

    /**
     * @generated
     * @ordered
     */
    @SuppressWarnings("hiding")
    public final static String _TypeName = "edu.ucdenver.ccp.knowtator.uima.assertion.CCPTriple";

    /**
     * @generated
     * @ordered
     */
    @SuppressWarnings("hiding")
    public final static int typeIndexID = JCasRegistry.register(CCPTriple.class);
    /**
     * @generated
     * @ordered
     */
    @SuppressWarnings("hiding")
    public final static int type = typeIndexID;
    public final static String _FeatName_annotator = "annotator";


    /* *******************
     *   Feature Offsets *
     * *******************/
    public final static String _FeatName_tripleID = "tripleID";
    public final static String _FeatName_object = "object";
    public final static String _FeatName_subject = "subject";
    public final static String _FeatName_property = "property";
    public final static String _FeatName_quantifier = "quantifier";
    public final static String _FeatName_quantifierValue = "quantifierValue";
    /* Feature Adjusted Offsets */
    private final static CallSite _FC_annotator = TypeSystemImpl.createCallSite(CCPTriple.class, "annotator");
    private final static MethodHandle _FH_annotator = _FC_annotator.dynamicInvoker();
    private final static CallSite _FC_tripleID = TypeSystemImpl.createCallSite(CCPTriple.class, "tripleID");
    private final static MethodHandle _FH_tripleID = _FC_tripleID.dynamicInvoker();
    private final static CallSite _FC_object = TypeSystemImpl.createCallSite(CCPTriple.class, "object");
    private final static MethodHandle _FH_object = _FC_object.dynamicInvoker();
    private final static CallSite _FC_subject = TypeSystemImpl.createCallSite(CCPTriple.class, "subject");
    private final static MethodHandle _FH_subject = _FC_subject.dynamicInvoker();
    private final static CallSite _FC_property = TypeSystemImpl.createCallSite(CCPTriple.class, "property");
    private final static MethodHandle _FH_property = _FC_property.dynamicInvoker();
    private final static CallSite _FC_quantifier = TypeSystemImpl.createCallSite(CCPTriple.class, "quantifier");
    private final static MethodHandle _FH_quantifier = _FC_quantifier.dynamicInvoker();
    private final static CallSite _FC_quantifierValue = TypeSystemImpl.createCallSite(CCPTriple.class, "quantifierValue");
    private final static MethodHandle _FH_quantifierValue = _FC_quantifierValue.dynamicInvoker();

    /**
     * Never called.  Disable default constructor
     *
     * @generated
     */
    protected CCPTriple() {/* intentionally empty block */}


    /**
     * Internal - constructor used by generator
     *
     * @param casImpl the CAS this Feature Structure belongs to
     * @param type    the type of this Feature Structure
     * @generated
     */
    public CCPTriple(TypeImpl type, CASImpl casImpl) {
        super(type, casImpl);
        readObject();
    }

    /**
     * @param jcas JCas to which this Feature Structure belongs
     * @generated
     */
    public CCPTriple(JCas jcas) {
        super(jcas);
        readObject();
    }

    /**
     * @return index of the type
     * @generated
     */
    @Override
    public int getTypeIndexID() {
        return typeIndexID;
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
    //* Feature: annotator

    /**
     * getter for annotator - gets
     *
     * @return value of the feature
     * @generated
     */
    public String getAnnotator() {
        return _getStringValueNc(wrapGetIntCatchException(_FH_annotator));
    }

    /**
     * setter for annotator - sets
     *
     * @param v value to set into the feature
     * @generated
     */
    public void setAnnotator(String v) {
        _setStringValueNfc(wrapGetIntCatchException(_FH_annotator), v);
    }


    //*--------------*
    //* Feature: tripleID

    /**
     * getter for tripleID - gets
     *
     * @return value of the feature
     * @generated
     */
    public String getTripleID() {
        return _getStringValueNc(wrapGetIntCatchException(_FH_tripleID));
    }

    /**
     * setter for tripleID - sets
     *
     * @param v value to set into the feature
     * @generated
     */
    public void setTripleID(String v) {
        _setStringValueNfc(wrapGetIntCatchException(_FH_tripleID), v);
    }


    //*--------------*
    //* Feature: object

    /**
     * getter for object - gets
     *
     * @return value of the feature
     * @generated
     */
    public CCPVertex getObject() {
        return (CCPVertex) (_getFeatureValueNc(wrapGetIntCatchException(_FH_object)));
    }

    /**
     * setter for object - sets
     *
     * @param v value to set into the feature
     * @generated
     */
    public void setObject(CCPVertex v) {
        _setFeatureValueNcWj(wrapGetIntCatchException(_FH_object), v);
    }


    //*--------------*
    //* Feature: subject

    /**
     * getter for subject - gets
     *
     * @return value of the feature
     * @generated
     */
    public CCPVertex getSubject() {
        return (CCPVertex) (_getFeatureValueNc(wrapGetIntCatchException(_FH_subject)));
    }

    /**
     * setter for subject - sets
     *
     * @param v value to set into the feature
     * @generated
     */
    public void setSubject(CCPVertex v) {
        _setFeatureValueNcWj(wrapGetIntCatchException(_FH_subject), v);
    }


    //*--------------*
    //* Feature: property

    /**
     * getter for property - gets
     *
     * @return value of the feature
     * @generated
     */
    public String getProperty() {
        return _getStringValueNc(wrapGetIntCatchException(_FH_property));
    }

    /**
     * setter for property - sets
     *
     * @param v value to set into the feature
     * @generated
     */
    public void setProperty(String v) {
        _setStringValueNfc(wrapGetIntCatchException(_FH_property), v);
    }


    //*--------------*
    //* Feature: quantifier

    /**
     * getter for quantifier - gets
     *
     * @return value of the feature
     * @generated
     */
    public String getQuantifier() {
        return _getStringValueNc(wrapGetIntCatchException(_FH_quantifier));
    }

    /**
     * setter for quantifier - sets
     *
     * @param v value to set into the feature
     * @generated
     */
    public void setQuantifier(String v) {
        _setStringValueNfc(wrapGetIntCatchException(_FH_quantifier), v);
    }


    //*--------------*
    //* Feature: quantifierValue

    /**
     * getter for quantifierValue - gets
     *
     * @return value of the feature
     * @generated
     */
    public String getQuantifierValue() {
        return _getStringValueNc(wrapGetIntCatchException(_FH_quantifierValue));
    }

    /**
     * setter for quantifierValue - sets
     *
     * @param v value to set into the feature
     * @generated
     */
    public void setQuantifierValue(String v) {
        _setStringValueNfc(wrapGetIntCatchException(_FH_quantifierValue), v);
    }

}

    