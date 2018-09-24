package edu.ucdenver.ccp.knowtator.view.chooser;

import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import edu.ucdenver.ccp.knowtator.model.text.concept.ConceptAnnotation;
import edu.ucdenver.ccp.knowtator.view.KnowtatorView;

public class ProfileChooser extends Chooser<Profile> {

    public ProfileChooser(KnowtatorView view) {
        super(view);
        setCollection(view.getController().getProfileCollection());
    }

    @Override
    void reactToTextSourceChange(TextSource previousSelection, TextSource currentSelection) {

    }

    @Override
    void reactToAnnotationChange(ConceptAnnotation previousSelection, ConceptAnnotation currentSelection) {

    }
}
