package edu.ucdenver.ccp.knowtator.model.annotation;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.textsource.TextSource;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;

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
        KnowtatorController controller = new KnowtatorController();

        int projectID = 0;
        int articleID = 0;
        String projectFileName = projectFileNames[projectID];
        File project = getProjectFile(projectFileName);
        String articleName = articleFileNames[articleID];

        controller.getProjectManager().loadProject(project);

        //noinspection ConstantConditions
        textSource = controller.getTextSourceManager().getTextSources().stream().filter(textSource1 -> textSource1.getDocID().equals(articleName)).findAny().get();
        annotationManager = textSource.getAnnotationManager();
        profile = controller.getProfileManager().addNewProfile("Default");

    }

    @Test
    public void addAnnotation() {
        setUp();
        Annotation annotation1 = new Annotation("class_2", "mention_3", textSource, profile, "identity");
        annotationManager.addAnnotation(annotation1);

        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanMap(null, null).size();

        assert numAnnotations == 3;
        assert numSpans == 3;
    }

    @Test
    public void addSpanToAnnotation() {
        setUp();
        Annotation annotation1 = new Annotation( "class_2", "mention_3", textSource, profile, "identity");
        annotationManager.addAnnotation(annotation1);

        Span span1 = new Span(1, 6, "");
        annotationManager.addSpanToAnnotation(annotation1, span1);

        int numAnnotations = annotationManager.getAnnotations().size();
        int numSpans = annotationManager.getSpanMap(null, null).size();

        assert numAnnotations == 3;
        assert numSpans == 4;
    }

    @Test
    public void removeAnnotation() {
        setUp();
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
        setUp();
        Annotation annotation1 = new Annotation( "class_2", "mention_3", textSource, profile, "identity");
        annotationManager.addAnnotation(annotation1);

        Span span1 = new Span(1, 6, "");
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
}
