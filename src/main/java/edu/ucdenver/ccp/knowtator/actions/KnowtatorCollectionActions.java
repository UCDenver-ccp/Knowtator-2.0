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

import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.NoSelectedOWLClassException;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.model.text.graph.GraphSpace;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.OWLClass;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class KnowtatorCollectionActions {
	public final static String ADD = AbstractKnowtatorCollectionAction.ADD;
	public static final String REMOVE = AbstractKnowtatorCollectionAction.REMOVE;

	public static void pickAction(Map<String, String> actionParameters, KnowtatorView view, String id, File file) {
		List<AbstractKnowtatorCollectionAction> actions = new ArrayList<>();

		actionParameters.forEach((objectName, actionName) -> {

			try {
				switch (objectName) {
					case KnowtatorXMLTags.ANNOTATION:
						actions.add(new ConceptAnnotationAction(actionName, view.getController()));
						break;
					case KnowtatorXMLTags.SPAN:
						actions.add(new SpanAction(actionName, view.getController()));
						break;
					case KnowtatorXMLTags.PROFILE:
						actions.add(new ProfileAction(actionName, view.getController(), id));
						break;
					case KnowtatorXMLTags.DOCUMENT:
						actions.add(new TextSourceAction(actionName, view.getController(), file));
				}

			} catch (NoSelectionException ignored) {

			}
		});

		if (!actions.isEmpty()) {
			AbstractKnowtatorAction action;
			if (actions.size() == 1) {
				action = actions.get(0);
			} else {
				int response = JOptionPane.showOptionDialog(view,
						"Choose an option",
						"New Concept Annotation or Span",
						JOptionPane.DEFAULT_OPTION,
						JOptionPane.PLAIN_MESSAGE,
						null,
						actions.stream().map(AbstractKnowtatorAction::getPresentationName).toArray(),
						2);
				action = actions.get(response);
			}

			view.getController().registerAction(action);
		}
	}

	public static class ConceptAnnotationAction extends AbstractKnowtatorCollectionAction<ConceptAnnotation> {

		private final KnowtatorController controller;
		private ConceptAnnotation newConceptAnnotation;
		private TextSource textSource;

		ConceptAnnotationAction(String actionName, KnowtatorController controller) throws NoSelectionException {
			super(actionName, "concept annotation", controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection());
			this.controller = controller;
			this.textSource = controller.getTextSourceCollection().getSelection();
		}

		@Override
		public void cleanUpAdd() {
		}

		@Override
		public void cleanUpRemove() {
			textSource.getGraphSpaceCollection().forEach(graphSpace -> graphSpace.getModel().removeListener(edit, mxEvent.UNDO));
		}


		@Override
		void prepareRemove() throws ActionUnperformableException {
			super.prepareRemove();
			edit = new KnowtatorCollectionEdit<>(REMOVE, collection, object, getPresentationName());
			textSource.getGraphSpaceCollection().forEach(graphSpace -> graphSpace.getModel().addListener(mxEvent.UNDO, edit));
		}

		@Override
		void prepareAdd() throws ActionUnperformableException {
			try {
				OWLClass owlClass = controller.getOWLModel().getSelectedOWLClass();
				Profile annotator = controller.getProfileCollection().getSelection();
				String owlClassID = controller.getOWLModel().getOWLEntityRendering(owlClass);


				newConceptAnnotation = new ConceptAnnotation(controller, null, owlClass, owlClassID, null, annotator, "identity", textSource);
				newConceptAnnotation.getSpanCollection().add(new Span(controller, textSource, newConceptAnnotation, null, controller.getSelectionModel().getStart(), controller.getSelectionModel().getEnd()));
				setObject(newConceptAnnotation);
			} catch (NoSelectedOWLClassException e) {
				throw new ActionUnperformableException();
			}
		}

	}

	public static class SpanAction extends AbstractKnowtatorCollectionAction<Span> {

		private final KnowtatorController controller;
		private final ConceptAnnotation conceptAnnotation;

		SpanAction(String actionName, KnowtatorController controller) throws NoSelectionException {
			super(actionName, "span", controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection().getSpanCollection());
			conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();

			this.controller = controller;
		}

		@Override
		void prepareAdd() {
			int start = controller.getSelectionModel().getStart();
			int end = controller.getSelectionModel().getEnd();

			Span newSpan = new Span(controller, conceptAnnotation.getTextSource(), conceptAnnotation, null, start, end);
			setObject(newSpan);
		}

		@Override
		void cleanUpRemove() {

		}

		@Override
		public void cleanUpAdd() {

		}

	}

	static class ProfileAction extends AbstractKnowtatorCollectionAction<Profile> {
		private final KnowtatorController controller;
		private final String profileId;

		ProfileAction(String actionName, KnowtatorController controller, String profileId) {
			super(actionName, "Add profile", controller.getProfileCollection());
			this.controller = controller;
			this.profileId = profileId;
		}

		@Override
		void prepareRemove() {
			setObject(collection.get(profileId));
		}

		@Override
		void prepareAdd() {
			Profile profile = new Profile(controller, profileId);
			setObject(profile);
		}

		@Override
		void cleanUpRemove() {

		}

		@Override
		public void cleanUpAdd() {

		}
	}

	static class TextSourceAction extends AbstractKnowtatorCollectionAction<TextSource> {
		private final KnowtatorController controller;
		private final File file;

		TextSourceAction(String actionName, KnowtatorController controller, File file) {
			super(actionName, "text source", controller.getTextSourceCollection());
			this.controller = controller;
			this.file = file;
		}

		@Override
		void prepareAdd() {
			setObject(new TextSource(controller, file, file.getName()));
			if (!file.getParentFile().equals(controller.getTextSourceCollection().getArticlesLocation())) {
				try {
					FileUtils.copyFile(file, new File(controller.getTextSourceCollection().getArticlesLocation(), file.getName()));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			setObject(new TextSource(controller, null, file.getName()));
		}

		@Override
		void cleanUpRemove() {

		}

		@Override
		void cleanUpAdd() {

		}
	}

	public static class GraphSpaceAction extends AbstractKnowtatorCollectionAction<GraphSpace> {
		private final KnowtatorController controller;
		private final String graphName;

		public GraphSpaceAction(String actionName, KnowtatorController controller, String graphName) throws NoSelectionException {
			super(actionName, "graph space", controller.getTextSourceCollection().getSelection().getGraphSpaceCollection());
			this.controller = controller;
			this.graphName = graphName;
		}

		@Override
		void prepareAdd() throws ActionUnperformableException {
			try {
				GraphSpace newGraphSpace = new GraphSpace(controller, controller.getTextSourceCollection().getSelection(), graphName);
				setObject(newGraphSpace);
			} catch (NoSelectionException e) {
				throw new ActionUnperformableException();
			}
		}

		@Override
		void cleanUpRemove() {

		}

		@Override
		void cleanUpAdd() {

		}
	}
}
