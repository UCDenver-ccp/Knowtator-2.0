package edu.ucdenver.ccp.knowtator.model.text.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public class AnnotationNode extends mxCell implements KnowtatorTextBoundObjectInterface, KnowtatorXMLIO {

	private final ConceptAnnotation conceptAnnotation;
	private final TextSource textSource;

	AnnotationNode(KnowtatorController controller, String id, ConceptAnnotation conceptAnnotation, TextSource textSource, double x, double y) {
		super(
				conceptAnnotation.getSpannedText(),
				new mxGeometry(x, y, 150, 150),
				"fontSize=16;fontColor=black;strokeColor=black");
		this.textSource = textSource;
		this.conceptAnnotation = conceptAnnotation;

		controller.verifyId(id, this, false);

		setVertex(true);
		setConnectable(true);
	}

	/*
	WRITERS
	 */

	@Override
	public void writeToKnowtatorXML(Document dom, Element parent) {
		Element vertexElem = dom.createElement(KnowtatorXMLTags.VERTEX);
		vertexElem.setAttribute(KnowtatorXMLAttributes.ID, getId());
		vertexElem.setAttribute(KnowtatorXMLTags.ANNOTATION, conceptAnnotation.getId());
		vertexElem.setAttribute(KnowtatorXMLAttributes.X_LOCATION, String.valueOf(getGeometry().getX()));
		vertexElem.setAttribute(KnowtatorXMLAttributes.Y_LOCATION, String.valueOf(getGeometry().getY()));
		parent.appendChild(vertexElem);
	}

	/*
	READERS
	 */

	@Override
	public void readFromKnowtatorXML(File file, Element parent) {
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {
	}

	/*
	GETTERS
	 */

	public ConceptAnnotation getConceptAnnotation() {
		return conceptAnnotation;
	}

	@Override
	public TextSource getTextSource() {
		return textSource;
	}

	@Override
	public void dispose() {

	}

	@Override
	public int compareTo(Object o) {
		return 0;
	}
}
