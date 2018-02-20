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

package edu.ucdenver.ccp.knowtator.model.graph;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.annotation.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.annotation.TextSource;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class GraphSpaceTest {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(GraphSpace.class);


    private String projectFileName = "test_project";
    private File project = new File(getClass().getResource("/" + projectFileName + "/" + projectFileName + ".xml").getFile());
    private GraphSpace graphSpace;

    @Before
    public void setUp() {
        KnowtatorManager manager = new KnowtatorManager();
        manager.loadProject(project);
        String docFileName = "test_document";
        TextSource textSource = manager.getTextSourceManager().getTextSources().get(docFileName);
        AnnotationManager annotationManager = textSource.getAnnotationManager();
        graphSpace = annotationManager.getGraphSpaces().get(0);

    }

    @Test
    public void writeToXml() {
    }

    @Test
    public void readFromXml() {

    }

    @Test
    public void reDrawVertices() {
    }

    @Test
    public void getVertices() {
        int numVertices = graphSpace.getVertices().size();

        assert numVertices == 2;


    }
}
