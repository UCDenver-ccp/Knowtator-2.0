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

import edu.ucdenver.ccp.knowtator.model.KnowtatorModel;
import edu.ucdenver.ccp.knowtator.model.object.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.object.Profile;
import edu.ucdenver.ccp.knowtator.model.object.TextSource;
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

	public ProfileAction(KnowtatorModel model, CollectionActionType actionType, String profileId) {
		super(model, actionType, "Add profile", model.getProfiles());
		this.profileId = profileId;
	}

	@Override
	public void prepareRemove() {
		collection.get(profileId).ifPresent(this::setObject);

	}

	@Override
	protected void prepareAdd() {
		Profile profile = new Profile(model, profileId);
		setObject(profile);
	}

	@Override
	protected void cleanUpRemove() throws ActionUnperformableException {
		for (TextSource textSource : model.getTextSources()) {
			// Cast to array to avoid concurrent modification exceptions
			Object[] array = textSource.getConceptAnnotations().getCollection().toArray();
			for (Object o : array) {
				ConceptAnnotation conceptAnnotation = (ConceptAnnotation) o;

				if (conceptAnnotation.getAnnotator().equals(object)) {

					ConceptAnnotationAction action = new ConceptAnnotationAction(model, REMOVE, textSource);
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

	public static void assignColorToClass(KnowtatorView view, OWLClass owlClass) {
		view.getModel().ifPresent(model1 -> {
			Optional<OWLClass> objectOptional = Optional.ofNullable(owlClass);

			if (!objectOptional.isPresent()) {
				objectOptional = model1.getSelectedTextSource()
						.flatMap(textSource -> textSource.getSelectedAnnotation()
								.map(ConceptAnnotation::getOwlClass));
			}
			objectOptional.ifPresent(_owlClass -> {
				Set<OWLClass> owlClasses = new HashSet<>();
				owlClasses.add(_owlClass);

				JColorChooser colorChooser = new KnowtatorColorPalette();

				final Optional[] finalC = new Optional[]{Optional.empty()};
				JDialog dialog = JColorChooser.createDialog(view, String.format("Pick a color for %s", _owlClass), true, colorChooser,
						e -> finalC[0] = Optional.ofNullable(colorChooser.getColor()), null);


				dialog.setVisible(true);

				Optional c = finalC[0];
				if (c.isPresent() && c.get() instanceof Color) {
					Color color = (Color) c.get();


					model1.getSelectedProfile()
							.ifPresent(profile -> profile.addColor(_owlClass, color));

					if (JOptionPane.showConfirmDialog(view, String.format("Assign color to descendants of %s?", _owlClass)) == JOptionPane.OK_OPTION) {
						owlClasses.addAll(model1.getOWLCLassDescendants(_owlClass));
					}

					model1.getSelectedProfile()
							.ifPresent(profile -> model1.registerAction(new ColorChangeAction(model1, profile, owlClasses, color)));


				}

			});
		});
	}
}
