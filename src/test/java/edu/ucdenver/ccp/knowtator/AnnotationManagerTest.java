package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.text.annotation.AnnotationManager;
import edu.ucdenver.ccp.knowtator.model.text.annotation.Span;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class AnnotationManagerTest {
    private static final Logger log = Logger.getLogger(AnnotationManagerTest.class);


    private String[] projectFileNames = new String[]{"test_project", "old_project"};
    private String[] articleFileNames = new String[]{"document1", "document2"};
    private String[] articleContent = new String[]{
            "This is a test document.",
            "A second test document has appeared!"
    };
    private String[] profileFileNames = new String[]{"profile1", "profile2"};
    private AnnotationManager annotationManager;
    private TextSource textSource;
    private Profile profile;
    private KnowtatorController controller;

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

    public void setUp() {
        controller = new KnowtatorController();

        int projectID = 0;
        int articleID = 0;
        String projectFileName = projectFileNames[projectID];
        File project = getProjectFile(projectFileName);
        String articleName = articleFileNames[articleID];

        try {
            controller.setSaveLocation(project);
            controller.loadProject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //noinspection ConstantConditions
        textSource = controller.getTextSourceManager().getTextSourceCollection().getCollection().stream().filter(textSource1 -> textSource1.getId().equals(articleName)).findAny().get();
        annotationManager = textSource.getAnnotationManager();
        profile = controller.getProfileManager().addProfile("Default");

    }

    @Test
    public void addAnnotation() {
        setUp();
        Annotation annotation1 = new Annotation(controller, "mention_3", null, "class_2", "class_2", profile, "identity", textSource);
        annotationManager.addAnnotation(annotation1);

        int numAnnotations = annotationManager.getAnnotations().getCollection().size();
        int numSpans = annotationManager.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 3;
        assert numSpans == 3;
    }

    @Test
    public void addSpanToAnnotation() {
        setUp();
        Annotation annotation1 = new Annotation(controller, "mention_3", null, "class_2", "class_2", profile, "identity", textSource);
        annotationManager.addAnnotation(annotation1);

        Span span1 = new Span(null, 1, 6, textSource, controller);
        annotationManager.addSpanToAnnotation(annotation1, span1);

        int numAnnotations = annotationManager.getAnnotations().getCollection().size();
        int numSpans = annotationManager.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 3;
        assert numSpans == 4;
    }

    @Test
    public void removeAnnotation() {
        setUp();
        annotationManager.removeAnnotation(annotationManager.getAnnotation("mention_1"));

        int numAnnotations = annotationManager.getAnnotations().getCollection().size();
        int numSpans = annotationManager.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 1;
        try {
            assert numSpans == 1;
        } catch (AssertionError ae) {
            annotationManager.getSpans(null, 0, textSource.getContent().length()).forEach((span -> log.warn(String.format("%s, %s", span, span.getAnnotation()))));
            throw new AssertionError();
        }
    }

    @Test
    public void removeSpanFromAnnotation() {
        setUp();
        Annotation annotation1 = new Annotation(controller, "mention_3", null, "class_2", "class_2", profile, "identity", textSource);
        annotationManager.addAnnotation(annotation1);

        Span span1 = new Span(null, 1, 6, textSource, controller);
        annotationManager.addSpanToAnnotation(annotation1, span1);

        annotationManager.removeSpanFromAnnotation(annotation1, span1);
        int numAnnotations = annotationManager.getAnnotations().getCollection().size();
        int numSpans = annotationManager.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 3;
        try {
            assert numSpans == 3;
        } catch (AssertionError ae) {
            annotationManager.getSpans(null, 0, textSource.getContent().length()).forEach((span -> log.warn(String.format("%s, %s", span, span.getAnnotation()))));
            throw new AssertionError();
        }
    }
}
