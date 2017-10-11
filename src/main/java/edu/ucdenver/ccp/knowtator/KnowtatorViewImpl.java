package edu.ucdenver.ccp.knowtator;

import edu.ucdenver.ccp.knowtator.Commands.*;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorGraphViewer;
import edu.ucdenver.ccp.knowtator.ui.KnowtatorTextViewer;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class KnowtatorViewImpl extends KnowtatorView {

    public static final Logger log = Logger.getLogger(KnowtatorView.class);

    @Override
    public void initialiseClassView() {

        super.initialiseClassView();

        createUI();
        setupInitial();

        log.warn("Initialized Knowtator");
    }

    public void createUI() {
        setLayout(new BorderLayout());

        /*
        Create a tabbed pane containing the text viewer for each document
         */
        textViewer = new KnowtatorTextViewer(this);
        textViewer.setMinimumSize(new Dimension(100, 50));

        /*
        Create a viewer to see the annotations as a graph
         */
        graphViewer = new KnowtatorGraphViewer(this);


        JSplitPane mainSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        mainSplitPane.setOneTouchExpandable(true);
        add(mainSplitPane);
        mainSplitPane.add(textViewer);
        mainSplitPane.add(graphViewer);
        mainSplitPane.setDividerLocation(300);

        /*
        Make the toolbar seen at the top of the view
         */
        createToolBar();
    }

    /**
     * TODO make initial document the last document that was edited
     */
    public void setupInitial() {
        /*
        Load GO ontology
         */
//        ((OWLModelManagerImpl) getOWLModelManager()).loadOntologyFromPhysicalURI(URI.create("http://purl.obolibrary.org/obo/go/go-basic.obo"));

        profileManager.setupDefault();

        /*
        An initial document to display
         */
        List<String> testArticles = new ArrayList<>();
        testArticles.add("/file/test_article.txt");
        testArticles.add("/file/test_article2.txt");
        List<String> testAnnotations = new ArrayList<>();
        testAnnotations.add("/file/test_annotations.xml");
        textViewer.addDocuments(testArticles, testAnnotations, true);

    }

    /**
     * Add buttons corresponding to each of the actions to the toolbar
     */
    public void createToolBar() {
        /*
        Text document related actions
         */
        addAction(new OpenDocumentCommand(this), "A", "A");
        addAction(new CloseDocumentCommand(this), "A", "B");

        addAction(new IncreaseTextSizeCommand(this), "A", "C");
        addAction(new DecreaseTextSizeCommand(this), "A", "D");

        /*
        Text annotation related actions
         */
        addAction(new LoadTextAnnotationsCommand(this), "B", "A");
        addAction(new SaveTextAnnotationsCommand(this), "B", "B");

        addAction(new RunIAACommand(this), "B", "E");

        addAction(new IncrementSelectionLeftCommand(this), "B", "F");
        addAction(new DecrementSelectionLeftCommand(this), "B", "G");
        addAction(new DecrementSelectionRightCommand(this), "B", "H");
        addAction(new IncrementSelectionRightCommand(this), "B", "I");

        /*
        Annotator and highlighter related commands
         */
        addAction(new NewProfileCommand(this), "C", "A");
        addAction(new SwitchProfileCommand(this), "C", "B");
        addAction(new NewHighlighterCommand(this), "C", "C");

        /*
        Graph Viewer related commands
         */
        addAction(new AddTextAnnotationNodeCommand(this), "D", "A");
    }
}
