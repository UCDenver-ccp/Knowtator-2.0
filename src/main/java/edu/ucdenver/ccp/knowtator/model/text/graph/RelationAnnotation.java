package edu.ucdenver.ccp.knowtator.model.text.graph;

import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGeometry;
import edu.ucdenver.ccp.knowtator.KnowtatorController;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLAttributes;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLIO;
import edu.ucdenver.ccp.knowtator.io.knowtator.KnowtatorXMLTags;
import edu.ucdenver.ccp.knowtator.model.OWLModel;
import edu.ucdenver.ccp.knowtator.model.profile.Profile;
import edu.ucdenver.ccp.knowtator.model.text.KnowtatorTextBoundObjectInterface;
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

public class RelationAnnotation extends mxCell implements KnowtatorXMLIO, KnowtatorTextBoundObjectInterface, OWLOntologyChangeListener, OWLModelManagerListener {
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
    private Logger log = Logger.getLogger(RelationAnnotation.class);
    private boolean dontRedraw;

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

        dontRedraw = false;

        controller.verifyId(id, this, false);

        getGeometry().setRelative(true);
        setEdge(true);
        setSource(source);
        setTarget(target);
        setValue(property);

        controller.getOWLModel().addOWLModelManagerListener(this);
        controller.getOWLModel().addOntologyChangeListener(this);

        dontRedraw = true;
        if (property == null) {
            setProperty(controller.getOWLModel().getOWLObjectPropertyByID(propertyID));
        } else {
            setProperty(property);
        }
        dontRedraw = false;
    }

    /*
    GETTERS
     */

    public Profile getAnnotator() {
        return annotator;
    }

    @SuppressWarnings("unused")
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
        value = String.format("%s%s\n%s %s",
                isNegated ? "not " : "",
                getOwlPropertyRendering(),
                quantifier,
                quantifierValue);


        super.setValue(value);
        if (graphSpace != null && !dontRedraw) {
            graphSpace.reDrawGraph();
        }
        textSource.save();
    }

    void setQuantifierValue(String quantifierValue) {
        this.quantifierValue = quantifierValue;
        setValue(property);
    }

    public void setProperty(OWLObjectProperty owlObjectProperty) {
        property = owlObjectProperty;
        setValue(property);
    }

    void setQuantifier(String quantifier) {
        this.quantifier = quantifier;
        setValue(property);
    }

    void setNegation(boolean negation) {
        this.isNegated = negation;
        setValue(property);
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