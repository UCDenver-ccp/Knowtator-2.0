package edu.ucdenver.ccp.knowtator.model.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class AnnotationNode extends mxCell implements Savable{

    private Annotation annotation;

    AnnotationNode(String id, Annotation annotation) {
        super(annotation.getSpannedText(), new mxGeometry(20, 20, 150, 150), "fontSize=16;fontColor=black;strokeColor=black");
        this.annotation = annotation;

        setId(id);
        setVertex(true);
        setConnectable(true);
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        Element vertexElem = dom.createElement(KnowtatorXMLTags.VERTEX);
        vertexElem.setAttribute(KnowtatorXMLAttributes.ID, getId());
        vertexElem.setAttribute(KnowtatorXMLTags.ANNOTATION, annotation.getID());
        parent.appendChild(vertexElem);
    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent, String content) {

    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent, String content) {

    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void writeToBratStandoff(Writer writer) throws  IOException {

    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

//    @Override
//    public void convertToUIMA(CAS cas) {
//
//    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
