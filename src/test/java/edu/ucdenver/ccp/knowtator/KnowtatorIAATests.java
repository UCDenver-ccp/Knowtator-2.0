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
import edu.ucdenver.ccp.knowtator.iaa.IAAException;
import edu.ucdenver.ccp.knowtator.iaa.KnowtatorIAA;
import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

class KnowtatorIAATests {

    private static KnowtatorIAA knowtatorIAA;

    @BeforeAll
    static void makeProjectTest() throws IAAException, IOException {
        KnowtatorModel controller = new KnowtatorModel();
        String projectFileName = "iaa_test_project";
        File projectDirectory = TestingHelpers.getProjectFile(projectFileName).getParentFile();
        File tempProjectDir = Files.createTempDir();
        FileUtils.copyDirectory(projectDirectory, tempProjectDir);
        controller.setSaveLocation(tempProjectDir);
        controller.setDebug();
        controller.loadProject();

//        File goldStandardDir = new File(controller.getProjectLocation(), "iaa");
        File outputDir = new File(controller.getProjectLocation(), "iaa_results");
        //noinspection ResultOfMethodCallIgnored
        outputDir.mkdir();
        knowtatorIAA = new KnowtatorIAA(outputDir, controller);

    }

    @Test
    void runClassIAATest() throws IAAException {
        knowtatorIAA.runClassIAA();
//TODO: Rerun test data because concept annotations no longer store owl class label

//        assert FileUtils.contentEqualsIgnoreEOL(new File(outputDir, "Class matcher.dat"), new File(goldStandardDir, "Class matcher.dat"), "utf-8");
    }

    @Test
    void runSpanIAATest() throws IAAException {
        knowtatorIAA.runSpanIAA();
//        assert FileUtils.contentEqualsIgnoreEOL(new File(outputDir, "Span matcher.dat"), new File(goldStandardDir, "Span matcher.dat"), "utf-8");
    }

    @Test
    void runClassAndSpanIAATest() throws IAAException {
        knowtatorIAA.runClassAndSpanIAA();

//        assert FileUtils.contentEqualsIgnoreEOL(new File(outputDir, "Class and span matcher.dat"), new File(goldStandardDir, "Class and span matcher.dat"), "utf-8");
    }
}
