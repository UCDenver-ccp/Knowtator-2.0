package edu.ucdenver.ccp.knowtator;

import com.google.common.io.Files;
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class KnowtatorIAATest {

    private KnowtatorIAA knowtatorIAA;
    private File outputDir;
    private File goldStandardDir;

    @Before
    public void setUp() throws IAAException, IOException {
        KnowtatorController controller = new KnowtatorController();
        String projectFileName = "iaa_test_project";
        File projectDirectory = TestingHelpers.getProjectFile(projectFileName).getParentFile();
        File tempProjectDir = Files.createTempDir();
        FileUtils.copyDirectory(projectDirectory, tempProjectDir);
        controller.setSaveLocation(tempProjectDir);
        controller.setDebug(true);
        controller.loadProject();

        goldStandardDir = new File(controller.getProjectLocation(), "iaa");
        outputDir = new File(controller.getProjectLocation(), "iaa_results");
        //noinspection ResultOfMethodCallIgnored
        outputDir.mkdir();
        knowtatorIAA = new KnowtatorIAA(outputDir, controller);

    }

    @Test
    public void runClassIAA() throws IAAException, IOException {
        knowtatorIAA.runClassIAA();

        assert FileUtils.contentEqualsIgnoreEOL(new File(outputDir, "Class matcher.dat"), new File(goldStandardDir, "Class matcher.dat"), "utf-8");
    }

    @Test
    public void runSpanIAA() throws IAAException, IOException {
        knowtatorIAA.runSpanIAA();
        assert FileUtils.contentEqualsIgnoreEOL(new File(outputDir, "Span matcher.dat"), new File(goldStandardDir, "Span matcher.dat"), "utf-8");
    }

    @Test
    public void runClassAndSpanIAA() throws IAAException, IOException {
        knowtatorIAA.runClassAndSpanIAA();

        assert FileUtils.contentEqualsIgnoreEOL(new File(outputDir, "Class and span matcher.dat"), new File(goldStandardDir, "Class and span matcher.dat"), "utf-8");
    }
}
