package edu.ucdenver.ccp.knowtator.view;

import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.KnowtatorColors;
import edu.ucdenver.ccp.knowtator.view.graph.GraphViewDialog;
import edu.ucdenver.ccp.knowtator.view.menu.MenuDialog;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Enumeration;
import java.util.Set;

public class KnowtatorActions {
    static void showMainMenuDialog(KnowtatorView view) {
        MenuDialog menuDialog = new MenuDialog(SwingUtilities.getWindowAncestor(view), view);
        menuDialog.pack();
        menuDialog.setVisible(true);
    }

    static void showGraphViewer(GraphViewDialog graphViewDialog) {
        graphViewDialog.setVisible(true);
    }

    public static void assignColorToClass(KnowtatorView view, Object owlClass) {
        if (owlClass == null) {
            try {
                owlClass =
                        view.getController()
                                .getTextSourceCollection().getSelection()
                                .getConceptAnnotationCollection()
                                .getSelection()
                                .getOwlClass();
            } catch (NoSelectionException e) {
                e.printStackTrace();
            }
        }
        if (owlClass != null) {
            JColorChooser colorChooser = new edu.ucdenver.ccp.knowtator.view.KnowtatorColorChooser();

            final Color[] finalC = {null};
            JDialog dialog = JColorChooser.createDialog(view, "Pick a color for " + owlClass, true, colorChooser,
                    e -> finalC[0] = colorChooser.getColor(), null);


            dialog.setVisible(true);

            Color c = finalC[0];
            if (c != null) {
                view.getController().getProfileCollection().getSelection().addColor(owlClass, c);


                if (owlClass instanceof OWLClass) {
                    if (JOptionPane.showConfirmDialog(
                            view, "Assign color to descendants of " + owlClass + "?")
                            == JOptionPane.OK_OPTION) {
                        Set<OWLClass> descendants =
                                view.getController()
                                        .getOWLModel()
                                        .getDescendants((OWLClass) owlClass);

                        for (OWLClass descendant : descendants) {
                            view.getController().getProfileCollection()
                                    .getSelection()
                                    .addColor(descendant, c);
                        }
                    }
                }
            }
        }

    }

    static class CustomColorChooser extends AbstractColorChooserPanel implements ActionListener {

        ButtonGroup colorChoices;

        @Override
        public void updateChooser() {
            Color color = getColorFromModel();
            Enumeration<AbstractButton> elements = colorChoices.getElements();
            while(elements.hasMoreElements()) {
                AbstractButton e = elements.nextElement();
                e.setSelected(e.getBackground().equals(color));
            }
        }

        @Override
        protected void buildChooser() {
            setLayout(new GridLayout(0, 1));

            colorChoices = new ButtonGroup();

            Border border = BorderFactory.createEmptyBorder(4, 4, 4, 4);

            KnowtatorColors.COLORS.forEach(color -> {
                JToggleButton button = new JToggleButton();
                button.setBackground(color);
                button.addActionListener(this);
                button.setBorder(border);

                colorChoices.add(button);
                add(button);
            });
        }

        @Override
        public String getDisplayName() {
            return "Color chooser";
        }

        @Override
        public Icon getSmallDisplayIcon() {
            return null;
        }

        @Override
        public Icon getLargeDisplayIcon() {
            return null;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            getColorSelectionModel().setSelectedColor(((JToggleButton) e.getSource()).getBackground());
        }
    }
}
