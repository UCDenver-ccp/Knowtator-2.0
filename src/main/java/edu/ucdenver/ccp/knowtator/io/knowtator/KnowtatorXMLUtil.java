package edu.ucdenver.ccp.knowtator.io.knowtator;

import edu.ucdenver.ccp.knowtator.io.BasicIOUtil;
import edu.ucdenver.ccp.knowtator.model.Savable;
import edu.ucdenver.ccp.knowtator.model.profile.ProfileManager;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSourceManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public final class KnowtatorXMLUtil extends OldKnowatorUtil implements BasicIOUtil {
    private static final Logger log = Logger.getLogger(KnowtatorXMLUtil.class);

    @Override
    public void read(Savable savable, File file) throws IOException {
        if (file.isDirectory()) {
            Files.newDirectoryStream(Paths.get(file.toURI()),
                    path -> path.toString().endsWith(".xml"))
                    .forEach(inputFile -> readFromInputFile(savable, inputFile.toFile()));
        } else {
            readFromInputFile(savable, file);
        }
    }

    private void readFromInputFile(Savable savable, File file) {
        try {
            /*
            doc parses the XML into a graph
             */
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputStream is = new FileInputStream(file);

            Document doc;
            try {
                doc = db.parse(is);
                doc.getDocumentElement().normalize();

                List<Node> knowtatorNodes = asList(doc.getElementsByTagName(KnowtatorXMLTags.KNOWTATOR_PROJECT));
                if (knowtatorNodes.size() > 0) {
                    Element knowtatorElement = (Element) knowtatorNodes.get(0);
                    savable.readFromKnowtatorXML(file, knowtatorElement, null);
                }

                List<Node> annotationNodes = asList(doc.getElementsByTagName(OldKnowtatorXMLTags.ANNOTATIONS));
                if (annotationNodes.size() > 0) {
                    savable.readFromOldKnowtatorXML(file, doc.getDocumentElement(), null);
                }
            } catch (IllegalArgumentException | IOException | SAXException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void write(Savable savable, File file) {
        if (savable instanceof TextSourceManager) {
            ((TextSourceManager) savable).getTextSources().forEach(textSource -> {
                File outputFile = textSource.getSaveFile();
                if (outputFile == null) {
                    String fileName = textSource.getId();
                    String extension = fileName.endsWith(".xml") ? "" : ".xml";
                    outputFile = new File(
                            file.getAbsolutePath() +
                                    File.separator +
                                    fileName +
                                    extension
                    );
                }
                writeToOutputFile(textSource, outputFile);
            });
        } else if(savable instanceof ProfileManager){
            ((ProfileManager) savable).getProfiles().values().forEach(profile -> {
                File outputFile = new File(
                        file.getAbsolutePath() +
                                File.separator +
                                profile.getId() +
                                ".xml"
                );
                writeToOutputFile(profile, outputFile);
            });
        } else {
            writeToOutputFile(savable, file);
        }

    }

    private void writeToOutputFile(Savable savable, File outputFile) {
        Document dom;

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            log.warn("Writing to " + outputFile.getAbsolutePath());
            DocumentBuilder db = dbf.newDocumentBuilder();
            dom = db.newDocument();

            Element root = dom.createElement(KnowtatorXMLTags.KNOWTATOR_PROJECT);
            dom.appendChild(root);
            savable.writeToKnowtatorXML(dom, root);

            finishWritingXML(dom, outputFile);
        } catch (ParserConfigurationException e1) {
            e1.printStackTrace();
        }
    }

}
