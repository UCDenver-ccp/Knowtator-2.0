package edu.ucdenver.ccp.knowtator.actions;

import com.mxgraph.util.mxEvent;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.collection.NoSelectionException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.model.text.concept.span.Span;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;
import org.apache.commons.io.FileUtils;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLEntity;

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

            } catch (NoSelectionException e) {
                e.printStackTrace();
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
            super(actionName, "concept annotation");
            this.controller = controller;
            this.textSource = controller.getTextSourceCollection().getSelection();
            setCollection(textSource.getConceptAnnotationCollection());
        }

        @Override
        public void cleanUpAdd() {
            newConceptAnnotation.getSpanCollection().add(new Span(controller, textSource, newConceptAnnotation, null, controller.getSelectionModel().getStart(), controller.getSelectionModel().getEnd()));
        }

        @Override
        public void cleanUpRemove() {
            textSource.getGraphSpaceCollection().forEach(graphSpace -> graphSpace.removeListener(edit, mxEvent.UNDO));
        }


        @Override
        void prepareRemove() throws ActionUnperformableException {
            try {
                setCollection(textSource.getConceptAnnotationCollection());

                ConceptAnnotation conceptAnnotation;

                conceptAnnotation = textSource.getConceptAnnotationCollection().getSelection();

                setObject(conceptAnnotation);
                edit = new KnowtatorCollectionEdit<>(ADD, collection, object, getPresentationName());
                textSource.getGraphSpaceCollection().forEach(graphSpace -> graphSpace.addListener(mxEvent.UNDO, edit));
            } catch (NoSelectionException e) {
                throw new ActionUnperformableException();
            }
        }

        @Override
        void prepareAdd() throws ActionUnperformableException {
            OWLEntity owlEntity = controller.getOWLModel().getSelectedOWLEntity();
            if (owlEntity instanceof OWLClass) {

                Profile annotator = controller.getProfileCollection().getSelection();
                String owlClassID = controller.getOWLModel().getOWLEntityRendering(owlEntity);


                newConceptAnnotation = new ConceptAnnotation(controller, null, (OWLClass) owlEntity, owlClassID, null, annotator, "identity", textSource);

                setObject(newConceptAnnotation);
            } else {
                throw new ActionUnperformableException();
            }
        }

    }

    public static class SpanAction extends AbstractKnowtatorCollectionAction<Span> {

        private final KnowtatorController controller;
        private final ConceptAnnotation conceptAnnotation;

        SpanAction(String actionName, KnowtatorController controller) throws NoSelectionException {
            super(actionName, "span");
            conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();
            setCollection(conceptAnnotation.getSpanCollection());

            this.controller = controller;
        }

        @Override
        void prepareRemove() throws ActionUnperformableException {
            try {
                ConceptAnnotation conceptAnnotation = controller.getTextSourceCollection().getSelection().getConceptAnnotationCollection().getSelection();
                setCollection(conceptAnnotation.getSpanCollection());

                setObject(collection.getSelection());
            } catch (NoSelectionException e) {
                throw new ActionUnperformableException();
            }
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

    private static class ProfileAction extends AbstractKnowtatorCollectionAction<Profile> {
        private final KnowtatorController controller;
        private final String profileId;

        ProfileAction(String actionName, KnowtatorController controller, String profileId) {
            super(actionName, "Add profile");
            setCollection(controller.getProfileCollection());
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

    private static class TextSourceAction extends AbstractKnowtatorCollectionAction<TextSource> {
        private final KnowtatorController controller;
        private final File file;

        TextSourceAction(String actionName, KnowtatorController controller, File file) {
            super(actionName, "text source");
            this.controller = controller;
            this.file = file;
        }

        @Override
        void prepareRemove() throws ActionUnperformableException {
            try {
                setObject(controller.getTextSourceCollection().getSelection());
            } catch (NoSelectionException e) {
                throw new ActionUnperformableException();
            }
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
}
