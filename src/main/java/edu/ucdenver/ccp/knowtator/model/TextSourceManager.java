package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.brat.StandoffTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLUtil;
import edu.ucdenver.ccp.knowtator.io.knowtator.OldKnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.model.collection.TextSourceCollection;
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

	private TextSourceCollection textSourceCollection;
	private KnowtatorController controller;

	public TextSourceManager(KnowtatorController controller) {
		this.controller = controller;
		textSourceCollection = new TextSourceCollection();
	}

	TextSource addTextSource(File file, String fileLocation) {
		String docID = FilenameUtils.getBaseName(fileLocation);
		Optional<TextSource> newTextSourceMatch =
				textSourceCollection
						.getData()
						.stream()
						.filter(textSource -> textSource.getId().equals(docID))
						.findAny();
		TextSource newTextSource;
		if (!newTextSourceMatch.isPresent()) {
			newTextSource = new TextSource(controller, file, docID);
			textSourceCollection.add(newTextSource);
		} else {
			log.warn(docID + " is not null");
			newTextSource = newTextSourceMatch.get();
		}
		controller.getSelectionManager().setSelected(newTextSource);
		return newTextSource;
	}

	public TextSourceCollection getTextSourceCollection() {
		return textSourceCollection;
	}

	@Override
	public void writeToKnowtatorXML(Document dom, Element parent) {
		textSourceCollection
				.getData()
				.forEach(textSource -> textSource.writeToKnowtatorXML(dom, parent));
	}

	@Override
	public void readFromKnowtatorXML(File file, Element parent, String content) {
		for (Node documentNode :
				KnowtatorXMLUtil.asList(parent.getElementsByTagName(KnowtatorXMLTags.DOCUMENT))) {
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
	public void readFromBratStandoff(
			File file, Map<Character, List<String[]>> annotationMap, String content) {
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

	@Override
	public void writeToGeniaXML(Document dom, Element parent) {
	}

	public KnowtatorController getController() {
		return controller;
	}

	/*
	React to changes in the ontology. For now, only handling entity renaming
	 */
	@Override
	public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {

		log.warn("Ontology Change Event");
		textSourceCollection
				.getData()
				.forEach(
						textSource ->
								textSource
										.getAnnotationManager()
										.getGraphSpaceCollection()
										.getData()
										.forEach(
												graphSpace -> {
													Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
													Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
													OWLEntityCollector addedCollector =
															new OWLEntityCollector(possiblyAddedEntities);
													OWLEntityCollector removedCollector =
															new OWLEntityCollector(possiblyRemovedEntities);

													for (OWLOntologyChange chg : changes) {
														if (chg.isAxiomChange()) {
															OWLAxiomChange axChg = (OWLAxiomChange) chg;
															//                    log.warn(String.format("Axiom Change: %s",
															// axChg));
															if (axChg.getAxiom().getAxiomType() == AxiomType.DECLARATION) {
																if (axChg instanceof AddAxiom) {
																	axChg.getAxiom().accept(addedCollector);
																} else {
																	axChg.getAxiom().accept(removedCollector);
																}
															}
														}
													}
													possiblyAddedEntities.forEach(
															owlEntity -> log.warn(String.format("Added: %s", owlEntity)));

													possiblyRemovedEntities.forEach(
															owlEntity -> log.warn(String.format("Removed: %s", owlEntity)));

                          /*
                          For now, I will assume that entity removed is the one that existed and the one
                          that is added is the new name for it.
                           */
													graphSpace.reassignProperty(
															possiblyRemovedEntities.iterator().next(),
															possiblyAddedEntities.iterator().next());
													graphSpace.reDrawGraph();
												}));
	}

	public void getPreviousTextSource() {
		controller
				.getSelectionManager()
				.setSelected(
						textSourceCollection.getPrevious(
								controller.getSelectionManager().getActiveTextSource()));
	}

	public void getNextTextSource() {
		controller
				.getSelectionManager()
				.setSelected(
						textSourceCollection.getNext(controller.getSelectionManager().getActiveTextSource()));
	}

	public void connectToOWLModelManager() {
		for (TextSource textSource : textSourceCollection.getData()) {
			textSource.getAnnotationManager().connectToOWLModelManager();
		}
	}
}
