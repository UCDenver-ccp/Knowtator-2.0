/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.model.text.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.KnowtatorDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.collection.KnowtatorCollection;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.File;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GraphSpaceCollection extends KnowtatorCollection<GraphSpace> implements KnowtatorXMLIO, BratStandoffIO {
    private final KnowtatorController controller;
    private final TextSource textSource;

    public GraphSpaceCollection(KnowtatorController controller, TextSource textSource) {
        super();
        this.controller = controller;
        this.textSource = textSource;
    }

  /*
  GETTERS
   */



  /*
  SETTERS
   */

    @Override
    public void setSelection(GraphSpace graphSpace) {
        if (graphSpace != null) {
            super.setSelection(graphSpace);
        }
    }

  /*
  ADDERS
   */


    /*
    WRITERS
     */
    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        forEach(graphSpace -> graphSpace.writeToKnowtatorXML(dom, parent));
    }


    /*
    READERS
     */
    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
        for (Node graphSpaceNode :
                KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.GRAPH_SPACE))) {
            Element graphSpaceElem = (Element) graphSpaceNode;

            String id = graphSpaceElem.getAttribute(KnowtatorXMLAttributes.ID);

            GraphSpace graphSpace = new GraphSpace(controller, textSource, id);
            add(graphSpace);

            graphSpace.readFromKnowtatorXML(null, graphSpaceElem);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {
    }


    String verifyID(String id, String idPrefix) {
        if (id == null) {
            id = String.format("%s_0", idPrefix);
        }
        List<String> ids = new ArrayList<>();
        for (GraphSpace graphSpace : this) {
            for (Object cell : graphSpace.getChildVertices(graphSpace.getDefaultParent())) {
                ids.add(((KnowtatorDataObjectInterface) cell).getId());
            }
        }

        while (ids.contains(id)) {
            int vertexIDIndex = Integer.parseInt(id.split(String.format("%s_", idPrefix))[1]);
            id = String.format("%s_%d", idPrefix, ++vertexIDIndex);
        }

        return id;
    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {

    }

    @Override
    public void writeToBratStandoff(Writer writer, Map<String, Map<String, String>> annotationConfig, Map<String, Map<String, String>> visualConfig) {

    }

}
