/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.xml.XmlTags;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Triple extends mxCell implements Savable {
    private final String quantifier;
    private final String quantifierValue;
    private Profile annotator;

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
//        setValue(property);
    }

    public void writeToXml(Document dom, Element graphElem) {
        Element tripleElem = dom.createElement(XmlTags.TRIPLE);
        tripleElem.setAttribute(XmlTags.ID, id);
        tripleElem.setAttribute(XmlTags.ANNOTATOR, annotator.getId());
        tripleElem.setAttribute(XmlTags.TRIPLE_SUBJECT, getSource().getId());
        tripleElem.setAttribute(XmlTags.TRIPLE_OBJECT, getTarget().getId());
        tripleElem.setAttribute(XmlTags.TRIPLE_PROPERTY, getValue() instanceof OWLObjectProperty ? ((OWLObjectProperty) getValue()).getIRI().getShortForm() : getValue().toString());
        tripleElem.setAttribute(XmlTags.TRIPLE_QUANTIFIER, quantifier);
        tripleElem.setAttribute(XmlTags.TRIPLE_VALUE, quantifierValue);
        graphElem.appendChild(tripleElem);
    }

    @Override
    public void readFromXml(Element parent, String content) {

    }

    @Override
    public void readFromOldXml(Element parent) {

    }
}
