package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotationCollection;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

@SuppressWarnings("unused")
public class ConceptAnnotationCollectionTest {
    private static final Logger log = Logger.getLogger(ConceptAnnotationCollectionTest.class);


    private String[] projectFileNames = new String[]{"test_project", "old_project"};
    private String[] articleFileNames = new String[]{"document1", "document2"};
    private String[] articleContent = new String[]{
            "This is a test document.",
            "A second test document has appeared!"
    };
    private String[] profileFileNames = new String[]{"profile1", "profile2"};
    private ConceptAnnotationCollection conceptAnnotationCollection;
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

        try {
            controller.setSaveLocation(project);
            controller.loadProject();
        } catch (IOException e) {
            e.printStackTrace();
        }

        textSource = controller.getTextSourceCollection().stream().filter(textSource1 -> textSource1.getId().equals(articleName)).findAny().get();
        conceptAnnotationCollection = textSource.getConceptAnnotationCollection();
        profile = controller.getProfileCollection().addProfile("Default");

    }

    @Test
    public void addAnnotation() {
        setUp();
        ConceptAnnotation conceptAnnotation1 = conceptAnnotationCollection.addAnnotation("mention_3", null, "class_2", "class_2", profile, "identity");

        int numAnnotations = conceptAnnotationCollection.size();
        int numSpans = conceptAnnotationCollection.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 3;
        assert numSpans == 3;
    }

    @Test
    public void addSpanToAnnotation() {
        setUp();

        ConceptAnnotation conceptAnnotation1 = conceptAnnotationCollection.addAnnotation("mention_3", null, "class_2", "class_2", profile, "identity");
        Span span1 = conceptAnnotation1.getSpanCollection().addSpan(null,1, 6);

        int numAnnotations = conceptAnnotationCollection.size();
        int numSpans = conceptAnnotationCollection.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 3;
        assert numSpans == 4;
    }

    @Test
    public void removeAnnotation() {
        setUp();
        conceptAnnotationCollection.removeAnnotation(conceptAnnotationCollection.get("mention_1"));

        int numAnnotations = conceptAnnotationCollection.size();
        int numSpans = conceptAnnotationCollection.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 1;
        try {
            assert numSpans == 1;
        } catch (AssertionError ae) {
            conceptAnnotationCollection.getSpans(null, 0, textSource.getContent().length()).forEach((span -> log.warn(String.format("%s, %s", span, span.getConceptAnnotation()))));
            throw new AssertionError();
        }
    }

    @Test
    public void removeSpanFromAnnotation() {
        setUp();
        ConceptAnnotation conceptAnnotation1 = conceptAnnotationCollection.addAnnotation("mention_3", null, "class_2", "class_2", profile, "identity");
        Span span1 = conceptAnnotation1.getSpanCollection().addSpan(null,1, 6);

        conceptAnnotationCollection.removeSpanFromAnnotation(conceptAnnotation1, span1);
        int numAnnotations = conceptAnnotationCollection.size();
        int numSpans = conceptAnnotationCollection.getSpans(null, 0, textSource.getContent().length()).size();

        assert numAnnotations == 3;
        try {
            assert numSpans == 3;
        } catch (AssertionError ae) {
            conceptAnnotationCollection.getSpans(null, 0, textSource.getContent().length()).forEach((span -> log.warn(String.format("%s, %s", span, span.getConceptAnnotation()))));
            throw new AssertionError();
        }
    }
}
