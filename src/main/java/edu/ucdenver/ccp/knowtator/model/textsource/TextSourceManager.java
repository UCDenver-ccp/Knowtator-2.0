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

package edu.ucdenver.ccp.knowtator.model.textsource;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.OldXmlTags;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class TextSourceManager implements Savable, OWLOntologyChangeListener {
    private Logger log = Logger.getLogger(TextSourceManager.class);


    private Map<String, TextSource> textSources;
    private KnowtatorManager manager;

    public TextSourceManager(KnowtatorManager manager) {
        this.manager = manager;
        textSources = new HashMap<>();
    }

    public TextSource addTextSource(String fileLocation) {
        String docID = FilenameUtils.getBaseName(fileLocation);
        TextSource newTextSource = textSources.get(docID);
        if (newTextSource == null) {
            newTextSource = new TextSource(manager, docID);
            textSources.put(docID, newTextSource);
        } else {
            log.warn(docID + " is not null");
        }

        return newTextSource;
    }

    public Map<String, TextSource> getTextSources() {
        return textSources;
    }

    @Override
    public void writeToKnowtatorXml(Document dom, Element parent) {
        textSources.values().forEach(textSource -> textSource.writeToKnowtatorXml(dom, parent));

    }

    @Override
    public void readFromKnowtatorXml(Element parent, String content) {
        for (Node documentNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.DOCUMENT))) {
            Element documentElement = (Element) documentNode;
            String documentID = documentElement.getAttribute(KnowtatorXMLTags.ID);
            TextSource newTextSource = addTextSource(documentID);
            log.warn("\tXML: " + newTextSource);
            newTextSource.readFromKnowtatorXml(documentElement, content);
        }
    }

    @Override
    public void readFromOldKnowtatorXml(Element parent) {

        String docID = parent.getAttribute(OldXmlTags.TEXT_SOURCE).replace(".txt", "");
        TextSource newTextSource = addTextSource(docID);
        log.warn("\tOLD XML: " + newTextSource);
        newTextSource.readFromOldKnowtatorXml(parent);
    }

    @Override
    public void readFromBratStandoff(Map<Character, List<String[]>> annotationMap, String content) {
        String docID = annotationMap.get(StandoffTags.DOCID).get(0)[0];

        TextSource newTextSource = addTextSource(docID);
        log.warn("\tBRAT: " + newTextSource);
        newTextSource.readFromBratStandoff(annotationMap, null);
    }

    @SuppressWarnings("RedundantThrows")
    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {
        textSources.values().forEach(textSource -> {
            try {
                textSource.writeToBratStandoff(writer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void readFromGeniaXml(Element parent, String content) {

    }

    public KnowtatorManager getManager() {
        return manager;
    }

    /*
    React to changes in the ontology. For now, only handling entity renaming
     */
    @Override
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {

        log.warn("Ontology Change Event");
        textSources.values().forEach(textSource -> textSource.getAnnotationManager().getGraphSpaces().forEach(graphSpace -> {
            Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
            Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
            OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
            OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

            for(OWLOntologyChange chg : changes) {
                if(chg.isAxiomChange()) {
                    OWLAxiomChange axChg = (OWLAxiomChange) chg;
//                    log.warn(String.format("Axiom Change: %s", axChg));
                    if (axChg.getAxiom().getAxiomType() == AxiomType.DECLARATION) {
                        if (axChg instanceof AddAxiom) {
                            axChg.getAxiom().accept(addedCollector);
                        } else {
                            axChg.getAxiom().accept(removedCollector);
                        }
                    }
                }
            }
            possiblyAddedEntities.forEach(owlEntity -> log.warn(String.format("Added: %s", owlEntity)));

            possiblyRemovedEntities.forEach(owlEntity -> log.warn(String.format("Removed: %s", owlEntity)));

            /*
            For now, I will assume that entity removed is the one that existed and the one
            that is added is the new name for it.
             */
            graphSpace.reassignProperty(possiblyRemovedEntities.iterator().next(), possiblyAddedEntities.iterator().next());
            graphSpace.reDrawGraph();
        }));
    }
}
