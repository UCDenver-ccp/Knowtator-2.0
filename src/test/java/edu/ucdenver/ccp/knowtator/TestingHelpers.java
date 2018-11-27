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

package edu.ucdenver.ccp.knowtator;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.OWLOntologyChange;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class TestingHelpers {

    public static final String projectFileName = "test_project";

    static File getProjectFile(String projectName) {
        return new File(
                TestingHelpers.class
                        .getResource(String.format("/%s/%s.knowtator", projectName, projectName))
                        .getFile());
    }

    public static File getArticleFile(String projectName, String articleName) {
        return new File(
                TestingHelpers.class
                        .getResource(String.format("/%s/Articles/%s.txt", projectName, articleName))
                        .getFile());
    }

    public static KnowtatorModel getLoadedController() {
        KnowtatorModel controller = new KnowtatorModel();

        try {
            File projectDirectory = getProjectFile(projectFileName).getParentFile();
            File tempProjectDir = Files.createTempDir();
            FileUtils.copyDirectory(projectDirectory, tempProjectDir);
            controller.setSaveLocation(tempProjectDir);
            controller.setDebug();
            controller.loadProject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return controller;
    }

    public static final int defaultExpectedTextSources = 3;
    public static final int defaultExpectedConceptAnnotations = 6;
    public static final int defaultExpectedSpans = 7;
    public static final int defaultExpectedGraphSpaces = 3;
    public static final int defaultExpectedProfiles = 2;
    public static int defaultExpectedHighlighters = 3;
    public static final int defaultExpectedAnnotationNodes = 7;
    public static final int defaultExpectedTriples = 4;

    public static void checkDefaultCollectionValues(KnowtatorModel controller) {
        TestingHelpers.countCollections(controller, defaultExpectedTextSources, defaultExpectedConceptAnnotations, defaultExpectedSpans, defaultExpectedGraphSpaces, defaultExpectedProfiles, defaultExpectedHighlighters, defaultExpectedAnnotationNodes, defaultExpectedTriples);
    }

    private static void countCollections(
            KnowtatorModel controller,
            int expectedTextSources,
            int expectedConceptAnnotations,
            int expectedSpans,
            int expectedGraphSpaces,
            int expectedProfiles,
            int expectedHighlighters,
            int expectedAnnotationNodes,
            int expectedTriples
    ) {

        int actualTextSources = controller.getTextSources().size();
        int actualConceptAnnotations = controller.getTextSources().stream().mapToInt(
                textSource -> textSource.getConceptAnnotationCollection().size()).sum();
        int actualSpans = controller.getTextSources().stream().mapToInt(
                textSource -> textSource.getConceptAnnotationCollection().getSpans(null).size()).sum();
        int actualGraphSpaces = controller.getTextSources().stream().mapToInt(
                textSource -> textSource.getGraphSpaceCollection().size()).sum();
        int actualProfiles = controller.getProfileCollection().size();
        int actualHighlighters = controller.getProfileCollection().stream().mapToInt(profile -> profile.getColors().size()).sum();
        int actualAnnotationNodes = controller.getTextSources().stream().mapToInt(
                textSource -> textSource.getGraphSpaceCollection().stream().mapToInt(
                        graphSpace1 -> graphSpace1.getChildVertices(graphSpace1.getDefaultParent()).length)
                        .sum()).sum();
        int actualTriples = controller.getTextSources().stream().mapToInt(
                textSource -> textSource.getGraphSpaceCollection().stream().mapToInt(
                        graphSpace1 -> graphSpace1.getChildEdges(graphSpace1.getDefaultParent()).length)
                        .sum()).sum();

        assert actualTextSources == expectedTextSources : "There were " + actualTextSources + " text sources instead of " + expectedTextSources;
        assert actualConceptAnnotations == expectedConceptAnnotations : "There were " + actualConceptAnnotations + " concept annotations instead of " + expectedConceptAnnotations;
        assert actualSpans == expectedSpans : "There were " + actualSpans + " spans instead of " + expectedSpans;
        assert actualProfiles == expectedProfiles : "There were " + actualProfiles + " profiles instead of " + expectedProfiles;
        assert actualHighlighters == expectedHighlighters : "There were " + actualHighlighters + " highlighters instead of " + expectedHighlighters;
        assert actualGraphSpaces == expectedGraphSpaces : "There were " + actualGraphSpaces + " graph spaces instead of " + expectedGraphSpaces;
        assert actualAnnotationNodes == expectedAnnotationNodes : "There were " + actualAnnotationNodes + " vertices instead of " + expectedAnnotationNodes;
        assert actualTriples == expectedTriples : "There were " + actualTriples + " triples instead of " + expectedTriples;
    }

    public static void testOWLAction(KnowtatorModel controller,
                                     List<? extends OWLOntologyChange> changes,
                                     int expectedTextSources,
                                     int expectedConceptAnnotations,
                                     int expectedSpans,
                                     int expectedGraphSpaces,
                                     int expectedProfiles,
                                     int expectedHighlighters,
                                     int expectedAnnotationNodes,
                                     int expectedTriples) {
        TestingHelpers.checkDefaultCollectionValues(controller);
        controller.getOwlOntologyManager().get().applyChanges(changes);
        TestingHelpers.countCollections(controller,
                expectedTextSources,
                expectedConceptAnnotations,
                expectedSpans,
                expectedGraphSpaces,
                expectedProfiles,
                expectedHighlighters,
                expectedAnnotationNodes,
                expectedTriples);
    }

    public static void testKnowtatorAction(KnowtatorModel controller,
                                           AbstractKnowtatorAction action,
                                           int expectedTextSources,
                                           int expectedConceptAnnotations,
                                           int expectedSpans,
                                           int expectedGraphSpaces,
                                           int expectedProfiles,
                                           int expectedHighlighters,
                                           int expectedAnnotationNodes,
                                           int expectedTriples) {
        TestingHelpers.checkDefaultCollectionValues(controller);
        controller.registerAction(action);
        TestingHelpers.countCollections(controller,
                expectedTextSources,
                expectedConceptAnnotations,
                expectedSpans,
                expectedGraphSpaces,
                expectedProfiles,
                expectedHighlighters,
                expectedAnnotationNodes,
                expectedTriples);
        controller.undo();
        TestingHelpers.checkDefaultCollectionValues(controller);
        controller.redo();
        TestingHelpers.countCollections(controller,
                expectedTextSources,
                expectedConceptAnnotations,
                expectedSpans,
                expectedGraphSpaces,
                expectedProfiles,
                expectedHighlighters,
                expectedAnnotationNodes,
                expectedTriples);
        controller.undo();
    }
}
