package edu.ucdenver.ccp.knowtator.model.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class Triple extends mxCell implements Savable {
    private String quantifier;
    private String quantifierValue;
    private Profile annotator;
    private String bratID;


    public Profile getAnnotator() {
        return annotator;
    }

    Triple(String id, mxCell source, mxCell target, Object property, Profile annotator, String quantifier, String quantifierValue) {
        super(property, new mxGeometry(), null);

        getGeometry().setRelative(true);

        this.annotator = annotator;
        this.quantifier = quantifier;
        this.quantifierValue = quantifierValue;
        setEdge(true);
        setId(id);
        setSource(source);
        setTarget(target);
    }

    public void writeToKnowtatorXML(Document dom, Element graphElem) {
        Element tripleElem = dom.createElement(KnowtatorXMLTags.TRIPLE);
        tripleElem.setAttribute(KnowtatorXMLAttributes.ID, id);
        tripleElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, annotator.getId());
        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_SUBJECT, getSource().getId());
        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_OBJECT, getTarget().getId());
        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_PROPERTY, getValue() instanceof OWLObjectProperty ? ((OWLObjectProperty) getValue()).getIRI().getShortForm() : getValue().toString());
        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_QUANTIFIER, quantifier);
        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_VALUE, quantifierValue);
        graphElem.appendChild(tripleElem);
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
    public void writeToBratStandoff(Writer writer) throws IOException {

    }

    @Override
    public void readFromGeniaXML(Element parent, String content) {

    }

    @Override
    public void writeToGeniaXML(Document dom, Element parent) {

    }

    public String getBratID() {
        return bratID;
    }

    public void setBratID(String bratID) {
        this.bratID = bratID;
    }

    public Object getProperty() {
        return getValue();
    }

    public String getQuantifier() {
        return quantifier;
    }

    public String getQuantifierValue() {
        return quantifierValue;
    }
}
