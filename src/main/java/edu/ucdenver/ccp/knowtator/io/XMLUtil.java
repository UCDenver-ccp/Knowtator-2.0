package edu.ucdenver.ccp.knowtator.io;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

public class XMLUtil {
	public static List<Node> asList(NodeList n) {
		return n.getLength() == 0 ? Collections.emptyList() : new NodeListWrapper(n);
	}

	protected static void finishWritingXML(Document dom, File file) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", 2);
			Transformer tr = transformerFactory.newTransformer();
			tr.setOutputProperty(OutputKeys.INDENT, "yes");
			tr.setOutputProperty(OutputKeys.METHOD, "xml");
			tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			tr.setOutputProperty("{http://knowtator.apache.org/xslt}indent-amount", "4");

			// send DOM to test_project
			PrintWriter pw = new PrintWriter(file);
			pw.close();
			OutputStream os = new FileOutputStream(file, false);
			tr.transform(new DOMSource(dom), new StreamResult(new OutputStreamWriter(os, "utf-8")));
			os.close();

		} catch (TransformerException | IOException te) {
			System.out.println(te.getMessage());
		}
	}

	static final class NodeListWrapper extends AbstractList<Node> implements RandomAccess {
		final NodeList list;

		NodeListWrapper(NodeList l) {
			list = l;
		}

		public Node get(int index) {
			return list.item(index);
		}

		public int size() {
			return list.getLength();
		}
	}
}
