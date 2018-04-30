package edu.ucdenver.ccp.knowtator.model;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

public class AnnotationNode extends mxCell implements Savable, KnowtatorTextBoundObject {

	private Annotation annotation;
	private TextSource textSource;

	AnnotationNode(KnowtatorController controller, String id, Annotation annotation, TextSource textSource) {
		super(
				annotation.getSpannedText(),
				new mxGeometry(20, 20, 150, 150),
				"fontSize=16;fontColor=black;strokeColor=black");
		this.textSource = textSource;
		this.annotation = annotation;

		controller.verifyId(id, this, false);

		setVertex(true);
		setConnectable(true);
	}

	@Override
	public void writeToKnowtatorXML(Document dom, Element parent) {
		Element vertexElem = dom.createElement(KnowtatorXMLTags.VERTEX);
		vertexElem.setAttribute(KnowtatorXMLAttributes.ID, getId());
		vertexElem.setAttribute(KnowtatorXMLTags.ANNOTATION, annotation.getId());
		parent.appendChild(vertexElem);
	}

	@Override
	public void readFromKnowtatorXML(File file, Element parent) {
	}

	@Override
	public void readFromOldKnowtatorXML(File file, Element parent) {
	}

	@Override
	public void readFromBratStandoff(
			File file, Map<Character, List<String[]>> annotationMap, String content) {
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

	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public TextSource getTextSource() {
		return textSource;
	}
}
