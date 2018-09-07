package edu.ucdenver.ccp.knowtator.model.text.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.KnowtatorTextBoundObject;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;

public class AnnotationNode extends mxCell implements KnowtatorTextBoundObject, KnowtatorXMLIO {

	private Annotation annotation;
	private TextSource textSource;

	AnnotationNode(KnowtatorController controller, String id, Annotation annotation, TextSource textSource, double x, double y) {
		super(
				annotation.getSpannedText(),
				new mxGeometry(x, y, 150, 150),
				"fontSize=16;fontColor=black;strokeColor=black");
		this.textSource = textSource;
		this.annotation = annotation;

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
		vertexElem.setAttribute(KnowtatorXMLTags.ANNOTATION, annotation.getId());
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

	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public TextSource getTextSource() {
		return textSource;
	}

	@Override
	public void dispose() {

	}
}
