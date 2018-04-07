package edu.ucdenver.ccp.knowtator.view.text;

import com.mxgraph.model.mxCell;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.annotation.Annotation;
import edu.ucdenver.ccp.knowtator.model.annotation.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.event.MouseEvent;
import java.util.Map;

class AnnotationPopupMenu extends JPopupMenu {
    @SuppressWarnings("unused")
    private static final Logger log = Logger.getLogger(AnnotationPopupMenu.class);


    private MouseEvent e;
    private TextPane textPane;
    private KnowtatorController controller;
    private KnowtatorView view;


    AnnotationPopupMenu(MouseEvent e, TextPane textPane, KnowtatorController controller, KnowtatorView view) {
        this.e = e;
        this.textPane = textPane;
        this.controller = controller;
        this.view = view;
    }

    private JMenuItem addAnnotationCommand() {
        JMenuItem menuItem = new JMenuItem("Add annotation");
        menuItem.addActionListener(e12 -> textPane.addAnnotation());

        return menuItem;
    }

    private JMenuItem addSpanToAnnotationCommand() {
        JMenuItem addSpanToAnnotation = new JMenuItem("Add span");
        addSpanToAnnotation.addActionListener(e4 -> textPane.addSpanToAnnotation());

        return addSpanToAnnotation;
    }

    private JMenuItem removeSpanFromAnnotationCommand() {
        JMenuItem removeSpanFromSelectedAnnotation = new JMenuItem(String.format("Delete span from %s", controller.getSelectionManager().getSelectedAnnotation().getOwlClass()));
        removeSpanFromSelectedAnnotation.addActionListener(e5 -> textPane.getTextSource().getAnnotationManager()
                .removeSpanFromAnnotation(
                        controller.getSelectionManager().getSelectedAnnotation(),
                        controller.getSelectionManager().getSelectedSpan()
                )
        );

        return removeSpanFromSelectedAnnotation;

    }

    private JMenuItem selectAnnotationCommand(Annotation annotation, Span span) {
        JMenuItem selectAnnotationMenuItem = new JMenuItem("Select " + annotation.getOwlClass());
        selectAnnotationMenuItem.addActionListener(e3 -> textPane.setSelection(span, annotation));

        return  selectAnnotationMenuItem;

    }

    private JMenuItem removeAnnotationCommand() {
        JMenuItem removeAnnotationMenuItem = new JMenuItem("Delete " + controller.getSelectionManager().getSelectedAnnotation().getOwlClass());
        removeAnnotationMenuItem.addActionListener(e4 -> textPane.removeAnnotation());

        return removeAnnotationMenuItem;
    }

    void chooseAnnotation(Map<Span, Annotation> spansContainingLocation) {
        // Menu items to select and remove annotations
        spansContainingLocation.forEach((span, annotation) -> add(selectAnnotationCommand(annotation, span)));

        show(e.getComponent(), e.getX(), e.getY());
    }

    private JMenu goToAnnotationInGraphCommand() {
        JMenu jMenu = new JMenu("Graphs");

        textPane.getTextSource().getAnnotationManager().getGraphSpaces()
                .forEach(graphSpace -> {
                    mxCell vertex = graphSpace.containsVertexCorrespondingToAnnotation(controller.getSelectionManager().getSelectedAnnotation());
                    if (vertex != null) {
                        JMenuItem menuItem = new JMenuItem(graphSpace.getId());
                        menuItem.addActionListener(e1 -> {
                            view.getGraphViewer().getDialog().setVisible(true);
                            view.getGraphViewer().goToAnnotationVertex(graphSpace, controller.getSelectionManager().getSelectedAnnotation());
                        });
                        jMenu.add(menuItem);
                    }
                });

        return jMenu;
    }

    void showPopUpMenu(int release_offset) {

        Annotation selectedAnnotation = controller.getSelectionManager().getSelectedAnnotation();
        Span selectedSpan = controller.getSelectionManager().getSelectedSpan();

        if (textPane.getSelectionStart() <= release_offset && release_offset <= textPane.getSelectionEnd() && textPane.getSelectionStart() != textPane.getSelectionEnd()) {
            textPane.select(textPane.getSelectionStart(), textPane.getSelectionEnd());
            add(addAnnotationCommand());
            if (controller.getSelectionManager().getSelectedAnnotation() != null) {
                add(addSpanToAnnotationCommand());
            }
        } else if (selectedAnnotation != null  && selectedSpan.getStart() <= release_offset && release_offset <= selectedSpan.getEnd()) {
            add(removeAnnotationCommand());
            if (controller.getSelectionManager().getSelectedSpan() != null && controller.getSelectionManager().getSelectedAnnotation() != null && controller.getSelectionManager().getSelectedAnnotation().getSpans().size() > 1) {
                add(removeSpanFromAnnotationCommand());
            }

            if (controller.getSelectionManager().getSelectedAnnotation() != null) {
                addSeparator();
                add(goToAnnotationInGraphCommand());
            }
        } else {
            return;
        }

        show(e.getComponent(), e.getX(), e.getY());
    }

}
