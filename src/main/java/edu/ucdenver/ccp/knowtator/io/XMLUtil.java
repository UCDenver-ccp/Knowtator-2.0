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

package edu.ucdenver.ccp.knowtator.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class XMLUtil {
	private static final Logger log = LoggerFactory.getLogger(XMLUtil.class);

	public static List<Node> asList(NodeList n) {
		return n.getLength() == 0 ? Collections.emptyList() : new NodeListWrapper(n);
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

	protected static Optional<Document> startRead(File file) {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(new FileInputStream(file));
			doc.getDocumentElement().normalize();

		} catch (ParserConfigurationException | IOException | SAXException e) {
			e.printStackTrace();
		}
		return Optional.ofNullable(doc);
	}

	protected Optional<Document> startWrite(File file) {
		log.info(String.format("Writing to %s", file.getAbsolutePath()));
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			return Optional.ofNullable(db.newDocument());
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		}

		return Optional.empty();
	}

	public static void finishWritingXML(Document dom, File file) {
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
			tr.transform(new DOMSource(dom), new StreamResult(new OutputStreamWriter(os, StandardCharsets.UTF_8)));
			os.close();

		} catch (TransformerException | IOException te) {
			System.out.println(te.getMessage());
		}
	}
}
