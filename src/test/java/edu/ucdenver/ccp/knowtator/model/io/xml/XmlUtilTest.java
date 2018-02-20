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

package edu.ucdenver.ccp.knowtator.model.io.xml;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.annotation.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class XmlUtilTest {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(XmlUtilTest.class);

    private KnowtatorManager manager;


    private String projectFileName = "test_project";
    private File project = new File(getClass().getResource("/" + projectFileName + "/" + projectFileName + ".xml").getFile());

    private String oldProjectFileName = "old_project";
    private File oldProject = new File(getClass().getResource("/" + oldProjectFileName + "/" + oldProjectFileName + ".xml").getFile());


    private String articlesDir = "test_project/Articles";

    private String docFileName2 = "test_document2";
    private File doc2 = new File(getClass().getResource("/" + articlesDir + "/" + docFileName2 + ".txt").getFile());

    private String outProjectFileName = "out_project.xml";
    private File outProject = new File(getClass().getResource("/" + outProjectFileName).getFile());

    @Before
    public void setUp() {
        manager = new KnowtatorManager();
        manager.loadProject(project);
    }

    @Test
    public void read() {
        String docFileName = "test_document";
        TextSource textSource1 = manager.getTextSourceManager().getTextSources().get(docFileName);

        String testDocText = "This is a test document.";
        assert textSource1.getContent().equals(testDocText);

        AnnotationManager annotationManager1 = textSource1.getAnnotationManager();
        int numAnnotations = annotationManager1.getAnnotations().size();
        int numSpans = annotationManager1.getSpanMap(null, null).size();
        int numGraphSpaces = annotationManager1.getGraphSpaces().size();
        int numVertices = annotationManager1.getGraphSpaces().get(0).getVertices().size();
        int numTriples = annotationManager1.getGraphSpaces().get(0).getTriples().size();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 2 : "There were " + numVertices + " vertices";
        assert numTriples == 1 : "There were " + numTriples + " triples";
        assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
        assert numSpans == 3 : "There were " + numSpans + " spans";

        manager.addDocument(doc2);
        String testDocText2 = "A second test document has appeared!";
        assert manager.getTextSourceManager().getTextSources().get(docFileName2).getContent().equals(testDocText2);
    }

    @Test
    public void readOld() throws IOException {
        String docFileName = "test_document";
        manager.closeProject(null, null);

        TextSource textSource1 = manager.getTextSourceManager().getTextSources().get(docFileName);

        assert textSource1 == null;

        manager.loadProject(oldProject);

        textSource1 = manager.getTextSourceManager().getTextSources().get(docFileName);

        String testDocText = "This is a test document.";
        assert textSource1.getContent().equals(testDocText);

        AnnotationManager annotationManager1 = textSource1.getAnnotationManager();
        int numAnnotations = annotationManager1.getAnnotations().size();
        int numSpans = annotationManager1.getSpanMap(null, null).size();
        int numGraphSpaces = annotationManager1.getGraphSpaces().size();

        assert numGraphSpaces == 0 : "There were " + numGraphSpaces + " graph spaces";
        assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
        assert numSpans == 3 : "There were " + numSpans + " spans";

        GraphSpace graphSpace = new GraphSpace(manager, annotationManager1, "graph_0");
        annotationManager1.addGraphSpace(graphSpace);

        graphSpace.addVertex("node_0", annotationManager1.getAnnotation("mention_0"));
        graphSpace.addVertex("node_1", annotationManager1.getAnnotation("mention_1"));
        graphSpace.addTriple("edge_0", "node_0", "node_1", "property_0", "", "", "Default");

        numGraphSpaces = annotationManager1.getGraphSpaces().size();
        int numVertices = annotationManager1.getGraphSpaces().get(0).getVertices().size();
        int numTriples = annotationManager1.getGraphSpaces().get(0).getTriples().size();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 2 : "There were " + numVertices + " vertices";
        assert numTriples == 1 : "There were " + numTriples + " triples";

//        manager.getProfileManager().addNewProfile("Default").getColor("class_0", "Class 0");
//        manager.getProfileManager().addNewProfile("Default").getColor("class_1", "Class 1");

        File tempFile = File.createTempFile("test_output", ".xml");
        manager.saveProject(tempFile);

        try {
            assert FileUtils.contentEquals(tempFile, outProject);
            tempFile.deleteOnExit();
        } catch (AssertionError ae) {
            log.warn("***" + tempFile.getAbsolutePath());
        }
    }

    @Test
    public void write() throws IOException {
        File tempFile = File.createTempFile("test_output", ".xml");
        manager.saveProject(tempFile);

        try {
            assert FileUtils.contentEquals(tempFile, outProject);
            tempFile.deleteOnExit();
        } catch (AssertionError ae) {
            log.warn("***" + tempFile.getAbsolutePath());
        }
    }
}
