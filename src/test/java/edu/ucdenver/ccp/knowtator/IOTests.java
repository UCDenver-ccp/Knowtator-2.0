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

  private String[] projectFileNames =
      new String[] {"test_project", "old_project", "test_load_old_coreference", "CRAFT_assertions"};
  private String[] articleFileNames =
      new String[] {
        "document1", "document2", "document3", "document1_old", "brat_test", "small_11319941"
      };
  private String[] articleContent =
      new String[] {
        "This is a test document.",
        "A second test document has appeared!",
        "And another one!",
        "This is a test document."
      };
  //    private String[] profileFileNames = new String[]{"profile1", "profile2"};

  private File getProjectFile(String projectName) {
    return new File(
        getClass()
            .getResource(String.format("/%s/%s.knowtator", projectName, projectName))
            .getFile());
  }

  private File getArticleFile(String projectName, String articleName) {
    return new File(
        getClass()
            .getResource(String.format("/%s/Articles/%s.txt", projectName, articleName))
            .getFile());
  }

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
    controller = new KnowtatorController();

    int projectID = 0;
    int articleID = 0;
    int articleID2 = 2;
    String projectFileName = projectFileNames[projectID];
    File projectFile = getProjectFile(projectFileName);

    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }

    TextSource textSource =
        controller
            .getTextSourceManager()
            .getTextSourceCollection()
            .getCollection()
            .stream()
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
      
      numAnnotations = textSource.getAnnotationManager().getAnnotations().getCollection().size();
      numSpans = textSource.getAnnotationManager().getSpans(null, 0, content.length()).size();
      numGraphSpaces = textSource.getGraphSpaceManager().getGraphSpaceCollection().getCollection().size();
      numVertices =
          textSource.getGraphSpaceManager()
              .getGraphSpaceCollection()
              .getCollection()
              .stream()
              .mapToInt(
                  graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length)
              .sum();
      numTriples =
          textSource.getGraphSpaceManager()
              .getGraphSpaceCollection()
              .getCollection()
              .stream()
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
            .getTextSourceManager()
            .getTextSourceCollection()
            .getCollection()
            .stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID2]))
            .findAny()
            .get();

    try {
      content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
      assert content.equals(articleContent[articleID2]);
      
      numAnnotations = textSource.getAnnotationManager().getAnnotations().getCollection().size();
      numSpans = textSource.getAnnotationManager().getSpans(null, 0, content.length()).size();
      numGraphSpaces = textSource.getGraphSpaceManager().getGraphSpaceCollection().getCollection().size();
      numVertices =
          textSource.getGraphSpaceManager()
              .getGraphSpaceCollection()
              .getCollection()
              .stream()
              .mapToInt(
                  graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length)
              .sum();
      numTriples =
          textSource.getGraphSpaceManager().getGraphSpaceCollection()
              .getCollection()
              .stream()
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
    controller = new KnowtatorController();

    int projectID = 0;
    int articleID = 1;

    String projectFileName = projectFileNames[projectID];
    File projectFile = getProjectFile(projectFileName);

    String articleFileName = articleFileNames[articleID];
    File articleFile = getArticleFile(projectFileName, articleFileName);

    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }

    controller.getTextSourceManager().addDocument(articleFile);

    TextSource textSource =
        controller
            .getTextSourceManager()
            .getTextSourceCollection()
            .getCollection()
            .stream()
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
    File projectFile = getProjectFile(projectFileName);

    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }

    TextSource textSource =
        controller
            .getTextSourceManager()
            .getTextSourceCollection()
            .getCollection()
            .stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID]))
            .findAny()
            .get();
    String content;
    try {
      content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
      assert content.equals(articleContent[articleID]);


      int numAnnotations = textSource.getAnnotationManager().getAnnotations().getCollection().size();
      int numSpans = textSource.getAnnotationManager().getSpans(null, 0, content.length()).size();
      int numGraphSpaces = textSource.getGraphSpaceManager().getGraphSpaceCollection().getCollection().size();

      assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
      assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
      assert numSpans == 3 : "There were " + numSpans + " spans";
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Test
  public void successfulAddGraphSpace() {
    controller = new KnowtatorController();

    int projectID = 0;
    int articleID = 0;

    String projectFileName = projectFileNames[projectID];
    File projectFile = getProjectFile(projectFileName);

    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }

    TextSource textSource =
        controller
            .getTextSourceManager()
            .getTextSourceCollection()
            .getCollection()
            .stream()
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
    textSource.getGraphSpaceManager().addGraphSpace(graphSpace);

    AnnotationNode v1 = graphSpace.makeOrGetAnnotationNode(textSource.getAnnotationManager().getAnnotation("mention_0"), null);
    AnnotationNode v2 = graphSpace.makeOrGetAnnotationNode(textSource.getAnnotationManager().getAnnotation("mention_1"), null);
    graphSpace.addTriple(
        v1,
        v2,
        "edge_1",
        controller.getProfileManager().getSelection(),
        null,
        "property_0",
        "",
        "",
        false);

    int numGraphSpaces = textSource.getGraphSpaceManager().getGraphSpaceCollection().getCollection().size();
    int numVertices =
        textSource.getGraphSpaceManager().getGraphSpaceCollection()
            .getCollection()
            .stream()
            .mapToInt(
                graphSpace1 -> graphSpace1.getChildVertices(graphSpace.getDefaultParent()).length)
            .sum();
    int numTriples =
        textSource.getGraphSpaceManager().getGraphSpaceCollection()
            .getCollection()
            .stream()
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
    File resourceProjectFile = getProjectFile(projectFileName);

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
    controller.saveProject();
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
//                .getTextSourceManager()
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
    File projectFile = getProjectFile(projectFileName);

    try {
      controller.setSaveLocation(projectFile);
      controller.loadProject();
    } catch (IOException e) {
      e.printStackTrace();
    }

    TextSource textSource =
        controller
            .getTextSourceManager()
            .getTextSourceCollection()
            .getCollection()
            .stream()
            .filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID]))
            .findAny()
            .get();

    int numAnnotations = textSource.getAnnotationManager().getAnnotations().getCollection().size();
    int numSpans = textSource.getAnnotationManager().getSpans(null, 0, textSource.getContent().length()).size();
    int numGraphSpaces = textSource.getGraphSpaceManager().getGraphSpaceCollection().getCollection().size();
    int numVertices =
        textSource.getGraphSpaceManager().getGraphSpaceCollection()
            .getCollection()
            .stream()
            .mapToInt(
                graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length)
            .sum();
    int numTriples =
        textSource.getGraphSpaceManager().getGraphSpaceCollection()
            .getCollection()
            .stream()
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
  //        controller1.getTextSourceManager().getTextSourceCollection().iterator();
  //    Iterator<TextSource> textSourceIterator2 =
  //        controller2.getTextSourceManager().getTextSourceCollection().iterator();
  //    while (textSourceIterator1.hasNext()) {
  //      TextSource textSource1 = textSourceIterator1.next();
  //      TextSource textSource2 = textSourceIterator2.next();
  //
  //      Iterator<Annotation> annotationIterator1 =
  //          textSource1.gettextSource.getAnnotationManager()().getAnnotations().iterator();
  //      Iterator<Annotation> annotationIterator2 =
  //          textSource2.gettextSource.getAnnotationManager()().getAnnotations().iterator();
  //
  //      while (annotationIterator1.hasNext()) {
  //        Annotation annotation1 = annotationIterator1.next();
  //        Annotation annotation2 = annotationIterator2.next();
  //
  //        try {
  //          assert Annotation.compare(annotation1, annotation2) == 0;
  //
  //        } catch (AssertionError e) {
  //          log.warn("Annotation 1:");
  //          log.warn("\tID: " + annotation1.getId());
  //          log.warn("\tSpans: ");
  //          for (Span span : annotation1.getSpanCollection()) {
  //            log.warn("\t\t" + span.toString());
  //          }
  //
  //          log.warn("Annotation 2:");
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
