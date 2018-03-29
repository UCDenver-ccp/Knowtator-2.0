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
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.io.xml.XmlTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    public void writeToXml(Document dom, Element parent) {
        Element vertexElem = dom.createElement(XmlTags.VERTEX);
        vertexElem.setAttribute(XmlTags.ID, getId());
        vertexElem.setAttribute(XmlTags.ANNOTATION, annotation.getID());
        parent.appendChild(vertexElem);
    }

    @Override
    public void readFromXml(Element parent, String content) {

    }

    @Override
    public void readFromOldXml(Element parent) {

    }

    public Annotation getAnnotation() {
        return annotation;
    }
}
