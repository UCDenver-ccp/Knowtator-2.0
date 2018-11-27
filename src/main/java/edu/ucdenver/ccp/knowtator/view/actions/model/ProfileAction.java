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

package edu.ucdenver.ccp.knowtator.view.actions.model;

import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorColorPalette;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import edu.ucdenver.ccp.knowtator.view.actions.ActionUnperformableException;
import edu.ucdenver.ccp.knowtator.view.actions.collection.AbstractKnowtatorCollectionAction;
import edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static edu.ucdenver.ccp.knowtator.view.actions.collection.CollectionActionType.REMOVE;

public class ProfileAction extends AbstractKnowtatorCollectionAction<Profile> {
	private final String profileId;

	public ProfileAction(CollectionActionType actionType, String profileId) {
		super(actionType, "Add profile", KnowtatorView.CONTROLLER.getProfileCollection());
		this.profileId = profileId;
	}

	@Override
	public void prepareRemove() {
		setObject(collection.get(profileId));

	}

	@Override
	protected void prepareAdd() {
		Profile profile = new Profile(KnowtatorView.CONTROLLER, profileId);
		setObject(profile);
	}

	@Override
	protected void cleanUpRemove() throws ActionUnperformableException {
		for (TextSource textSource : KnowtatorView.CONTROLLER.getTextSourceCollection()) {
			// Cast to array to avoid concurrent modification exceptions
			Object[] array = textSource.getConceptAnnotationCollection().getCollection().toArray();
			for (Object o : array) {
				ConceptAnnotation conceptAnnotation = (ConceptAnnotation) o;

				if (object.map(object -> conceptAnnotation.getAnnotator().equals(object)).orElse(false)) {
					ConceptAnnotationAction action = new ConceptAnnotationAction(REMOVE, conceptAnnotation.getTextSource());
					action.setObject(conceptAnnotation);
					action.execute();
					edit.addKnowtatorEdit(action.getEdit());
				}
			}
		}
	}

	@Override
	public void cleanUpAdd() {

	}

	public static void assignColorToClass(KnowtatorView view, Object owlClass) {
		Optional<Object> objectOptional = Optional.ofNullable(owlClass);

		if (!objectOptional.isPresent()) {
			objectOptional =
					KnowtatorView.CONTROLLER.getTextSourceCollection().getSelection()
							.flatMap(textSource -> textSource.getConceptAnnotationCollection().getSelection()
									.map(ConceptAnnotation::getOwlClass));
		}
		objectOptional.ifPresent(owlClass1 -> {
			Set<Object> owlClasses = new HashSet<>();
			owlClasses.add(owlClass1);

			JColorChooser colorChooser = new KnowtatorColorPalette();

			final Color[] finalC = {null};
			JDialog dialog = JColorChooser.createDialog(view, "Pick a color for " + owlClass1, true, colorChooser,
					e -> finalC[0] = colorChooser.getColor(), null);


			dialog.setVisible(true);

			Color c = finalC[0];
			if (c != null) {

				KnowtatorView.CONTROLLER.getProfileCollection().getSelection()
						.ifPresent(profile -> profile.addColor(owlClass1, c));

				if (owlClass1 instanceof OWLClass) {
					if (JOptionPane.showConfirmDialog(view, "Assign color to descendants of " + owlClass1 + "?") == JOptionPane.OK_OPTION) {
						owlClasses.addAll(KnowtatorView.CONTROLLER.getOWLModel().getDescendants((OWLClass) owlClass1));
					}
				}

				KnowtatorView.CONTROLLER.getProfileCollection().getSelection()
						.ifPresent(profile -> KnowtatorView.CONTROLLER.registerAction(new ColorChangeAction(profile, owlClasses, c)));


			}

		});
	}
}
