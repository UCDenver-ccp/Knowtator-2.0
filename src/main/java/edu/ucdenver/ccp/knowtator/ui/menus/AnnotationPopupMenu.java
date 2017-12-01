package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.actions.AnnotationActions;
import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.annotation.TextSource;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Map;

public class AnnotationPopupMenu extends JPopupMenu {

    private BasicKnowtatorView view;
    private TextPane textPane;
    private final TextSource textSource;
    private final ConceptAnnotation selectedAnnotation;
    private final Span selectedSpan;

    public AnnotationPopupMenu(BasicKnowtatorView view, TextPane textPane, TextSource textSource, ConceptAnnotation selectedAnnotation, Span selectedSpan) {
        this.view = view;

        this.textPane = textPane;
        this.textSource = textSource;
        this.selectedAnnotation = selectedAnnotation;
        this.selectedSpan = selectedSpan;
    }



    private JMenuItem addAnnotationCommand(TextSource textSource, int start, int end) {
        JMenuItem annotateWithCurrentSelectedClass = new JMenuItem("Annotate with current selected class");
        annotateWithCurrentSelectedClass.addActionListener(e12 -> AnnotationActions.addSelectedAnnotation(textSource, new Span(start, end)));

        return annotateWithCurrentSelectedClass;
    }

    private JMenuItem addSpanToAnnotationCommand(BasicKnowtatorView view, TextSource textSource, ConceptAnnotation annotation, int start, int end) {
        JMenuItem addSpanToAnnotation = new JMenuItem("Add span to selected annotation");
        addSpanToAnnotation.addActionListener(e4 -> AnnotationActions.addSpan(view, textSource, annotation, new Span(start, end)));

        return addSpanToAnnotation;
    }

    private JMenuItem removeSpanFromAnnotationCommand(TextSource textSource, ConceptAnnotation annotation, Span span) {
        JMenuItem removeSpanFromSelectedAnnotation = new JMenuItem(String.format("Remove span from %s - %s", annotation.getClassID(), annotation.getClassName()));
        removeSpanFromSelectedAnnotation.addActionListener(e5 -> AnnotationActions.removeSpan(textSource, annotation, span));

        return removeSpanFromSelectedAnnotation;

    }

    private JMenuItem selectAnnotationCommand(ConceptAnnotation annotation, Span span) {
        JMenuItem selectAnnotationMenuItem = new JMenuItem(String.format("Select %s - %s", annotation.getClassID(), annotation.getClassName()));
        selectAnnotationMenuItem.addActionListener(e3 -> textPane.setSelection(span, annotation));

        return  selectAnnotationMenuItem;

    }

    private JMenuItem removeAnnotationCommand(TextSource textSource, ConceptAnnotation annotation) {
        JMenuItem removeAnnotationMenuItem = new JMenuItem(String.format("Remove %s - %s", annotation.getClassID(), annotation.getClassName()));
        removeAnnotationMenuItem.addActionListener(e4 -> {
            textPane.setSelection(null, null);
            textSource.getAnnotationManager().removeAnnotation(annotation);
        });

        return removeAnnotationMenuItem;
    }

    public void chooseAnnotation(MouseEvent e, Map<Span, ConceptAnnotation> spansContainingLocation) {
        // Menu items to select and remove annotations
        spansContainingLocation.forEach((span, annotation) -> add(selectAnnotationCommand(annotation, span)));

        show(e.getComponent(), e.getX(), e.getY());
    }

    private JMenuItem displayAnnotationVertexCommand(BasicKnowtatorView view) {
        JMenuItem menuItem = new JMenuItem("Add annotation as a vertex in graph");
        menuItem.addActionListener(e -> {
            view.getGraphDialog().setVisible(true);
            ConceptAnnotation selectedAnnotation = view.getTextViewer().getSelectedTextPane().getSelectedAnnotation();
            if (selectedAnnotation != null) {
                view.getGraphDialog().getGraphViewer().addVertex(selectedAnnotation);
            }
        });

        return menuItem;
    }

    private JMenuItem goToAnnotationInGraphCommand(BasicKnowtatorView view) {
        JMenuItem goToAnnotationInGraph = new JMenuItem("Go to annotation in graph");
        goToAnnotationInGraph.addActionListener(e -> {
            view.getGraphDialog().setVisible(true);
            view.getGraphDialog().getGraphViewer().goToVertex(view.getTextViewer().getSelectedTextPane().getSelectedAnnotation());
        });

        return goToAnnotationInGraph;
    }

    public void showPopUpMenu(MouseEvent e) {

        if (textPane.getSelectionStart() < textPane.getSelectionEnd()) {
            add(addAnnotationCommand(textSource, textPane.getSelectionStart(), textPane.getSelectionEnd()));
            addSeparator();

            if (selectedSpan != null) {
                add(addSpanToAnnotationCommand(view, textSource, selectedAnnotation, textPane.getSelectionStart(), textPane.getSelectionEnd()));

                addSeparator();
            }
        } else {

            if (selectedSpan != null && selectedAnnotation.getSpans().size() > 1 ) add(removeSpanFromAnnotationCommand(textSource, selectedAnnotation, selectedSpan));
            if (selectedAnnotation != null) {
                add(removeAnnotationCommand(textSource, selectedAnnotation));
                addSeparator();
                add(displayAnnotationVertexCommand(view));
                add(goToAnnotationInGraphCommand(view));
            }
        }

        show(e.getComponent(), e.getX(), e.getY());
    }

}
