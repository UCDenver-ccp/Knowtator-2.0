package edu.ucdenver.ccp.knowtator.model.text.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.OWLModel;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundDataObjectInterface;
import edu.ucdenver.ccp.knowtator.model.text.TextSource;
import org.apache.log4j.Logger;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyChangeListener;
import org.semanticweb.owlapi.util.OWLEntityCollector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.annotation.Nonnull;
import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RelationAnnotation extends mxCell implements KnowtatorXMLIO, KnowtatorTextBoundDataObjectInterface, OWLOntologyChangeListener, OWLModelManagerListener {
    private final String quantifier;
    private final String quantifierValue;
    private final Profile annotator;
    private String bratID;
    private final KnowtatorController controller;
    private final Boolean isNegated;
    private final TextSource textSource;
    private OWLObjectProperty property;
    private final String propertyID;
    private final GraphSpace graphSpace;
    @SuppressWarnings("unused")
    private final Logger log = Logger.getLogger(RelationAnnotation.class);

    RelationAnnotation(
            String id,
            mxCell source,
            mxCell target,
            OWLObjectProperty property,
            String propertyID,
            Profile annotator,
            String quantifier,
            String quantifierValue,
            Boolean isNegated,
            KnowtatorController controller, TextSource textSource, GraphSpace graphSpace) {
        super(null, new mxGeometry(), null);

        this.property = property;
        this.propertyID = propertyID;
        this.isNegated = isNegated;
        this.textSource = textSource;
        this.controller = controller;
        this.annotator = annotator;
        this.quantifier = quantifier;
        this.quantifierValue = quantifierValue;
        this.graphSpace = graphSpace;

        controller.verifyId(id, this, false);

        getGeometry().setRelative(true);
        setEdge(true);
        setSource(source);
        setTarget(target);
        setValue(property);

        controller.getOWLModel().addOWLModelManagerListener(this);
        controller.getOWLModel().addOntologyChangeListener(this);

        if (property == null) {
            setProperty(controller.getOWLModel().getOWLObjectPropertyByID(propertyID));
        } else {
            setProperty(property);
        }
    }

    /*
    GETTERS
     */

    @SuppressWarnings("unused")
    String getBratID() {
        return bratID;
    }

    @Override
    public TextSource getTextSource() {
        return textSource;
    }

    /*
    SETTERS
     */

    @SuppressWarnings("unused")
    void setBratID(String bratID) {
        this.bratID = bratID;
    }

    private String getOwlPropertyRendering() {
        if (controller.getOWLModel().isWorkSpaceSet()) {
            return controller.getOWLModel().getOWLEntityRendering(property);
        }
        else {
            return propertyID;
        }
    }

    @Override
    public void setValue(Object value) {
        if (value != null) {
            value = String.format("%s%s\n%s %s",
                    isNegated ? "not " : "",
                    getOwlPropertyRendering(),
                    quantifier,
                    quantifierValue);


            super.setValue(value);
            if (graphSpace != null && controller.isNotLoading()) {
                graphSpace.reDrawGraph();
            }
//            textSource.save();
        }
    }

    private void setProperty(OWLObjectProperty owlObjectProperty) {
        property = owlObjectProperty;
        setValue(property == null ? propertyID : property);
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

        String propertyID = getOwlPropertyRendering();

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
    public void ontologiesChanged(@Nonnull List<? extends OWLOntologyChange> changes) {
        Set<OWLEntity> possiblyAddedEntities = new HashSet<>();
        Set<OWLEntity> possiblyRemovedEntities = new HashSet<>();
        OWLEntityCollector addedCollector = new OWLEntityCollector(possiblyAddedEntities);
        OWLEntityCollector removedCollector = new OWLEntityCollector(possiblyRemovedEntities);

        OWLModel.processOntologyChanges(changes, addedCollector, removedCollector);

        log.warn("Added entities:");
        possiblyAddedEntities.forEach(log::warn);
        log.warn("Removed entities");
        possiblyRemovedEntities.forEach(log::warn);

    /*
    For now, I will assume that entity removed is the one that existed and the one
    that is added is the new name for it.
     */
        if (!possiblyAddedEntities.isEmpty() && !possiblyRemovedEntities.isEmpty()) {
            OWLEntity oldProperty = possiblyRemovedEntities.iterator().next();
            OWLEntity newProperty = possiblyAddedEntities.iterator().next();
            if (property == oldProperty) {
                setProperty((OWLObjectProperty) newProperty);
            }
        }
    }



    @Override
    public void handleChange(OWLModelManagerChangeEvent event) {
        if (event.isType(EventType.ENTITY_RENDERER_CHANGED)) {
            setValue(property);
        }
    }

    @Override
    public void dispose() {
        controller.getOWLModel().removeOWLModelManagerListener(this);
        controller.getOWLModel().removeOntologyChangeListener(this);
    }

    @Override
    public int compareTo(Object o) {
        return 0;
    }
}
