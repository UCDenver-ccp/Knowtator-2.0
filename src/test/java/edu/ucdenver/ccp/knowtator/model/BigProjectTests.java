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

package edu.ucdenver.ccp.knowtator.model;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;

public class BigProjectTests {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(BigProjectTests.class);

    private KnowtatorManager manager;

    private String[] projectFileNames = new String[]{"CRAFT_assertions"};
    private String[] articleFileNames = new String[]{"11319941"};


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

    @Test
    public void loadBigProject() {
        manager = new KnowtatorManager();

        int projectID = 0;
        int articleID = 0;
        String projectFileName = projectFileNames[projectID];
        File projectFile = getProjectFile(projectFileName);

        long startTime = System.currentTimeMillis();
        manager.getProjectManager().loadProject(projectFile);

        log.warn(manager.getTextSourceManager().getTextSources().get(articleFileNames[articleID]).getAnnotationManager().getAnnotations().size());
        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;
        log.warn(elapsedTime);
    }
}
