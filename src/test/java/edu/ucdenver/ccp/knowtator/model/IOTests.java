
package edu.ucdenver.ccp.knowtator.model;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.annotation.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.graph.AnnotationNode;
import edu.ucdenver.ccp.knowtator.model.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.model.io.brat.BratStandoffUtil;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("ConstantConditions")
public class IOTests {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(IOTests.class);

    private KnowtatorManager manager;

    private String[] projectFileNames = new String[]{"test_project", "old_project"};
    private String[] articleFileNames = new String[]{"document1", "document2", "document3", "document1_old", "brat_test"};
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
//        manager = new KnowtatorManager();
//
//        int projectID = 2;
//        String projectFileName = projectFileNames[projectID];
//        File projectFile = getProjectFile(projectFileName);
//
//        manager.getProjectManager().loadProject(projectFile);
//    }

    @Test
    public void successfulLoad() {
        manager = new KnowtatorManager();

        int projectID = 0;
        int articleID = 0;
        int articleID2 = 2;
        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        manager.getProjectManager().loadProject(projectFile);

        TextSource textSource = manager.getTextSourceManager().getTextSources().stream().filter(textSource1 -> textSource1.getDocID().equals(articleFileNames[articleID])).findAny().get();
        String content;
        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnnotationManager annotationManager = textSource.getAnnotationManager();
        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanMap(null, null).size();
        int numGraphSpaces = annotationManager.getGraphSpaces().size();
        int numVertices = annotationManager.getGraphSpaces().stream().mapToInt(graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length).sum();
        int numTriples = annotationManager.getGraphSpaces().stream().mapToInt(graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length).sum();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 2 : "There were " + numVertices + " vertices";
        assert numTriples == 1 : "There were " + numTriples + " triples";
        assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
        assert numSpans == 3 : "There were " + numSpans + " spans";


        textSource = manager.getTextSourceManager().getTextSources().stream().filter(textSource1 -> textSource1.getDocID().equals(articleFileNames[articleID2])).findAny().get();

        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID2]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        annotationManager = textSource.getAnnotationManager();
        numAnnotations = annotationManager.getAnnotations().size();
        numSpans = annotationManager.getSpanMap(null, null).size();
        numGraphSpaces = annotationManager.getGraphSpaces().size();
        numVertices = annotationManager.getGraphSpaces().stream().mapToInt(graphSpace -> graphSpace.getChildVertices(graphSpace.getDefaultParent()).length).sum();
        numTriples = annotationManager.getGraphSpaces().stream().mapToInt(graphSpace -> graphSpace.getChildEdges(graphSpace.getDefaultParent()).length).sum();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 2 : "There were " + numVertices + " vertices";
        assert numTriples == 1 : "There were " + numTriples + " triples";
        assert numAnnotations == 1 : "There were " + numAnnotations + " annotations";
        assert numSpans == 1 : "There were " + numSpans + " spans";
    }

    @Test
    public void successfulAddDocument() {
        manager = new KnowtatorManager();

        int projectID = 0;
        int articleID = 1;

        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        String articleFileName = articleFileNames[articleID];
        File articleFile = getArticleFile(projectFileName, articleFileName);

        manager.getProjectManager().loadProject(projectFile);

        manager.getProjectManager().addDocument(articleFile);

        TextSource textSource = manager.getTextSourceManager().getTextSources().stream().filter(textSource1 -> textSource1.getDocID().equals(articleFileNames[articleID])).findAny().get();
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
        manager = new KnowtatorManager();

        int projectID = 0;
        int articleID = 0;

        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        manager.getProjectManager().loadProject(projectFile);
        manager.getProjectManager().closeProject(null, null);

        manager.getTextSourceManager().getTextSources().stream().filter(textSource -> textSource.getDocID().equals(articleFileNames[articleID])).findAny().orElseThrow(RuntimeException::new);
    }

    @Test
    public void successfulMakeNew() {
        manager = new KnowtatorManager();

        File newProject = Files.createTempDir();
        manager.getProjectManager().newProject(newProject);

        assert new File(newProject, "Articles").exists();
        assert new File(newProject, "Annotations").exists();
        assert new File(newProject, "Ontologies").exists();
        assert new File(newProject, "Profiles").exists();

        newProject.deleteOnExit();
    }

    @Test
    public void successfulLoadOld() {
        manager = new KnowtatorManager();

        int projectID = 1;
        int articleID = 3;

        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        manager.getProjectManager().loadProject(projectFile);
        TextSource textSource = manager.getTextSourceManager().getTextSources().stream().filter(textSource1 -> textSource1.getDocID().equals(articleFileNames[articleID])).findAny().get();
        String content;
        try {
            content = FileUtils.readFileToString(textSource.getTextFile(), "UTF-8");
            assert content.equals(articleContent[articleID]);
        } catch (IOException e) {
            e.printStackTrace();
        }

        AnnotationManager annotationManager1 = textSource.getAnnotationManager();
        int numAnnotations = annotationManager1.getAnnotations().size();
        int numSpans = annotationManager1.getSpanMap(null, null).size();
        int numGraphSpaces = annotationManager1.getGraphSpaces().size();

        assert numGraphSpaces == 1 : "There were " + numGraphSpaces + " graph spaces";
        assert numAnnotations == 2 : "There were " + numAnnotations + " annotations";
        assert numSpans == 3 : "There were " + numSpans + " spans";
    }

    @Test
    public void successfulAddGraphSpace() {
        manager = new KnowtatorManager();

        int projectID = 0;
        int articleID = 0;

        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        manager.getProjectManager().loadProject(projectFile);

        TextSource textSource = manager.getTextSourceManager().getTextSources().stream().filter(textSource1 -> textSource1.getDocID().equals(articleFileNames[articleID])).findAny().get();
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
        graphSpace.addTriple(v1, v2, "edge_1", manager.getProfileManager().getCurrentProfile(), "property_0", "", "");

        int numGraphSpaces = annotationManager.getGraphSpaces().size();
        int numVertices = annotationManager.getGraphSpaces().stream().mapToInt(graphSpace1 -> graphSpace1.getChildVertices(graphSpace.getDefaultParent()).length).sum();
        int numTriples = annotationManager.getGraphSpaces().stream().mapToInt(graphSpace1 -> graphSpace1.getChildEdges(graphSpace.getDefaultParent()).length).sum();

        assert numGraphSpaces == 2 : "There were " + numGraphSpaces + " graph spaces";
        assert numVertices == 4 : "There were " + numVertices + " vertices";
        assert numTriples == 2 : "There were " + numTriples + " triples";
    }

    @Test
    public void successfulSave() {
        manager = new KnowtatorManager();


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
        manager.getProjectManager().loadProject(projectFile);
        manager.getProjectManager().saveProject();
        successfulLoad();

        projectDirectory.deleteOnExit();
    }

    @Test
    public void successfulExportToBrat() {
        manager = new KnowtatorManager();

        int projectID = 0;
        int articleID = 4;
        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);
        String article = articleFileNames[articleID];

        File bratAnnotationFile = getBratFile(projectFileName, article);

        File outputFile = new File("E:/Documents/Test/brat_test.ann");

        manager.getProjectManager().loadProject(projectFile);
        manager.getProjectManager().loadFromFormat(BratStandoffUtil.class, bratAnnotationFile);
        manager.getProjectManager().saveToFormat(BratStandoffUtil.class, manager.getTextSourceManager().getTextSources().stream().filter(textSource1 -> textSource1.getDocID().equals(article)).findAny().get(), outputFile);
    }


}
