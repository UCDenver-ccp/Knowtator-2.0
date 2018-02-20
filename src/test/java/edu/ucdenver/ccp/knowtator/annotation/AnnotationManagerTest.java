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

package edu.ucdenver.ccp.knowtator.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorManager;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class AnnotationManagerTest {

    private String projectFileName = "test_project";
    private File project = new File(getClass().getResource("/" + projectFileName + "/" + projectFileName + ".xml").getFile());
    private AnnotationManager annotationManager;
    private TextSource textSource;
    private Profile profile;
    private Logger log = Logger.getLogger(AnnotationManagerTest.class);

    @Before
    public void setUp() {
        KnowtatorManager manager = new KnowtatorManager();
        manager.loadProject(project);
        String docFileName = "test_document";
        textSource = manager.getTextSourceManager().getTextSources().get(docFileName);
        annotationManager = textSource.getAnnotationManager();
        profile = manager.getProfileManager().getCurrentProfile();
    }

    @Test
    public void addAnnotation() {
        Annotation annotation1 = new Annotation("class_2", "class_2", "mention_3", textSource, profile, "identity");
        annotationManager.addAnnotation(annotation1);

        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanMap(null, null).size();

        assert numAnnotations == 3;
        assert numSpans == 3;
    }

    @Test
    public void addSpanToAnnotation() {
        Annotation annotation1 = new Annotation("class_2", "class_2", "mention_3", textSource, profile, "identity");
        annotationManager.addAnnotation(annotation1);

        Span span1 = new Span(1, 6);
        annotationManager.addSpanToAnnotation(annotation1, span1);

        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanMap(null, null).size();

        assert numAnnotations == 3;
        assert numSpans == 4;
    }

    @Test
    public void removeAnnotation() {
        annotationManager.removeAnnotation("mention_1");

        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanMap(null, null).size();

        assert numAnnotations == 1;
        try {
            assert numSpans == 1;
        } catch (AssertionError ae) {
            annotationManager.getSpanMap(null, null).forEach(((span, annotation) -> log.warn(String.format("%s, %s", span, annotation))));
            throw new AssertionError();
        }
    }

    @Test
    public void removeSpanFromAnnotation() {
        Annotation annotation1 = new Annotation("class_2", "class_2", "mention_3", textSource, profile, "identity");
        annotationManager.addAnnotation(annotation1);

        Span span1 = new Span(1, 6);
        annotationManager.addSpanToAnnotation(annotation1, span1);

        annotationManager.removeSpanFromAnnotation(annotation1, span1);
        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanMap(null, null).size();

        assert numAnnotations == 3;
        try {
            assert numSpans == 3;
        } catch (AssertionError ae) {
            annotationManager.getSpanMap(null, null).forEach(((span, annotation) -> log.warn(String.format("%s, %s", span, annotation))));
            throw new AssertionError();
        }
    }

    @Test
    public void getSpanMap() {
    }

    @Test
    public void growSpanStart() {
    }

    @Test
    public void growSpanEnd() {
    }

    @Test
    public void shrinkSpanEnd() {
    }

    @Test
    public void shrinkSpanStart() {
    }

    @Test
    public void addAnnotation1() {
    }

    @Test
    public void findOverlaps() {
    }

    @Test
    public void getAnnotation() {
    }

    @Test
    public void removeGraphSpace() {

    }
}
