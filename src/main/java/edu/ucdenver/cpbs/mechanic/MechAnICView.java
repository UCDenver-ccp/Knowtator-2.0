package edu.ucdenver.cpbs.mechanic;

import edu.ucdenver.cpbs.mechanic.Commands.*;
import edu.ucdenver.cpbs.mechanic.TextAnnotation.TextAnnotationManager;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICProfileViewer;
import edu.ucdenver.cpbs.mechanic.ui.MechAnICTextViewer;
import edu.ucdenver.cpbs.mechanic.xml.XmlUtil;
import org.apache.log4j.Logger;
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import java.awt.*;
import java.awt.dnd.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * @author Harrison Pielke-Lombardo
 */
@SuppressWarnings("PackageAccessibility")
public class MechAnICView extends AbstractOWLClassViewComponent implements DropTargetListener {

    private static final Logger log = Logger.getLogger(MechAnICView.class);

    private JSplitPane splitPane;
    private JTabbedPane tabbedPane;
    private ProfileManager profileManager;

    private TextAnnotationManager textAnnotationManager;
    private MechAnICSelectionModel selectionModel;
    private XmlUtil xmlUtil;
    private MechAnICTextViewer textViewer;

    /**
     *
     */
    //TODO add a POS highlighter
    @Override
    public void initialiseClassView() {

        /*
        Initialize the managers, models, and utils
         */
        selectionModel = new MechAnICSelectionModel();  //helps get the selected OWL API classes
        profileManager = new ProfileManager();  //manipulates profiles and highlighters
        textAnnotationManager = new TextAnnotationManager(this, selectionModel, profileManager);  //manipulates the annotations
        xmlUtil = new XmlUtil(textAnnotationManager);  //reads and writes to XML

        createUI();

        DropTarget dt = new DropTarget(this, this);
        dt.setActive(true);

        setupInitial();
        setupListeners();

        log.warn("Initialized MechAnIC");
    }

    @Override
    protected OWLClass updateView(OWLClass selectedClass) {
        selectionModel.setSelectedClass(selectedClass);
        return selectedClass;
    }

    /**
     * TODO add view to see and manage the mechanism Graphs
     */
    private void createUI() {
        setLayout(new BorderLayout());

        /*
        Create a tabbed pane containing the text viewer for each document
         */
        tabbedPane = new JTabbedPane();
        tabbedPane.setMinimumSize(new Dimension(100, 50));

        /*
        Create a viewer to see the highlighters for the current profile
         */
        MechAnICProfileViewer profileViewer = new MechAnICProfileViewer();
        profileViewer.setMinimumSize(new Dimension(100, 50));
        profileManager.setProfileViewer(profileViewer);
        profileManager.setupDefault();

        /*
        Place the tabbed text viewers and the profile viewer in a split pane
         */
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setOneTouchExpandable(true);
        add(splitPane);
        splitPane.add(tabbedPane);
        splitPane.add(profileViewer);
        splitPane.setDividerLocation(1200);

        /*
        Make the toolbar seen at the top of the view
         */
        createToolBar();
    }

    /**
     * TODO make initial document the last document that was edited
     */
    private void setupInitial() {
        /*
        An initial document to display
         */
        try {
            String fileName = "/file/test_article.txt";
            textViewer = new MechAnICTextViewer();
            textViewer.setName(fileName);
            JScrollPane sp = new JScrollPane(textViewer);
            tabbedPane.add(sp);
            tabbedPane.setTitleAt(0, fileName);

            InputStream is = getClass().getResourceAsStream(fileName);
            textViewer.read(new BufferedReader(new InputStreamReader(is)), fileName);


        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            String fileName = "/file/test_annotations.xml";
//            InputStream is = getClass().getResourceAsStream(fileName);
//            MechAnICTextViewer textViewer = (MechAnICTextViewer)((JScrollPane)tabbedPane.getSelectedComponent()).getViewport().getView();
//            xmlUtil.loadTextAnnotationsFromXML(is, textViewer);
//        } catch (ParserConfigurationException | IOException | SAXException e) {
//            e.printStackTrace();
//        }
    }

    /**
     * Add buttons corresponding to each of the actions to the toolbar
     */
    private void createToolBar() {
        /*
        Text document related actions
         */
        addAction(new OpenDocumentCommand(tabbedPane), "A", "A");
        addAction(new CloseDocumentCommand(tabbedPane), "A", "B");
        addAction(new IncreaseTextSizeCommand(tabbedPane), "A", "C");
        addAction(new DecreaseTextSizeCommand(tabbedPane), "A", "D");

        /*
        Text annotation related actions
         */
        addAction(new LoadAnnotationsCommand(tabbedPane, xmlUtil), "B", "A");
        addAction(new SaveAnnotationsToXmlCommand(tabbedPane, xmlUtil), "B", "B");
        addAction(new AddTextAnnotationCommand(textAnnotationManager, tabbedPane, selectionModel), "B", "C");
        addAction(new RemoveTextAnnotationCommand(textAnnotationManager, tabbedPane, selectionModel), "B", "D");

        /*
        Profile and highlighter related commands
         */
        addAction(new NewProfileCommand(profileManager), "C", "A");
        addAction(new SwitchProfileCommand(profileManager), "C", "B");
        addAction(new NewHighlighterCommand(selectionModel, profileManager), "C", "C");
    }

    public void setGlobalSelection1(OWLEntity selObj) {
        setGlobalSelection(selObj);
    }

    private void setupListeners() {
        textViewer.addMouseListener(
                new MouseListener() {
                    int press_offset;
                    int release_offset;
                    @Override
                    public void mouseClicked(MouseEvent e) {

                    }

                    @Override
                    public void mousePressed(MouseEvent e) {
                        press_offset = textViewer.viewToModel(e.getPoint());
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        release_offset = textViewer.viewToModel(e.getPoint());

                        int start, end;
                        try {
                            if (press_offset < release_offset) {

                                    start = Utilities.getWordStart(textViewer, press_offset);
                                    end = Utilities.getWordEnd(textViewer, release_offset);
                            } else {
                                start = Utilities.getWordStart(textViewer, release_offset);
                                end = Utilities.getWordEnd(textViewer, press_offset);
                            }
                            textAnnotationManager.setSelectedAnnotation(start, end);
                            textViewer.setSelectionStart(start);
                            textViewer.setSelectionEnd(end);
                        } catch (BadLocationException e1) {
                            e1.printStackTrace();
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {

                    }

                    @Override
                    public void mouseExited(MouseEvent e) {

                    }
                }
        );
    }

    @Override
    public void disposeView() {

    }

    @Override
    public void dragEnter(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragOver(DropTargetDragEvent dtde) {

    }

    @Override
    public void dropActionChanged(DropTargetDragEvent dtde) {

    }

    @Override
    public void dragExit(DropTargetEvent dte) {

    }

    @Override
    public void drop(DropTargetDropEvent dtde) {

    }

    public static void main(String[] args) throws IOException { }
}
