package edu.ucdenver.ccp.knowtator.ui.menus;

import edu.ucdenver.ccp.knowtator.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.annotation.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.annotation.Span;
import edu.ucdenver.ccp.knowtator.ui.BasicKnowtatorView;
import edu.ucdenver.ccp.knowtator.ui.text.TextPane;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Map;

public class AnnotationPopupMenu extends JPopupMenu {

    private MouseEvent e;
    private BasicKnowtatorView view;
    private TextPane textPane;

    public AnnotationPopupMenu(MouseEvent e, BasicKnowtatorView view, TextPane textPane) {
        this.e = e;
        this.view = view;

        this.textPane = textPane;
    }



    private JMenuItem addAnnotationCommand() {
        JMenuItem annotateWithCurrentSelectedClass = new JMenuItem("Annotate with current selected class");
        annotateWithCurrentSelectedClass.addActionListener(e12 -> textPane.getTextSource().getAnnotationManager()
                .addConceptAnnotation(
                        new Span(textPane.getSelectionStart(), textPane.getSelectionEnd())
                )
        );

        return annotateWithCurrentSelectedClass;
    }

    private JMenuItem addSpanToAnnotationCommand() {
        JMenuItem addSpanToAnnotation = new JMenuItem("Add span to selected annotation");
        addSpanToAnnotation.addActionListener(e4 -> textPane.getTextSource().getAnnotationManager()
                .addSpanToConceptAnnotation(
                        textPane.getSelectedAnnotation(),
                        new Span(textPane.getSelectionStart(), textPane.getSelectionEnd())
                )
        );

        return addSpanToAnnotation;
    }

    private JMenuItem removeSpanFromAnnotationCommand() {
        JMenuItem removeSpanFromSelectedAnnotation = new JMenuItem(String.format("Remove span from %s - %s", textPane.getSelectedAnnotation().getClassID(), textPane.getSelectedAnnotation().getClassName()));
        removeSpanFromSelectedAnnotation.addActionListener(e5 -> textPane.getTextSource().getAnnotationManager()
                .removeSpanFromConceptAnnotation(
                        textPane.getSelectedAnnotation(),
                        textPane.getSelectedSpan()
                )
        );

        return removeSpanFromSelectedAnnotation;

    }

    private JMenuItem selectAnnotationCommand(ConceptAnnotation annotation, Span span) {
        JMenuItem selectAnnotationMenuItem = new JMenuItem(String.format("Select %s - %s", annotation.getClassID(), annotation.getClassName()));
        selectAnnotationMenuItem.addActionListener(e3 -> textPane.setSelection(span, annotation));

        return  selectAnnotationMenuItem;

    }

    private JMenuItem removeAnnotationCommand() {
        JMenuItem removeAnnotationMenuItem = new JMenuItem(
                String.format("Remove %s - %s", textPane.getSelectedAnnotation().getClassID(), textPane.getSelectedAnnotation().getClassName())
        );
        removeAnnotationMenuItem.addActionListener(e4 -> {
            textPane.getTextSource().getAnnotationManager().removeAnnotation(textPane.getSelectedAnnotation().getID());
            textPane.setSelection(null, null);
        });

        return removeAnnotationMenuItem;
    }

    public void chooseAnnotation( Map<Span, ConceptAnnotation> spansContainingLocation) {
        // Menu items to select and remove annotations
        spansContainingLocation.forEach((span, annotation) -> add(selectAnnotationCommand(annotation, span)));

        show(e.getComponent(), e.getX(), e.getY());
    }

    private JMenuItem displayAnnotationVertexCommand() {
        JMenuItem menuItem = new JMenuItem("Add annotation as a vertex in graph");
        menuItem.addActionListener(e -> {
            textPane.getGraphDialog().setVisible(true);
            if (textPane.getSelectedAnnotation() != null) {
                textPane.getGraphDialog().getGraphViewer().addAnnotationVertex(textPane.getSelectedAnnotation().getID());
            }
        });

        return menuItem;
    }

    private JMenuItem goToAnnotationInGraphCommand() {
        JMenuItem goToAnnotationInGraph = new JMenuItem("Go to annotation in graph");
        goToAnnotationInGraph.addActionListener(e -> {
            view.getTextViewer().getSelectedTextPane().getGraphDialog().setVisible(true);
            view.getTextViewer().getSelectedTextPane().getGraphDialog().getGraphViewer().goToVertex(view.getTextViewer().getSelectedTextPane().getSelectedAnnotation());
        });

        return goToAnnotationInGraph;
    }

    public void showPopUpMenu(int release_offset) {

        Annotation selectedAnnotation = textPane.getSelectedAnnotation();
        Span selectedSpan = textPane.getSelectedSpan();

        if (textPane.getSelectionStart() <= release_offset && release_offset <= textPane.getSelectionEnd() && textPane.getSelectionStart() != textPane.getSelectionEnd()) {
            textPane.select(textPane.getSelectionStart(), textPane.getSelectionEnd());
            add(addAnnotationCommand());
            if (textPane.getSelectedAnnotation() != null) {
                add(addSpanToAnnotationCommand());
            }
        } else if (selectedAnnotation != null  && selectedSpan.getStart() <= release_offset && release_offset <= selectedSpan.getEnd()) {
            add(removeAnnotationCommand());
            if (textPane.getSelectedSpan() != null && textPane.getSelectedAnnotation() != null && textPane.getSelectedAnnotation().getSpans().size() > 1 ) {
                add(removeSpanFromAnnotationCommand());
            }

            if (textPane.getSelectedAnnotation() != null) {
                addSeparator();
                add(displayAnnotationVertexCommand());
                add(goToAnnotationInGraphCommand());
            }
        } else {
            return;
        }

        show(e.getComponent(), e.getX(), e.getY());
    }

}
