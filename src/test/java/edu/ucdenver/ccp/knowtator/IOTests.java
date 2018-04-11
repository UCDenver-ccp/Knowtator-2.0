
package edu.ucdenver.ccp.knowtator;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.model.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.TextSource;
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

    private String[] projectFileNames = new String[]{"test_project", "old_project", "test_load_old_coreference", "CRAFT_assertions"};
    private String[] articleFileNames = new String[]{"document1", "document2", "document3", "document1_old", "brat_test", "small_11319941"};
    private String[] articleContent = new String[]{
            "This is a test document.",
            "A second test document has appeared!",
            "And another one!",
            "This is a test document."
    };
//    private String[] profileFileNames = new String[]{"profile1", "profile2"};

    private File getProjectFile(String projectName) {
        return new File(getClass().getResource(String.format(
                "/%s/%s.knowtator",
                projectName,
                projectName)
        ).getFile());
    }

    private File getArticleFile(String projectName, String articleName) {
        return new File(getClass().getResource(String.format(
                "/%s/Articles/%s.txt",
                projectName,
                articleName)
        ).getFile());
    }

    private File getBratFile(String projectName, String bratFileName) {
        return new File(getClass().getResource(String.format(
                "/%s/Annotations/%s.ann",
                projectName,
                bratFileName)
        ).getFile());
    }

//    @Test
//    public void loadLargeOld() {
//        controller = new KnowtatorController();
//
//        int projectID = 3;
//        String projectFileName = projectFileNames[projectID];
//        File projectFile = getProjectFile(projectFileName);
//
//        controller.getProjectManager().loadProject(projectFile);
//    }

    @Test
    public void successfulLoad() {
        controller = new KnowtatorController();

        int projectID = 0;
        int articleID = 0;
        int articleID2 = 2;
        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        controller.getProjectManager().loadProject(projectFile);

		TextSource textSource = controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID])).findAny().get();
        String content;
        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnnotationManager annotationManager = textSource.getAnnotationManager();
        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanSet(null).size();
		int numGraphSpaces = annotationManager.getGraphSpaceCollection().getData().size();
		int numVertices = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length).sum();
		int numTriples = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length).sum();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 2 : "There were " + numVertices + " vertices";
        assert numTriples == 1 : "There were " + numTriples + " triples";
        assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
        assert numSpans == 3 : "There were " + numSpans + " spans";


		textSource = controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID2])).findAny().get();

        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID2]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        annotationManager = textSource.getAnnotationManager();
        numAnnotations = annotationManager.getAnnotations().size();
        numSpans = annotationManager.getSpanSet(null).size();
		numGraphSpaces = annotationManager.getGraphSpaceCollection().getData().size();
		numVertices = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length).sum();
		numTriples = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length).sum();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 2 : "There were " + numVertices + " vertices";
        assert numTriples == 1 : "There were " + numTriples + " triples";
        assert numAnnotations == 1 : "There were " + numAnnotations + " annotations";
        assert numSpans == 1 : "There were " + numSpans + " spans";
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

        controller.getProjectManager().loadProject(projectFile);

        controller.getProjectManager().addDocument(articleFile);

		TextSource textSource = controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID])).findAny().get();
        String content;
        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test(expected = RuntimeException.class)
    public void successfulClose() {
        controller = new KnowtatorController();

        int projectID = 0;
        int articleID = 0;

        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        controller.getProjectManager().loadProject(projectFile);
        controller.getProjectManager().closeProject(null, null);

		controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource -> textSource.getId().equals(articleFileNames[articleID])).findAny().orElseThrow(RuntimeException::new);
    }

    @Test
    public void successfulMakeNew() {
        controller = new KnowtatorController();

        File newProject = Files.createTempDir();
        controller.getProjectManager().newProject(newProject);

        assert new File(newProject, "Articles").exists();
        assert new File(newProject, "Annotations").exists();
        assert new File(newProject, "Ontologies").exists();
		assert new File(newProject, "ProfileCollection").exists();

        newProject.deleteOnExit();
    }

    @Test
    public void successfulLoadOld() {
        controller = new KnowtatorController();

        int projectID = 1;
        int articleID = 3;

        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        controller.getProjectManager().loadProject(projectFile);
		TextSource textSource = controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID])).findAny().get();
        String content;
        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnnotationManager annotationManager1 = textSource.getAnnotationManager();
        int numAnnotations = annotationManager1.getAnnotations().size();
        int numSpans = annotationManager1.getSpanSet(null).size();
		int numGraphSpaces = annotationManager1.getGraphSpaceCollection().getData().size();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
        assert numSpans == 3 : "There were " + numSpans + " spans";
    }

    @Test
    public void successfulAddGraphSpace() {
        controller = new KnowtatorController();

        int projectID = 0;
        int articleID = 0;

        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        controller.getProjectManager().loadProject(projectFile);

		TextSource textSource = controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID])).findAny().get();
        String content;
        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnnotationManager annotationManager = textSource.getAnnotationManager();
        GraphSpace graphSpace = annotationManager.addGraphSpace("graph_1");


        AnnotationNode v1 = graphSpace.addNode("node_0", annotationManager.getAnnotation("mention_0"));
        AnnotationNode v2 = graphSpace.addNode("node_1", annotationManager.getAnnotation("mention_1"));
        graphSpace.addTriple(v1, v2, "edge_1", controller.getSelectionManager().getActiveProfile(), "property_0", "", "");

		int numGraphSpaces = annotationManager.getGraphSpaceCollection().getData().size();
		int numVertices = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace1 -> graphSpace1.getChildVertices(graphSpace.getDefaultParent()).length).sum();
		int numTriples = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace1 -> graphSpace1.getChildEdges(graphSpace.getDefaultParent()).length).sum();

        assert numGraphSpaces == 2 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 4 : "There were " + numVertices + " vertices";
        assert numTriples == 2 : "There were " + numTriples + " triples";
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
        controller.getProjectManager().loadProject(projectFile);
        controller.getProjectManager().saveProject();
        successfulLoad();

        projectDirectory.deleteOnExit();
    }

    @Test
    public void successfulExportToBrat() {
        controller = new KnowtatorController();

        int projectID = 0;
        int articleID = 4;
        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);
        String article = articleFileNames[articleID];

        File bratAnnotationFile = getBratFile(projectFileName, article);

        File outputFile = new File("E:/Documents/Test/brat_test.ann");

        controller.getProjectManager().loadProject(projectFile);
        controller.getProjectManager().loadFromFormat(BratStandoffUtil.class, bratAnnotationFile);
		controller.getProjectManager().saveToFormat(BratStandoffUtil.class, controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource1 -> textSource1.getId().equals(article)).findAny().get(), outputFile);
    }

    @Test
    public void successfulLoadOldCoreference() {
        controller = new KnowtatorController();

        int projectID = 2;
        int articleID = 5;
        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        controller.getProjectManager().loadProject(projectFile);

		TextSource textSource = controller.getTextSourceManager().getTextSourceCollection().getData().stream().filter(textSource1 -> textSource1.getId().equals(articleFileNames[articleID])).findAny().get();

        AnnotationManager annotationManager = textSource.getAnnotationManager();
        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanSet(null).size();
		int numGraphSpaces = annotationManager.getGraphSpaceCollection().getData().size();
		int numVertices = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length).sum();
		int numTriples = annotationManager.getGraphSpaceCollection().getData().stream().mapToInt(graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length).sum();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 12 : "There were " + numVertices + " vertices";
        assert numTriples == 11 : "There were " + numTriples + " triples";
        assert numAnnotations == 12 : "There were " + numAnnotations + " annotations";
        assert numSpans == 12 : "There were " + numSpans + " spans";
    }
}
