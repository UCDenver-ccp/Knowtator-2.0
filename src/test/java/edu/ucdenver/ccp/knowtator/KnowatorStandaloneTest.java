package edu.ucdenver.ccp.knowtator;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Unit test for simple KnowatorStandalone.
 */
@SuppressWarnings("unused")
public class KnowatorStandaloneTest extends TestCase {


    @SuppressWarnings("FieldCanBeLocal")
    private KnowtatorController controller;

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


    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public KnowatorStandaloneTest(String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( KnowatorStandaloneTest.class );
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        String brat = null;
        String uima = null;
        String articles = null;
        String annotations = null;
        String knowtator = null;
        try {
            brat = Files.createTempDirectory("brat_output_test").toFile().getAbsolutePath();
            uima = Files.createTempDirectory("uima_output_test").toFile().getAbsolutePath();
            knowtator = Files.createTempDirectory("knowtator_output_test").toFile().getAbsolutePath();
            articles = getClass().getResource("/test_project/Articles").getFile();
            annotations = getClass().getResource("/test_project/Annotations").getFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String[] args = new String[]{
                "--articles", articles,
                "--annotations", annotations,
                "--brat", brat,
                "--uima", uima,
                "--knowtator", knowtator
        };

        KnowtatorStandalone.main(args);
    }

//    public void testExportToUIMAXMI() {
//        controller = new KnowtatorController();
//
//        int projectID = 0;
////        int articleID = 0;
//        String projectFileName = projectFileNames[projectID];
//        File projectFile = getProjectFile(projectFileName);
////        String article = articleFileNames[articleID];
//
//        File outputDir = new File("E:/Documents/Test/");
//
//        controller.loadProject(projectFile);
//
////        TextSource textSource = controller.getTextSourceCollection().getTextSourceCollection().get(article);
//        controller.saveToFormat(UIMAXMIUtil.class, null, outputDir);
//    }
}
