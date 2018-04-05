package edu.ucdenver.ccp.knowtator.model.textsource;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.model.io.knowtator.OldKnowtatorXMLAttributes;
import org.apache.commons.io.FilenameUtils;
import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.*;

public class TextSourceManager implements Savable, OWLOntologyChangeListener {
    private Logger log = Logger.getLogger(TextSourceManager.class);


    private List<TextSource> textSources;
    private KnowtatorManager manager;

    public TextSourceManager(KnowtatorManager manager) {
        this.manager = manager;
        textSources = new ArrayList<>();
    }

    public TextSource addTextSource(File file, String fileLocation) {
        String docID = FilenameUtils.getBaseName(fileLocation);
        Optional<TextSource> newTextSourceMatch = textSources.stream().filter(textSource -> textSource.getDocID().equals(docID)).findAny();
        if (!newTextSourceMatch.isPresent()) {
            TextSource newTextSource = new TextSource(manager, file, docID);
            textSources.add(newTextSource);
            return newTextSource;
        } else {
            log.warn(docID + " is not null");
            return newTextSourceMatch.get();
        }
    }

    public List<TextSource> getTextSources() {
        return textSources;
    }

    @Override
    public void writeToKnowtatorXML(Document dom, Element parent) {
        textSources.forEach(textSource -> textSource.writeToKnowtatorXML(dom, parent));

    }

    @Override
    public void readFromKnowtatorXML(File file, Element parent, String content) {
        for (Node documentNode : KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.DOCUMENT))) {
            Element documentElement = (Element) documentNode;
            String documentID = documentElement.getAttribute(KnowtatorXMLAttributes.ID);
            TextSource newTextSource = addTextSource(file, documentID);
            log.warn("\tXML: " + newTextSource);
            newTextSource.readFromKnowtatorXML(null, documentElement, null);
        }
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent, String content) {

        String docID = parent.getAttribute(OldKnowtatorXMLAttributes.TEXT_SOURCE).replace(".txt", "");
        TextSource newTextSource = addTextSource(file, docID);
        log.warn("\tOLD XML: " + newTextSource);
        newTextSource.readFromOldKnowtatorXML(null, parent, null);
    }

    @Override
    public void readFromBratStandoff(File file, Map<Character, List<String[]>> annotationMap, String content) {
        String docID = annotationMap.get(StandoffTags.DOCID).get(0)[0];

        TextSource newTextSource = addTextSource(file, docID);
        log.warn("\tBRAT: " + newTextSource);
        newTextSource.readFromBratStandoff(null, annotationMap, null);
    }


    @SuppressWarnings("RedundantThrows")
    @Override
    public void writeToBratStandoff(Writer writer) throws IOException {
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

    public KnowtatorManager getManager() {
        return manager;
    }

    /*
    React to changes in the ontology. For now, only handling entity renaming
     */
    @Override
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {

        log.warn("Ontology Change Event");
        textSources.forEach(textSource -> textSource.getAnnotationManager().getGraphSpaces().forEach(graphSpace -> {
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
