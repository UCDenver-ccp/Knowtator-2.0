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
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ConstantConditions")
public class IOTests {
  @SuppressWarnings("unused")
  private static final Logger log = Logger.getLogger(IOTests.class);

  private KnowtatorController controller;

  private final String[] projectFileNames =
      new String[] {"test_project", "old_project", "test_load_old_coreference", "CRAFT_assertions"};
  private final String[] articleFileNames =
      new String[] {
        "document1", "document2", "document3", "document1_old", "brat_test", "small_11319941"
      };
  private final String[] articleContent =
      new String[] {
        "This is a test document.",
        "A second test document has appeared!",
        "And another one!",
        "This is a test document."
      };
  //    private String[] profileFileNames = new String[]{"profile1", "profile2"};



//  private File getBratFile(String projectName, String bratFileName) {
//    return new File(
//        getClass()
//            .getResource(String.format("/%s/Annotations/%s.ann", projectName, bratFileName))
//            .getFile());
//  }

  //    @Test
  //    public void loadLargeOld() {
  //        controller = new KnowtatorController();
  //
  //        int projectID = 3;
  //        String projectFileName = projectFileNames[projectID];
  //        File projectFile = getProjectFile(projectFileName);
  //
  //        controller.loadProject(projectFile);
  //    }

  @Test
  public void successfulLoad() {
    controller = TestingHelpers.getLoadedController();

    int articleID = 0;
    int articleID2 = 2;

    TextSource textSource =
        controller
            .getTextSourceCollection().stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID]))
            .findAny()
            .get();
    String content;
    int numAnnotations;
    int numSpans;
    int numGraphSpaces;
    int numVertices;
    int numTriples;

    try {
      content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
      assert content.equals(articleContent[articleID]);
      
      numAnnotations = textSource.getConceptAnnotationCollection().size();
      numSpans = textSource.getConceptAnnotationCollection().getSpans(null).size();
      numGraphSpaces = textSource.getGraphSpaceCollection().size();
      numVertices =
          textSource.getGraphSpaceCollection().stream()
              .mapToInt(
                  graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length)
              .sum();
      numTriples =
          textSource.getGraphSpaceCollection().stream()
              .mapToInt(
                  graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length)
              .sum();

      assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
      assert numVertices == 2 : "There were " + numVertices + " vertices";
      assert numTriples == 1 : "There were " + numTriples + " triples";
      assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
      assert numSpans == 3 : "There were " + numSpans + " spans";
    } catch (IOException e) {
      e.printStackTrace();
    }

    textSource =
        controller
            .getTextSourceCollection().stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID2]))
            .findAny()
            .get();

    try {
      content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
      assert content.equals(articleContent[articleID2]);
      
      numAnnotations = textSource.getConceptAnnotationCollection().size();
      numSpans = textSource.getConceptAnnotationCollection().getSpans(null).size();
      numGraphSpaces = textSource.getGraphSpaceCollection().size();
      numVertices =
          textSource.getGraphSpaceCollection().stream()
              .mapToInt(
                  graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length)
              .sum();
      numTriples =
          textSource.getGraphSpaceCollection().stream()
              .mapToInt(
                  graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length)
              .sum();

      assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
      assert numVertices == 2 : "There were " + numVertices + " vertices";
      assert numTriples == 1 : "There were " + numTriples + " triples";
      assert numAnnotations == 1 : "There were " + numAnnotations + " annotations";
      assert numSpans == 1 : "There were " + numSpans + " spans";
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void successfulAddDocument() {
    controller = TestingHelpers.getLoadedController();

    int projectID = 0;
    int articleID = 1;

    String projectFileName = projectFileNames[projectID];

    String articleFileName = articleFileNames[articleID];
    File articleFile = TestingHelpers.getArticleFile(projectFileName, articleFileName);

    controller.getTextSourceCollection().addDocument(articleFile);

    TextSource textSource =
        controller
            .getTextSourceCollection().stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID]))
            .findAny()
            .get();
    String content;
    try {
      content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
      assert content.equals(articleContent[articleID]);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  //  @Test
  //  public void successfulMakeNew() {
  //    controller = new KnowtatorController();
  //
  //    File newProject = Files.createTempDir();
  //    controller.newProject(newProject);
  //
  //    assert new File(newProject, "Articles").exists();
  //    assert new File(newProject, "Annotations").exists();
  //    assert new File(newProject, "Ontologies").exists();
  //    assert new File(newProject, "Profiles").exists();
  //
  //    newProject.deleteOnExit();
  //  }

  @Test
  public void successfulLoadOld() {
    controller = new KnowtatorController();

    int projectID = 1;
    int articleID = 3;

    String projectFileName = projectFileNames[projectID];
    File projectFile = TestingHelpers.getProjectFile(projectFileName);

    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }

    TextSource textSource =
        controller
            .getTextSourceCollection().stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID]))
            .findAny()
            .get();
    String content;
    try {
      content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
      assert content.equals(articleContent[articleID]);


      int numAnnotations = textSource.getConceptAnnotationCollection().size();
      int numSpans = textSource.getConceptAnnotationCollection().getSpans(null).size();
      int numGraphSpaces = textSource.getGraphSpaceCollection().size();

      assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
      assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
      assert numSpans == 3 : "There were " + numSpans + " spans";
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void successfulAddGraphSpace() {
    controller = TestingHelpers.getLoadedController();

    int articleID = 0;

    TextSource textSource =
        controller
            .getTextSourceCollection().stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID]))
            .findAny()
            .get();
    String content;
    try {
      content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
      assert content.equals(articleContent[articleID]);
    } catch (IOException e) {
      e.printStackTrace();
    }

    GraphSpace graphSpace = new GraphSpace(controller, textSource, "graph_0");
    textSource.getGraphSpaceCollection().add(graphSpace);

    AnnotationNode v1 = graphSpace.makeOrGetAnnotationNode(textSource.getConceptAnnotationCollection().get("mention_0"), null);
    AnnotationNode v2 = graphSpace.makeOrGetAnnotationNode(textSource.getConceptAnnotationCollection().get("mention_1"), null);
    graphSpace.addTriple(
        v1,
        v2,
        "edge_1",
        controller.getProfileCollection().getSelection(),
        null,
        "property_0",
        "",
        "",
            false, "");

    int numGraphSpaces = textSource.getGraphSpaceCollection().size();
    int numVertices =
        textSource.getGraphSpaceCollection().stream()
            .mapToInt(
                graphSpace1 -> graphSpace1.getChildVertices(graphSpace.getDefaultParent()).length)
            .sum();
    int numTriples =
        textSource.getGraphSpaceCollection().stream()
            .mapToInt(
                graphSpace1 -> graphSpace1.getChildEdges(graphSpace.getDefaultParent()).length)
            .sum();

    assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
    assert numVertices == 2 : "There were " + numVertices + " vertices";
    assert numTriples == 1 : "There were " + numTriples + " triples";
  }

  @Test
  public void successfulSave() {
    controller = new KnowtatorController();

    int projectID = 0;

    String projectFileName = projectFileNames[projectID];
    File resourceProjectFile = TestingHelpers.getProjectFile(projectFileName);

    File projectDirectory = Files.createTempDir();

    try {
      FileUtils.copyDirectory(resourceProjectFile.getParentFile(), projectDirectory);
    } catch (IOException e) {
      e.printStackTrace();
    }
    File projectFile = new File(projectDirectory, resourceProjectFile.getName());
    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }
    successfulLoad();

    projectDirectory.deleteOnExit();
  }

//  @Test
//  public void successfulExportToBrat() {
//    controller = new KnowtatorController();
//
//    int projectID = 0;
//    int articleID = 4;
//    String projectFileName = projectFileNames[projectID];
//    File projectFile = getProjectFile(projectFileName);
//    String article = articleFileNames[articleID];
//
//    File bratAnnotationFile = getBratFile(projectFileName, article);
//
//    File outputFile = new File("E:/Documents/Test/brat_test.ann");
//
//    controller.loadProject(projectFile);
//    controller.loadFromFormat(BratStandoffUtil.class, bratAnnotationFile);
//    controller
//        .getProjectManager()
//        .saveToFormat(
//            BratStandoffUtil.class,
//            controller
//                .getTextSourceCollection()
//                .getTextSourceCollection()
//                .getCollection()
//                .stream()
//                .filter(textSource1 -> textSource1.getId().equals(article))
//                .findAny()
//                .get(),
//            outputFile);
//  }

  @Test
  public void successfulLoadOldCoreference() {
    controller = new KnowtatorController();

    int projectID = 2;
    int articleID = 5;
    String projectFileName = projectFileNames[projectID];
    File projectFile = TestingHelpers.getProjectFile(projectFileName);

    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }

    TextSource textSource =
        controller
            .getTextSourceCollection().stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID]))
            .findAny()
            .get();

    int numAnnotations = textSource.getConceptAnnotationCollection().size();
    int numSpans = textSource.getConceptAnnotationCollection().getSpans(null).size();
    int numGraphSpaces = textSource.getGraphSpaceCollection().size();
    int numVertices =
        textSource.getGraphSpaceCollection().stream()
            .mapToInt(
                graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length)
            .sum();
    int numTriples =
        textSource.getGraphSpaceCollection().stream()
            .mapToInt(graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length)
            .sum();

    assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
    assert numVertices == 12 : "There were " + numVertices + " vertices";
    assert numTriples == 11 : "There were " + numTriples + " triples";
    assert numAnnotations == 12 : "There were " + numAnnotations + " annotations";
    assert numSpans == 12 : "There were " + numSpans + " spans";
  }

  //  @Test
  //  public void compareProjects() {
  //    KnowtatorController controller1 = new KnowtatorController();
  //    KnowtatorController controller2 = new KnowtatorController();
  //
  //    controller1
  //        .getProjectManager()
  //        .loadProject(new File("E:/Documents/Test/CRAFT_Test/CRAFT_Test.knowtator"));
  //    controller2
  //        .getProjectManager()
  //        .loadProject(
  //            new File(
  //
  // "E:/Documents/Test/CRAFT_Test_CodePointCorrected/CRAFT_Test_CodePointCorrected.knowtator"));
  //
  //    Iterator<TextSource> textSourceIterator1 =
  //        controller1.getTextSourceCollection().getTextSourceCollection().iterator();
  //    Iterator<TextSource> textSourceIterator2 =
  //        controller2.getTextSourceCollection().getTextSourceCollection().iterator();
  //    while (textSourceIterator1.hasNext()) {
  //      TextSource textSource1 = textSourceIterator1.next();
  //      TextSource textSource2 = textSourceIterator2.next();
  //
  //      Iterator<ConceptAnnotation> annotationIterator1 =
  //          textSource1.gettextSource.getConceptAnnotationCollection()().getAnnotations().iterator();
  //      Iterator<ConceptAnnotation> annotationIterator2 =
  //          textSource2.gettextSource.getConceptAnnotationCollection()().getAnnotations().iterator();
  //
  //      while (annotationIterator1.hasNext()) {
  //        ConceptAnnotation annotation1 = annotationIterator1.next();
  //        ConceptAnnotation annotation2 = annotationIterator2.next();
  //
  //        try {
  //          assert ConceptAnnotation.compare(annotation1, annotation2) == 0;
  //
  //        } catch (AssertionError e) {
  //          log.warn("ConceptAnnotation 1:");
  //          log.warn("\tID: " + annotation1.getId());
  //          log.warn("\tSpans: ");
  //          for (Span span : annotation1.getSpanCollection()) {
  //            log.warn("\t\t" + span.toString());
  //          }
  //
  //          log.warn("ConceptAnnotation 2:");
  //          log.warn("\tID: " + annotation2.getId());
  //          log.warn("\tSpans: ");
  //          for (Span span : annotation2.getSpanCollection()) {
  //            log.warn("\t\t" + span.toString());
  //          }
  //
  //          e.printStackTrace();
  //        }
  //      }
  //    }
  //  }
}
