

   
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
public class CCPVertex extends TOP {

    /**
     * @generated
     * @ordered
     */
    @SuppressWarnings("hiding")
    public final static String _TypeName = "edu.ucdenver.ccp.knowtator.uima.assertion.CCPVertex";

    /**
     * @generated
     * @ordered
     */
    @SuppressWarnings("hiding")
    public final static int typeIndexID = JCasRegistry.register(CCPVertex.class);
    /**
     * @generated
     * @ordered
     */
    @SuppressWarnings("hiding")
    public final static int type = typeIndexID;
    public final static String _FeatName_annotation = "annotation";


    /* *******************
     *   Feature Offsets *
     * *******************/
    public final static String _FeatName_vertexID = "vertexID";
    /* Feature Adjusted Offsets */
    private final static CallSite _FC_annotation = TypeSystemImpl.createCallSite(CCPVertex.class, "annotation");
    private final static MethodHandle _FH_annotation = _FC_annotation.dynamicInvoker();
    private final static CallSite _FC_vertexID = TypeSystemImpl.createCallSite(CCPVertex.class, "vertexID");
    private final static MethodHandle _FH_vertexID = _FC_vertexID.dynamicInvoker();

    /**
     * Never called.  Disable default constructor
     *
     * @generated
     */
    protected CCPVertex() {/* intentionally empty block */}


    /**
     * Internal - constructor used by generator
     *
     * @param casImpl the CAS this Feature Structure belongs to
     * @param type    the type of this Feature Structure
     * @generated
     */
    public CCPVertex(TypeImpl type, CASImpl casImpl) {
        super(type, casImpl);
        readObject();
    }

    /**
     * @param jcas JCas to which this Feature Structure belongs
     * @generated
     */
    public CCPVertex(JCas jcas) {
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
    //* Feature: annotation

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


    //*--------------*
    //* Feature: vertexID

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

    