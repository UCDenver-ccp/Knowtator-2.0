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

package edu.ucdenver.ccp.knowtator.view.actions.modelactions;

import edu.ucdenver.ccp.knowtator.model.BaseModel;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.view.actions.AbstractKnowtatorAction;
import edu.ucdenver.ccp.knowtator.view.actions.KnowtatorEdit;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.undo.UndoableEdit;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ColorChangeAction extends AbstractKnowtatorAction {

	private final Map<OWLClass, Color> oldColorAssignments;
	private final Profile profile;
	private final Set<OWLClass> owlClasses;
	private final Color color;

	public ColorChangeAction(BaseModel model, Profile profile, Set<OWLClass> owlClasses, Color color) {
		super(model, "Change color");
		this.profile = profile;
		this.owlClasses = owlClasses;
		this.color = color;

		oldColorAssignments = new HashMap<>();
		owlClasses.forEach(owlClass -> {
			Color oldColor = profile.getColor(owlClass);
			if (oldColor != null) oldColorAssignments.put(owlClass, oldColor);
		});
	}

	@Override
	public void execute() {
		owlClasses.forEach(owlClass -> profile.addColor(owlClass, color));
		profile.save();
	}

	@Override
	public UndoableEdit getEdit() {
		return new KnowtatorEdit("Set color for OWL class") {

			@Override
			public void undo() {

				super.undo();
				oldColorAssignments.forEach(profile::addColor);
				profile.save();
			}

			@Override
			public void redo() {
				super.redo();
				oldColorAssignments.keySet().forEach(owlClass -> profile.addColor(owlClass, color));
				profile.save();
			}
		};
	}
}
