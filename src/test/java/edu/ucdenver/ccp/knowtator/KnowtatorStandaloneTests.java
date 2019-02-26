/*
 * MIT License
 *
 * Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator;

import java.io.IOException;
import java.nio.file.Files;
import org.junit.jupiter.api.Test;

class KnowtatorStandaloneTests {

  @Test
  void conversionTest() {
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
    String[] args =
        new String[] {
          "--articles", articles,
          "--annotations", annotations,
          "--brat", brat,
          "--uima", uima,
          "--knowtator", knowtator
        };

    KnowtatorStandalone.main(args);
  }

  //    public void testExportToUIMAXMI() {
  //        model = new KnowtatorModel();
  //
  //        int projectID = 0;
  ////        int articleID = 0;
  //        String projectFileName = projectFileNames[projectID];
  //        File projectFile = getProjectFile(projectFileName);
  ////        String article = articleFileNames[articleID];
  //
  //        File outputDir = new File("E:/Documents/Test/");
  //
  //        model.loadProjectTest(projectFile);
  //
  ////        TextSource textSource =
  // model.getTextSourceCollection().getTextSourceCollection().getAnnotation(article);
  //        model.saveToFormatTest(UIMAXMIUtil.class, null, outputDir);
  //    }
}
