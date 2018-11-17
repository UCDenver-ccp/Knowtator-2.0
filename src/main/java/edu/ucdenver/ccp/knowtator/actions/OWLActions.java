/*
 *  MIT License
 *
 *  Copyright (c) 2018 Harrison Pielke-Lombardo
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package edu.ucdenver.ccp.knowtator.actions;

import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.model.NoSelectedOWLClassException;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorColorPalette;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OWLActions {
	public static class ReassignOWLClassAction extends AbstractKnowtatorAction {

		private final OWLClass oldOwlClass;
		private ConceptAnnotation conceptAnnotation;
		private final OWLClass newOwlClass;

		public ReassignOWLClassAction(KnowtatorController controller) throws NoSelectionException, ActionUnperformableException {
			super("Reassign OWL class");

			this.conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();

			oldOwlClass = conceptAnnotation.getOwlClass();
			try {
				this.newOwlClass = controller.getOWLModel().getSelectedOWLClass();
			} catch (NoSelectedOWLClassException e) {
				throw new ActionUnperformableException();
			}
		}

		@Override
		public void execute() {
			conceptAnnotation.setOwlClass(newOwlClass);
		}

		@Override
		public UndoableEdit getEdit() {
			return new KnowtatorEdit(getPresentationName()) {
				@Override
				public void undo() {
					super.undo();
					conceptAnnotation.setOwlClass(oldOwlClass);
				}

				@Override
				public void redo() {
					super.redo();
					conceptAnnotation.setOwlClass(newOwlClass);
				}

			};
		}
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
            Set<Object> owlClasses = new HashSet<>();
            owlClasses.add(owlClass);

            JColorChooser colorChooser = new KnowtatorColorPalette();

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

                        owlClasses.addAll(
                                view.getController()
                                        .getOWLModel()
                                        .getDescendants((OWLClass) owlClass));
                    }

	                ColorChangeAction action = new ColorChangeAction(view.getController(), view.getController().getProfileCollection().getSelection(), owlClasses, c);
                    view.getController().registerAction(action);
                }


            }
        }

    }

	static class ColorChangeAction extends AbstractKnowtatorAction {


		private final ColorChangeEdit edit;
		private final Map<Object, Color> oldColorAssignments;
		private final Profile profile;
		private final Set<Object> owlClasses;
		private final Color color;

		ColorChangeAction(KnowtatorController controller, Profile profile, Set<Object> owlClasses, Color color) {
			super("Change color");
			this.profile = profile;
			this.owlClasses = owlClasses;
			this.color = color;

			oldColorAssignments = new HashMap<>();
			owlClasses.forEach(owlClass -> {
				Color oldColor = profile.getColors().get(owlClass);
				if (oldColor != null) {
					oldColorAssignments.put(owlClass, oldColor);
				}
			});

			edit = new ColorChangeEdit(profile, oldColorAssignments, color);
		}

		@Override
		public void execute() {
			owlClasses.forEach(owlClass -> profile.addColor(owlClass, color));
		}

		@Override
		public UndoableEdit getEdit() {
			return edit;
		}
	}

	static class ColorChangeEdit extends KnowtatorEdit {

		private final Profile profile;
		private final Color color;
		private final Map<Object, Color> oldColorAssignments;

		ColorChangeEdit(Profile profile, Map<Object, Color> oldColorAssignments, Color color) {
			super("Set color for OWL class");
			this.profile = profile;
			this.oldColorAssignments = oldColorAssignments;
			this.color = color;
		}

		@Override
		public void undo() {

			super.undo();
			oldColorAssignments.forEach(profile::addColor);
		}

		@Override
		public void redo() {
			super.redo();
			oldColorAssignments.keySet().forEach(owlClass -> profile.addColor(owlClass, color));
		}
	}
}
