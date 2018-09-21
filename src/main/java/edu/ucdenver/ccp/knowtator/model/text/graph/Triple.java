package edu.ucdenver.ccp.knowtator.model.text.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.Savable;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.listeners.OWLSetupListener;
import edu.ucdenver.ccp.knowtator.listeners.ProjectListener;
import edu.ucdenver.ccp.knowtator.model.KnowtatorTextBoundObject;
import edu.ucdenver.ccp.knowtator.model.owl.OWLEntityNullException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLObjectPropertyNotFoundException;
import edu.ucdenver.ccp.knowtator.model.owl.OWLWorkSpaceNotSetException;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Triple extends mxCell implements Savable, KnowtatorXMLIO, KnowtatorTextBoundObject, ProjectListener, OWLSetupListener, OWLOntologyChangeListener, OWLModelManagerListener {
    private String quantifier;
    private String quantifierValue;
    private Profile annotator;
    private String bratID;
    private KnowtatorController controller;
    private Boolean isNegated;
    private TextSource textSource;
    private OWLObjectProperty property;
    private String propertyID;
    private GraphSpace graphSpace;
    @SuppressWarnings("unused")
    private Logger log = Logger.getLogger(Triple.class);
    private boolean dontRedraw;

    Triple(
            String id,
            mxCell source,
            mxCell target,
            OWLObjectProperty property,
            Profile annotator,
            String quantifier,
            String quantifierValue,
            Boolean isNegated,
            KnowtatorController controller, TextSource textSource, GraphSpace graphSpace) {
        super(null, new mxGeometry(), null);

        this.property = property;
        this.isNegated = isNegated;
        this.textSource = textSource;
        this.controller = controller;
        this.annotator = annotator;
        this.quantifier = quantifier;
        this.quantifierValue = quantifierValue;
        this.graphSpace = graphSpace;

        dontRedraw = false;
        controller.getOWLManager().addOWLSetupListener(this);
        controller.addProjectListener(this);

        controller.verifyId(id, this, false);

        getGeometry().setRelative(true);
        setEdge(true);
        setSource(source);
        setTarget(target);
        setValue(null);
    }

    Triple(String id,
           mxCell source,
           mxCell target,
           String propertyID,
           Profile annotator,
           String quantifier,
           String quantifierValue,
           Boolean isNegated,
           KnowtatorController controller, TextSource textSource, GraphSpace graphSpace) {
        super(null, new mxGeometry(), null);
        this.isNegated = isNegated;

        this.propertyID = propertyID;
        this.textSource = textSource;
        this.controller = controller;
        this.annotator = annotator;
        this.quantifier = quantifier;
        this.quantifierValue = quantifierValue;
        this.graphSpace = graphSpace;

        controller.getOWLManager().addOWLSetupListener(this);

        controller.verifyId(id, this, false);

        getGeometry().setRelative(true);
        setEdge(true);
        setSource(source);
        setTarget(target);

        owlSetup();
        setValue(null);
    }

    @Override
    public void save() {
        textSource.save();
    }

    @Override
    public void load() {

    }

    @Override
    public File getSaveLocation() {
        return null;
    }

    @Override
    public void setSaveLocation(File saveLocation) {

    }

    /*
    GETTERS
     */

    public Profile getAnnotator() {
        return annotator;
    }

    String getBratID() {
        return bratID;
    }

    public OWLObjectProperty getProperty() {
        return property;
    }

    String getQuantifier() {
        return quantifier;
    }

    String getQuantifierValue() {
        return quantifierValue;
    }

    @Override
    public TextSource getTextSource() {
        return textSource;
    }

    boolean getNegated() {
        return isNegated;
    }

    /*
    SETTERS
     */

    void setBratID(String bratID) {
        this.bratID = bratID;
    }

    @Override
    public void setValue(Object value) {
        if (controller != null) {
            try {
                value =
                        String.format(
                                "%s%s\n%s %s",
                                isNegated ? "not " : "",
                                controller.getOWLManager().getOWLEntityRendering(property),
                                quantifier,
                                quantifierValue);
            } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
                value =
                        String.format(
                                "%s%s\n%s %s", isNegated ? "not " : "", propertyID, quantifier, quantifierValue);
            }

            super.setValue(value);
            if (graphSpace != null && !dontRedraw) {
                graphSpace.reDrawGraph();
            }
            save();
        }
    }

    public void setQuantifierValue(String quantifierValue) {
        this.quantifierValue = quantifierValue;
        setValue(null);
    }

    public void setProperty(OWLObjectProperty owlObjectProperty) {
        this.property = owlObjectProperty;
        setValue(null);
    }

    public void setQuantifier(String quantifier) {
        this.quantifier = quantifier;
        setValue(null);
    }

    public void setNegation(boolean negation) {
        this.isNegated = negation;
        setValue(null);
    }

    /*
    WRITERS
     */

    public void writeToKnowtatorXML(Document dom, Element graphElem) {
        Element tripleElem = dom.createElement(KnowtatorXMLTags.TRIPLE);
        tripleElem.setAttribute(KnowtatorXMLAttributes.ID, id);
        try {
            tripleElem.setAttribute(KnowtatorXMLAttributes.ANNOTATOR, annotator.getId());
            tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_SUBJECT, getSource().getId());
            tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_OBJECT, getTarget().getId());
        } catch (NullPointerException ignore) {

        }

        String propertyID;
        try {
            propertyID = controller.getOWLManager().getOWLEntityRendering(property);
        } catch (OWLEntityNullException | OWLWorkSpaceNotSetException e) {
            propertyID = this.propertyID;
        }
        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_PROPERTY, propertyID);

        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_QUANTIFIER, quantifier);
        tripleElem.setAttribute(KnowtatorXMLAttributes.TRIPLE_VALUE, quantifierValue);
        tripleElem.setAttribute(KnowtatorXMLAttributes.IS_NEGATED, isNegated ? KnowtatorXMLAttributes.IS_NEGATED_TRUE : KnowtatorXMLAttributes.IS_NEGATED_FALSE);

        graphElem.appendChild(tripleElem);
    }



    /*
    READERS
     */

    @Override
    public void readFromKnowtatorXML(File file, Element parent) {
    }

    @Override
    public void readFromOldKnowtatorXML(File file, Element parent) {
    }




    @Override
    public void owlSetup() {
        try {
            controller.getOWLManager().getWorkSpace().getOWLModelManager().addListener(this);
            controller.getOWLManager().getWorkSpace().getOWLModelManager().addOntologyChangeListener(this);

            property = controller.getOWLManager().getOWLObjectPropertyByID(propertyID);
            dontRedraw = true;
            setValue(String.format("%s\n%s %s", controller.getOWLManager().getOWLEntityRendering(property), quantifier, quantifierValue));
            dontRedraw = false;
        } catch (OWLWorkSpaceNotSetException | OWLObjectPropertyNotFoundException | OWLEntityNullException ignored) {

        }
    }

    @Override
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {
        Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
        Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
        OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
        OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

        processOntologyChanges(changes, addedCollector, removedCollector);

        log.warn("Added entities:");
        possiblyAddedEntities.forEach(owlEntity -> log.warn(owlEntity));
        log.warn("Removed entities");
        possiblyRemovedEntities.forEach(owlEntity -> log.warn(owlEntity));

    /*
    For now, I will assume that entity removed is the one that existed and the one
    that is added is the new name for it.
     */
        if (!possiblyAddedEntities.isEmpty() && !possiblyRemovedEntities.isEmpty()) {
            OWLEntity oldProperty = possiblyRemovedEntities.iterator().next();
            OWLEntity newProperty = possiblyAddedEntities.iterator().next();
            if (property == oldProperty) {
                try {
                    property = ((OWLObjectProperty) newProperty);
                    setValue(String.format("%s\n%s, %s", controller.getOWLManager().getOWLEntityRendering(newProperty), quantifier, quantifierValue));
                } catch (OWLWorkSpaceNotSetException | OWLEntityNullException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void processOntologyChanges(@Nonnull List<? extends OWLOntologyChange> changes, OWLEntityCollector addedCollector, OWLEntityCollector removedCollector) {
        for (OWLOntologyChange chg : changes) {
            if (chg.isAxiomChange()) {
                OWLAxiomChange axChg = (OWLAxiomChange) chg;
                if (axChg.getAxiom().getAxiomType() == AxiomType.DECLARATION) {
                    if (axChg instanceof AddAxiom) {
                        axChg.getAxiom().accept(addedCollector);
                    } else {
                        axChg.getAxiom().accept(removedCollector);
                    }
                }
            }
        }
    }

    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
            try {
                setValue(String.format("%s\n%s %s", controller.getOWLManager().getOWLEntityRendering(property), quantifier, quantifierValue));
            } catch (OWLWorkSpaceNotSetException | OWLEntityNullException ignored) {
            }
        }
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void projectLoaded() {
        owlSetup();
    }

    @Override
    public void dispose() {
        try {
            controller.getOWLManager().getWorkSpace().getOWLModelManager().removeListener(this);
            controller.getOWLManager().getWorkSpace().getOWLModelManager().removeOntologyChangeListener(this);
        } catch (OWLWorkSpaceNotSetException e) {
            e.printStackTrace();
        }
    }
}
